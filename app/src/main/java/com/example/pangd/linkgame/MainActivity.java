package com.example.pangd.linkgame;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.dou361.dialogui.DialogUIUtils;
import com.dou361.dialogui.listener.DialogUIListener;
import com.example.pangd.linkgame.Other.CopyRight;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private RankInfoReceiver rankInfoReceiver;
    private IntentFilter intentFilter;
    private Thread thread;// 声明一个线程对象
    private static MediaPlayer mp = null;// 声明一个MediaPlayer对象
    static boolean bar_Action = true;
    public static Game_F Now_Fragmet;
    static public Game_F getNow_Fragmet() {
        return Now_Fragmet;
    }
    static public void setBar_Action(boolean _bar_Action){
        bar_Action = _bar_Action;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Now_Fragmet = new Game_F();
        getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, Now_Fragmet).commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                playBGSound();// 播放背景音乐
            }
        });
        thread.start();
        buildRegisterRank(); //注册广播

    }

    private void buildRegisterRank(){
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.broadcast.RANK_INFOMATION");
        rankInfoReceiver = new RankInfoReceiver();
        registerReceiver(rankInfoReceiver, intentFilter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(!bar_Action)
            return true;
        //noinspection SimplifiableIfStatement
        if (id == R.id.sub_difficulty_1) {
            Now_Fragmet.mode = 1;
            Now_Fragmet.initView();
            return true;
        }
        if (id == R.id.sub_difficulty_2) {
            Now_Fragmet.mode = 2;
            Now_Fragmet.initView();
            return true;
        }
        if (id == R.id.sub_difficulty_3) {
            Now_Fragmet.mode = 3;
            Now_Fragmet.initView();
            return true;
        }
        if (id == R.id.sub_number_1) {
            Now_Fragmet.degree = 10;
            Now_Fragmet.initGame(true);
            return true;
        }
        if (id == R.id.sub_number_2) {
            Now_Fragmet.degree = 15;
            Now_Fragmet.initGame(true);
            return true;
        }
        if (id == R.id.sub_number_3) {
            Now_Fragmet.degree = 25;
            Now_Fragmet.initGame(true);
            return true;
        }

        if (id == R.id.action_flush) {
            Now_Fragmet.fab.performClick();
            return true;
        }

        if(id==R.id.action_review){
            Now_Fragmet.initView();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_rank) {
            rankInfoReceiver.getActivityRank(Now_Fragmet.getMode(), Now_Fragmet.getDegree());
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(MainActivity.this, CopyRight.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void playBGSound(){
        if (mp != null) {
            mp.release();// 释放资源
        }
        mp = MediaPlayer.create(MainActivity.this, R.raw.merry_christmas);
        mp.start();// 开始播放
        // 为MediaPlayer添加播放完事件监听器
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                try {
                    Thread.sleep(1000);// 线程休眠1秒钟
                    mp.start();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (mp != null) {
            mp.stop();// 停止播放
            mp.release();// 释放资源
            mp = null;
        }
        if (thread != null) {
            thread = null;
        }
        super.onDestroy();
        unregisterReceiver(rankInfoReceiver);
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
    }

    class RankInfoReceiver extends BroadcastReceiver{
        private Database dbHelper;
        public RankInfoReceiver(){
            super();
            dbHelper = new Database(getApplicationContext(), "RankInfo.db", null, 2);
            dbHelper.getWritableDatabase();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            final int degreeDifficult = intent.getIntExtra("degreeDifficult", 0);
            final int numberBlock = intent.getIntExtra("numberBlock", 0);
            values.put("playerName", intent.getStringExtra("playerName"));
            values.put("costTime", intent.getDoubleExtra("costTime", 0.0));
            values.put("inputTime", getTimeNow());
            values.put("degreeDifficult", degreeDifficult);
            values.put("numberBlock", numberBlock);
            Log.d(TAG, "onReceive: playerName: " + intent.getStringExtra("playerName"));
            Log.d(TAG, "onReceive: costTime: " + intent.getDoubleExtra("costTime", 0.0));
            Log.d(TAG, "onReceive: inputTime: " + getTimeNow());
            Log.d(TAG, "onReceive: degreeDifficult: " + intent.getIntExtra("degreeDifficult", 0));
            Log.d(TAG, "onReceive: numberBlock: " + intent.getIntExtra("numberBlock", 0));
            db.insert("RANK", null, values);
            DialogUIUtils.showMdAlert(MainActivity.this, "Good Job",
                    "恭喜你完成了游戏，是否查看排行榜", new DialogUIListener() {
                @Override
                public void onPositive() {
                    Now_Fragmet.initView(); //重置画面
                    getActivityRank(degreeDifficult, numberBlock);
                }

                @Override
                public void onNegative() {
                    Now_Fragmet.initView();
                }

            }).show();
//            getActivityRank(degreeDifficult, numberBlock);

        }

        public void getActivityRank(int degreeDifficult, int numberBlock){
            bar_Action = false;
            Fragment fragment_rank = new Rank_F();
            ((Rank_F) fragment_rank).setDegreeDifficult(degreeDifficult);
            ((Rank_F) fragment_rank).setNumberBlock(numberBlock);
            replaceFragment(fragment_rank);
        }

        String getTimeNow(){
            SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd");
            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
            return formatter.format(curDate);
        }
    }
}
