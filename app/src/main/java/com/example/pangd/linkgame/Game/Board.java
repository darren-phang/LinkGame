package com.example.pangd.linkgame.Game;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.pangd.linkgame.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

public class Board {
    private static final String TAG = "Board";
    private _Location end;
    private int[][] TypeBlock;  //连连看当前信息
    private Stack<_Location> location_link = new Stack<_Location>(); ;

    static Queue<_Location> location_queue = new LinkedList<_Location>();  //寻径时的队列

    static public int nullType = 1314;   //界面中代表已被消除或着无效的点

    private NewGame Game;  //游戏参数类

    private int clickNumber = 0;  //有效点击次数

    private int position_temp;  //第一次点击位置的缓存区

    private int allBlock;  //所有的带消除的方块

    private int Type_temp;  //第一次点击的类别的缓存区

    private int max_turn;  //最大允许转几次弯

    private int[] Location = new int[4];  //两次有效的点击的位置信息

    public Board() {
    }

    public void StartGame(NewGame newGame) {
        Game = newGame;
        TypeBlock = Game.getGameInfo();
        max_turn = Game.getTurn();
        clickNumber = 0;
        allBlock = (Game.getRow() - 2) * (Game.getCol() - 2);
    }

    public int[] NextStep(int position) {
        // return[0] = 0 无效, return[1] return[2], 选择的第一二个点
        //
        if (TypeBlock[position / Game.getCol()][position % Game.getCol()] == Board.nullType) {
            return new int[]{0};  // 点击无效的区域
        }
        clickNumber++;
        if (clickNumber % 2 != 0) {  //点击次数不为2的倍数，返回点击位置
            position_temp = position;
            Location[0] = position / Game.getCol();
            Location[1] = position % Game.getCol();
            Type_temp = TypeBlock[position / Game.getCol()][position % Game.getCol()];
            return new int[]{1, position, R.drawable.selected};
        } else { //点击次数为2的倍数，可达性判断
            Location[2] = position / Game.getCol();
            Location[3] = position % Game.getCol();
            if (position_temp != position
                    && Type_temp == TypeBlock[position / Game.getCol()][position % Game.getCol()]
                    && isArrive()) {
                TypeBlock[Location[0]][Location[1]] = Board.nullType;
                TypeBlock[Location[2]][Location[3]] = Board.nullType;
                allBlock -= 2;
                Log.d(TAG, "onClick: succeed: " + allBlock);
                if (allBlock == 0) {
                    Log.d(TAG, "onClick: end the game: " + allBlock);
                    return new int[]{1, position, position_temp, 1, 1};
                }
                return new int[]{1, position, position_temp, 1};
            }
            return new int[]{1, position_temp, R.drawable.null_image};
        }
    }

    public int[][] getTypeBlock() {
        return TypeBlock;
    }

    public void disorganize() {
        Random random = new Random();
        for (int i = 1; i < Game.getRow() - 1; i++) {
            for (int j = 1; j < Game.getCol() - 1; j++) {
                int new_row = random.nextInt(Game.getRow() - 3) + 1;
                int new_col = random.nextInt(Game.getCol() - 3) + 1;
                int type_temp = TypeBlock[new_row][new_col];
                TypeBlock[new_row][new_col] = TypeBlock[i][j];
                TypeBlock[i][j] = type_temp;
            }
        }
    }

    private int X_YToPosition(int x, int y) {
        return x * Game.getCol() + y;
    }

    @NonNull
    private int[] PositionToX_Y(int position) {
        return new int[]{position / Game.getCol(), position % Game.getCol()};
    }

    public List<Integer> getLinkRoad(){
        List<Integer> link = new ArrayList<>();
        // not_go 上下左右 1,2,3,4
        int[] offset = new int[]{-Game.getCol(), Game.getCol(), -1, 1}; //要调整位置信息
        link.add(X_YToPosition(Location[2], Location[3]));
        Log.d(TAG, "getLinkRoad: location:"+Location[2] + " " + Location[3]);
        int location_Xnow = Location[2];
        int location_Ynow = Location[3];
        _Location temp = location_link.pop();
        int not_go = temp.getNot_go();
        while ((location_Xnow != Location[0] || location_Ynow != Location[1])){
            int position = X_YToPosition(location_Xnow, location_Ynow);
            int[] location_now = PositionToX_Y(position + offset[not_go-1]);
            do {
//                Log.d(TAG, "getLinkRoad: len:"+location_link.size());
                temp = location_link.pop();
            }while ((temp.getX()!=location_now[0] || temp.getY()!=location_now[1]) && !location_link.empty());
            location_Xnow = temp.getX();
            location_Ynow = temp.getY();
//            Log.d(TAG, "getLinkRoad: location: " + location_Xnow + " " + location_Ynow);
//            Log.d(TAG, "getLinkRoad: not go: " + not_go + " " + temp.getNot_go());
            if (not_go != temp.getNot_go()){
                link.add(X_YToPosition(location_Xnow, location_Ynow));
                Log.d(TAG, "getLinkRoad: location:"+location_Xnow + " " + location_Ynow);
            }
            not_go = temp.getNot_go();
        }
//        link.add(X_YToPosition(Location[0], Location[1]));
        Log.d(TAG, "getLinkRoad: len: " + link.size());
        return link;
    }

    private void _isArrive(int x, int y, int not_go, int have_turn) {
        // not_go 上下左右 1,2,3,4
        // not_go 就是上次从那个方向来的
        int[] offset = new int[]{-Game.getCol(), Game.getCol(), -1, 1}; //要调整位置信息
        int[] go = new int[]{2, 1, 4, 3};
        int position = X_YToPosition(x, y);
        for (int i = 0; i < 4; i++) {
            int positionNow = position + offset[i]; //加了offset后就为上下左右的位置
            int[] X_y = PositionToX_Y(positionNow);
            boolean InRange = X_y[0] >= 0 && X_y[0] < Game.getRow()
                    && X_y[1] >= 0 && X_y[1] < Game.getCol(); //是否在棋盘范围内
            // 按照上下左右搜索, 所以not_go != (i+1)，即每次只搜索3个方向，不搜索来的方向
            if (InRange && (TypeBlock[X_y[0]][X_y[1]] == nullType && not_go != (i + 1))
                    || (X_y[0] == Location[2] && X_y[1] == Location[3])) {
                //在范围类且这个位置在空白且不为上次的方向，或者这个位置就是目标点
                if (i >= 2 && X_y[0] != x)//如果在搜索左右方向时跳行了就不执行下面
                    continue;
                int offset_turn = 0;  //是否转弯的补充值
                if (not_go != go[i])  //不是来的方向就是补充1
                    offset_turn = 1;
                if (have_turn + offset_turn <= max_turn)//转弯数小于最大转弯数，加入队列等待搜索
                    location_queue.add(new _Location(X_y[0], X_y[1], go[i], have_turn + offset_turn));
            }
        }
    }

    //两点之间是否可达
    private boolean isArrive() {
        // direction 下: -1 上: 1 右: -1 左:1
        location_queue.clear(); //清空队列
        int now_x = Location[0];//起始点x坐标
        int now_y = Location[1];//起始点y坐标
        int not_go = 0;//初始化not_go参数，此参数为记录这个点是为了不让搜索原路返回
        int have_turn = -1;//初始化已经转弯的次数
        while (now_x != Location[2] || now_y != Location[3]) { //如果现在的位置不是目标位置就继续搜索
            _isArrive(now_x, now_y, not_go, have_turn); //判断现在
            Log.d(TAG, "isArrive: size" + location_queue.size());
            location_link.push(new _Location(now_x, now_y, not_go, 0));
            if (location_queue.isEmpty()) { //如果队列空了，折代表不可达
                Log.d(TAG, "isArrive: false");
                return false;
            }
            Log.d(TAG, "isArrive: location" + now_x + " " + now_y + " " + have_turn);
            _Location position = location_queue.poll(); //弹出队首元素，继续搜索
            Log.d(TAG, "isArrive: len_location: " + location_link.size());
            now_x = position.getX();
            now_y = position.getY();
            not_go = position.getNot_go();
            have_turn = position.getHave_turn();
        }
        location_link.push(new _Location(now_x, now_y, not_go, have_turn));
        Log.d(TAG, "isArrive: len: " + location_link.size());
        return true;
    }


}

class _Location {
    private int x;
    private int y;
    private int not_go;
    private int have_turn;

    // not_go 1:上 2:下 3:左 4:右
    _Location(int x, int y, int not_go, int have_turn) {
        this.x = x;
        this.y = y;
        this.not_go = not_go;
        this.have_turn = have_turn;
    }

    public int getX() {
        return x;
    }

    int getNot_go() {
        return not_go;
    }

    int getHave_turn() {
        return have_turn;
    }

    public int getY() {
        return y;
    }
}
