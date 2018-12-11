package com.example.pangd.linkgame;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.pangd.linkgame.Game.Board;
import com.example.pangd.linkgame.Other.CopyRight;
import com.example.pangd.linkgame.Game.NewGame;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
//    private NewGame game;
//    private Board board = new Board();
//    private List<Item> blockList = new ArrayList<>();
//    private int[] shape;
//    private BlockAdapter adapter;
//    private int degree = 20;
//    private int mode = 3;
//    private int flush = 3;
//    final private int allTime = 1200;
//    private int allTime_Temp;
//    static private Timer timer;
//    Button fab;
//    RecyclerView recyclerView;
//    StaggeredGridLayoutManager layoutManager;
//    ProgressBar progressBar;
//    Button startButton;
//    boolean IsStart = false;

    Game_F Now_Fragmet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Now_Fragmet = new Game_F();
        getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, Now_Fragmet).commit();
//        fab = (Button) findViewById(R.id.fab);
//        startButton = (Button) findViewById(R.id.start);
//        startButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!IsStart) {
//                    timer = new Timer("MainTimer", true);
//                    timer.schedule(new TimerTask() {
//                        @Override
//                        public void run() {
//                            Message message = new Message();
//                            message.what = 2;
//                            handler.sendMessage(message);
//                        }
//                    }, 1000, 100);
//                    IsStart = true;
//                    startButton.setVisibility(View.INVISIBLE);
//                }
//            }
//        });
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (flush > 0 && IsStart) {
//                    flush -= 1;
//                    fab.setText(Integer.toString(flush));
//                    board.disorganize();
//                    blockList.clear();
//                    initWindow(true);
//                }
//            }
//        });
//        progressBar = (ProgressBar) findViewById(R.id.TimeProgressbar);
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();
//
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
//        // ###################################################################
//        recyclerView = (RecyclerView) findViewById(R.id.main_view);
//        initView();
//        progressBar = (ProgressBar) findViewById(R.id.TimeProgressbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
//
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
            Now_Fragmet.degree = 20;
            Now_Fragmet.initGame(true);
            return true;
        }

//        if (id == R.id.action_flush) {
//            fab.performClick();
//        }

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

//    final Handler handler = new Handler(new Handler.Callback() {
//        public boolean handleMessage(Message msg) {
//            switch (msg.what) {
//                case 2:
//                    allTime_Temp -= 1;
//                    progressBar.setProgress(allTime_Temp);
//                    if (allTime_Temp < 0) {
//                        Toast.makeText(MainActivity.this, "loss", Toast.LENGTH_LONG).show();
//                        initView();
////                        Intent intent = new Intent(MainActivity.this, EndGame.class);
////                        startActivity(intent);
//                    }
//            }
//            return false;
//        }
//    });

//    private void stopTimer() {
//        if (timer != null) {
//            timer.cancel();
//            // 一定设置为null，否则定时器不会被回收
//            timer = null;
//        }
//    }
//
//    private int[] get_parameters() {
//        if (mode == 1) {
//            return new int[]{7, 6};
//        } else if (mode == 2) {
//            return new int[]{8, 7};
//        } else if (mode == 3) {
//            return new int[]{9, 8};
//        } else {
//            return new int[]{9, 8};
//        }
//    }
//
//    public int getImageID(String resName) {
//        return this.getResources().getIdentifier(resName, "drawable", this.getPackageName());
//    }
//
//    private void initWindow(boolean update) {
//        blockList.clear();
//        int[][] TypeArray = board.getTypeBlock();
//        Log.d(TAG, "initGame: " + shape[0] * shape[1]);
//        for (int i = 0; i < shape[0] * shape[1]; i++) {
//            int Type = TypeArray[i / shape[1]][i % shape[1]];
//            Log.d(TAG, "initGame: Type；" + Type);
//            String name = "p_" + String.valueOf(Type + 1);
//            if (Type == Board.nullType) {
//                name = "null_image";
//            }
//            Item block = new Item(getImageID(name), getImageID("null"));
//            blockList.add(block);
//        }
//        if (update)
//            adapter.notifyDataSetChanged();
//    }
//
//    private void initGame(boolean update) {
//        progressBar.setMax(allTime_Temp);
//        progressBar.setProgress(allTime_Temp);
//
//        Log.d(TAG, "onCreate: timer" + timer);
//        shape = get_parameters();
//        shape[0] += 2;
//        shape[1] += 2;
//        game = new NewGame(shape[0], shape[1], degree, 2);
//        board.StartGame(game);
//        flush = 3;
//        fab.setText(Integer.toString(flush));
//        initWindow(update);
//
//    }
//
//    private void initView() {
//        stopTimer();
//        allTime_Temp = allTime;
//        initGame(false);
//        layoutManager = new
//                StaggeredGridLayoutManager(shape[1], StaggeredGridLayoutManager.VERTICAL);
//
//        recyclerView.setLayoutManager(layoutManager);
//        adapter = new BlockAdapter(blockList);
//        adapter.setOnItemClickListener(new BlockAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                int[] _info = board.NextStep(position);
//                if (_info[0] != 0 && IsStart) {
//                    if (_info.length == 3) {
//                        adapter.changeItem(_info[1], -1, _info[2]);
//                    } else {
//                        adapter.changeItem(_info[1], R.drawable.null_image, -1);
//                        adapter.changeItem(_info[2], R.drawable.null_image, R.drawable.null_image);
//                        if (_info.length == 5) {
//                            Toast.makeText(getApplicationContext(), "Win", Toast.LENGTH_LONG).show();
//                            initView();
//                        }
//                    }
//                }
//            }
//        });
//        recyclerView.setAdapter(adapter);
//        startButton.setVisibility(View.VISIBLE);
//        IsStart = false;
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        stopTimer();
//    }
}
