package com.example.pangd.linkgame.Game;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NewGame {
    private static final String TAG = "NewGame";
    private int row;

    private int col;

    private List<Integer> nullLocation = new ArrayList<>();

    private int all_Number;

    private int TypeNumber;

    private int[][] GameInfo;

    private int turn;

    public NewGame(int row, int col, int typeNumber, int turn){
        this.row = row;
        this.col = col;
        this.turn = turn;
        this.TypeNumber = typeNumber;
        all_Number = row * col;
        GameInfo = new int[row][col];
        initLocation();
    }

    public int getTurn() {
        return turn;
    }

    private void initLocation() {
        for (int i = 1; i < row - 1; i++) {
            for (int j = 1; j < col - 1; j++) {
                nullLocation.add(i * col + j);
            }
        }
        for (int i = 0; i < (row - 2) * (col - 2) / 2; i++) {
            int[] InfoRandom = getLocationAndType_Random();
            GameInfo[InfoRandom[1]][InfoRandom[2]] = InfoRandom[0];
            GameInfo[InfoRandom[3]][InfoRandom[4]] = InfoRandom[0];
        }
        for (int i = 0; i < col; i++) {
            GameInfo[0][i] = Board.nullType;
            GameInfo[row - 1][i] = Board.nullType;
        }
        for (int i = 0; i < row; i++) {
            GameInfo[i][0] = Board.nullType;
            GameInfo[i][col - 1] = Board.nullType;
        }
    }

    private int[] getLocationAndType_Random() {
        // 每次随机两个模块，随机位置和类别
        int LocationAndType[] = new int[5];
        Random random = new Random();
//        random.setSeed(1314);

        int Type = random.nextInt(TypeNumber);
        Log.d(TAG, "getLocationAndType_Random: type: " + Type);
        LocationAndType[0] = Type;
        for (int i = 0; i < 2; i++) {
            int Temp[] = getLocation_Random();
            LocationAndType[1 + i * 2] = Temp[0];
            LocationAndType[2 + i * 2] = Temp[1];
        }

        return LocationAndType;
    }

    private int[] getLocation_Random() {
        //获得随机的位置，从剩余的位置里面选
        int rangeRandomNow = nullLocation.size();
        int random_nullLocation = (int) (Math.random() * rangeRandomNow);
        int Location = nullLocation.get(random_nullLocation);
        Log.d(TAG, "getLocation_Random: Location: " + Location + " " + nullLocation.indexOf(Location));
        nullLocation.remove(nullLocation.indexOf(Location));
        return new int[]{Location / col, Location % col};
    }

    public int getAll_Number() {
        return all_Number;
    }

    public int getTypeNumber() {
        return TypeNumber;
    }

    public int[][] getGameInfo() {
        return GameInfo;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }
}
