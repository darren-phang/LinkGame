package com.example.pangd.linkgame.Game;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.pangd.linkgame.R;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Board {
    private static final String TAG = "Board";

    private int[][] TypeBlock;

    static Queue<_Location> location_queue = new LinkedList<_Location>();

    static public int nullType = 1314;

    private NewGame Game;

    private int clickNumber = 0;

    private int position_temp;

    private int allBlock;

    private int Type_temp;

    private int max_turn;

    private int[] Location = new int[4];

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
            return new int[]{0};
        }
        clickNumber++;
        if (clickNumber % 2 != 0) {
            position_temp = position;
            Location[0] = position / Game.getCol();
            Location[1] = position % Game.getCol();
            Type_temp = TypeBlock[position / Game.getCol()][position % Game.getCol()];
            return new int[]{1, position, R.drawable.selected};
        } else {
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

    private void _isArrive(int x, int y, int not_go, int have_turn) {
        // not_go 上下左右 1,2,3,4
        // not_go 就是上次从那个方向来的
        int[] offset = new int[]{-Game.getCol(), Game.getCol(), -1, 1};
        int[] go = new int[]{2, 1, 4, 3};
        int position = X_YToPosition(x, y);
        for (int i = 0; i < 4; i++) {
            int positionNow = position + offset[i];
            int[] X_y = PositionToX_Y(positionNow);
            boolean InRange = X_y[0] >= 0 && X_y[0] < Game.getRow()
                    && X_y[1] >= 0 && X_y[1] < Game.getCol();
            // 按照上下左右搜索, 说以not_go != (i+1)
            if (InRange && (TypeBlock[X_y[0]][X_y[1]] == nullType && not_go != (i + 1))
                    || (X_y[0] == Location[2] && X_y[1] == Location[3])) {
                if (i >= 2 && X_y[0] != x)
                    continue;
                int offset_turn = 0;
                if (not_go != go[i])
                    offset_turn = 1;
                if (have_turn + offset_turn <= max_turn)
                    location_queue.add(new _Location(X_y[0], X_y[1], go[i], have_turn + offset_turn));
            }
        }
    }

    private boolean isArrive() {
        // direction 下: -1 上: 1 右: -1 左:1
        location_queue.clear();
        int now_x = Location[0];
        int now_y = Location[1];
        int not_go = 0;
        int have_turn = -1;
        while (now_x != Location[2] || now_y != Location[3]) {
            _isArrive(now_x, now_y, not_go, have_turn);
            Log.d(TAG, "isArrive: size" + location_queue.size());
            if (location_queue.isEmpty()) {
                Log.d(TAG, "isArrive: false");
                return false;
            }
            Log.d(TAG, "isArrive: location" + now_x + " " + now_y + " " + have_turn);
            _Location position = location_queue.poll();
            now_x = position.getX();
            now_y = position.getY();
            not_go = position.getNot_go();
            have_turn = position.getHave_turn();
        }
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
