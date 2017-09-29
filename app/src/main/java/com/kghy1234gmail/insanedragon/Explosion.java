package com.kghy1234gmail.insanedragon;

import android.graphics.Bitmap;

public class Explosion {

    Bitmap[] imgExplosions;
    int x, y;
    int w, h;
    Bitmap imgExplosion;
    int loop;
    int index = 0;

    int lifeTime = 130;
    boolean isDead = false;

    public Explosion(Bitmap[] imgExplosions, int width, int height) {

        this.imgExplosions = imgExplosions;
        imgExplosion = imgExplosions[index];
        x = width/2;
        y = height/2;
        w = imgExplosion.getWidth()/2;
        h = imgExplosion.getHeight()/2;

    }

    void move(){
        lifeTime--;

        if(lifeTime<=0) {
            isDead = true;
            return;
        }

        loop++;
        if(loop%10 == 0){
            index++;
            if(index>11) return;
            imgExplosion = imgExplosions[index];
        }


    }
}
