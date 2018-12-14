package com.example.pangd.linkgame;

import android.content.Context;
import android.content.Intent;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dou361.dialogui.DialogUIUtils;
import com.dou361.dialogui.listener.DialogUIListener;
import com.example.pangd.linkgame.FragmentSet.BlockAdapter;
import com.example.pangd.linkgame.FragmentSet.Item;
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
    public int degree = 25;
    public int mode = 3;
    public int flush = 3;
    final private int allTime = 1200;
    private int allTime_Temp;
    static private Timer timer;
    public Button fab;
    RecyclerView recyclerView;
    StaggeredGridLayoutManager layoutManager;
    ProgressBar progressBar;
    Button startButton;
    boolean IsStart = false;
    SoundPoolUtil sound;
    private static final String TAG = "Game_F";

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public int getDegree() {
        return degree;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game, container,
                false);
        sound = SoundPoolUtil.getInstance(getContext());
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
                    sound.play(sound.START);
                    IsStart = true;
                    startButton.setVisibility(View.INVISIBLE);
                }
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sound.play(sound.FLUSH);
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
                        sound.play(sound.LOSS);
                        DialogUIUtils.showMdAlert(getActivity(), "Loss",
                                "在来一局", new DialogUIListener() {
                                    @Override
                                    public void onPositive() {
                                        initView(); //重置画面
                                    }

                                    @Override
                                    public void onNegative() {
                                    }
                                }).show();
//                        Toast.makeText(getActivity(), "loss", Toast.LENGTH_LONG).show();
                        initView();
//                        Intent intent = new Intent(getActivity(), EndGame.class);
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
            String name = "p_" + String.valueOf(Type);
            if (Type == Board.nullType) {
                name = "null_image";
            }
            Item block = new Item(getImageID(name), getImageID("null"));
            blockList.add(block);
        }
        if (update)
            adapter.notifyDataSetChanged();
    }

    public void initGame(boolean update) {
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
                if (IsStart) {
                    int[] _info = board.NextStep(position);
                    if (_info[0] != 0) {
                        if (_info.length == 3) {
                            if(_info[2]==R.drawable.null_image){
                                sound.play(sound.REPEAT);
                            }//点重
                            adapter.changeItem(_info[1], -1, _info[2]);
                        } else {
                            adapter.changeItem(_info[1], R.drawable.null_image, -1);
                            adapter.changeItem(_info[2], R.drawable.null_image, R.drawable.null_image);
                            sound.play(sound.LINK);    //消除
                            if (_info.length == 5) {
                                sound.play(sound.WIN);   //获胜
                                sendRankBroadcast("defaultUser", (double)(allTime-allTime_Temp)/10);
//                                Intent intent = new Intent("com.example.broadcast.RANK_INFOMATION");
//                                getActivity().sendBroadcast(intent);
//                                Toast.makeText(getActivity(), "Win", Toast.LENGTH_LONG).show();
//                                initView();
                            }
                        }
                    }
                    else {
                        sound.play(sound.WRONG);   //点错
                    }
                }
            }

        });
        recyclerView.setAdapter(adapter);
        startButton.setVisibility(View.VISIBLE);
        IsStart = false;
    }

    private void sendRankBroadcast(String name, double costTime){
        // 名字、耗时、上榜时间
        Intent intent = new Intent("com.example.broadcast.RANK_INFOMATION");
        intent.putExtra("playerName", name);
        intent.putExtra("costTime", costTime);
        intent.putExtra("degreeDifficult", mode);
        intent.putExtra("numberBlock", degree);
        getActivity().sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
    }
}
class SoundPoolUtil {
    private static SoundPoolUtil soundPoolUtil;
    private SoundPool soundPool;
    public int START=1;
    public int WIN=2;
    public int LOSS=3;
    public int LINK=4;
    public int FLUSH=5;
    public int WRONG=6;
    public int REPEAT=7;
    //单例模式
    public static SoundPoolUtil getInstance(Context context) {
        if (soundPoolUtil == null)
            soundPoolUtil = new SoundPoolUtil(context);
        return soundPoolUtil;
    }

    private SoundPoolUtil(Context context) {
        soundPool = new SoundPool.Builder()
                    .setMaxStreams(10)
                    .build();
        //加载音频文件
        soundPool.load(context, R.raw.start, 1);
        soundPool.load(context, R.raw.win, 3);
        soundPool.load(context, R.raw.lose, 3);
        soundPool.load(context, R.raw.link, 1);
        soundPool.load(context, R.raw.spread, 2);
        soundPool.load(context, R.raw.wrong,1);
        soundPool.load(context, R.raw.repeat, 1);
    }

    public void play(int number) {
        Log.d("tag", "number " + number);
        //播放音频
        soundPool.play(number, 1, 1, 0, 0, 1);
    }

}
