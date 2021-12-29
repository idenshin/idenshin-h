package com.zz.danmakuall;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangzhao
 * @date 2021/7/14 11:33 上午
 * @describes
 */
public class EmotionAdapter extends RecyclerView.Adapter<EmotionAdapter.EmotionViewHolder> {

    private List<Integer> imgList = new ArrayList<>(20);

    @NonNull
    @Override
    public EmotionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_emotion, parent, false);
        return new EmotionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmotionAdapter.EmotionViewHolder holder, int position) {
        holder.img.setImageResource(imgList.get(position));
    }

    @Override
    public int getItemCount() {
        return imgList.size();
    }

    public void setImgList(List<Integer> imgList) {
        this.imgList = imgList;
        notifyDataSetChanged();
    }

    class EmotionViewHolder extends RecyclerView.ViewHolder {

        private ImageView img;

        public EmotionViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.item_emotion_iv);
        }
    }
}
