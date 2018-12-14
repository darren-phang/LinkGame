package com.example.pangd.linkgame.FragmentSet;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.pangd.linkgame.R;

import java.util.List;

public class BlockAdapter extends RecyclerView.Adapter<BlockAdapter.ViewHolder> {

    private static final String TAG = "BlockAdapter";

    private List<Item> mBlockList;

    private OnItemClickListener mOnItemClickListener;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View blockView;
        ImageView Block_image;
        ImageView Block_background;
        public ViewHolder(View view) {
            super(view);
            blockView = view;
            Block_image = (ImageView) view.findViewById(R.id.block_image);
            Block_background = (ImageView) view.findViewById(R.id.block_background);
        }
    }

    public BlockAdapter(List<Item> blockList) {
        mBlockList = blockList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.block_item, viewGroup, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.Block_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                mOnItemClickListener.onItemClick(v, position);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        Item block = mBlockList.get(i);
        holder.Block_image.setImageResource(block.getImageId());
        holder.Block_background.setImageResource(block.getBackgroundId());

    }

    @Override
    public int getItemCount() {
        return mBlockList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void changeItem(int position, int ImageID, int BackgroundID){
        Item block = mBlockList.get(position);
        if (ImageID == -1)
            ImageID = block.getImageId();
        if (BackgroundID == -1)
            BackgroundID = block.getBackgroundId();
        block.setImageId(ImageID);
        block.setBackgroundId(BackgroundID);
        notifyItemChanged(position);
    }
}
