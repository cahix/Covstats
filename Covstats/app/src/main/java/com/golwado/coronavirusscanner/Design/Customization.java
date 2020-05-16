package com.golwado.coronavirusscanner.Design;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.golwado.coronavirusscanner.Model.Controller;
import com.golwado.coronavirusscanner.R;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class Customization {
    public static void setGreyButtonTheme(Button button, Context context){
        button.setBackgroundResource(R.drawable.roundedbutton_grey);
        button.setTextColor(ContextCompat.getColor(context, R.color.whiteColor));
    }

    public static void setBlueButtonTheme(Button button, Context context){
        button.setBackgroundResource(R.drawable.roundedbutton);
        button.setTextColor(ContextCompat.getColor(context, R.color.mainColor));
    }

    public static void setLightBlueButtonTheme(Button button, Context context){
        button.setBackgroundResource(R.drawable.roundedbutton_lightblue);
        button.setTextColor(ContextCompat.getColor(context, R.color.whiteColor));
    }

    public static void setDarkBlueButtonTheme(Button button, Context context){
        button.setBackgroundResource(R.drawable.roundedbutton_lightblue_contrast);
        button.setTextColor(ContextCompat.getColor(context, R.color.whiteColor));
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int mixColor(int firstColor , int secondColor, float interpolate){
        Color firstColorAsRGB = Color.valueOf(firstColor);
        Color secondColorAsRGB = Color.valueOf(secondColor);
        float[] firstColorHSV = new float[3];
        float[] secondColorHSV = new float[3];
        Color.RGBToHSV((int)(firstColorAsRGB.red() * 255), (int)(firstColorAsRGB.green() * 255) , (int)(firstColorAsRGB.blue() * 255) , firstColorHSV);
        Color.RGBToHSV((int)(secondColorAsRGB.red() * 255), (int)(secondColorAsRGB.green() * 255) , (int)(secondColorAsRGB.blue() * 255) , secondColorHSV);
        float[] resultColorHSV = {firstColorHSV[0] * (1-interpolate) + secondColorHSV[0] * interpolate , .5f  , 1.f};
        return Color.HSVToColor(resultColorHSV);
    }

    public static String getFormattedInteger(int value){
        NumberFormat numberFormat = NumberFormat.getIntegerInstance();
        return numberFormat.format(value);
    }

    public static String getFormattedPercentage(float value){
        DecimalFormat percentageFormat = new DecimalFormat("#.##%");
        percentageFormat.setRoundingMode(RoundingMode.DOWN);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setMaximumFractionDigits(2);
        return percentageFormat.format(value);
    }

    public static String getFormattedCountryName(String countryCode , boolean isShort){
        /*String countryName = Controller.getInstance().getRecordsByCountryCode().get(countryCode).getFirst().getCountryName();
        if(isShort) {
            if (countryName.split("_").length > 2) {
                return countryCode;
            } else {
                return countryName.replace("_", " ");
            }
        } else {
            return countryName.replace("_" , " ");
        }*/
        Locale currentLocale = new Locale(Controller.getInstance().getSystemLanguage().toString().toLowerCase());
        String result       = (new Locale("" , countryCode)).getDisplayCountry(currentLocale);
        if(result.equals("JPG11668")){
            System.out.println("Error in country : " + countryCode);
        }
        return result;
    }
    public static int[] getGraphColors(Resources resources){
        return new int[]{
                resources.getColor(R.color.navy),
                resources.getColor(R.color.dark_green),
                resources.getColor(R.color.mint),
                resources.getColor(R.color.lavender),
                resources.getColor(R.color.apricot),
        };
    }

    public static void fadeOutAndHideImage(final ImageView img)
    {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(1000);

        fadeOut.setAnimationListener(new Animation.AnimationListener()
        {
            public void onAnimationEnd(Animation animation)
            {
                img.setVisibility(View.GONE);
            }
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationStart(Animation animation) {}
        });

        img.startAnimation(fadeOut);
    }


}
