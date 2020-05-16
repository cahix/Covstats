package com.golwado.coronavirusscanner.actions;

import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class SwipeHandler implements View.OnTouchListener {
    private float xDown;
    private float xUp;
    private float minimumDistance;

    public SwipeHandler(WindowManager windowManager , float sensitivity){
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        sensitivity = 1.f - sensitivity;
        minimumDistance = sensitivity * outMetrics.widthPixels;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        boolean result = false;
        switch (action){
            case MotionEvent.ACTION_DOWN : {
                xDown = xUp = event.getX();
                result = true;
            }break;
            case MotionEvent.ACTION_UP : {
                xUp = event.getX();
                result = true;
            }break;
        }
        if(result)
            handleResult();
        return result;
    }

    private void handleResult(){
        if(Math.abs(xDown - xUp) >= minimumDistance){
           System.out.println("swapped!!!");
        }
    }
}
