package com.example.picturerecognition;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;


public class    MainActivity extends AppCompatActivity {
    // UI Obj
    private TextView txtHistory;
    private TextView txtSearch;
    private TextView txtUser;
    private FrameLayout fragmentFrame;
    public boolean whetherLogin = false;
    //Fragment Obj
    private LoginPage loginFragment;
    private FragmentManager fragManager;
    private UserPage userFragment;
    private HistoryPage historyFragment;
    public LoginViewModel loginViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initLoginStatus();
        requestsPerssion();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        loginViewModel = new ViewModelProvider(MainActivity.this).get(LoginViewModel.class);
        whetherLogin = loginViewModel.isLoginStatus();
        builder.detectFileUriExposure();
        fragManager = getSupportFragmentManager();
        bindViews();
        initSelected();
        txtUser.performClick();
    }

    public void requestsPerssion(){
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }


    public void initLoginStatus(){
        whetherLogin = false;
    }

    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            if( msg.what == 1){
                whetherLogin = true;
                txtUser.performClick();
            }
            else if( msg.what == 2 ){
                whetherLogin = false;
                Toast toast = Toast.makeText(MainActivity.this,"登录失败，请检查！",Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    };

    public void onLoginButtonClicked(View view) {
        EditText userNameGot = (EditText)findViewById(R.id.usertxt);
        EditText passwdGot = (EditText)findViewById(R.id.passwdtxt);
        String userNameStr = userNameGot.getText().toString();
        String passwdStr = passwdGot.getText().toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                loginViewModel.setUsername(userNameStr);
                loginViewModel.setPasswd(passwdStr);
                loginViewModel.loginAction();
                if(loginViewModel.isLoginStatus()){
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                }
                else{
                    Message msg = new Message();
                    msg.what = 2;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }

    private void bindViews(){
        txtHistory = (TextView) findViewById(R.id.txt_history);
        txtSearch = (TextView) findViewById(R.id.txt_search);
        txtUser = (TextView) findViewById(R.id.txt_user);
        fragmentFrame = (FrameLayout) findViewById(R.id.frag_frame);
    }

    private void initSelected(){
        txtHistory.setSelected(false);
        txtUser.setSelected(false);
        txtSearch.setSelected(false);
    }

    private void hideAllFragment(FragmentTransaction fragmentTransaction){
        if(loginFragment != null)
            fragmentTransaction.hide(loginFragment);
        if(userFragment != null)
            fragmentTransaction.hide(userFragment);
        if(historyFragment != null)
            fragmentTransaction.hide(historyFragment);

    }

    @SuppressLint("NonConstantResourceId")
    public void changeTabStatus(View v){
        Log.d("=========================>","cilck something");
        FragmentTransaction transaction = fragManager.beginTransaction();
        hideAllFragment(transaction);
        switch(v.getId()){
            case R.id.txt_history:
                {
                    initSelected();
                    txtHistory.setSelected(true);
                    if(!whetherLogin) {
                        if (historyFragment == null) {
                            historyFragment = new HistoryPage(null);
                            transaction.add(R.id.frag_frame,historyFragment);
                        } else {
                            transaction.show(historyFragment);
                        }
                    }
                    else{
                        if(historyFragment == null){
                            historyFragment = new HistoryPage(loginViewModel.getUsername());
                            transaction.add(R.id.frag_frame,historyFragment);
                        }else {
                            transaction.remove(historyFragment);
                            historyFragment = new HistoryPage(loginViewModel.getUsername());
                            transaction.add(R.id.frag_frame,historyFragment);
                        }
                    }
                }
                break;
            case R.id.txt_user:
                {
                    Log.d("=====>>>>","userPage");
                    initSelected();
                    txtUser.setSelected(true);
                    if(!whetherLogin) {
                        if (loginFragment == null) {
                            loginFragment = new LoginPage();
                            transaction.add(R.id.frag_frame,loginFragment);
                        } else {
                            transaction.show(loginFragment);
                        }
                    }
                    else{
                        if(userFragment == null) {
                            userFragment = new UserPage(loginViewModel.getUsername(),loginViewModel.getSolvingNumber());
                            transaction.add(R.id.frag_frame,userFragment);
                        }
                        else{
                            transaction.show(userFragment);
                        }
                    }
                }
                break;
            case R.id.txt_search:
                {
                    initSelected();
                    txtSearch.setSelected(true);
                }
                break;
        }
        transaction.commit();
    }

    public void onLogoutButtonClicked(View view){
        loginViewModel.setPasswd(null);
        loginViewModel.setUsername(null);
        loginViewModel.setLoginStatus(false);
        loginViewModel.setSolvingNumber(0);
        whetherLogin = false;
        txtUser.performClick();
    }

    public void onGetPhotoClicked(View view) {
        Log.d("-======>>>","takePhoto");
        if (whetherLogin) {
            Intent intent = new Intent(MainActivity.this, GetPhotoActivity.class);
            if (loginViewModel.getUsername() != null) {
                Bundle bundleToP = new Bundle();
                bundleToP.putString("userName", loginViewModel.getUsername());
                intent.putExtra("bun", bundleToP);
                startActivity(intent);
            }
            else{
                Toast toast = Toast.makeText(this,"找不到用户名！",Toast.LENGTH_LONG);
                toast.show();
            }
        }
        else{
            Toast toast = Toast.makeText(this,"烦请登录先！！！",Toast.LENGTH_LONG);
            toast.show();
        }
    }

}