package com.example.picturerecognition;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class GetPhotoActivity extends AppCompatActivity {
    public String capturePath;
    public String resultName;
    public PlantMessage plantMessage;
    public FragmentManager fragmentManager;
    public String resultOfPlant;
    public String score,info,imgUrl;
    public Bitmap bitMapRet;
    public String userName;

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d("================>>>>>>>>","WHY????????????????????//");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_photo);
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bun");
        userName = bundle.getString("userName");
        fragmentManager = getSupportFragmentManager();
    }

    private void takePhoto(){
        String state = Environment.getExternalStorageState();
        capturePath = Environment.getExternalStorageDirectory().getPath() + "/" + Calendar.getInstance().getTimeInMillis() + ".png";
        if(state.equals(Environment.MEDIA_MOUNTED)){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri photoUri = Uri.fromFile(new File(capturePath));
            intent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
            startActivityForResult(intent,123);
        }
    }

    private void getPhotoFromDir(){
        Intent localIntent = new Intent(Intent.ACTION_PICK,null);
        localIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");

        startActivityForResult(localIntent,111);
    }

    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1){
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                plantMessage = new PlantMessage(bitMapRet,info,score);
                transaction.add(R.id.frag_show,plantMessage);
                transaction.commit();
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 123 && resultCode == RESULT_OK){
            showPhoto(capturePath);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    showSearchResult();
                }
            }).start();
        }
        else if(requestCode == 111 && resultCode == RESULT_OK){
            Cursor cursor=this.getContentResolver().query(data.getData(),null,null,null,null);
            cursor.moveToFirst();
            String imagePath=cursor.getString(cursor.getColumnIndex("_data"));
            capturePath = imagePath;
            cursor.close();
            showPhoto(imagePath);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    showSearchResult();
                }
            }).start();
        }
    }

    public void showPhoto(String str){
        TextView txtView = findViewById(R.id.textHHH);
        txtView.setVisibility(View.INVISIBLE);
        Bitmap bitmap = BitmapFactory.decodeFile(str);
        ImageView imgView = (ImageView)findViewById(R.id.showItHere);
        imgView.setImageBitmap(bitmap);
    }

    public void showSearchResult(){
        RecognizeTool recognizeTool = new RecognizeTool(capturePath);
        String whatWeGot = recognizeTool.recognizePlant();
        resultOfPlant = whatWeGot;
        score = recognizeTool.getScore();
        info = recognizeTool.getInfo()[0];
        imgUrl = recognizeTool.getInfo()[1];
        resultName = recognizeTool.getInfo()[2];
        recognizeTool.addNewReco(capturePath,"lxy",resultName);
        try {
            URL imgUrlToShow = new URL(imgUrl);
            bitMapRet = Glide.with(this).asBitmap().load(imgUrl).submit(500,500).get();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Message msg = new Message();
        msg.what = 1;
        handler.sendMessage(msg);
    }



    public void onTakePhotoClicked(View view){
        if(plantMessage != null){
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.remove(plantMessage);
            transaction.commit();
        }
        takePhoto();
    }

    public void onGetPhotoClicked(View view){
        if(plantMessage != null){
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.remove(plantMessage);
            transaction.commit();
        }
        getPhotoFromDir();
    }


}