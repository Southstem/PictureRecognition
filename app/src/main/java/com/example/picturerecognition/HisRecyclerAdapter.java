package com.example.picturerecognition;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class HisRecyclerAdapter extends RecyclerView.Adapter<HisRecyclerAdapter.ViewHolder> {
    public HistoryItem[] I;

    public HisRecyclerAdapter(HistoryItem[] it){
        I = new HistoryItem[100];
        if(it != null){
            for(int i = 0; it[i] != null; i++){
                I[i] = it[i];
            }
        }
    }


    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @org.jetbrains.annotations.NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @org.jetbrains.annotations.NotNull HisRecyclerAdapter.ViewHolder holder, int position) {
        holder.getTextView(1).setText(I[position].timeHis);
        holder.getTextView(2).setText("鉴定结果：\n【"+I[position].infoHis + "】");
        Bitmap bitmap;
        ImageView imgView = holder.getImageView();
        try{
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(I[position].imgUrlHis));
            bitmap = BitmapFactory.decodeStream(bis);
            bis.close();
            imgView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        int i = 0;
        if(I != null) {
            for (; i <= I.length; i++) {
                if (I[i] == null)
                    break;
            }
        }
        return i;
    }

    public void newItem(HistoryItem itemNew){
        int i = getItemCount();
        Log.e("=====================>",String.valueOf(i));
        for( ; i > 0 ; i--){
            I[i] = I[i-1];
        }
        I[0] = itemNew;

        notifyItemInserted(0);
    }

    public void removeAllData(){
        I = new HistoryItem[100];
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tv_time;
        public TextView tv_info;
        public ImageView tv_img;
        public ViewHolder(View itemView) {
            super(itemView);
            tv_img = (ImageView)itemView.findViewById(R.id.imageView2);
            tv_info = (TextView) itemView.findViewById(R.id.searchInfo);
            tv_time = (TextView) itemView.findViewById(R.id.searchTime);
        }

        public TextView getTextView(int i){
            switch (i){
                case 1:return tv_time;
                case 2:return tv_info;
            }
            TextView tv = (TextView) itemView.findViewById(R.id.searchInfo);
            tv.setText("error");
            return tv;
        }

        public ImageView getImageView(){
            return tv_img;
        }

    }

}
