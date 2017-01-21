package com.liruya.colorpicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by liruya on 2017/1/21.
 */

public class ColorPicker extends View
{
    private final int DEFAULT_COLOR = 0xFF0055AA;
    private final int DEFAULT_DIVIDER_COLOR = 0xFFFFFF;
    private final float DEFAULT_CIRCLE_RADIUS = 200;
    private final float DEFAULT_CENTER_WEIGHT = 0.4f;
    private final float DEFAULT_CIRCLE_WEIGHT = 0.2f;

    private Paint mCirclePaint;
    private Paint mCenterPanit;
    private Paint mLinePaint;

    private int mSelectedColor;
    private int mDividerColor;
    private float mCenterWeight;
    private float mCircleWeight;
    private final int[] mCircleColors = new int[]{0xFFFF0000, 0xFFFFFF00, 0xFF00FF00, 0xFF00FFFF, 0xFF0000FF, 0xFFFF00FF, 0xFFFF0000};
    private int mCircleRadius;
    private int mCenterRadius;
    private int mCenterX;
    private int mCenterY;
    private int mRadiusX;
    private int mRadiusY;
    private int mCircleThickness;

    private OnColorChangeListener mOnColorChangeListener;

    public ColorPicker ( Context context )
    {
        super( context );
    }

    public ColorPicker ( Context context, AttributeSet attrs )
    {
//        super( context, attrs );
        this( context, attrs, 0 );
    }

    public ColorPicker ( Context context, AttributeSet attrs, int defStyleAttr )
    {
        super( context, attrs, defStyleAttr );

        TypedArray a = context.obtainStyledAttributes( attrs, R.styleable.ColorPicker );
        mSelectedColor = a.getColor( R.styleable.ColorPicker_selectedColor, DEFAULT_COLOR );
        mCircleWeight = a.getFloat( R.styleable.ColorPicker_circleWeight, DEFAULT_CIRCLE_WEIGHT );
        mCenterWeight = a.getFloat( R.styleable.ColorPicker_centerWeight, DEFAULT_CENTER_WEIGHT );
        mDividerColor = a.getColor( R.styleable.ColorPicker_dividerColor, DEFAULT_DIVIDER_COLOR );
        if ( mCenterWeight >= 1 || mCircleWeight >= 1 || mCenterWeight + mCircleWeight >= 1 )
        {
            mCenterWeight = DEFAULT_CENTER_WEIGHT;
            mCircleWeight = DEFAULT_CIRCLE_WEIGHT;
        }
        a.recycle();
    }

    @Override
    protected void onMeasure ( int widthMeasureSpec, int heightMeasureSpec )
    {
        super.onMeasure( widthMeasureSpec, heightMeasureSpec );
        int widthMode = MeasureSpec.getMode( widthMeasureSpec );
        int widthSize = MeasureSpec.getSize( widthMeasureSpec );
        int heightMode = MeasureSpec.getMode( heightMeasureSpec );
        int heightSize = MeasureSpec.getSize( heightMeasureSpec );
        int width = (int) ( DEFAULT_CIRCLE_RADIUS * 2) + getPaddingLeft() + getPaddingRight();
        int height = (int) ( DEFAULT_CIRCLE_RADIUS * 2) + getPaddingTop() + getPaddingBottom();
        switch ( widthMode )
        {
            case MeasureSpec.EXACTLY:
                width = widthSize;
                break;

            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:

                break;
        }

        switch ( heightMode )
        {
            case MeasureSpec.EXACTLY:
                height = heightSize;
                break;

            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                break;
        }
        setMeasuredDimension( width, height );
    }

    @Override
    protected void onDraw ( Canvas canvas )
    {
        super.onDraw( canvas );
        int l = getPaddingLeft();
        int t = getPaddingTop();
        int r = getPaddingRight();
        int b = getPaddingBottom();
        mRadiusX = (getMeasuredWidth() - l - r)/2;
        mRadiusY = (getMeasuredHeight() - t - b)/2;
        mCircleRadius = mRadiusX > mRadiusY ? mRadiusY : mRadiusX;
        mCenterX = l + mRadiusX;
        mCenterY = t + mRadiusY;
        mCenterRadius = (int) ( mCircleRadius * mCenterWeight);
        mCenterPanit = new Paint();
        mCenterPanit.setColor( mSelectedColor );
        mCenterPanit.setStyle( Paint.Style.FILL_AND_STROKE );
        canvas.drawCircle( mCenterX, mCenterY, mCenterRadius, mCenterPanit );

        mCircleThickness = (int) ( mCircleRadius * mCircleWeight );
        Shader shader = new SweepGradient( mCenterX, mCenterY, mCircleColors, null );
        mCirclePaint = new Paint();
        mCirclePaint.setShader( shader );
        mCirclePaint.setStyle( Paint.Style.STROKE );
        mCirclePaint.setStrokeWidth( mCircleThickness );
        canvas.drawOval( mCenterX-mCircleRadius+mCircleThickness/2,
                         mCenterY-mCircleRadius+mCircleThickness/2,
                         mCenterX+mCircleRadius-mCircleThickness/2,
                         mCenterY+mCircleRadius-mCircleThickness/2,
                         mCirclePaint );
    }

    @Override
    public boolean onTouchEvent ( MotionEvent event )
    {
        float x = event.getX() - mCenterX;
        float y = event.getY() - mCenterY;
        float val = x*x + y*y;
        float vo = mCircleRadius*mCircleRadius;
        float vi = (mCircleRadius-mCircleThickness)*(mCircleRadius-mCircleThickness);
        if ( val > vi && val < vo )
        {
            float angle = (float) Math.atan2( y, x );
            float p = (float) ( angle / ( Math.PI * 2));
            if ( p < 0 )
            {
                p += 1;
            }
            float d = p * ( mCircleColors.length - 1);
            int idx = (int) d;
            int c0 = mCircleColors[idx];
            int c1 = mCircleColors[idx+1];
            int r = Color.red( c0 ) + Math.round( (d - idx) * ( Color.red( c1 ) - Color.red( c0 )) );
            int g = Color.green( c0 ) + Math.round( (d - idx) * ( Color.green( c1 ) - Color.green( c0 )) );
            int b = Color.blue( c0 ) + Math.round( (d - idx) * ( Color.blue( c1 ) - Color.blue( c0 )) );
            mSelectedColor = Color.argb( 0xFF, r, g, b );
            mCenterPanit.setColor( mSelectedColor );
            mOnColorChangeListener.onColorChanged( r, g, b );
            invalidate();
        }
        return true;
    }

    public void setOnColorChangeListener( OnColorChangeListener listener )
    {
        mOnColorChangeListener = listener;
    }

    interface OnColorChangeListener
    {
        void onColorChanged( int r, int g, int b );
    }
}
