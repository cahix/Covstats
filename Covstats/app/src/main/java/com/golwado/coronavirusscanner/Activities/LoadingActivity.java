package com.golwado.coronavirusscanner.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.golwado.coronavirusscanner.Model.Controller;
import com.golwado.coronavirusscanner.R;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class LoadingActivity extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        Controller.getInstance().loadData();
        imageView = findViewById(R.id.loading_icon);
        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(imageView,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                PropertyValuesHolder.ofFloat("scaleY", 1.2f));
        scaleDown.setDuration(2000);

        scaleDown.setRepeatCount(ObjectAnimator.INFINITE);
        scaleDown.setRepeatMode(ObjectAnimator.REVERSE);

        scaleDown.start();

        initWindowSize();
        Controller.getInstance().addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                doneLoading();
            }
        });
    }

    private void doneLoading(){
        //imageView.clearAnimation();
        Intent intent = new Intent(this , MainActivity.class);
        startActivity(intent);
    }

    private void initWindowSize(){
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        Controller.getInstance().setWindowWidth(outMetrics.widthPixels);
        Controller.getInstance().setWindowHeight(outMetrics.heightPixels);
    }
}
