package com.example.picturerecognition;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HistoryPage extends Fragment {
    public String userName;
    public HistoryItem[] hisItems;
    public RecyclerView RE;
    public LinearLayoutManager manager;
    public HisRecyclerAdapter adapter;

    public HistoryPage(String userNameGet) {
        // Required empty public constructor
        if(null != userNameGet){
            userName = userNameGet;
            hisItems = new HistoryItem[100];
        }
        else{
            userName = null;
            hisItems = null;
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1){
                manager = new LinearLayoutManager(getContext());
                manager.setOrientation(RecyclerView.VERTICAL);
                RE.setLayoutManager(manager);
                adapter = new HisRecyclerAdapter(hisItems);
                RE.setAdapter(adapter);
                Log.d("=========================>>>>>>>>>>>>>>>>>.","add items ");
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("=====================>>","created");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("=====================>>","resumed");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("=====================>>","paused");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("=====================>>","stopped");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_history_page, container, false);
        TextView notLoginTxt = root.findViewById(R.id.textView3);
        if(userName == null || hisItems == null){
            notLoginTxt.setVisibility(View.VISIBLE);
        }
        else{
            notLoginTxt.setVisibility(View.INVISIBLE);
            RE = root.findViewById(R.id.recyclerHistory);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    BufferedReader bufferedReader;
                    int k = 0;
                    StringBuilder jsonDataStr = new StringBuilder();
                    try{
                        URL reqUrl = new URL("https://southstem.cloud/getRecognizeData?user=" + userName);
                        HttpURLConnection connection = (HttpURLConnection) reqUrl.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(5000);
                        connection.setReadTimeout(8000);
                        if(connection.getResponseCode() == 200){
                            InputStream in = connection.getInputStream();
                            bufferedReader = new BufferedReader(new InputStreamReader(in));
                            String line;
                            while((line = bufferedReader.readLine()) != null){
                                jsonDataStr.append(line);
                            }
                        }
                        JSONArray jsonArray = new JSONArray(jsonDataStr.toString());
                        for(int i = 0;i < jsonArray.length(); i++){
                            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                            HistoryItem newItem = new HistoryItem(jsonObject.getString("info"),jsonObject.getString("imgUrl"),jsonObject.getString("time"));
                            hisItems[k] = newItem;
                            k++;
                        }
                        connection.disconnect();
                        Message msg = new Message();
                        msg.what = 1;
                        handler.sendMessage(msg);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("***********************","exit internet");
                }
            }).start();
        }
        return root;
    }
}