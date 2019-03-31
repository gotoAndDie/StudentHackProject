package com.studenthack.project;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class FingerDraw extends View {
    private Path path;
    private Paint paint;
    TextView tw;
    private static final String TAG = "FingerDraw";


    public FingerDraw(Context context) {
        super(context);
        tw = findViewById(R.id.info);
    }

    public FingerDraw(Context context, AttributeSet attrs) {
        super(context, attrs);
        path = new Path();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(6f);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (isInEditMode()) {
            canvas.drawARGB(120, 255, 0, 0);
        }
        canvas.drawCircle(100, 100, 50, paint);
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x,y);
                break;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(x,y);
                break;
            case MotionEvent.ACTION_UP:
                break;

        }
        tw.setText("Coordinates: " + x + ", " + y);
        Log.i(TAG, "Coordinates: " + x + ", " + y);
        invalidate();
        return true;
    }
}
