package com.example.picturerecognition;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.zip.Inflater;

public class PlantMessage extends Fragment {

    public String introducePlant, scoreOfPlant;
    public Bitmap imgBit;

    public PlantMessage(Bitmap imgBit,String introducePlant,String scoreOfPlant) {
        // Required empty public constructor
        this.imgBit = imgBit;
        this.introducePlant = introducePlant;
        this.scoreOfPlant = scoreOfPlant;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_plant_message, container, false);
        TextView scoreToShow = root.findViewById(R.id.score);
        scoreToShow.setText("置信度： " + scoreOfPlant);
        TextView infoToShow = root.findViewById(R.id.introduce);
        infoToShow.setText(introducePlant);
        ImageView imgToShow = root.findViewById(R.id.showInternetPic);
        imgToShow.setImageBitmap(imgBit);
        return root;
    }
}