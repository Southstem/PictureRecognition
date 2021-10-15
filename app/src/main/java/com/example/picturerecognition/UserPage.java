package com.example.picturerecognition;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class UserPage extends Fragment {
    private String userName;
    private int solvedNumb;


    public UserPage(String userName, int solvedNumb) {
        // Required empty public constructor
        this.solvedNumb = solvedNumb;
        this.userName = userName;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_user_page, container, false);
        // Inflate the layout for this fragment
        TextView userNameView = (TextView)root.findViewById(R.id.userNameTxt);
        userNameView.setText("您好，用户  【" + userName + "】\n\n截至目前您已成功识别 " + String.valueOf(solvedNumb) + " 种物品！");
        return root;
    }
}