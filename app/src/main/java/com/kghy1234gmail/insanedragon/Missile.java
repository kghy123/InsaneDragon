package com.kghy1234gmail.insanedragon;

import android.graphics.Bitmap;
import android.graphics.RadialGradient;

public class Missile {

    int width, height;

    Bitmap img;
    int x, y;
    int w, h;

    boolean isDead = false;

    double radian;  //이동 각도
    int speed;      //이동 속도
    int angle;      //회전 각도

    int kind;       //미사일 종류 RED : 0 , PURPLE : 1, BLACK : 2

    public Missile(int width, int height, Bitmap[] imgMissile, int px, int py, int pAngle, int pKind){

        this.width = width;
        this.height = height;
        x = px;
        y = py;
        kind = pKind;

        img = imgMissile[kind];
        w = img.getWidth()/2;
        h = img.getHeight()/2;

        speed = w/4;

        angle = pAngle;

        radian = Math.toRadians(270 - angle);

    }

    void move(){
        x = (int) (x + Math.cos(radian) * speed);
        y = (int) (y - Math.sin(radian) * speed);

        if(x < - w || x > width + w || y < -h || y > height + h) isDead = true;

    }

}
