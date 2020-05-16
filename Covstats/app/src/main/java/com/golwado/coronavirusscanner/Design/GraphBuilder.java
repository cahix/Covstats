package com.golwado.coronavirusscanner.Design;

import android.graphics.drawable.Drawable;

import com.golwado.coronavirusscanner.Model.Controller;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class GraphBuilder {
    private int legendTextColor;
    private Drawable backgroundShape;
    private float lineWidth;
    public GraphBuilder(int legendTextColor , Drawable backgroundShape , float lineWidth){
        this.backgroundShape = backgroundShape;
        this.legendTextColor = legendTextColor;
        this.lineWidth = lineWidth;
    }
    public void initGraph(LineChart graphChart , int[] linesColors, GraphEntriesInitializer initializer){
        //LAYOUT
        graphChart.setTouchEnabled(false);
        graphChart.setBackground(backgroundShape);
        graphChart.getLegend().setTextColor(legendTextColor);
        graphChart.setDrawGridBackground(false);
        graphChart.setDescription(null);
        graphChart.animateX(1500);

        //X Axis
        XAxis xAxis = graphChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(legendTextColor);
        xAxis.setDrawAxisLine(false);
        XAxisFormatter xAxisFormatter = new XAxisFormatter();
        xAxis.setLabelCount(5 , true);
        xAxis.setValueFormatter(new XAxisFormatter());
        xAxis.setLabelRotationAngle(-45);

        YAxis yaxisLeft = graphChart.getAxisLeft();
        graphChart.getAxisRight().setEnabled(false);
        yaxisLeft.setDrawGridLines(true);
        yaxisLeft.setTextColor(legendTextColor);
        yaxisLeft.setDrawLabels(true);
        yaxisLeft.setDrawAxisLine(false);

        Map<String,ArrayList<Entry>> dataSetsByLegendNames = initializer.loadDataIntoArrayList();
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        int index = 0;
        for(Map.Entry<String,ArrayList<Entry>> dataSetByLegendName : dataSetsByLegendNames.entrySet()) {
            LineDataSet lineDataSet;
            lineDataSet = new LineDataSet(dataSetByLegendName.getValue(), dataSetByLegendName.getKey());
            lineDataSet.setDrawIcons(false);
            lineDataSet.setColor(linesColors[index]);
            lineDataSet.setDrawCircles(false);
            lineDataSet.setDrawValues(false);
            lineDataSet.setDrawFilled(false);
            lineDataSet.setMode(LineDataSet.Mode.LINEAR);
            lineDataSet.setLineWidth(lineWidth);
            dataSets.add(lineDataSet);
            index++;
        }
        LineData data = new LineData(dataSets);
        graphChart.setData(data);
    }

    private class XAxisFormatter implements IAxisValueFormatter {
        private Date firstDate;
        private Date lastDate;

        private XAxisFormatter(){
            firstDate = Controller.getInstance().getFirstDate();
            lastDate = Controller.getInstance().getLastDate();
        }

        private int getRequiredNumberOfLabels(){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(firstDate);
            int firstMonth = calendar.get(Calendar.MONTH);
            calendar.setTime(lastDate);
            int lastMonth = calendar.get(Calendar.MONTH);
            int diff;
            if(firstMonth >= lastMonth)
                diff = lastMonth - (firstMonth - 12);
            else
                diff = lastMonth - firstMonth;
            return diff;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(firstDate);
            calendar.add(Calendar.DATE , (int)value);
            int month = calendar.get(Calendar.MONTH);
            int year  = calendar.get(Calendar.YEAR);
            return (new DateFormatSymbols().getMonths()[month]).substring(0,3) + "/" + (""+year).substring(2);
        }
    }

    public interface GraphEntriesInitializer {
        Map<String,ArrayList<Entry>> loadDataIntoArrayList();
    }
}
