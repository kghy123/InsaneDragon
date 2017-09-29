package com.kghy1234gmail.insanedragon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    MediaPlayer mp;

    RelativeLayout layout;
    Random rnd = new Random();
    ImageView img_start_main;
    ImageView img_exit_main;

    boolean isBackWait = false;
    boolean isBackRun = true;

    MainBackgroundView backgroundView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        layout = (RelativeLayout)findViewById(R.id.layout_relative_main);
//        layout.setBackgroundResource(R.drawable.back_01 + rnd.nextInt(6));

        img_start_main = (ImageView)findViewById(R.id.img_start_main);
        img_exit_main = (ImageView)findViewById(R.id.img_exit_main);

        loadData();

        mp = MediaPlayer.create(this, R.raw.dragon_flight);
        mp.setLooping(true);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        if(mp.isPlaying() && mp != null) mp.pause();
        super.onStop();
    }

    @Override
    protected void onResume() {

        if(mp!=null && !mp.isPlaying()) {
            if(G.isMusic)mp.setVolume(0.5f, 0.5f);
            else mp.setVolume(0.0f, 0.0f);
            mp.start();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {

        if(mp != null){
            mp.stop();
            mp.release();
            mp = null;
        }
        super.onDestroy();
    }

    public void loadData(){

        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        G.gem = pref.getInt("gem", 0);
        G.champion = pref.getInt("champion", 0);
        G.kind = pref.getInt("kind", 0);

        G.isMusic = pref.getBoolean("isMusic", true);
        G.isSound = pref.getBoolean("isSound", true);
        G.isVibrate = pref.getBoolean("isVibrate", true);

        G.imgUri = pref.getString("imgUri", null);
    }

    public void clickStart(View v){

        Intent intent = new Intent(this, InGameActivity.class);
        startActivity(intent);

        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);


    }

    public void clickExit(View v){

        finish();
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

        }
    };

}
