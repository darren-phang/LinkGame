package com.example.pangd.linkgame;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

    public static final String CREATE_RANK = "create table RANK (" +
            "id integer primary key autoincrement, " +
            "playerName text, " +
            "costTime double, " +
            "inputTime time," +
            "degreeDifficult integer," +
            "numberBlock integer)";

    private Context mcontext;

    public Database(Context context, String name,
                    SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
        mcontext = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_RANK);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }
}
