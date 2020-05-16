package com.golwado.coronavirusscanner.Model;

import android.os.AsyncTask;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataReader extends AsyncTask{
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private Result loadingResult = null;


    @Override
    protected Object doInBackground(Object[] objects) {
        Map<String , LinkedList<Record>> recordsByCountryCode;
        int totalCases = 0;
        int totalDeaths = 0;
        Date firstMonth = null;
        Date lastMonth = null;
        try {
            URL url = new URL("https://opendata.ecdc.europa.eu/covid19/casedistribution/json/");
            BufferedReader reader   = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            Record toBuildRecord = null;
            String countryCode = null;
            Pattern datePattern = Pattern.compile("\\d{2}/\\d{2}/\\d{4}");
            Pattern numberPattern = Pattern.compile("\\d+");
            recordsByCountryCode = new HashMap<>();
            while ((line = reader.readLine()) != null){
                if(line.contains("{"))
                    toBuildRecord = new Record();
                if(line.contains("dateRep")){
                    Matcher dateMatcher = datePattern.matcher(line);
                    if(dateMatcher.find()){
                        assert toBuildRecord != null;
                        toBuildRecord.date = new SimpleDateFormat("dd/MM/yyyy").parse(dateMatcher.group(0));
                        if(firstMonth == null)
                            firstMonth = toBuildRecord.date;
                        else
                            firstMonth = toBuildRecord.date.before(firstMonth) ? toBuildRecord.date : firstMonth;

                        if(lastMonth == null)
                            lastMonth = toBuildRecord.date;
                        else
                            lastMonth = toBuildRecord.date.after(lastMonth) ? toBuildRecord.date : lastMonth;
                    }
                }
                if(line.contains("cases")){
                    Matcher numberMatcher = numberPattern.matcher(line);
                    if (numberMatcher.find()) {
                        assert toBuildRecord != null;
                        toBuildRecord.cases = Integer.parseInt(numberMatcher.group(0));
                        totalCases += toBuildRecord.cases;
                    }
                }
                if(line.contains("deaths")){
                    Matcher numberMatcher = numberPattern.matcher(line);
                    if (numberMatcher.find()) {
                        assert toBuildRecord != null;
                        toBuildRecord.deaths = Integer.parseInt(numberMatcher.group(0));
                        totalDeaths += toBuildRecord.deaths;
                    }
                }
                if(line.contains("geoId")){
                    countryCode = line.split("\"")[3];
                    if(countryCode.equals("EL"))
                        countryCode = "GR";
                }
                if(line.contains("countryterritoryCode")){
                    String cc = line.split("\"")[3];
                    if(cc.equals("N/A"))
                        toBuildRecord = null;
                }

                if(line.contains("}") && toBuildRecord != null){
                    if(!recordsByCountryCode.containsKey(countryCode)){
                        recordsByCountryCode.put(countryCode , new LinkedList<Record>());
                    }
                    recordsByCountryCode.get(countryCode).addFirst(toBuildRecord);
                    toBuildRecord = null;
                    countryCode    = null;
                }
            }
            reader.close();
            loadingResult = new Result(totalCases , totalDeaths ,firstMonth , lastMonth, recordsByCountryCode);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    public Result getLoadingResult() {
        return loadingResult;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        this.pcs.firePropertyChange("doneLoading" , false , true);
    }


    public class Result{
        private int worldCases;
        private int worldDeaths;
        private Date firstMonth;
        private Date lastMonth;
        private Map<String , LinkedList<Record>> recordsByCountyCode;

        Result(int worldCases , int worldDeaths ,Date firstMonth , Date lastMonth, Map<String , LinkedList<Record>> recordsByCountyCode){
            this.recordsByCountyCode = recordsByCountyCode;
            this.firstMonth = firstMonth;
            this.lastMonth = lastMonth;
            this.worldCases = worldCases;
            this.worldDeaths = worldDeaths;
        }

        public int getWorldCases() {
            return worldCases;
        }

        public int getWorldDeaths() {
            return worldDeaths;
        }

        public Date getFirstMonth() {
            return firstMonth;
        }

        public Date getLastMonth() {
            return lastMonth;
        }

        public Map<String, LinkedList<Record>> getRecordsByCountyCode() {
            return recordsByCountyCode;
        }

        @Override
        public String toString() {
            return "Result{" +
                    "worldCases=" + worldCases +
                    ", worldDeaths=" + worldDeaths +
                    ", recordsByCountyName=" + recordsByCountyCode +
                    '}';
        }
    }

    public class Record {
        private Date date;
        private int cases;
        private int deaths;

        @Override
        public String toString() {
            return "Record{" +
                    "date=" + date +
                    ", cases=" + cases +
                    ", deaths=" + deaths +
                    '}';
        }

        public Date getDate() {
            return date;
        }

        public int getCases() {
            return cases;
        }

        public int getDeaths() {
            return deaths;
        }
    }
}
