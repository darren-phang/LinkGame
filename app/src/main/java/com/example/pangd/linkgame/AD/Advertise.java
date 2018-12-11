package com.example.pangd.linkgame.AD;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.pangd.linkgame.MainActivity;
import com.example.pangd.linkgame.R;

import java.util.Timer;
import java.util.TimerTask;


public class Advertise extends AppCompatActivity implements View.OnClickListener {
    Timer timer;
    int delay = 0;
    int period = 3000;
    private int cycle = 0;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timer = new Timer("adTimer",true);
        setContentView(R.layout.activity_advertise);
        button = (Button) findViewById(R.id.passButton);
        button.setOnClickListener(this);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        }, delay, period);

    }

    final Handler handler = new Handler(new Handler.Callback() {
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    cycle += 1;
                    if (cycle == 2){
                        button.performClick();
                    }
            }
            return false;
        }
    });

    // 停止定时器
    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            // 一定设置为null，否则定时器不会被回收
            timer = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.passButton:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                stopTimer();
                this.finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }
}
