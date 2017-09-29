package com.kghy1234gmail.insanedragon;

import android.graphics.Bitmap;
import android.graphics.Rect;

import java.util.Random;

public class Enemy {

    int width, height;

    Bitmap img;
    int x, y;
    int w, h;
    boolean isDead = false; // 총 맞아 죽음
    boolean isOut = false;  // 화면밖에 나가서 죽음
    boolean wasShown = false;   //화면에 보였던 적이 있는가

    Rect screen;    //화면만한 사각형

    double radian;  //이동 각도
    int speed;      //이동 속도

    int angle;;     //회전 각도

    int kind;       //적군의 종류

    Bitmap[] imgs;  //종류에 맞는 이미지;
    int index;      //날개짓 이미지 번호

    int loop;

    int hp;

    Bitmap[] imgGauges;   //게이지 이미지 원본
    Bitmap imgCurrentGauge; //현재 게이지

    public Enemy(int width, int height, Bitmap[][] imgEnemy, int px, int py, Bitmap[][] imgGauge) {

        this.width = width;
        this.height = height;

        screen = new Rect(0,0,width,height);

        Random rnd = new Random();

        //적군 종류 정하기     WHITE : 0, YELLOW : 1, PINK : 2
        int n = rnd.nextInt(20);
        kind = n < 9 ? 0 : n < 16 ? 1: 2;      // 45%, 35%, 20%
        imgs = imgEnemy[kind];
        img = imgs[index];

        w = img.getWidth()/2;
        h = img.getHeight()/2;

        //hp = WHITE : 2 YELLOW : 5 PINK : 3
        hp = kind==0 ? 1 : kind == 1 ? 5 : 3;

        speed = kind==0 ? w/6 : kind == 1 ? w/8 : w/10;

        //현근's Way to how to make enemies
//        x = rnd.nextInt(width);
//        x = x<width/2 ? x - width : x + width;
//
//        y = rnd.nextInt(height);
//        y = y<height/2 ? y - height : y + height;

        int a = rnd.nextInt(360);   //0~359;
        x = (int) (width/2 + Math.cos(Math.toRadians(a)) * width);
        y = (int) (height/2 - Math.sin(Math.toRadians(a)) * width);

        //플레이어를 바라보는 각도(적군이 이동할 각도)
        culAngle(px, py);

        if(kind>0){//White가 아닌 경우 게이지를 가짐
            imgGauges = imgGauge[kind-1];
            imgCurrentGauge = imgGauges[0];
        }

    }

    void culAngle(int px, int py){
        radian = Math.atan2(y - py, px - x);

        //회전 각도
        angle = (int) (270 - Math.toDegrees(radian));
    }

    void getDamaged(int n){
        hp -= n;
        if(hp<=0) {
            isDead = true;
            return;
        }

        imgCurrentGauge = imgGauges[imgGauges.length - hp];
    }

    void move(int px, int py){

        if(kind == 2) culAngle(px, py);

        //날개짓
        loop++;
        if(loop % 5 == 0){
            index++;
            if(index>3) index = 0;
            img = imgs[index];
        }

        //이동
        x = (int)(x + Math.cos(radian) * speed);
        y = (int)(y - Math.sin(radian) * speed);

        if(screen.contains(x,y)) wasShown = true;

        if(wasShown) {
            if(x<-w || w> width + w || y < -h || y > height + h) isOut = true;
        }
    }

}
