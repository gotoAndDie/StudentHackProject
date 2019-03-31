package com.studenthack.project;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.Deque;

public class TouchEventView extends View {
    private Paint paint = new Paint();
    private Path path = new Path();
    TextView tw;
    private static final String TAG = "FingerDraw";
    private static final double EDGE = 0.35;
    float x,y;
    double proportionX, proportionY;
    Path joyPoint = new Path();
    Path areaPoint = new Path();
    boolean isPositive = true;


    public TouchEventView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint.setAntiAlias(true);
        paint.setStrokeWidth(6f);
        paint.setColor(Color.BLACK);
        paint.setShader(new LinearGradient(0, 0, 0, getHeight(), Color.BLACK, Color.WHITE, Shader.TileMode.MIRROR));

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        tw = ((Activity)context).findViewById(R.id.infoView);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawARGB(255, 255, 255, 255);
        //canvas.drawPath(path, paint);
        joyPoint.reset();
        joyPoint.addCircle(x,y, 5, Path.Direction.CW);
        if(proportionY >= 0.6 || proportionY <= 0.4)
            canvas.drawPath(joyPoint, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        x = event.getX();
        proportionX = x / getWidth();

        float newy = event.getY();
        double testY = newy / getHeight();

        if (action == MotionEvent.ACTION_DOWN) {
            if(testY < 0.4) // Positive part
                isPositive = true;
            else if (testY > 0.6)
                isPositive = false;
        }

        if(isPositive) {
            if (testY < 0.4) {
                y = newy;
                proportionY = testY;
            } else {
                proportionY = 0.4;
                y = (float) proportionY * getHeight();
            }
        } else {
            if (testY > 0.6) {
                y = newy;
                proportionY = testY;
            } else {
                proportionY = 0.6;
                y = (float) proportionY * getHeight();
            }
        }

        if (action == MotionEvent.ACTION_DOWN) {
            //
            path.moveTo(x,y);
        }

        if (action == MotionEvent.ACTION_MOVE) {
            //
            path.lineTo(x,y);
        }

        double left, right;

        double processX, processY;

        // Using a center of 0.5, 0.6 or 0.5, 0.4:
        if(proportionY <= 0.4) // Positive part
            processY = -(proportionY - 0.4);
        else if (proportionY >= 0.6) // Negative part
            processY = proportionY - 0.6;
        else
            processY = 0; // Ignore process Y

        processX = proportionX - 0.5;
        double magnitude = Math.sqrt(processX * processX + processY * processY);
        double divider = magnitude > EDGE ? magnitude / EDGE : 1;
        processX /= divider; processY /= divider; // Cap the magnitude at EDGE
        magnitude = magnitude > EDGE ? EDGE : magnitude;

        // Each direction is max if pointing towards them, scales down with increasing magnitude
        // of opposite if pointing away
        left = processX > 0 ? (EDGE - processX) * magnitude * (1/EDGE): magnitude;
        right = processX < 0 ? (EDGE + processX) * magnitude * (1/EDGE): magnitude;

        if(proportionY <= 0.4) { // Positive part
            left = (1 / EDGE) * left;
            right = (1 / EDGE) * right;
        } else if (proportionY >= 0.6) { // Negative part
            right = -(1 / EDGE) * left;
            left = -(1 / EDGE) * right;
        } else {
            left = 0;
            right = 0;
        }

        // Reset to center on up
        if (action == MotionEvent.ACTION_UP) {
            proportionX = 0.5; proportionY = 0.5; x = getWidth() / (float) 2; y = getHeight() / (float) 2;
            left = 0; right = 0;
        }

        if(tw != null)
            tw.setText("Left driver: " + left + " Right driver: " + right);


        invalidate();
        return true;
    }

    public void setInfoBox(TextView tw)
    {
        this.tw = tw;
    }
}