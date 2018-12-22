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
    private NewGame game;  //定义关于游戏参数设置和生成游戏的类
    private Board board = new Board(); //定义一个关于连连看操作的类
    private List<Item> blockList = new ArrayList<>();//定义适配器的list
    private int[] shape; //连连看规模
    private BlockAdapter adapter; //适配器
    public int degree = 25;//连连看有多少种动物
    public int mode = 3;//哪种棋盘大小
    public int flush = 3; //剩余刷新次数
    final private int allTime = 1200;//全部的时间
    private int allTime_Temp; //剩余时间
    static private Timer timer;//定时器
    public Button fab; //刷新按钮
    boolean IsStart = false;//是否已经开始游戏
    RecyclerView recyclerView; //recyclerview界面
    StaggeredGridLayoutManager layoutManager;//界面管理器
    ProgressBar progressBar; //进度条
    Button startButton;//开始按钮
    SoundPoolUtil sound;//管理声音的类
    private static final String TAG = "Game_F";
    int click_music = 1;//是否有点击音效

    public int getClick_music() {
        return click_music;
    }

    public void setClick_music(int click_music){
        this.click_music = click_music;
    }

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
                    sound.play(sound.START, click_music);
                    IsStart = true;
                    startButton.setVisibility(View.INVISIBLE);
                }
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flush > 0 && IsStart) {
                    sound.play(sound.FLUSH, click_music);
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
                        sound.play(sound.LOSS, click_music);
                        DialogUIUtils.showMdAlert(getActivity(), "Loss",
                                "在来一局", new DialogUIListener() {
                                    @Override
                                    public void onPositive() {
                                    }

                                    @Override
                                    public void onNegative() {
                                    }
                                }).show();
                        initView();
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
                                sound.play(sound.REPEAT, click_music);
                            }//点重
                            adapter.changeItem(_info[1], -1, _info[2]);
                        } else {
                            adapter.changeItem(_info[1], R.drawable.null_image, -1);
                            adapter.changeItem(_info[2], R.drawable.null_image, R.drawable.null_image);
                            sound.play(sound.LINK, click_music);    //消除
                            if (_info.length == 5) {
                                sound.play(sound.WIN, click_music);   //获胜
                                stopTimer();
                                sendRankBroadcast((double)(allTime-allTime_Temp)/10);
                            }
                        }
                    }
                    else {
                        sound.play(sound.WRONG, click_music);   //点错
                    }
                }
            }

        });
        recyclerView.setAdapter(adapter);
        startButton.setVisibility(View.VISIBLE);
        IsStart = false;
    }

    private void sendRankBroadcast(double costTime){
        // 名字、耗时、上榜时间
        Log.d(TAG, "sendRankBroadcast: send broadcast succeefully");
        Intent intent = new Intent("com.example.broadcast.RANK_INFOMATION");
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
    public int START=1;  //开始音效
    public int WIN=2;    //获胜音效
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
                    .setMaxStreams(10)   //可同时播放声音数量
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

    public void play(int number, int Isopen) {
        //播放音频
        if (Isopen == 1){
            soundPool.play(number, 1, 1, 0, 0, 1);
            //左右声道相同，优先级最低，不循环播放，速率正常
        }
    }

}
