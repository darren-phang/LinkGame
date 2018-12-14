package com.example.pangd.linkgame.FragmentSet;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pangd.linkgame.R;

import java.util.List;

public class RankAdapter extends RecyclerView.Adapter<RankAdapter.ViewHolder> {

    private static final String TAG = "RankAdapter";

    private List<RankItem> mRankList;

    private RankAdapter.OnItemClickListener mOnItemClickListener;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View blockView;

        TextView rank_number;
        TextView player_name;
        TextView cost_name;
        TextView input_time;
        public ViewHolder(View view) {
            super(view);
            blockView = view;
            rank_number = (TextView) view.findViewById(R.id.rank_number);
            player_name = (TextView) view.findViewById(R.id.player_name);
            cost_name = (TextView) view.findViewById(R.id.cost_time);
            input_time = (TextView) view.findViewById(R.id.input_time);
        }
    }

    public RankAdapter(List<RankItem> blockList) {
        mRankList = blockList;
    }

    @NonNull
    @Override
    public RankAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.rank_item, viewGroup, false);
        final RankAdapter.ViewHolder holder = new RankAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RankAdapter.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public void onBindViewHolder(@NonNull RankAdapter.ViewHolder holder, int i) {
        RankItem block = mRankList.get(i);
        holder.rank_number.setText(block.getRank_number());
        holder.player_name.setText(block.getPlayer_name());
        holder.cost_name.setText(block.getCost_time());
        holder.input_time.setText(block.getInput_time());

    }

    @Override
    public int getItemCount() {
        return mRankList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(RankAdapter.OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

//    public void changeItem(int position, int ImageID, int BackgroundID){
//        RankItem block = mRankList.get(position);
//        if (ImageID == -1)
//            ImageID = block.getImageId();
//        if (BackgroundID == -1)
//            BackgroundID = block.getBackgroundId();
//        block.setImageId(ImageID);
//        block.setBackgroundId(BackgroundID);
//        notifyItemChanged(position);
//    }
}
