package com.kghy1234gmail.insanedragon;

import android.graphics.Bitmap;

public class Player {

    int width, height; //화면 사이즈

    Bitmap img; //플레이어 이미지
    int x, y, w, h, angle = 0; //플레이어 좌표 및 보정값
    int dAngle = 2; //회전각도 변화량

    //RED = 0, PURPLE = 1, BLACK = 2
    int kind; //플레이어의 종류

    int hp = 3;

    boolean canMove = false; // 움직일 수 있는가
    double radian;            // 이동 각도
    int speed;                //이동 속도

    Bitmap[][] imgs;
    int index = 0;  //날개짓 이미지 번호

    int loop = 0;

    public Player(int width, int height, Bitmap[][] imgPlayer, int kind) {

        this.width = width;
        this.height = height;
        imgs = imgPlayer;
        this.kind = kind;

        hp = 3 + 2*kind;

        img = imgs[kind][index];
        w = img.getWidth()/2;
        h = img.getHeight()/2;

        x = width/2;
        y = height/2;

        //이동속도
//        speed = w/8;
    }

    void move(){

        //날개짓
        loop++;
        if(loop%5==0) {
            index++;
            if (index > 3) index = 0;
            img = imgs[kind][index];
        }

        //회전
        angle += dAngle;

        //조이패드에 따라 움직이기
        if(canMove){
            x = (int) (x + Math.cos(radian)*speed);
            y = (int) (y - Math.sin(radian)*speed);

            if(x<w) x=w;
            if(x>width-w) x=width-w;
            if(y<h) y=h;
            if(y>height-h) y=height-h;
        }



    }

}
