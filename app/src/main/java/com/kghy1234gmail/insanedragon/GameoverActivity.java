package com.kghy1234gmail.insanedragon;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class GameoverActivity extends AppCompatActivity {

    TextView tv_champion_score;
    TextView tv_player_score;
    boolean isChampion = false;
    ImageView img_champion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_gameover);

        tv_champion_score = (TextView)findViewById(R.id.tv_champion_score);
        tv_player_score = (TextView)findViewById(R.id.tv_player_score);
        img_champion = (ImageView)findViewById(R.id.img_champion);

        Bundle data = getIntent().getBundleExtra("data");

        int score = data.getInt("score",0);
        int coin = data.getInt("coin", 0);

        int playerScore = score + coin*10;

        tv_player_score.setText(String.format("%07d", playerScore));

        if(playerScore > G.champion){
            //챔피언 점수 갱신
            G.champion = playerScore;
            isChampion = true;

            tv_champion_score.setTextColor(0xff888888);
            tv_player_score.setTextColor(0xffff8800);
        }

        tv_champion_score.setText(String.format("%07d", G.champion));


        if(G.imgUri != null){

            Uri uri = Uri.parse(G.imgUri);
            Glide.with(this).load(uri).into(img_champion);

        }

    }

    void saveData(){
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putInt("gem" , G.gem);
        editor.putInt("champion", G.champion);
        editor.putInt("kind", G.kind);

        editor.putBoolean("isMusic", G.isMusic);
        editor.putBoolean("isSound", G.isSound);
        editor.putBoolean("isVibrate", G.isVibrate);

        editor.putString("imgUri", G.imgUri);

        editor.commit();
    }

    @Override
    protected void onDestroy() {
        saveData();
        super.onDestroy();

    }

    public void clickChampion(View v){
        if(!isChampion) return;

        //내 폰에 있는 사진 선택 가능하도록 사진보기 실행

        if(Build.VERSION.SDK_INT<19){

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 10);     //requestCode는 인텐트끼리의 구분자



        }else{
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, 10);

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if(resultCode== Activity.RESULT_OK) {
                    Glide.with(this).load(data.getData()).into(img_champion);

                    G.imgUri = data.getData().toString();

                }
                else {
                    return;
                }
                break;
        }
    }

    public  void clickRetry(View v){

        Intent intent = new Intent(this, InGameActivity.class);
        startActivity(intent);
        finish();

    }

    public  void clickExit(View v){
        finish();
    }


}
