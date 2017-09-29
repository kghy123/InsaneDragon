package com.kghy1234gmail.insanedragon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.util.Random;

public class MainBackgroundView extends View {

    Context context;

    int width, height;

    Bitmap[] bgs = new Bitmap[2];
    int x1,x2;

    Random rnd = new Random();

    public MainBackgroundView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.context = context;

        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();

        bgs[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.back_01 + rnd.nextInt(6));
        bgs[1] = Bitmap.createScaledBitmap(bgs[0], width, height, true);
        bgs[0].recycle();
        bgs[0] = Bitmap.createScaledBitmap(bgs[1], width, height, true);

        x1 = 0;
        x2 = -width;

        handler.sendEmptyMessageDelayed(0, 16);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawBitmap(bgs[0], x1, 0, null);
        canvas.drawBitmap(bgs[1], x2, 0, null);


    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            x1++;
            x2++;

            if(x1 >= width || x2 >= 0){
                x1 = 0;
                x2 = -width;
            }

            invalidate();
            sendEmptyMessageDelayed(0, 16);
        }
    };

}
