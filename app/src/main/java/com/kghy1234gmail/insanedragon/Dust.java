package com.kghy1234gmail.insanedragon;

import android.graphics.Bitmap;

import java.util.Random;

public class Dust {

    Bitmap[] imgs;
    int[] x = new int[6];
    int[] y = new int[6];
    int[] r = new int[6];

    int[] dx = new int[6];
    int[] dy = new int[6];

    boolean isDead = false;
    int life = 45;

    public Dust(Bitmap[] imgDust, int ex, int ey) {

        imgs = imgDust;

        Random rnd = new Random();

        for(int i=0; i<6; i++){
            x[i] = ex;
            y[i] = ey;
            r[i] = imgs[i].getWidth()/2;

            int k = rnd.nextBoolean() ? 1 : -1;
            dx[i] = (rnd.nextInt(3) + 1) * k;

            k = rnd.nextBoolean() ? 1 : -1;
            dy[i] =  (rnd.nextInt(3) + 1) * k;
        }

    }

    void move(){
        for(int i=0; i<6; i++){
            x[i] += dx[i];
            y[i] += dy[i];
        }

        life--;
        if(life <= 0) isDead = true;
    }

}
