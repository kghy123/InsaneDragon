package com.kghy1234gmail.insanedragon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    //효과음
    SoundPool sp;
    int sd_player_die, sd_explosion_bomb, sd_fire, sd_coin, sd_gem, sd_protect, sd_item, sd_mon_die;

    //진동관리자
    Vibrator vibrator;

    Context context;
    SurfaceHolder holder;   //공장장 객체 참조 변수

    int width, height;      //GameView의 화면 사이즈

    Random rnd = new Random();

    int backPos; //배경 이미지의 x좌표

    GameThread gameThread;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        holder = getHolder();
        holder.addCallback(this);

        
    }

    //생성자 메소드 실행 후 자동으로 실행되는 메소드
    //이 GameView가 화면에 보이면 자동으로 실행되는 메소드
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    //surfaceCreated()메소드가 실행된 후 자동으로 실행되는 메소드
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int foramt, int width, int height) {

        if(gameThread == null) {
            this.width = getWidth();
            this.height = getHeight();

            gameThread = new GameThread();
            gameThread.start();
        }else resumeGame();

    }

    //이 GameView가 화면에 보이지 않으면 자동으로 실행되는 메소드
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {



    }//surfaceDestroyed()

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int act = event.getAction();
        int x = 0, y = 0;

        switch(act){
            case MotionEvent.ACTION_DOWN:
                x = (int)event.getX();
                y = (int)event.getY();

                gameThread.touchDown(x, y);

                break;

            case MotionEvent.ACTION_UP:
                x = (int)event.getX();
                y = (int)event.getY();

                gameThread.touchUp(x, y);

                break;

            case MotionEvent.ACTION_MOVE:
                x = (int)event.getX();
                y = (int)event.getY();

                gameThread.touchMove(x, y);

                break;

        }

        return true;
    }

    void stopGame(){
        gameThread.stopThread();
    }

    void pauseGame(){
        gameThread.pauseThread();
    }

    void resumeGame(){
        gameThread.resumeThread();
    }

    class GameThread extends Thread{

        //fps조절용 변수
        int fps = 45;                // 초당 fps장
        int frameTime = 1000/fps;   //1장당 걸리는 시간
        long loopTime;               //while문 한바퀴를 도는 시간
        long sleepTime;              //잠잘 시간
        long lastTime;              //이전 시간
        long currentTime;           //현재 시간

        int skippedFrame;           //스킵된 프레임 숫자

        ///////////////
        //Stage관련 변수
        int stage = 1;      // 1 - 10
        int bomb = 3;
        int score = 0;
        int coin = 0;

        boolean isRun = true;
        boolean isWait = false;

        Bitmap imgBack;
        Bitmap[][] imgPlayer = new Bitmap[3][4];

        Bitmap imgBombBtn;
        //폭탄버튼 관련 변수
        boolean isBomb = false;     //버튼이 눌렸는가
        Rect rectBombBtn;           //폭탄 버튼의 사각형 영역
        Bitmap[] imgExplosions = new Bitmap[12];
        ArrayList<Explosion> explosions = new ArrayList<>();

        //아이템
        Bitmap[] imgItem = new Bitmap[7];
        ArrayList<Item> items = new ArrayList<>();
        Bitmap imgProtect;
        int protectRad;     //보호막 이미지 반지름
        int protectAngle;   //보호막 이미지 회전 각도
        Bitmap imgStrong;

        //FAST아이템의 지속시간
        int fastLifeTime = 0;
        int protectLifeTime = 0;
        int magnetLifeTime = 0;
        int strongLifeTime = 0;


        //적군
        Bitmap[][] imgEnemy = new Bitmap[3][4];
        ArrayList<Enemy> enemies = new ArrayList<>();

        //적군
        //게이지
        Bitmap[][] imgGauge = new Bitmap[2][];

        //미사일
        Bitmap[] imgMissile = new Bitmap[3];
        ArrayList<Missile> missiles = new ArrayList<>();
        int missileGap = 5; //미사일이 발사되는 간격
        int missileTime = missileGap;

        //먼저 이미지
        Bitmap[] imgDust = new Bitmap[6];
        ArrayList<Dust> dusts = new ArrayList<>();

        //조이패드
        Bitmap imgJoypad;
        Bitmap imgJoypadCircle;
        int padx, pady, padr;
        Bitmap imgJoypadCircleBg;
        int jpx, jpy, jpr;
        boolean isJoypad = false;
        Paint alphaPaint = new Paint();

        Player player;

        public GameThread() {

        }

        void createBitmaps(){

            //배경 이미지
            Bitmap img = null;
            img = BitmapFactory.decodeResource(getResources(), R.drawable.back_01 + rnd.nextInt(6));
            imgBack = Bitmap.createScaledBitmap(img, width, height, false);
            img.recycle(); img = null;

            //폭탄 버튼 이미지
            int size = height/4;
            img = BitmapFactory.decodeResource(getResources(), R.drawable.btn_bomb);
            imgBombBtn = Bitmap.createScaledBitmap(img, size, size, true);
            img.recycle(); img = null;
            rectBombBtn = new Rect(width-size, height-size, width, height);
            for(int i=0; i<imgExplosions.length; i++){
                img = BitmapFactory.decodeResource(getResources(), R.drawable.explosion_00 + i);
                imgExplosions[i] = Bitmap.createScaledBitmap(img, height, height/2, true);
                img.recycle(); img = null;
            }


            //아이템 이미지
            for(int i=0; i<imgItem.length; i++){
                img = BitmapFactory.decodeResource(getResources(), R.drawable.item_0_coin + i);
                imgItem[i] = Bitmap.createScaledBitmap(img, height/18, height/18, true);
                img.recycle(); img = null;
            }

            img = BitmapFactory.decodeResource(getResources(), R.drawable.effect_protect);
            imgProtect = Bitmap.createScaledBitmap(img, height/4, height/4 , true);
            img.recycle(); img = null;
            protectRad = imgProtect.getWidth()/2;

            img = BitmapFactory.decodeResource(getResources(), R.drawable.bullet_04);
            imgStrong = Bitmap.createScaledBitmap(img, height/10, height/10, true);
            img.recycle(); img = null;


            //Dust이미지
            float[] ratios = new float[]{0.5f, 0.7f, 1.0f, 1.4f, 1.8f, 2.2f};

            for(int i=0; i<imgDust.length; i++){
                img = BitmapFactory.decodeResource(getResources(), R.drawable.dust);
                imgDust[i] = Bitmap.createScaledBitmap(img, (int) (height/9 * ratios[i]), (int) (height/9 * ratios[i]), true);
            }
//            img.recycle(); img = null;

            //조이패드 서클 이미지
            img = BitmapFactory.decodeResource(getResources(), R.drawable.joypad_btn);
            imgJoypadCircle = Bitmap.createScaledBitmap(img, height/8, height/8, true);
            img.recycle(); img = null;
            img = BitmapFactory.decodeResource(getResources(), R.drawable.joypad_circle);
            imgJoypadCircleBg = Bitmap.createScaledBitmap(img, height/2, height/2, true);
            img.recycle(); img = null;

            //미사일 이미지
            for(int i = 0; i < imgMissile.length; i++){
                img = BitmapFactory.decodeResource(getResources(), R.drawable.bullet_01 + i);
                imgMissile[i] = Bitmap.createScaledBitmap(img, height/10, height/10, true);
                img.recycle(); img = null;
            }

            //적군 이미지
            for(int i=0; i<3; i++){
                for(int j=0; j<3; j++){
                    img = BitmapFactory.decodeResource(getResources(), R.drawable.enemy_a_01 + j + (i * 3));
                    imgEnemy[i][j] = Bitmap.createScaledBitmap(img, height/9, height/9, true);
                    img.recycle(); img = null;
                }
                imgEnemy[i][3] = imgEnemy[i][1];
            }

            //Gauge 이미지
            imgGauge[0] = new Bitmap[5];
            imgGauge[1] = new Bitmap[3];
            for(int i=0; i<imgGauge[0].length; i++){
                img = BitmapFactory.decodeResource(getResources(), R.drawable.gauge_step5_01 + i);
                imgGauge[0][i] = Bitmap.createScaledBitmap(img, height/9, height/36, true);
                img.recycle(); img = null;
            }
            for(int i=0; i<imgGauge[1].length; i++){
                img = BitmapFactory.decodeResource(getResources(), R.drawable.gauge_step3_01 + i);
                imgGauge[1][i] = Bitmap.createScaledBitmap(img, height/9, height/36, true);
                img.recycle(); img = null;
            }


            //플레이어 이미지
            //RED
            for(int i=0; i<3; i++){
                img  = BitmapFactory.decodeResource(getResources(), R.drawable.char_a_01 + i);
                imgPlayer[0][i] = Bitmap.createScaledBitmap(img, height/8, height/8, true);
                img.recycle(); img = null;
            }
            imgPlayer[0][3] = imgPlayer[0][1];

            //PURPLE
            for(int i=0; i<3; i++){
                img  = BitmapFactory.decodeResource(getResources(), R.drawable.char_b_01 + i);
                imgPlayer[1][i] = Bitmap.createScaledBitmap(img, height/8, height/8, true);
                img.recycle(); img = null;
            }
            imgPlayer[1][3] = imgPlayer[1][1];

            //BLACK
            for(int i=0; i<3; i++){
                img  = BitmapFactory.decodeResource(getResources(), R.drawable.char_c_01 + i);
                imgPlayer[2][i] = Bitmap.createScaledBitmap(img, height/8, height/8, true);
                img.recycle(); img = null;
            }
            imgPlayer[2][3] = imgPlayer[2][1];

            //조이패드 이미지
//            img = BitmapFactory.decodeResource(getResources(), R.drawable.img_joypad);
//            imgJoypad = Bitmap.createScaledBitmap(img, height/2, height/2, true);
//            img.recycle(); img = null;

        }

        //모든 자원들(Bitmap ...)메모리 제거
        void removeResouces(){
            //배경 자원
            imgBack.recycle();
            imgBack = null;

//            for(int i = 0; i < 12; i++){
//                imgExplosions[i].recycle(); imgExplosions = null;
//            }

            //조이패드 자원
//            imgJoypad.recycle(); imgJoypad = null;

            //폭탄 버튼 자원
            imgBombBtn.recycle(); imgBombBtn = null;

            //아이템 자원
            for(int i=0; i<imgItem.length; i++){
                imgItem[i].recycle(); imgItem[i] = null;
            }
            imgProtect.recycle(); imgProtect = null;
            imgStrong.recycle(); imgStrong = null;

            //Dust 자원
            for(int i=0; i<imgDust.length; i++){
                imgDust[i].recycle(); imgDust[i] = null;
            }

            //미사일 자원
            for(int i = 0; i < imgMissile.length; i++){
                imgMissile[i].recycle();
                imgMissile[i] = null;
            }

            //Gauge 자원
            for(int i=0; i < imgGauge.length; i++){
                for(int j=0; j < imgGauge[i].length; j++){
                    imgGauge[i][j].recycle();
                    imgGauge[i][j] = null;
                }
            }

            //적군 자원
            for(int i=0; i<3; i++){
                for(int j=0; j<3; j++){
                    imgEnemy[i][j].recycle();
                    imgEnemy[i][j] = null;
                }
                imgEnemy[i][3] = null;
            }

            //플레이어 자원
            for(int i=0; i<3; i++){
                for(int j=0; j<3; j++){
                    imgPlayer[i][j].recycle();
                    imgPlayer[i][j] = null;
                }
                imgPlayer[i][3] = null;
            }

        }

        //게임이 시작할 떄 설정할 각종 초기값 작업 메소드
        void initializeSetting(){

            player = new Player(width, height, imgPlayer, G.kind);

//            jpr = imgJoypad.getWidth()/2;
            jpr = imgJoypadCircleBg.getWidth()/2;
            jpx = jpr;
            jpy = height - jpr;

            padr = imgJoypadCircle.getWidth()/2;
            padx = jpx;
            pady = jpy;

            //텍스트뷰 값 설정
            setTextViewValue();

            //효과음 등록
            sp = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
            sd_player_die = sp.load(context, R.raw.ch_die, 1);
            sd_explosion_bomb = sp.load(context, R.raw.explosion_bomb, 1);
            sd_fire = sp.load(context, R.raw.fireball, 1);
            sd_coin = sp.load(context, R.raw.get_coin, 1);
            sd_gem = sp.load(context, R.raw.get_gem, 1);
            sd_protect = sp.load(context, R.raw.get_invincible, 1);
            sd_item = sp.load(context, R.raw.get_item, 1);
            sd_mon_die = sp.load(context, R.raw.mon_die, 1);

            //진동 관리자 객체
            vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);

        }

        //텍스트뷰 값 설정 작업
        void setTextViewValue(){

            post(new Runnable() {
                @Override
                public void run() {

                    ((InGameActivity)context).tv_score.setText(String.format("%07d",score) + "");
                    ((InGameActivity)context).tv_coin.setText(String.format("%04d", coin) + "");
                    ((InGameActivity)context).tv_gem.setText(String.format("%04d", G.gem) + "");
                    ((InGameActivity)context).tv_bomb.setText(String.format("%04d", bomb) + "");
                    ((InGameActivity)context).tv_champion_score.setText(String.format("%07d", G.champion) + "");

                }
            });


        }

        void makeAll(){
            //미사일 생성
            missileTime--;
            if(missileTime<=0){
                if(G.isSound) sp.play(sd_fire, 0.2f, 0.2f, 0, 0 ,1.0f);
                missiles.add(new Missile(width, height, imgMissile, player.x, player.y, player.angle, player.kind));
                missileTime = missileGap;
            }

            //적군 생성
            int p = rnd.nextInt(21 - stage);
            if(p == 0){
                enemies.add(new Enemy(width, height, imgEnemy, player.x, player.y, imgGauge));
            }
        }

        void moveAll(){
            //배경 움직이기
            backPos-=2;
            if(backPos <= -width) backPos += width;

            //미사일 움직이기
           for(int i = missiles.size()-1; i>=0; i--){
               missiles.get(i).move();
               if(missiles.get(i).isDead) {
                   missiles.remove(i);
               }
           }

           //적군 움직이기
            for(int i = enemies.size()-1; i>=0; i--){
                enemies.get(i).move(player.x, player.y);
                if(enemies.get(i).isDead) {

                    //폭발 효과음
                    if(G.isSound) sp.play(sd_mon_die, 0.8f, 0.8f, 1, 0 ,1.0f);
                    //점수 획득
                    score += (enemies.get(i).kind + 1);
                    if(stage<20) {
                        if (score >= stage * stage * 400) {
                            stage++;
                        }
                    }
                    setTextViewValue();

                    //폭발 효과 이미지
                    dusts.add(new Dust(imgDust, enemies.get(i).x, enemies.get(i).y));

                    //아이템 생성
                    if(rnd.nextInt(2)==0) items.add(new Item(width, height, imgItem, enemies.get(i).x, enemies.get(i).y));

                    enemies.remove(i);
                }
                else if(enemies.get(i).isOut) enemies.remove(i);
            }

            //아이템 움직이기
            for(int i = items.size()-1; i>=0; i--){

                if(magnetLifeTime>0 && items.get(i).kind<2){
                    items.get(i).move(player.x, player.y);
                }else items.get(i).move();
                if(items.get(i).isDead) items.remove(i);
            }

            for(int i = explosions.size()-1; i>=0; i--){
                explosions.get(i).move();
                if(explosions.get(i).isDead) explosions.remove(i);
            }

            //플레이어 움직이기
            player.move();

            //먼지 움직이기
            for(int i = dusts.size()-1; i>=0; i--){
                dusts.get(i).move();
                if(dusts.get(i).isDead) dusts.remove(i);
            }

            //폭발 이미지 바꾸기
            for(int i = explosions.size()-1; i>=0; i--){
                explosions.get(i).move();
                if(explosions.get(i).isDead) explosions.remove(i);
            }

            checkItemLifeTime();

        }

        //아이템의 지속시간 체크
        void checkItemLifeTime(){

            if(fastLifeTime >0){
                fastLifeTime--;
                if(fastLifeTime==0){
                    player.dAngle = 2;
                    missileGap = 5;
                }
            }

            if(protectLifeTime > 0){
                protectLifeTime--;
            }

            if(magnetLifeTime >0){
                magnetLifeTime--;
            }

            if(strongLifeTime > 0){
                strongLifeTime--;
            }

        }

        //얻은 아이템에 따른 작업
        void actionItem(int kind){

            switch (kind){
                case Item.COIN:
                    coin++;
                    if(G.isSound) sp.play(sd_coin, 1.0f, 1.0f, 2, 0 ,1.0f);
                    setTextViewValue();
                    break;

                case Item.GEM:
                    G.gem++;
                    if(G.isSound) sp.play(sd_gem, 1.0f, 1.0f, 3, 0 ,1.0f);
                    setTextViewValue();
                    break;

                case Item.FAST:
                    if(G.isSound) sp.play(sd_item, 1.0f, 1.0f, 3, 0 ,1.0f);
                    fastLifeTime = fps * 4;
                    player.dAngle = 8;
                    missileGap = 1;
                    break;

                case Item.PROTECT:
                    if(G.isSound) sp.play(sd_protect, 0.5f, 0.5f, 5, 4 ,1.0f);
                    protectLifeTime = fps * 7;
                    break;

                case Item.MAGNET:
                    if(G.isSound) sp.play(sd_item, 1.0f, 1.0f, 3, 0 ,1.0f);
                    magnetLifeTime = fps * 2;
                    break;

                case Item.BOMB:
                    if(bomb<100)bomb++;
                    setTextViewValue();
                    break;

                case Item.STRONG:
                    if(G.isSound) sp.play(sd_item, 0.6f, 0.6f, 0, 0 ,1.0f);
                    strongLifeTime = fps * 6;
                    break;
            }

        }

        void checkCollision(){

            //적군과 플레이어의 충돌
            for(Enemy e : enemies){
                if(protectLifeTime>0){

                    if(Math.pow(e.x - player.x, 2) + Math.pow(e.y - player.y, 2) <= Math.pow(e.w + protectRad, 2)){

                        e.isDead = true;
                        break;
                    }

                }else {

                    if(Math.pow(e.x - player.x, 2) + Math.pow(e.y - player.y, 2) <= Math.pow(e.w + player.w,2)){


                        e.isDead = true;
                        player.hp--;
                        if(G.isVibrate)vibrator.vibrate(500);

                        if(player.hp <= 0){
                            //GAME OVER !!
                            if(G.isSound) sp.play(sd_player_die, 1.0f, 1.0f, 6, 0, 1.0f);
                            //GO TO GAME OVER ACTIVITY 실행 요청
                            Message msg = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putInt("score", score);
                            bundle.putInt("coin", coin);
                            msg.setData(bundle);
                            ((InGameActivity)context).handler.sendMessage(msg);

                        }
                        break;
                    }

                }
            }


            //미사일과 적군의 충돌 체크
            for(Missile t : missiles){
                for(Enemy e : enemies){
                    if(Math.pow(t.x - e.x, 2) + Math.pow(t.y - e.y, 2) <= Math.pow(t.w + e.w, 2)){

                        score += 1;
                        setTextViewValue();

                        e.getDamaged(t.kind+1);
                        if(strongLifeTime==0)t.isDead=true;
                    }
                }
            }

            //아이템과 플레이어의 충돌 체크
            for(Item i : items){
                if(Math.pow(i.x - player.x, 2) + Math.pow(i.y - player.y, 2) <= Math.pow(i.w + player.w,2)){

                    actionItem(i.kind);
                    i.isDead = true;
                    break;
                }
            }


        }

        void drawAll(Canvas canvas){

            //배경
            canvas.drawBitmap(imgBack,backPos,0,null);
            canvas.drawBitmap(imgBack,backPos + width, 0, null);

            //폭발버튼 눌렀을때 이미지
            for(Explosion x : explosions){
                alphaPaint.setAlpha(180);
                canvas.drawBitmap(x.imgExplosion, x.x - x.w, x.y - x.h, alphaPaint);
            }

            //아이템 그리기
            for(Item i : items){
                canvas.drawBitmap(i.img, i.x - i.w, i.y- i.h, null);
            }

            //먼지들 그리기
            for(Dust d : dusts){
                for(int i=0; i < d.imgs.length; i++){
                    canvas.drawBitmap(d.imgs[i], d.x[i] - d.r[i], d.y[i] - d.r[i], null);
                }
            }

            //적군들 그리기
            for(Enemy t : enemies){
                canvas.save();
                canvas.rotate(t.angle, t.x, t.y);
                canvas.drawBitmap(t.img, t.x-t.w, t.y-t.h, null);
                canvas.restore();
                if(t.kind >0) canvas.drawBitmap(t.imgCurrentGauge, t.x - t.w, t.y + t.h, null);
            }

            //미사일들
            for(Missile t : missiles){
                canvas.save();
                canvas.rotate(t.angle, t.x, t.y);
                canvas.drawBitmap(strongLifeTime>0 ? imgStrong : t.img, t.x-t.w, t.y-t.h, null);
                canvas.restore();
            }

            //플레이어
            canvas.save();
            canvas.rotate(player.angle, player.x, player.y);
            canvas.drawBitmap(player.img, player.x - player.w, player.y - player.h, null);
            canvas.restore();

            //보호막 그리기
            if(protectLifeTime>0){
                canvas.save();
                protectAngle += 30;
                canvas.rotate(protectAngle, player.x, player.y);
                canvas.drawBitmap(imgProtect, player.x - protectRad, player.y - protectRad, null);
                canvas.restore();
            }

            //조이패드
            alphaPaint.setAlpha(isJoypad ? 230 : 100);
//            canvas.drawBitmap(imgJoypad, jpx - jpr, jpy - jpr, alphaPaint);
            canvas.drawBitmap(imgJoypadCircle, padx - padr, pady - padr, alphaPaint);
            canvas.drawBitmap(imgJoypadCircleBg, jpx - jpr, jpy - jpr, alphaPaint);

            alphaPaint.setAlpha(isBomb ? 230 : 100);
            canvas.drawBitmap(imgBombBtn, rectBombBtn.left, rectBombBtn.top, alphaPaint);



        }

        void touchDown(int x, int y){

//            //누른곳에 조이패드 생성
//            jpx = x;
//            jpy = y;

            if(Math.pow(x - jpx, 2) + Math.pow(y - jpy, 2) <= Math.pow(jpr, 2)){
                player.radian = Math.atan2(jpy-y, x-jpx);

                isJoypad = true;
                player.canMove = true;

                player.speed = (int)(Math.pow(x - jpx, 2) + Math.pow(y - jpy, 2)) / 1000;
            }

            if(rectBombBtn.contains(x, y)){
                isBomb = true;

                if(bomb > 0){

                    if(G.isSound) sp.play(sd_explosion_bomb, 1.0f, 1.0f, 5, 0 ,1.0f);

                    bomb--;
                    setTextViewValue();

                    if(explosions.size()<2) explosions.add(new Explosion(imgExplosions, width, height));

                    //적군들 모두 제거
                    for(Enemy e : enemies){
                        if(e.wasShown) e.isDead = true;
                    }
                }
            }

        }

        void touchUp(int x, int y){
            isJoypad = false;
            player.canMove = false;

            isBomb = false;

            padx = jpx;
            pady = jpy;

            player.speed = 0;
        }

        void touchMove(int x, int y){

            if(isJoypad){
                player.radian = Math.atan2(jpy-y, x-jpx);

                if(Math.pow(x - jpx, 2) + Math.pow(y - jpy, 2) <= Math.pow(jpr-padr, 2)) {
                    padx = x;
                    pady = y;

                    player.speed = (int)(Math.pow(x - jpx, 2) + Math.pow(y - jpy, 2)) / 1000;

                }else{
                    padx = (int) (jpx + Math.cos(player.radian) * (jpr-padr*2));
                    pady = (int) (jpy - Math.sin(player.radian) * (jpr-padr*2));

                    player.speed = (int)Math.pow(jpr - padr, 2) / 1500;
                }
            }

        }

        //fps를 조절하는 작업
        void adjustFPS(){

            currentTime = System.currentTimeMillis();
            loopTime = currentTime - lastTime;
            lastTime = currentTime;

            sleepTime = frameTime - loopTime;

            //fast
            if(sleepTime>0){

                try {Thread.sleep(sleepTime);} catch (InterruptedException e) {e.printStackTrace();}

            }

            //slow
            skippedFrame = 0;
            while(sleepTime<0 && skippedFrame<5){
                makeAll();
                moveAll();
                checkCollision();

                sleepTime += frameTime;
                skippedFrame++;
            }

        }


        @Override
        public void run() {

            createBitmaps();

            //게임의 각종 초기값 설정
            initializeSetting();

            lastTime = System.currentTimeMillis();

            Canvas canvas = null;

            while (isRun){

                canvas = holder.lockCanvas();

                try{
                    synchronized (holder){
                        makeAll();
                        moveAll();
                        checkCollision();

                        adjustFPS();

                        drawAll(canvas);
                    }
                }finally {
                    if(canvas !=null) holder.unlockCanvasAndPost(canvas);
                }

                synchronized (this) {
                    if(isWait){
                        try {
                            wait();
                        } catch (InterruptedException e) {}
                    }
                }

            }

            removeResouces();

        }//run()

        void stopThread(){
            isRun = false;

            synchronized (this){
                this.notify();
            }
        }

        void pauseThread(){

            isWait = true;

        }

        void resumeThread(){
            isWait = false;

            synchronized (this){
                this.notify();
            }
        }

    }//GameThread

}//GameView
