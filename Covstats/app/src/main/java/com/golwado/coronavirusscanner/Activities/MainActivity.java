package com.golwado.coronavirusscanner.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.golwado.coronavirusscanner.Design.Customization;
import com.golwado.coronavirusscanner.Design.GraphBuilder;
import com.golwado.coronavirusscanner.Model.Controller;
import com.golwado.coronavirusscanner.Model.DataReader;
import com.golwado.coronavirusscanner.R;
import com.golwado.coronavirusscanner.actions.ButtonClickHandler;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static MainActivity instance = null;
    public static MainActivity getInstance(){
        return instance;
    }
    private Button startButton;
    private Button statisticsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;
        addStats();
        addTranslatedTexts();
        addListeners();
        Controller.getInstance().setActivityIsLoading(false);
    }

    private void addTranslatedTexts(){
        ((Button) findViewById(R.id.button_start)).setText(Controller.getInstance().getLabel("__StartTest"));
        ((TextView) findViewById(R.id.text_death_rate_label)).setText(Controller.getInstance().getLabel("__DeathRate"));
        ((TextView) findViewById(R.id.text_death_cases)).setText(Controller.getInstance().getLabel("__DeathCases"));
        ((TextView) findViewById(R.id.text_total_cases)).setText(Controller.getInstance().getLabel("__TotalCases"));
        ((TextView) findViewById(R.id.text_view_worldwide_statistics)).setText(Controller.getInstance().getLabel("__World_statistics"));
        ((Button)   findViewById(R.id.button_show_statistics)).setText(Controller.getInstance().getLabel("__StatisticsButton"));
        ((TextView) findViewById(R.id.text_view_cases_today)).setText(Controller.getInstance().getLabel("__CasesToday"));
        ((TextView) findViewById(R.id.text_world_death_cases_today_label)).setText(Controller.getInstance().getLabel("__DeathCasesToday"));
        ((TextView) findViewById(R.id.text_view_total_cases_by_country)).setText(Controller.getInstance().getLabel("__TotalCasesByCountry"));
    }


    @SuppressLint("ClickableViewAccessibility")
    private void addListeners(){
        ButtonClickHandler bch = new ButtonClickHandler(ButtonClickHandler.ACTION_TYPE.onTouchFeedBack);
        statisticsButton = findViewById(R.id.button_show_statistics);
        statisticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (MainActivity.this, StatisticsActivity.class);
                Controller.getInstance().setAlreadyFadedOutExpansionIcons(false);
                startActivity(intent);
            }
        });
        bch.setOnTouchFeedBackBlueTheme(statisticsButton,this);

        startButton = findViewById(R.id.button_start);
        startButton.setOnClickListener(new ButtonClickHandler(ButtonClickHandler.ACTION_TYPE.Test_Start));
        bch.setOnTouchFeedBackBlueTheme(startButton,this);

        Button infoButton = findViewById(R.id.button_info);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,InfoActivity.class);
                startActivity(intent);
            }
        });
        Button infobuttom = findViewById(R.id.button_info);
        infobuttom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (MainActivity.this,InfoActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.scroll_pane).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_MOVE){
                    Customization.setLightBlueButtonTheme(statisticsButton , getApplicationContext());
                }
                return false;
            }
        });

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void addStats(){
        ((TextView)findViewById(R.id.text_corona_total_cases)).setText(Customization.getFormattedInteger(Controller.getInstance().getCoronaCasesTotalCount()));
        ((TextView)findViewById(R.id.text_death_total_cases)).setText(Customization.getFormattedInteger(Controller.getInstance().getDeathCasesCount()));
        ((TextView)findViewById(R.id.text_death_rate_value)).setText(Customization.getFormattedPercentage(Controller.getInstance().getGlobalDeathRate()));
        ((TextView)findViewById(R.id.text_corona_total_cases_today_value)).setText(Customization.getFormattedInteger(Controller.getInstance().getCasesToday()));
        ((TextView)findViewById(R.id.text_world_death_cases_today_value)).setText(Customization.getFormattedInteger(Controller.getInstance().getDeathCasesToday()));
        GraphBuilder graphBuilder = new GraphBuilder(Color.WHITE , getResources().getDrawable(R.drawable.background_rounded_corners) , 2f);
        graphBuilder.initGraph((LineChart) findViewById(R.id.chart_total_cases_comparison)

                , Customization.getGraphColors(getResources())
                , new GraphBuilder.GraphEntriesInitializer() {
                    @Override
                    public Map<String, ArrayList<Entry>> loadDataIntoArrayList() {
                        Map<String , ArrayList<Entry>> dataSets = new HashMap<>();
                        String[] relevantCountries = {"DE" , "IT" , "FR", "US", "ES"};
                        for(String countryName : relevantCountries){
                            ArrayList<Entry> countriesDataSet = new ArrayList<>();
                            int totalCases  = 0;
                            int counter     = 0;
                            for(DataReader.Record record : Controller.getInstance().getRecordsByCountryCode().get(countryName)){
                                totalCases += record.getCases();
                                countriesDataSet.add(new Entry(counter++, totalCases));
                            }
                            dataSets.put(Customization.getFormattedCountryName(countryName , true) ,countriesDataSet);
                        }
                        return dataSets;}
                });
    }

}
