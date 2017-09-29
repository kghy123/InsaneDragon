package com.kghy1234gmail.insanedragon;

import android.content.Intent;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;

public class InGameActivity extends AppCompatActivity {

    MediaPlayer mp;

    GameView gameView;
    TextView tv_score;
    TextView tv_coin;
    TextView tv_gem;
    TextView tv_bomb;
    TextView tv_champion_score;

    View dialog = null;

    ToggleButton toggle_music, toggle_sound, toggle_vibrate;

    ImageView img_coin, img_gem, img_bomb, img_pause, img_quit;
    ImageView btn_dialog_quit_ok, btn_dialog_quit_cancel, btn_dialog_check, img_label_music;
    ImageView img_label_sound, img_label_vibrate, btn_setting_check;
    ImageView dialog_pause_bg, dialog_quit_bg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_in_game);

        gameView = (GameView)findViewById(R.id.gameview);

        toggle_music = (ToggleButton)findViewById(R.id.toggle_music);
        toggle_sound = (ToggleButton)findViewById(R.id.toggle_sound);
        toggle_vibrate = (ToggleButton)findViewById(R.id.toggle_vibrate);

        tv_score = (TextView)findViewById(R.id.tv_score);
        tv_coin = (TextView)findViewById(R.id.tv_coin);
        tv_gem = (TextView)findViewById(R.id.tv_gem);
        tv_bomb = (TextView)findViewById(R.id.tv_bomb);
        tv_champion_score = (TextView)findViewById(R.id.tv_champion_score);

        dialog_pause_bg = (ImageView)findViewById(R.id.dialog_pause_bg);
        Glide.with(this).load(R.drawable.bg_pause).into(dialog_pause_bg);
        dialog_quit_bg = (ImageView)findViewById(R.id.dialog_quit_bg);
        Glide.with(this).load(R.drawable.dialog_quit).into(dialog_quit_bg);

        img_coin = (ImageView)findViewById(R.id.img_coin);
        Glide.with(this).load(R.drawable.label_coin).into(img_coin);
        img_gem = (ImageView)findViewById(R.id.img_gem);
        Glide.with(this).load(R.drawable.label_gem).into(img_gem);
        img_bomb = (ImageView)findViewById(R.id.img_bomb);
        Glide.with(this).load(R.drawable.label_bomb).into(img_bomb);
        img_pause = (ImageView)findViewById(R.id.img_pause);
        img_quit = (ImageView)findViewById(R.id.img_quit);
        Glide.with(this).load(R.drawable.btn_quit).into(img_quit);

        btn_dialog_quit_ok = (ImageView)findViewById(R.id.btn_dialog_quit_ok);
        Glide.with(this).load(R.drawable.select_ok).into(btn_dialog_quit_ok);
        btn_dialog_quit_cancel = (ImageView)findViewById(R.id.btn_dialog_quit_cancel);
        Glide.with(this).load(R.drawable.select_cancel).into(btn_dialog_quit_cancel);

        btn_dialog_check = (ImageView)findViewById(R.id.btn_dialog_check);
        Glide.with(this).load(R.drawable.check).into(btn_dialog_check);
        img_label_music = (ImageView)findViewById(R.id.img_label_music);
        Glide.with(this).load(R.drawable.ui_setting_label_music).into(img_label_music);
        img_label_sound = (ImageView)findViewById(R.id.img_label_sound);
        Glide.with(this).load(R.drawable.ui_setting_label_sound).into(img_label_sound);
        img_label_vibrate = (ImageView)findViewById(R.id.img_label_vibrate);
        Glide.with(this).load(R.drawable.ui_setting_label_vibrate).into(img_label_vibrate);
        btn_setting_check = (ImageView)findViewById(R.id.btn_setting_check);
        Glide.with(this).load(R.drawable.check).into(btn_setting_check);

        mp = MediaPlayer.create(this, R.raw.my_friend_dragon);
        mp.setLooping(true);

        toggle_music.setOnCheckedChangeListener(checkedChangeListener);
        toggle_sound.setOnCheckedChangeListener(checkedChangeListener);
        toggle_vibrate.setOnCheckedChangeListener(checkedChangeListener);

        toggle_music.setChecked(G.isMusic);
        toggle_sound.setChecked(G.isSound);
        toggle_vibrate.setChecked(G.isVibrate);


    }//onCreate()

    CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            switch (compoundButton.getId()){
                case R.id.toggle_music:
                    G.isMusic = b;
                    compoundButton.setChecked(G.isMusic);

                    if(G.isMusic){
                        mp.setVolume(0.7f, 0.7f);
                    }else mp.setVolume(0.0f, 0.0f);
                    break;
                case R.id.toggle_sound:
                    G.isSound = b;
                    compoundButton.setChecked(G.isSound);
                    break;
                case R.id.toggle_vibrate:
                    G.isVibrate = b;
                    compoundButton.setChecked(G.isVibrate);
                    break;
            }
        }
    };

    @Override
    protected void onResume() {

        if(mp!=null && !mp.isPlaying()){
            if(G.isMusic)mp.setVolume(0.7f, 0.7f);
            else mp.setVolume(0.0f, 0.0f);
        }
        mp.start();
        super.onResume();
    }

    @Override
    protected void onPause() {
        if(mp!=null && mp.isPlaying()){
            mp.pause();
        }

        gameView.pauseGame();
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        if(mp!=null){
            mp.stop();
            mp.release();
            mp=null;
        }
        super.onDestroy();
    }

    //각 다이얼로그의 안에 있는 버튼을 클릭했을 때 발동하는 메소드
    public void clickDialogBtn(View v){
        switch (v.getId()){
            case R.id.btn_dialog_quit_ok:
                gameView.stopGame();
                finish();
                break;

            case R.id.btn_dialog_quit_cancel:
                dialog.setVisibility(View.GONE);
                dialog = null;
                gameView.resumeGame();
                break;

            case R.id.btn_dialog_pause_play:

                Animation ani = AnimationUtils.loadAnimation(this, R.anim.disappear_dialog_pause);
                ani.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        dialog.setVisibility(View.GONE);
                        dialog = null;
                        gameView.resumeGame();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                dialog.startAnimation(ani);
                break;

            case R.id.btn_setting_check:
                hideDialog();
                break;

            case R.id.btn_dialog_check:
                hideDialog();
                break;
        }
    }

    void hideDialog(){
        Animation ani = AnimationUtils.loadAnimation(this, R.anim.disappear_dialog);
        dialog.startAnimation(ani);
        ani.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                dialog.setVisibility(View.GONE);
                dialog = null;
                gameView.resumeGame();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    void showQuitDialog(){
        if(dialog!=null) return;

        gameView.pauseGame();
        dialog = findViewById(R.id.dialog_quit);
        dialog.setVisibility(View.VISIBLE);
        Animation ani = AnimationUtils.loadAnimation(this, R.anim.appear_dialog_quit);
        dialog.startAnimation(ani);
    }

    @Override
    public void onBackPressed() {
        showQuitDialog();
    }

    void showPauseDialog(){
        if(dialog != null)return;

        gameView.pauseGame();
        dialog = findViewById(R.id.dialog_pause);
        dialog.setVisibility(View.VISIBLE);
        Animation ani = AnimationUtils.loadAnimation(this, R.anim.appear_dialog_pause);
        dialog.startAnimation(ani);
    }

    public void clickPause(View v){

        showPauseDialog();

    }//clickPause()

    public void clickQuit(View v){

        showQuitDialog();

    }//clickQuit()

    public void clickShop(View v){

        showSettingAndShop(R.id.dialog_shop);

//        switch (v.getId()){
//            case R.id.img_class_shop:
//                break;
//
//            case R.id.img_item_shop:
//                break;
//        }

    }//clickShop()

    public void showSettingAndShop(int viewId){

        if(dialog!=null) return;

        gameView.pauseGame();

        dialog = findViewById(viewId);
        dialog.setVisibility(View.VISIBLE);

        Animation ani = AnimationUtils.loadAnimation(this, R.anim.appear_dialog);
        dialog.startAnimation(ani);

    }

    public void clickSetting(View v){

        showSettingAndShop(R.id.dialog_setting);

    }//clickSetting()

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            gameView.stopGame();

            Intent intent = new Intent(InGameActivity.this, GameoverActivity.class);
            intent.putExtra("data", msg.getData());

            startActivity(intent);
            finish();


        }
    };


}//InGameActivity
