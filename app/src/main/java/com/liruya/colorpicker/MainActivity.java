package com.liruya.colorpicker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";
    private ColorPicker main_cp;
    @Override
    protected void onCreate ( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        main_cp = (ColorPicker) findViewById( R.id.main_cp );
        main_cp.setOnColorChangeListener( new ColorPicker.OnColorChangeListener() {
            @Override
            public void onColorChanged ( int r, int g, int b )
            {
                Log.e( TAG, "onColorChanged: " + r + "\t" + g + "\t" + b );
            }
        } );
    }
}
