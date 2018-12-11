package com.example.pangd.linkgame.Other;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.example.pangd.linkgame.R;

public class CopyRight extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copy_right);
    }


    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }

}
