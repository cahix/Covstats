package com.golwado.coronavirusscanner.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.golwado.coronavirusscanner.Design.Customization;
import com.golwado.coronavirusscanner.Design.GraphBuilder;
import com.golwado.coronavirusscanner.Model.Controller;
import com.golwado.coronavirusscanner.Model.DataReader;
import com.golwado.coronavirusscanner.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SingleCountryStatsActivity extends AppCompatActivity {

    private String countryCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_country_stats);
        this.countryCode = getIntent().getStringExtra("countryCode");
        initComponents();
        initExpandIcons();
    }

    private void initExpandIcons(){
            final ImageView expandIcon1 = findViewById(R.id.image_view_expand_1);
            final ImageView expandIcon2 = findViewById(R.id.image_view_expand_2);
            if(Controller.getInstance().isAlreadyFadedOutExpansionIcons()){
                expandIcon1.setVisibility(View.GONE);
                expandIcon2.setVisibility(View.GONE);
            }
            findViewById(R.id.scroll_view_single_country).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        if(!Controller.getInstance().isAlreadyFadedOutExpansionIcons()) {
                            Customization.fadeOutAndHideImage(expandIcon1);
                            Customization.fadeOutAndHideImage(expandIcon2);
                            Controller.getInstance().setAlreadyFadedOutExpansionIcons(true);
                            }
                    }
                    return false;
                }
            });
    }

    private void initComponents(){
        int totalCasesCount     = getIntent().getIntExtra("totalCasesCount" , 0);
        int totalDeathCount     = getIntent().getIntExtra("deathCasesCount" , 0);
        int casesToday          = Controller.getInstance().getRecordsByCountryCode().get(countryCode).getLast().getCases();
        int deathToday          = Controller.getInstance().getRecordsByCountryCode().get(countryCode).getLast().getDeaths();
        float deathRate         = totalDeathCount * 1.f / totalCasesCount;
        ((TextView) findViewById(R.id.text_view_country_name)).setText(Customization.getFormattedCountryName(countryCode , false));
        ((TextView) findViewById(R.id.text_corona_total_cases_value)).setText(Customization.getFormattedInteger(totalCasesCount));
        ((TextView) findViewById(R.id.text_death_cases_value)).setText(Customization.getFormattedInteger(totalDeathCount));
        ((TextView) findViewById(R.id.text_death_rate_value)).setText(Customization.getFormattedPercentage(deathRate));
        ((TextView) findViewById(R.id.text_world_death_cases_today_value)).setText(Customization.getFormattedInteger(deathToday));
        ((TextView) findViewById(R.id.text_total_cases_today_value)).setText(Customization.getFormattedInteger(casesToday));
        ((TextView) findViewById(R.id.text_view_total_cases_sc)).setText(Controller.getInstance().getLabel("__TotalCases"));
        ((TextView) findViewById(R.id.text_view_total_deaths_sc)).setText(Controller.getInstance().getLabel("__DeathCases"));
        ((TextView) findViewById(R.id.text_view_death_rate_sc)).setText(Controller.getInstance().getLabel("__DeathRate"));
        ((TextView) findViewById(R.id.text_view_cases_today_sc)).setText(Controller.getInstance().getLabel("__CasesToday"));
        ((TextView) findViewById(R.id.text_view_deaths_today_sc)).setText(Controller.getInstance().getLabel("__DeathCasesToday"));
        ((TextView) findViewById(R.id.chart_total_cases_label)).setText(Controller.getInstance().getLabel("__TotalCasesOverTime"));
        ((TextView) findViewById(R.id.chart_total_deaths_label)).setText(Controller.getInstance().getLabel("__TotalDeathsOverTime"));
        ((TextView) findViewById(R.id.chart_death_rate_label)).setText(Controller.getInstance().getLabel("__DeathRateOverTime"));
        GraphBuilder graphBuilder = new GraphBuilder(getResources().getColor(R.color.whiteColor) , getResources().getDrawable(R.drawable.background_rounded_corners) , 4f);
        graphBuilder.initGraph(
                (LineChart) findViewById(R.id.chart_total_cases)
                ,   new int[]{getResources().getColor(R.color.yellowColor)}
                ,   new GraphBuilder.GraphEntriesInitializer() {
                    @Override
                    public Map<String,ArrayList<Entry>> loadDataIntoArrayList() {
                        Map<String , ArrayList<Entry>> dataSet = new HashMap<>();
                        ArrayList<Entry> entries = new ArrayList<>();
                        int recordsCounter = 0;
                        int casesCounter = 0;
                        for(DataReader.Record record : Controller.getInstance().getRecordsByCountryCode().get(countryCode)){
                            casesCounter += record.getCases();
                            entries.add(new Entry(recordsCounter++, casesCounter));
                        }
                        dataSet.put(Controller.getInstance().getLabel("__TotalCases") , entries);
                        return dataSet;
                    }});

        graphBuilder.initGraph(
                (LineChart) findViewById(R.id.chart_total_deaths)
                , new int[]{getResources().getColor(R.color.redColor)}
                , new GraphBuilder.GraphEntriesInitializer() {
                    @Override
                    public Map<String,ArrayList<Entry>> loadDataIntoArrayList() {
                        Map<String , ArrayList<Entry>> dataSet = new HashMap<>();
                        ArrayList<Entry> entries = new ArrayList<>();
                        int recordsCounter = 0;
                        int casesCounter = 0;
                        for(DataReader.Record record : Controller.getInstance().getRecordsByCountryCode().get(countryCode)){
                            casesCounter += record.getDeaths();
                            entries.add(new Entry(recordsCounter++, casesCounter));
                        }
                        dataSet.put(Controller.getInstance().getLabel("__DeathCases") , entries);
                        return dataSet;
                    }});

        graphBuilder.initGraph(
                (LineChart) findViewById(R.id.chart_death_rate)
                , new int[]{getResources().getColor(R.color.orangeColor)}
                , new GraphBuilder.GraphEntriesInitializer() {
                    @Override
                    public Map<String,ArrayList<Entry>> loadDataIntoArrayList() {
                        Map<String , ArrayList<Entry>> dataSet = new HashMap<>();
                        ArrayList<Entry> entries = new ArrayList<>();
                        int recordsCounter = 0;
                        float casesCounter = 0;
                        float deathCounter = 0;
                        for(DataReader.Record record : Controller.getInstance().getRecordsByCountryCode().get(countryCode)){
                            casesCounter += record.getCases();
                            deathCounter += record.getDeaths();
                            float deathRateOverTime = casesCounter == 0 ? 0 : deathCounter / casesCounter;
                            entries.add(new Entry(recordsCounter++, deathRateOverTime));
                        }
                        dataSet.put(Controller.getInstance().getLabel("__DeathRate"), entries);
                        return dataSet;
                    }
                });
    }
}