package com.example.pangd.linkgame;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.pangd.linkgame.FragmentSet.RankAdapter;
import com.example.pangd.linkgame.FragmentSet.RankItem;

import java.util.ArrayList;
import java.util.List;


public class Rank_F extends Fragment {
    private int degreeDifficult;
    private Database_RANK dbHelper;
    private int numberBlock;
    private List<RankItem> blockList = new ArrayList<>();

    public void setDegreeDifficult(int degreeDifficult) {
        this.degreeDifficult = degreeDifficult;
    }

    public void setNumberBlock(int numberBlock) {
        this.numberBlock = numberBlock;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        dbHelper = new Database_RANK(getActivity().getApplicationContext(), "RankInfo.db", null, 2);
        View view = inflater.inflate(R.layout.fragment_rank, container, false);

        Button button = (Button) view.findViewById(R.id.return_to_main);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment();
            }
        });
        getRankInfo();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycle_rank_view);
        StaggeredGridLayoutManager layoutManager = new
                StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);
        RankAdapter adapter = new RankAdapter(blockList);
        recyclerView.setAdapter(adapter);
        return view;
    }

    private void getRankInfo(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("RANK", null, "degreeDifficult=? and numberBlock=?",
                new String[]{Integer.toString(degreeDifficult), Integer.toString(numberBlock)},
                null, null, "costTime asc");

        int rank_number_auto = 1;
        if(cursor.moveToFirst()){
            do{
                String rank_number = Integer.toString(rank_number_auto);
                String cost_time = Double.toString(cursor.getDouble(cursor.getColumnIndex("costTime")));
                String player_name = cursor.getString(cursor.getColumnIndex("playerName"));
                String input_time = cursor.getString(cursor.getColumnIndex("inputTime"));
                rank_number_auto += 1;
                RankItem rankItem = new RankItem(rank_number, cost_time, player_name, input_time);
                blockList.add(rankItem);
            }while (cursor.moveToNext() && rank_number_auto<=40);
        }
    }

    private void replaceFragment(){
        MainActivity.setBar_Action(true);  // 让bar按键可响应
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, MainActivity.getNow_Fragmet());
        transaction.commit();
    }

}
