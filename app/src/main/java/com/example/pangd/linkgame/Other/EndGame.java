package com.example.pangd.linkgame.Other;

import android.app.Activity;
import android.os.Bundle;


import com.example.pangd.linkgame.R;

public class EndGame extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);

    }

    @Override
    public void finish() {
        super.finish();
        //重写finish，解决退出动画无效的问题
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }

}
