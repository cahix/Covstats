package com.golwado.coronavirusscanner.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.golwado.coronavirusscanner.Design.Customization;
import com.golwado.coronavirusscanner.Model.Controller;
import com.golwado.coronavirusscanner.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.List;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;


public class ResultActivity extends AppCompatActivity {

    private ProgressBar circularProgressBar;
    private TextView covid19Percentage;
    private TextView fluPercentage;
    private TextView coldPercentage;
    private TextView resultDescription;
    private TextView covid19String;
    private TextView coldString;
    private TextView fluString;
    private Button shareButton;
    public static float[] result = new float[3];
    final static String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Screenshots";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_constraint_result);
        getComponents();
        getResult();
        if(Controller.getInstance().isActivityIsLoading()) {
            startProgressBarAnimation();
        } else circularProgressBar.setAlpha(0f);
        setResultValues();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setResultTextColors();
        }
        ((TextView) findViewById(R.id.result_title)).setText(Controller.getInstance().getLabel("__Result"));
        Controller.getInstance().setActivityIsLoading(false);
        setUpShareButton();
    }

    @Override
    public void onBackPressed() {
        Intent mainActivityIntent = new Intent(this , MainActivity.class);
        Controller.getInstance().setActivityIsLoading(true);
        startActivity(mainActivityIntent);
    }

    protected void getComponents(){
        circularProgressBar = findViewById(R.id.fullCircularProgressBar);
        covid19Percentage = findViewById(R.id.textViewCovidPercentage);
        fluPercentage = findViewById(R.id.textViewFluPercentage);
        coldPercentage = findViewById(R.id.textViewColdPercentage);
        resultDescription = findViewById(R.id.text_view_result_description);
        covid19String = findViewById(R.id.textViewCovidString);
        coldString = findViewById(R.id.textViewColdString);
        fluString = findViewById(R.id.textViewFluString);
        shareButton = findViewById(R.id.button_share_result);
    }

    private void getResult(){
        result = getIntent().getFloatArrayExtra("results");
    }

    private void setResultValues(){
        resultDescription.setText(Controller.getInstance().getLabel("__Result"));
        covid19String.setText(Controller.getInstance().getLabel("__Covid-19"));
        coldString.setText(Controller.getInstance().getLabel("__Cold"));
        fluString.setText(Controller.getInstance().getLabel("__Flu"));
        shareButton.setText(Controller.getInstance().getLabel("__ShareButton"));
        covid19Percentage.setText(NumberFormat.getIntegerInstance().format((int)result[0]) + "%");
        coldPercentage.setText(NumberFormat.getIntegerInstance().format((int)result[1]) + "%");
        fluPercentage.setText(NumberFormat.getIntegerInstance().format((int)result[2]) + "%");
        if     (covidIsHighest() && result[0] > 20f)resultDescription.setText(Controller.getInstance().getLabel("__ResultDescriptionCovid19"));
        else if(coldIsHighest() && result[1] > 20f)resultDescription.setText(Controller.getInstance().getLabel("__ResultDescriptionCold"));
        else if(fluIsHighest() && result[2] > 20f)resultDescription.setText(Controller.getInstance().getLabel("__ResultDescriptionFlu"));
        else                                     resultDescription.setText(Controller.getInstance().getLabel("__ResultDescriptionNothing"));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setResultTextColors(){
        float[] result = getIntent().getFloatArrayExtra("results");
        float sizeDifferenceMultiplier = 6f;
        int maxColor = 0xff6666;
        int minColor = 0x80c341;
        covid19Percentage.setTextColor(Customization.mixColor(minColor , maxColor , remap(0 , 100 , result[0])));
        coldPercentage.setTextColor(Customization.mixColor(minColor ,maxColor , remap(0 , 100 , result[1])));
        fluPercentage.setTextColor(Customization.mixColor(minColor , maxColor , remap(0 , 100 , result[2])));
    }

    private float remap(float min , float max , float x){
        return (x - min) / (max - min);
    }
    private float getValueRank(float value, float[] values){
        float rank = values.length;
        for(float i : values){
            if(i > value) rank-=1;
        }
        return rank;
    }

    private boolean covidIsHighest(){
        return(result[0] >= result[1] && result[0] >= result[2]);
    }

    private boolean coldIsHighest(){
        return(result[1] > result[0] && result[1] >= result[2]);
    }

    private boolean fluIsHighest(){
        return(result[2] > result[0] && result[2] > result[1]);
    }



    private void startProgressBarAnimation(){
        AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(2000);
        circularProgressBar.startAnimation(anim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                circularProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }



    private void setUpShareButton(){
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                circularProgressBar.setAlpha(0f);
                //checkWritingPermission();
                checkReadingPermission();
            }
        });
        shareButton.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    shareButton.setBackgroundResource(R.drawable.roundedbutton_grey);
                    shareButton.setTextColor(getApplicationContext().getResources().getColor(R.color.whiteColor));
                    if (Build.VERSION.SDK_INT >= 23) {
                        shareButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.share_icon_white, 0, 0, 0);
                    }
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    shareButton.setBackgroundResource(R.drawable.roundedbutton);
                    shareButton.setTextColor(getApplicationContext().getResources().getColor(R.color.mainColor));
                    if (Build.VERSION.SDK_INT >= 23) {
                        shareButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.share_icon, 0, 0, 0);
                    }
                }
                return false;
            }
        });
        if (Build.VERSION.SDK_INT >= 23) {
            shareButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.share_icon, 0, 0, 0);
        }

    }

    private void takeAndShareScreenshot(String fileName){
        shareButton.setAlpha(0f);
        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        Bitmap bm = getScreenShot(rootView);
        shareButton.setAlpha(1f);

        if (Build.VERSION.SDK_INT >= 29) {
            try {
                Uri imageUri = saveImageHighApi(bm, fileName);
                shareHighApiImage(imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            saveImageLowApi(bm, fileName);
            shareLowApiImage(new File(dirPath + "/" + fileName));
        }
    }

    public static Bitmap getScreenShot(View view) {
        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    //FOR API < 29
    public static void saveImageLowApi(Bitmap bm, String fileName){
        File dir = new File(dirPath);
        if(!dir.exists())
            dir.mkdirs();
        File file = new File(dirPath, fileName);
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 50, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //FOR API >= 29
    private Uri saveImageHighApi(Bitmap bm, @NonNull String name) throws IOException {
        OutputStream fos;
        ContentResolver resolver = getApplicationContext().getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/" + "Screenshots");
        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        try {
            fos = resolver.openOutputStream(imageUri);
            bm.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
        System.out.println(imageUri);
        return imageUri;
    }

    //FOR API >= 29
    private void shareHighApiImage(Uri imageUri){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_SUBJECT, "");
            intent.putExtra(Intent.EXTRA_TEXT, "My Covid-19 test result !");
            intent.putExtra(Intent.EXTRA_STREAM, imageUri);
            Intent chooser = Intent.createChooser(intent, "Share File");
            startActivity(chooser);
            onActivityResult(0 , 1 , chooser);
        }
    }

    //FOR API < 29
    private void shareLowApiImage(File file){
        //Uri uri = Uri.fromFile(file);
        Context context = ResultActivity.this;
        Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");

        intent.putExtra(Intent.EXTRA_SUBJECT, "");
        intent.putExtra(Intent.EXTRA_TEXT, "");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        // intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // try {
        //startActivity(Intent.createChooser(intent, "Share Screenshot"));
        // } catch (ActivityNotFoundException e) {
        //     Toast.makeText(getApplicationContext(), "No App Available", Toast.LENGTH_SHORT).show();
        // }
        Intent chooser = Intent.createChooser(intent, "Share File");

        List<ResolveInfo> resInfoList = this.getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);

        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            this.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | FLAG_GRANT_READ_URI_PERMISSION);
        }

        startActivity(chooser);
    }

    private void checkWritingPermission() {
        if (ContextCompat.checkSelfPermission(ResultActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            System.out.println("WRITING NOT GRANTED =====================");
            ActivityCompat.requestPermissions(ResultActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    private void checkReadingPermission() {
        if (ContextCompat.checkSelfPermission(ResultActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            System.out.println("READING NOT GRANTED =====================");
            ActivityCompat.requestPermissions(ResultActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        else {
            takeAndShareScreenshot("result.png");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takeAndShareScreenshot("result.png");
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(ResultActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }

        // other 'case' lines to check for other
        // permissions this app might request
    }








}











