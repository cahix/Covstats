package com.golwado.coronavirusscanner.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.golwado.coronavirusscanner.Model.Controller;
import com.golwado.coronavirusscanner.R;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        ((TextView) findViewById(R.id.text_view_sources)).setText(Controller.getInstance().getLabel("__Sources"));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(InfoActivity.this, MainActivity.class);
        startActivity(intent);
    }

}
