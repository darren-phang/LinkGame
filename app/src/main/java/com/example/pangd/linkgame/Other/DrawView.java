package com.example.pangd.linkgame.Other;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class DrawView extends View {

    private Paint paint;

    private List<Point> points = new ArrayList<>();

    private Timer timer = new Timer();


    public DrawView(Context context) {

        super(context);

    }


    public DrawView(Context context, AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);

    }


    public DrawView(Context context, AttributeSet attrs) {

        this(context, attrs, 0);

        paint = new Paint();

        paint.setColor(Color.RED);

        paint.setStrokeWidth(7);

        paint.setAntiAlias(true);
        CornerPathEffect cornerPathEffect = new CornerPathEffect(200);
        paint.setPathEffect(cornerPathEffect);

//        paint.setStyle(Paint.Style.FILL);

//        BlurMaskFilter maskFilter = new BlurMaskFilter(5, BlurMaskFilter.Blur.SOLID);
//
//        paint.setMaskFilter(maskFilter);

    }


    public void drawLine(List<Point> points) {

        timer.cancel();

        timer.purge();

        timer = new Timer();

        this.points = points;

        invalidate();

    }


    @Override

    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);


        for (int i = 0; i < points.size() - 1; i++) {

            Point p1 = points.get(i);

            Point p2 = points.get(i + 1);
            canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint);

        }

        /**

         * 500毫秒后清除线

         */

        timer.schedule(new TimerTask() {

            @Override

            public void run() {

                points = new ArrayList<>();

                invalidate();

            }

        }, 500);

    }

}
