package com.example.picturerecognition;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Handler;

public class LoginViewModel extends AndroidViewModel {
    public String username = null;
    public String passwd = null;
    public boolean loginStatus = false;
    public int solvingNumber = 0;

    public LoginViewModel(@NonNull Application application) {
        super(application);

    }

    public void loginAction(){
        try {
            if (username != null && passwd != null && !loginStatus) {
                String urlStr = "https://southstem.cloud/result";
                //建立连接
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                //设置连接参数
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                //设置请求
                String dataSend = "passwd="+ URLEncoder.encode(passwd,"UTF-8") + "&username=" + URLEncoder.encode(username,"UTF-8");
                OutputStream out = conn.getOutputStream();
                out.write(dataSend.getBytes());
                out.flush();
                if(conn.getResponseCode() == 200){
                    InputStream in = conn.getInputStream();
                    byte[] dataGot = StreamTool.read(in);
                    String whatWeGet = new String(dataGot,"UTF-8");
                    if(!whatWeGet.equals("login_false")){
                        loginStatus = true;
                        solvingNumber = Integer.parseInt(whatWeGet);
                    }
                    else{
                        loginStatus = false;
                    }
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap getImage(String urlImage){
        Bitmap bmp = null;
        try {
            URL myurl = new URL(urlImage);
            // 获得连接
            HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
            conn.setConnectTimeout(6000);//设置超时
            conn.setDoInput(true);
            conn.setUseCaches(false);//不缓存
            conn.connect();
            InputStream is = conn.getInputStream();//获得图片的数据流
            bmp = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }


    public void setUsername(String usrnameGot){
        this.username = usrnameGot;
    }

    public void setPasswd(String passwdGot){
        this.passwd = passwdGot;
    }

    public String getPasswd() {
        return passwd;
    }

    public String getUsername(){
        return username;
    }

    public boolean isLoginStatus() {
        return loginStatus;
    }

    public void setSolvingNumber(int solvingNumber) {
        this.solvingNumber = solvingNumber;
    }

    public int getSolvingNumber() {
        return solvingNumber;
    }

    public void setLoginStatus(boolean loginStatus) {
        this.loginStatus = loginStatus;
    }
}
