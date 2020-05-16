package com.golwado.coronavirusscanner.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.golwado.coronavirusscanner.Design.Customization;
import com.golwado.coronavirusscanner.Model.Controller;
import com.golwado.coronavirusscanner.Model.DataReader;
import com.golwado.coronavirusscanner.Model.Language;
import com.golwado.coronavirusscanner.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;



public class StatisticsActivity extends AppCompatActivity {
    float width;
    float height;
    int rowHeight;
    final int arrowPlaceHolderWidth = 60;
    static String sortedBy  = "__TotalCases";
    static boolean isDesc   = true;
    static String searched  = "";
    TableRow lastTouchedRow = null;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_constraint_statistics);
        ((TextView) findViewById(R.id.text_view_all_countries_title)).setText(Controller.getInstance().getLabel("__StatisticsButton"));

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        width = outMetrics.widthPixels;
        height = outMetrics.heightPixels;
        rowHeight = (int) (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? (height / 6) : (height/12));
        setUpCountriesTable(searched);
        final SearchView searchView = findViewById(R.id.country_search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searched = newText.replace(" " , "_");
                setUpCountriesTable(searched);
                return false;
            }
        });
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.onActionViewExpanded();
            }
        });
        findViewById(R.id.scroll_view).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    if(lastTouchedRow != null)
                        lastTouchedRow.setBackgroundResource(R.color.mainColor);
                }
                return false;
            }
        });
    }

    private TableRow makeTableRow(TableRowData tableRowData){
        TableRow row = new TableRow(this);
        row.setMinimumHeight(rowHeight);
        row.setGravity(Gravity.CENTER);

        String[] columns = {
                Customization.getFormattedCountryName(tableRowData.countryCode , true),
                Customization.getFormattedInteger(tableRowData.total_cases_number),
                Customization.getFormattedInteger(tableRowData.total_deaths_number),
                Customization.getFormattedPercentage(tableRowData.death_rate)
        };

        row.setTag(tableRowData.countryCode);
        int[] colorsMap = {Color.WHITE , getResources().getColor(R.color.yellowColor) , getResources().getColor(R.color.redColor) , getResources().getColor(R.color.orangeColor)};

        for(int i = 0; i<columns.length;i++){
            TextView column = new TextView(this);
            column.setText(columns[i]);
            column.setTextColor(colorsMap[i]);
            column.setGravity(Gravity.CENTER);
           // column.setGravity(Controller.getInstance().getSystemLanguage().equals(Language.AR) ? Gravity.CENTER : Gravity.CENTER);
            column.setLayoutParams(new TableRow.LayoutParams((int)((width-arrowPlaceHolderWidth) / columns.length), TableRow.LayoutParams.WRAP_CONTENT ));
            column.setSingleLine(false);
            row.addView(column);
        }
        ImageView arrowIcon = new ImageView(this);
        arrowIcon.setImageResource(Controller.getInstance().getSystemLanguage() == Language.AR ? R.drawable.ic_arrow_left_white : R.drawable.ic_arrow_right_white);
        //arrowIcon.setImageResource(Controller.getInstance().getSystemLanguage().equals(Language.AR.toString()) ? R.drawable.arrow_right : R.drawable.arrow_left);
        TableRow.LayoutParams arrowLayoutParams = new TableRow.LayoutParams(arrowPlaceHolderWidth , arrowPlaceHolderWidth);
        arrowLayoutParams.gravity = Gravity.CENTER;
        arrowIcon.setLayoutParams(arrowLayoutParams);
        row.addView(arrowIcon);
        return row;
    }

    private void setUpCountriesTable(String searchInput){
        String[] columns    = {"__Countries" , "__TotalCases" , "__DeathCases" , "__DeathRate"};
        TableLayout countries_table =  findViewById(R.id.table_layout_countries);
        countries_table.removeAllViews();
        //header
        TableRow header = new TableRow(this);
        header.setGravity(Gravity.CENTER);
        for(int i = 0; i < columns.length ; i++){
            TextView column = new TextView(this);
            column.setTag(columns[i]);
            column.setText(Controller.getInstance().getLabel(columns[i]));
            column.setTextColor(Color.WHITE);
            column.setTypeface(column.getTypeface(), Typeface.BOLD);
            column.setWidth((int)((width - arrowPlaceHolderWidth) /columns.length));
            column.setHeight(rowHeight);
            column.setGravity(Controller.getInstance().getSystemLanguage().equals(Language.AR) ? Gravity.CENTER : Gravity.CENTER);
            if(i > 0) column.setOnClickListener(new SortHandler());
            int right = !Controller.getInstance().getSystemLanguage().equals(Language.AR) ? (column.getTag().toString().equals(sortedBy) ?
                    (isDesc ? R.drawable.ic_arrow_down_white : R.drawable.ic_arrow_up_white) : 0) : 0;
            int left = (Controller.getInstance().getSystemLanguage().equals(Language.AR)) ? (column.getTag().toString().equals(sortedBy) ?
                    (isDesc ? R.drawable.ic_arrow_down_white : R.drawable.ic_arrow_up_white) : 0) : 0;
            column.setCompoundDrawablesWithIntrinsicBounds(left, 0,
                    right, 0);
            header.addView(column);
        }
        ImageView arrowIcon = new ImageView(this);    //EMPTY VIEW FOR SPACING
        TableRow.LayoutParams arrowLayoutParams = new TableRow.LayoutParams(arrowPlaceHolderWidth , arrowPlaceHolderWidth);
        arrowLayoutParams.gravity = Gravity.CENTER;
        arrowIcon.setLayoutParams(arrowLayoutParams);
        header.addView(arrowIcon);

        countries_table.addView(header);
        countries_table.addView(getRowSeparator());

        //body
        Map<String, LinkedList<DataReader.Record>> recordsByCountryName = Controller.getInstance().getRecordsByCountryCode();
        List<TableRowData> tableRowDataList = new ArrayList<>();
        for (Map.Entry<String,LinkedList<DataReader.Record>> country : recordsByCountryName.entrySet()){
            String countryName = Customization.getFormattedCountryName(country.getKey() , false);
            if(countryName.toLowerCase().startsWith(searchInput.toLowerCase())) {
                int total_cases_number = 0;
                int cases_today;
                int total_deaths_number = 0;
                int deaths_today;
                for (DataReader.Record record : country.getValue()) {
                    total_cases_number += record.getCases();
                    total_deaths_number += record.getDeaths();
                }
                cases_today = country.getValue().getLast().getCases();
                deaths_today = country.getValue().getLast().getDeaths();

                TableRowData tableRowData = new TableRowData(country.getKey(),  total_cases_number, cases_today, total_deaths_number, deaths_today);
                tableRowDataList.add(tableRowData);
            }
        }
        Collections.sort(tableRowDataList, new Comparator<TableRowData>() {
            @Override
            public int compare(TableRowData o1, TableRowData o2) {
                return o2.compareTo(o1);
            }
        });
        for(final TableRowData tableRowData : tableRowDataList){
            final TableRow tableRow = makeTableRow(tableRowData);
            tableRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String selectedCountry = (String) tableRow.getTag();
                    Intent intent = new Intent (StatisticsActivity.this, SingleCountryStatsActivity.class);
                    intent.putExtra("countryCode" , selectedCountry);
                    intent.putExtra("totalCasesCount", tableRowData.total_cases_number);
                    intent.putExtra("deathCasesCount", tableRowData.total_deaths_number);
                    Controller.getInstance().setAlreadyFadedOutExpansionIcons(false);
                    startActivity(intent);
                }
            });

            tableRow.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        lastTouchedRow = (TableRow) v;
                        tableRow.setBackgroundResource(R.color.lightBlue);
                    }
                    if(event.getAction() == MotionEvent.ACTION_UP){
                        lastTouchedRow = null;
                        tableRow.setBackgroundResource(R.color.mainColor);
                    }
                    return false;
                }
            });
            countries_table.addView(tableRow);
            countries_table.addView(getRowSeparator());
        }
    }

    private View getRowSeparator(){
        View borderline = new View(this);
        borderline.setBackgroundColor(getResources().getColor(R.color.grayColor));
        borderline.setLayoutParams(new TableRow.LayoutParams((int)width, 1));
        return borderline;
    }
    private class TableRowData implements Comparable<TableRowData>{
        String countryCode;
        int total_cases_number;
        int cases_today;
        int total_deaths_number;
        int deaths_today;
        float death_rate;

        public TableRowData(String countryCode , int total_cases_number, int cases_today, int total_deaths_number, int deaths_today) {
            this.countryCode = countryCode;
            this.total_cases_number = total_cases_number;
            this.cases_today = cases_today;
            this.total_deaths_number = total_deaths_number;
            this.deaths_today = deaths_today;
            this.death_rate = total_cases_number == 0 ? 0 : total_deaths_number * 1.f/ total_cases_number;
        }

        @Override
        public String toString() {
            return "Record{" +
                    "countryName=" + countryCode +
                    ", total_cases_number=" + total_cases_number +
                    ", cases_today=" + cases_today +
                    ", total_deaths_number='" + total_deaths_number + '\'' +
                    ", deaths_today='" + deaths_today + '\'' +
                    '}';
        }

        @Override
        public int compareTo(TableRowData o) {
            float objectValue = 0;
            float compareWithValue = 0;
            switch (sortedBy){
                case "__TotalCases" : {
                    objectValue = this.total_cases_number;
                    compareWithValue = o.total_cases_number;
                    break;
                }
                case "__DeathCases" : {
                    objectValue = this.total_deaths_number;
                    compareWithValue = o.total_deaths_number;
                    break;
                }
                case "__DeathRate" : {
                    objectValue = this.death_rate;
                    compareWithValue = o.death_rate;
                    break;
                }
            }
            if(objectValue > compareWithValue)
                return isDesc ? 1 : -1;
            else if (objectValue < compareWithValue)
                return isDesc ? -1 : 1;
            else return 0;
        }
    }

    private class SortHandler implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            String sortBy = (v).getTag().toString();
            if(sortBy.equals(sortedBy))
                isDesc = !isDesc;
            else{
                sortedBy = sortBy;
                isDesc = true;
            }
            setUpCountriesTable(searched);
        }
    }
}



