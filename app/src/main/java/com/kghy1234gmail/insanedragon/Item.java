package com.kghy1234gmail.insanedragon;

import android.graphics.Bitmap;

import java.util.Random;

public class Item {

    public static final int COIN = 0;
    public static final int GEM = 1;
    public static final int FAST = 2;
    public static final int PROTECT = 3;
    public static final int MAGNET = 4;
    public static final int BOMB = 5;
    public static final int STRONG = 6;

    int width, height;

    Bitmap img;
    int x, y;
    int w, h;

    boolean isDead = false;

    int dx, dy;
    int kind;

    int lifeTime = 45 * 8;  //아이템 생성 유지 시간

    public Item(int width, int height, Bitmap[] imgItem, int ex, int ey) {

        this.width = width;
        this.height = height;

        x = ex;
        y = ey;

        // kind = 0 : coin , 1 : gem, 2 : fast , 3 : protect , 4 : magnet , 5 : bomb , 6 : strong missile
        //          70%         1%      8%         5%              8%         3%          5%
        Random rnd = new Random();

        int n = rnd.nextInt(100);
        kind = n < 70 ? 0 : n < 71 ? 1 : n < 79 ? 2 : n < 84 ? 3 : n < 92 ? 4 : n <95 ? 5 : 6;

        img = imgItem[kind];
        w = img.getWidth()/2;
        h = img.getHeight()/2;

        int k;
        k = rnd.nextBoolean() ? 1 : -1;
        dx = (w/6) * k;

        k = rnd.nextBoolean() ? 1 : -1;
        dy = (w/6) * k;
    }

    void move(int px, int py){
        double radian = Math.atan2(y - py, px - x);
        x = (int)(x + Math.cos(radian) * (w /2));
        y = (int)(y - Math.sin(radian) * (w /2));
    }

    void move(){

        x += dx;
        y += dy;

        lifeTime--;

        if(lifeTime>0) {

            if (x <= w) {
                dx = -dx;
                x = w;
            }

            if (x >= width - w) {
                dx = -dx;
                x = width - w;
            }

            if (y <= h) {
                dy = -dy;
                y = h;
            }

            if (y >= height - h) {
                dy = -dy;
                y = height - h;
            }


        }else if(x<-w||x>width+w || y<-h || y>height+h) isDead = true;

    }

}
