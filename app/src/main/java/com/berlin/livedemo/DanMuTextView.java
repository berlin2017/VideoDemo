package com.berlin.livedemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;

import java.util.Random;

/**
 * Created by AnHuiNews on 2017/5/2.
 */

public class DanMuTextView extends android.support.v7.widget.AppCompatTextView {

    private Paint mPaint;
    private float mWindowWidth = 0;
    private float textWidth = 0;
    private int textSize = 60;
    private int minSize = 60;
    private int maxSize = 90;
    private float mY = textSize;
    private float mX = mWindowWidth;
    private int mPadding = 30;
    private float mWindowHeight = 0;
    private int textColor = 0;
    private int lineSize = 5;
    private Thread myThread;
    private boolean isMove = true;
    private boolean isSelf = true;

    public DanMuTextView(Context context) {
        this(context, null);
    }

    public DanMuTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DanMuTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public DanMuTextView(Context context, boolean isSelf, boolean isMove) {
        this(context);
        this.isSelf = isSelf;
        this.isMove = isMove;
        initView();
    }

    private void initView() {
        mPaint = new Paint();
        Rect rect = new Rect();
        getWindowVisibleDisplayFrame(rect);
        mWindowWidth = rect.width();
        mWindowHeight = rect.height();
        mX = mWindowWidth;
        textSize = textSize + new Random().nextInt(maxSize - minSize);
        mY = textSize + mPadding + new Random().nextInt((int) (mWindowHeight - textSize - 2 * mPadding));
        textColor = Color.rgb(new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256));
        if (isMove) {
            myThread = new MyThread();
        } else {
            myThread = new MyThread2();
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setColor(textColor);
        mPaint.setTextSize(textSize);
        textWidth = mPaint.measureText(getText().toString());
        Paint linePaint = new Paint();
        linePaint.setColor(textColor);
        linePaint.setTextSize(lineSize);
        if (isSelf) {
            canvas.drawLine(mX - mPadding, mY - textSize - mPadding, mX + textWidth + mPadding, mY - textSize - mPadding, linePaint);//上横
            canvas.drawLine(mX - mPadding, mY + mPadding, mX + textWidth + mPadding, mY + mPadding, linePaint);//下横
            canvas.drawLine(mX - mPadding, mY - textSize - mPadding, mX - mPadding, mY + mPadding, linePaint);//zuoshu
            canvas.drawLine(mX + textWidth + mPadding, mY - textSize - mPadding, mX + textWidth + mPadding, mY + mPadding, linePaint);//you shu
        }
        if (!myThread.isAlive()) {
            myThread.start();
        }
        if (isMove) {
            canvas.drawText(getText().toString(), mX, mY, mPaint);
        } else {
            canvas.drawText(getText().toString(), (mX - textWidth) / 2, mY, mPaint);
        }

    }


    private class MyThread extends Thread {
        @Override
        public void run() {
            while (true) {
                mX -= 10;
                postInvalidate();
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (mX <= -textWidth) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            if ((ViewGroup) DanMuTextView.this.getParent() != null) {
                                ((ViewGroup) DanMuTextView.this.getParent()).removeView(DanMuTextView.this);
                                interrupt();
                            }
                        }
                    });
                    break;
                }
            }
        }
    }


    private class MyThread2 extends Thread {
        @Override
        public void run() {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            post(new Runnable() {
                @Override
                public void run() {
                    if ((ViewGroup) DanMuTextView.this.getParent() != null) {
                        ((ViewGroup) DanMuTextView.this.getParent()).removeView(DanMuTextView.this);
                        interrupt();
                    }
                }
            });
        }
    }
}
