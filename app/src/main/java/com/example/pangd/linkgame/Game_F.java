package com.example.pangd.linkgame;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.pangd.linkgame.Game.Board;
import com.example.pangd.linkgame.Game.NewGame;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class Game_F extends Fragment {
    private NewGame game;
    private Board board = new Board();
    private List<Item> blockList = new ArrayList<>();
    private int[] shape;
    private BlockAdapter adapter;
    public int degree = 20;
    public int mode = 3;
    public int flush = 3;
    final private int allTime = 1200;
    private int allTime_Temp;
    static private Timer timer;
    Button fab;
    RecyclerView recyclerView;
    StaggeredGridLayoutManager layoutManager;
    ProgressBar progressBar;
    Button startButton;
    boolean IsStart = false;

    private static final String TAG = "Game_F";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_game, container,
                false);

        fab = (Button) view.findViewById(R.id.fab);
        startButton = (Button) view.findViewById(R.id.start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!IsStart) {
                    timer = new Timer("MainTimer", true);
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Message message = new Message();
                            message.what = 2;
                            handler.sendMessage(message);
                        }
                    }, 1000, 100);
                    IsStart = true;
                    startButton.setVisibility(View.INVISIBLE);
                }
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flush > 0 && IsStart) {
                    flush -= 1;
                    fab.setText(Integer.toString(flush));
                    board.disorganize();
                    blockList.clear();
                    initWindow(true);
                }
            }
        });
        progressBar = (ProgressBar) view.findViewById(R.id.TimeProgressbar);

        // ###################################################################
        recyclerView = (RecyclerView) view.findViewById(R.id.main_view);

        initView();
        return view;
    }

    final Handler handler = new Handler(new Handler.Callback() {
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 2:
                    allTime_Temp -= 1;
                    progressBar.setProgress(allTime_Temp);
                    if (allTime_Temp < 0) {
                        Toast.makeText(getActivity(), "loss", Toast.LENGTH_LONG).show();
                        initView();
//                        Intent intent = new Intent(MainActivity.this, EndGame.class);
//                        startActivity(intent);
                    }
            }
            return false;
        }
    });

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            // 一定设置为null，否则定时器不会被回收
            timer = null;
        }
    }

    private int[] get_parameters() {
        if (mode == 1) {
            return new int[]{7, 6};
        } else if (mode == 2) {
            return new int[]{8, 7};
        } else if (mode == 3) {
            return new int[]{9, 8};
        } else {
            return new int[]{9, 8};
        }
    }

    public int getImageID(String resName) {
        return this.getResources().getIdentifier(resName, "drawable", getActivity().getPackageName());
    }

    private void initWindow(boolean update) {
        blockList.clear();
        int[][] TypeArray = board.getTypeBlock();
        Log.d(TAG, "initGame: " + shape[0] * shape[1]);
        for (int i = 0; i < shape[0] * shape[1]; i++) {
            int Type = TypeArray[i / shape[1]][i % shape[1]];
            Log.d(TAG, "initGame: Type；" + Type);
            String name = "p_" + String.valueOf(Type + 1);
            if (Type == Board.nullType) {
                name = "null_image";
            }
            Item block = new Item(getImageID(name), getImageID("null"));
            blockList.add(block);
        }
        if (update)
            adapter.notifyDataSetChanged();
    }

    void initGame(boolean update) {
        progressBar.setMax(allTime_Temp);
        progressBar.setProgress(allTime_Temp);

        Log.d(TAG, "onCreate: timer" + timer);
        shape = get_parameters();
        shape[0] += 2;
        shape[1] += 2;
        game = new NewGame(shape[0], shape[1], degree, 2);
        board.StartGame(game);
        flush = 3;
        fab.setText(Integer.toString(flush));
        initWindow(update);

    }

    public void initView() {
        stopTimer();
        allTime_Temp = allTime;
        initGame(false);
        layoutManager = new
                StaggeredGridLayoutManager(shape[1], StaggeredGridLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);
        adapter = new BlockAdapter(blockList);
        adapter.setOnItemClickListener(new BlockAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int[] _info = board.NextStep(position);
                if (_info[0] != 0 && IsStart) {
                    if (_info.length == 3) {
                        adapter.changeItem(_info[1], -1, _info[2]);
                    } else {
                        adapter.changeItem(_info[1], R.drawable.null_image, -1);
                        adapter.changeItem(_info[2], R.drawable.null_image, R.drawable.null_image);
                        if (_info.length == 5) {
                            Toast.makeText(getActivity(), "Win", Toast.LENGTH_LONG).show();
                            initView();
                        }
                    }
                }
            }
        });
        recyclerView.setAdapter(adapter);
        startButton.setVisibility(View.VISIBLE);
        IsStart = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
    }
}
