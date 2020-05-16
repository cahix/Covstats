package com.golwado.coronavirusscanner.Model;

import android.graphics.Point;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;

public class Controller {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private static Controller instance = null;
    private Language systemLanguage = Language.EN; //default english
    private final ArrayList<Symptom> symptoms;
    private final Map<String,Label> labelsByAPIName;
    private int coronaCasesTotalCount;
    private int deathCasesCount;
    private int deathCasesToday;
    private int casesToday;
    private int windowWidth;
    private int windowHeight;
    private float globalDeathRate;
    private Date firstDate;
    private Date lastDate;
    private Map<String, LinkedList<DataReader.Record>> recordsByCountryCode = null;
    private boolean activityIsLoading = true;
    private Quiz quiz;
    private int questionCounter;
    private int questionMaxCount;
    private boolean alreadyFadedOutExpansionIcons = false;

    private Controller(){
        setSystemLanguage();
        //Labels
        this.labelsByAPIName = new HashMap<>();
        initLabels();
        //Symptoms
        this.symptoms = new ArrayList<>();
        initSymptoms();
    }

    public static Controller getInstance(){
        if(instance == null){
            instance = new Controller();
        }
        return instance;
    }

    public void loadData(){
        final DataReader dataReader =  new DataReader();
        dataReader.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                recordsByCountryCode = dataReader.getLoadingResult().getRecordsByCountyCode();
                firstDate               = dataReader.getLoadingResult().getFirstMonth();
                lastDate                = dataReader.getLoadingResult().getLastMonth();

                //world stats
                coronaCasesTotalCount   = dataReader.getLoadingResult().getWorldCases();
                deathCasesCount         = dataReader.getLoadingResult().getWorldDeaths();
                globalDeathRate         = (dataReader.getLoadingResult().getWorldDeaths() * 1.0f / dataReader.getLoadingResult().getWorldCases());
                deathCasesToday         = 0;
                casesToday              = 0;
                for(LinkedList<DataReader.Record> records : recordsByCountryCode.values()){
                    deathCasesToday     += records.getLast().getDeaths();
                    casesToday          += records.getLast().getCases();
                }

                pcs.firePropertyChange("doneLoading" , false ,true);
            }
        });
        dataReader.execute();
    }

    public Quiz getQuiz(){
        if(quiz == null){
            quiz = new Quiz();
        }
        return quiz;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    public int getCasesToday() {
        return casesToday;
    }

    public int getDeathCasesToday() {
        return deathCasesToday;
    }

    public Date getFirstDate() {
        return firstDate;
    }

    public Date getLastDate() {
        return lastDate;
    }

    public int getCoronaCasesTotalCount() {
        return coronaCasesTotalCount;
    }

    public int getDeathCasesCount() {
        return deathCasesCount;
    }

    public float getGlobalDeathRate() {
        return globalDeathRate;
    }

    public Map<String, LinkedList<DataReader.Record>> getRecordsByCountryCode() {
        return recordsByCountryCode;
    }

    private void setSystemLanguage(){
        String language = Locale.getDefault().getLanguage();
        switch (language){
            case "de" : {
                systemLanguage = Language.DE;
                break;
            }
            case "ru" : {
                systemLanguage = Language.RU;
                break;
            }
            case "ar" : {
                systemLanguage = Language.AR;
                break;
            }
            default:{
                systemLanguage = Language.EN;
                break;
            }
        }
    }
    private void initLabels(){
        labelsByAPIName.put("__FeverQuestion"                        , new Label("__FeverQuestion"           ));
        labelsByAPIName.put("__DryCough"                             , new Label("__DryCough"                ));
        labelsByAPIName.put("__BreathShortness"                      , new Label("__BreathShortness"         ));
        labelsByAPIName.put("__RunnyNose"                            , new Label("__RunnyNose"               ));
        labelsByAPIName.put("__SoreThroat"                           , new Label("__SoreThroat"              ));
        labelsByAPIName.put("__Headache"                             , new Label("__Headache"                ));
        labelsByAPIName.put("__LimbPain"                             , new Label("__LimbPain"                ));
        labelsByAPIName.put("__LostOfTaste"                          , new Label("__LostOfTaste"             ));
        labelsByAPIName.put("__Diarrhea"                             , new Label("__Diarrhea"                ));
        labelsByAPIName.put("__FeelingWeak"                          , new Label("__FeelingWeak"             ));
        labelsByAPIName.put("__SevereCourse"                         , new Label("__SevereCourse"            ));
        labelsByAPIName.put("__StartTest"                            , new Label("__StartTest"               ));
        labelsByAPIName.put("__YesButton"                            , new Label("__YesButton"               ));
        labelsByAPIName.put("__NoButton"                             , new Label("__NoButton"                ));
        labelsByAPIName.put("__ConfirmButton"                        , new Label("__ConfirmButton"           ));
        labelsByAPIName.put("__TotalCasesNumber"                     , new Label("__TotalCasesNumber"        ));
        labelsByAPIName.put("__DeathCasesNumber"                     , new Label("__DeathCasesNumber"        ));
        labelsByAPIName.put("__RecoveredCasesNumber"                 , new Label("__RecoveredCasesNumber"    ));
        labelsByAPIName.put("__Result"                               , new Label("__Result"                  ));
        labelsByAPIName.put("__ResultDescriptionCovid19"             , new Label("__ResultDescriptionCovid19"));
        labelsByAPIName.put("__ResultDescriptionCold"                , new Label("__ResultDescriptionCold"   ));
        labelsByAPIName.put("__ResultDescriptionFlu"                 , new Label("__ResultDescriptionFlu"    ));
        labelsByAPIName.put("__ResultDescriptionNothing"             , new Label("__ResultDescriptionNothing"));
        labelsByAPIName.put("__Covid-19"                             , new Label("__Covid-19"                ));
        labelsByAPIName.put("__Cold"                                 , new Label("__Cold"                    ));
        labelsByAPIName.put("__Flu"                                  , new Label("__Flu"                     ));
        labelsByAPIName.put("__World_statistics"                     , new Label("__World_statistics"        ));
        labelsByAPIName.put("__ShareButton"                          , new Label("__ShareButton"             ));
        labelsByAPIName.put("__StatisticsButton"                     , new Label("__StatisticsButton"        ));
        labelsByAPIName.put("__Countries"                            , new Label("__Countries"               ));
        labelsByAPIName.put("__TotalCases"                           , new Label("__TotalCases"              ));
        labelsByAPIName.put("__DeathCases"                           , new Label("__DeathCases"              ));
        labelsByAPIName.put("__DeathRate"                            , new Label("__DeathRate"               ));
        labelsByAPIName.put("__CasesToday"                           , new Label("__CasesToday"              ));
        labelsByAPIName.put("__DeathCasesToday"                      , new Label("__DeathCasesToday"         ));
        labelsByAPIName.put("__TotalCasesByCountry"                  , new Label("__TotalCasesByCountry"     ));
        labelsByAPIName.put("__TotalCasesOverTime"                   , new Label("__TotalCasesOverTime"      ));
        labelsByAPIName.put("__TotalDeathsOverTime"                  , new Label("__TotalDeathsOverTime"     ));
        labelsByAPIName.put("__DeathRateOverTime"                    , new Label("__DeathRateOverTime"       ));
        labelsByAPIName.put("__Sources"                              , new Label("__Sources"                 ));

        initTranslations();
    }

    private void initTranslations(){
        switch (systemLanguage){
            case EN:{
                labelsByAPIName.get("__FeverQuestion")               .addTranslation(Language.EN , "Do you suffer from fever?");
                labelsByAPIName.get("__DryCough")                    .addTranslation(Language.EN , "Do you have a DRY cough?");
                labelsByAPIName.get("__BreathShortness")             .addTranslation(Language.EN , "Do you have shortness of breath?");
                labelsByAPIName.get("__RunnyNose")                   .addTranslation(Language.EN , "Is your nose running?");
                labelsByAPIName.get("__SoreThroat")                  .addTranslation(Language.EN , "Do you have a sore throat?");
                labelsByAPIName.get("__Headache")                    .addTranslation(Language.EN , "Do you have a headache?");
                labelsByAPIName.get("__LimbPain")                    .addTranslation(Language.EN , "Does your body ache?");
                labelsByAPIName.get("__LostOfTaste")                 .addTranslation(Language.EN , "Do you suffer from loss of taste?");
                labelsByAPIName.get("__Diarrhea")                    .addTranslation(Language.EN , "Do you have diarrhea?");
                labelsByAPIName.get("__FeelingWeak")                 .addTranslation(Language.EN , "Do you feel generally weak?");
                labelsByAPIName.get("__SevereCourse")                .addTranslation(Language.EN , "Are the symptoms getting worse over time?");
                labelsByAPIName.get("__StartTest")                   .addTranslation(Language.EN , "Start Test");
                labelsByAPIName.get("__YesButton")                   .addTranslation(Language.EN , "Yes");
                labelsByAPIName.get("__NoButton")                    .addTranslation(Language.EN , "No");
                labelsByAPIName.get("__ConfirmButton")               .addTranslation(Language.EN , "Continue");
                labelsByAPIName.get("__TotalCasesNumber")            .addTranslation(Language.EN , "Coronavirus Cases");
                labelsByAPIName.get("__DeathCasesNumber")            .addTranslation(Language.EN , "Deaths");
                labelsByAPIName.get("__RecoveredCasesNumber")        .addTranslation(Language.EN , "Recovered");
                labelsByAPIName.get("__Result")                      .addTranslation(Language.EN , "Result");
                labelsByAPIName.get("__ResultDescriptionCovid19")    .addTranslation(Language.EN , "Your symptoms are most likely to be connected to Covid-19. Please stay at home and keep distance to other people. If the symptoms get critical call a doctor. Keep in mind that this is not a medical test and only weights the symptoms for each illness.");
                labelsByAPIName.get("__ResultDescriptionCold")       .addTranslation(Language.EN , "Your symptoms are most likely to be connected to a Cold. There is still a chance that you have the Covid-19 Virus, so please act according to the official safety instructions. Keep in mind that this is not a medical test and only weights the symptoms for each illness.");
                labelsByAPIName.get("__ResultDescriptionFlu")        .addTranslation(Language.EN , "Your symptoms are most likely to be connected to the Flu. There is still a chance that you have the Covid-19 Virus, so please act according to the official safety instructions. Keep in mind that this is not a medical test and only weights the symptoms for each illness.");
                labelsByAPIName.get("__ResultDescriptionNothing")    .addTranslation(Language.EN , "You are probably not sick at all.");
                labelsByAPIName.get("__Covid-19")                    .addTranslation(Language.EN , "Covid-19");
                labelsByAPIName.get("__Cold")                        .addTranslation(Language.EN , "Cold");
                labelsByAPIName.get("__Flu")                         .addTranslation(Language.EN , "Flu");
                labelsByAPIName.get("__World_statistics")            .addTranslation(Language.EN , "World statistics");
                labelsByAPIName.get("__ShareButton")                 .addTranslation(Language.EN , "Share!");
                labelsByAPIName.get("__StatisticsButton")            .addTranslation(Language.EN , "All Countries");
                labelsByAPIName.get("__Countries")                   .addTranslation(Language.EN , "Countries");
                labelsByAPIName.get("__TotalCases")                  .addTranslation(Language.EN , "Total Cases");
                labelsByAPIName.get("__DeathCases")                  .addTranslation(Language.EN , "Death Cases");
                labelsByAPIName.get("__DeathRate")                   .addTranslation(Language.EN , "Death Rate");
                labelsByAPIName.get("__CasesToday")                  .addTranslation(Language.EN , "Cases Today");
                labelsByAPIName.get("__DeathCasesToday")             .addTranslation(Language.EN , "Deaths Today");
                labelsByAPIName.get("__TotalCasesByCountry")         .addTranslation(Language.EN , "Total Cases By Country");
                labelsByAPIName.get("__TotalCasesOverTime")          .addTranslation(Language.EN , "Total Cases By Date");
                labelsByAPIName.get("__TotalDeathsOverTime")         .addTranslation(Language.EN , "Death Cases By Date");
                labelsByAPIName.get("__DeathRateOverTime")           .addTranslation(Language.EN , "Death Rate By Date");
                labelsByAPIName.get("__Sources")                     .addTranslation(Language.EN , "Sources");
            }
            case DE:{
                labelsByAPIName.get("__FeverQuestion")              .addTranslation(Language.DE , "Leiden Sie unter Fieber?");
                labelsByAPIName.get("__DryCough")                   .addTranslation(Language.DE , "Leiden Sie unter trockenem Husten?");
                labelsByAPIName.get("__BreathShortness")            .addTranslation(Language.DE , "Leiden Sie unter Atemnot?");
                labelsByAPIName.get("__RunnyNose")                  .addTranslation(Language.DE , "Haben Sie Schnupfen ?");
                labelsByAPIName.get("__SoreThroat")                 .addTranslation(Language.DE , "Haben Sie Halsschmerzen ?");
                labelsByAPIName.get("__Headache")                   .addTranslation(Language.DE , "Leiden Sie unter Kopfschmerzen?");
                labelsByAPIName.get("__LimbPain")                   .addTranslation(Language.DE , "Leiden Sie unter Gliederschmerzen?");
                labelsByAPIName.get("__LostOfTaste")                .addTranslation(Language.DE , "Ist Ihr Geschmackssinn verloren gegangen?");
                labelsByAPIName.get("__Diarrhea")                   .addTranslation(Language.DE , "Haben Sie Durchfall?");
                labelsByAPIName.get("__FeelingWeak")                .addTranslation(Language.DE , "Fühlen Sie sich allgemein schwach?");
                labelsByAPIName.get("__SevereCourse")               .addTranslation(Language.DE , "Verschlimmern sich die Symptome mit der Zeit?");
                labelsByAPIName.get("__StartTest")                  .addTranslation(Language.DE , "Test beginnen");
                labelsByAPIName.get("__YesButton")                  .addTranslation(Language.DE , "Ja");
                labelsByAPIName.get("__NoButton")                   .addTranslation(Language.DE , "Nein");
                labelsByAPIName.get("__ConfirmButton")              .addTranslation(Language.DE , "Weiter");
                labelsByAPIName.get("__TotalCasesNumber")           .addTranslation(Language.DE, "Coronavirus-Fälle");
                labelsByAPIName.get("__DeathCasesNumber")           .addTranslation(Language.DE, "Todesfälle");
                labelsByAPIName.get("__RecoveredCasesNumber")       .addTranslation(Language.DE, "Genesen");
                labelsByAPIName.get("__Result")                     .addTranslation(Language.DE,"Ergebnis");
                labelsByAPIName.get("__ResultDescriptionCovid19")   .addTranslation(Language.DE,"Ihre Symptome ähneln denen von Covid-19 am stärksten. Bleiben Sie wenn möglich bitte zu Hause und halten Abstand zu anderen Menschen. Wenn die Symptome kritisch werden, rufen Sie bitte einen Arzt. Bitte beachten Sie, dass dies kein medizinischer Test ist und nur die Symptome der Krankheiten gewichtet.");
                labelsByAPIName.get("__ResultDescriptionCold")      .addTranslation(Language.DE,"Ihre Symptome weisen höchstwahrscheinlich auf eine Erkältung hin. Es besteht trotzdem die Möglichkeit, dass Sie mit Covid-19 infiziert sind. Halten Sie sich deshalb an die Offiziellen Sicherheitsmaßnahmen. Bitte beachten Sie, dass dies kein medizinischer Test ist und nur die Symptome der Krankheiten gewichtet.");
                labelsByAPIName.get("__ResultDescriptionFlu")       .addTranslation(Language.DE,"Ihre Symptome weisen höchstwahrscheinlich auf eine Grippe hin. Es besteht trotzdem die Möglichkeit, dass Sie mit Covid-19 infiziert sind. Halten Sie sich deshalb an die Offiziellen Sicherheitsmaßnahmen. Bitte beachten Sie, dass dies kein medizinischer Test ist und nur die Symptome der Krankheiten gewichtet.");
                labelsByAPIName.get("__ResultDescriptionNothing")   .addTranslation(Language.DE,"Sie sind höchstwahrscheinlich nicht krank.");
                labelsByAPIName.get("__Covid-19")                   .addTranslation(Language.DE,"Covid-19");
                labelsByAPIName.get("__Cold")                       .addTranslation(Language.DE,"Erkältung");
                labelsByAPIName.get("__Flu")                        .addTranslation(Language.DE,"Grippe");
                labelsByAPIName.get("__World_statistics")           .addTranslation(Language.DE,"Weltweite Statistik");
                labelsByAPIName.get("__ShareButton")                .addTranslation(Language.DE,"Teilen!");
                labelsByAPIName.get("__StatisticsButton")           .addTranslation(Language.DE,"Alle Länder");
                labelsByAPIName.get("__Countries")                  .addTranslation(Language.DE, "Länder");
                labelsByAPIName.get("__TotalCases")                 .addTranslation(Language.DE, "Fälle insgesamt");
                labelsByAPIName.get("__DeathCases")                 .addTranslation(Language.DE, "Todesfälle");
                labelsByAPIName.get("__DeathRate")                  .addTranslation(Language.DE, "Sterberate");
                labelsByAPIName.get("__CasesToday")                 .addTranslation(Language.DE, "Fälle heute");
                labelsByAPIName.get("__DeathCasesToday")            .addTranslation(Language.DE, "Tote heute");
                labelsByAPIName.get("__TotalCasesByCountry")        .addTranslation(Language.DE , "Fälle insgesamt nach Ländern");
                labelsByAPIName.get("__TotalCasesOverTime")         .addTranslation(Language.DE , "Fälle insgesamt nach Datum");
                labelsByAPIName.get("__TotalDeathsOverTime")        .addTranslation(Language.DE , "Todesfälle nach Datum");
                labelsByAPIName.get("__DeathRateOverTime")          .addTranslation(Language.DE , "Sterberate nach Datum");
                labelsByAPIName.get("__Sources")                    .addTranslation(Language.DE , "Quellen");


            } break;
            case RU:{
                labelsByAPIName.get("__FeverQuestion")              .addTranslation(Language.RU , "Страдаете ли вы от лихорадки?");
                labelsByAPIName.get("__DryCough")                   .addTranslation(Language.RU , "Есть ли у вас сухой кашель?");
                labelsByAPIName.get("__BreathShortness")            .addTranslation(Language.RU , "Есть ли у вас одышка?");
                labelsByAPIName.get("__RunnyNose")                  .addTranslation(Language.RU , "Если у вас есть насморк?");
                labelsByAPIName.get("__SoreThroat")                 .addTranslation(Language.RU , "У вас болит горло?");
                labelsByAPIName.get("__Headache")                   .addTranslation(Language.RU , "Есть ли у Вас головная боль?");
                labelsByAPIName.get("__LimbPain")                   .addTranslation(Language.RU , "Вы страдаете от боли в конечностях?");
                labelsByAPIName.get("__LostOfTaste")                .addTranslation(Language.RU , "Вы страдаете от потери вкуса?");
                labelsByAPIName.get("__Diarrhea")                   .addTranslation(Language.RU , "Есть ли у вас понос?");
                labelsByAPIName.get("__FeelingWeak")                .addTranslation(Language.RU , "Чувствуете ли вы общую слабость?");
                labelsByAPIName.get("__SevereCourse")               .addTranslation(Language.RU , "Симптомы ухудшаются с течением времени?");
                labelsByAPIName.get("__StartTest")                  .addTranslation(Language.RU , "Начать тест");
                labelsByAPIName.get("__YesButton")                  .addTranslation(Language.RU , "да");
                labelsByAPIName.get("__NoButton")                   .addTranslation(Language.RU , "нет");
                labelsByAPIName.get("__ConfirmButton")              .addTranslation(Language.RU , "дальше");
                labelsByAPIName.get("__TotalCasesNumber")           .addTranslation(Language.RU, "случаи коронавируса");
                labelsByAPIName.get("__DeathCasesNumber")           .addTranslation(Language.RU, "смертей");
                labelsByAPIName.get("__RecoveredCasesNumber")       .addTranslation(Language.RU, "выздоровел");
                labelsByAPIName.get("__Result")                     .addTranslation(Language.RU,"Результат");
                labelsByAPIName.get("__ResultDescriptionCovid19")   .addTranslation(Language.RU,"Ваши симптомы, скорее всего, связаны с Ковид-19. Пожалуйста, оставайтесь дома и держитесь на расстоянии от других людей. Если симптомы становятся критическими, обратитесь к врачу. Имейте в виду, что это не медицинский тест и только взвешивает ваши симптомы между тремя болезнями");
                labelsByAPIName.get("__ResultDescriptionCold")      .addTranslation(Language.RU,"Ваши симптомы, скорее всего, связаны с простудой. Есть еще шанс, что у вас есть вирус Ковид-19, поэтому, пожалуйста, действуйте в соответствии с официальными инструкциями по технике безопасности. Имейте в виду, что это не медицинский тест и только взвешивает ваши симптомы между тремя болезнями.");
                labelsByAPIName.get("__ResultDescriptionFlu")       .addTranslation(Language.RU,"Ваши симптомы, скорее всего, связаны с гриппом. Есть еще шанс, что у вас есть вирус Ковид-19, поэтому, пожалуйста, действуйте в соответствии с официальными инструкциями по технике безопасности. Имейте в виду, что это не медицинский тест и только взвешивает ваши симптомы между тремя болезнями.");
                labelsByAPIName.get("__ResultDescriptionNothing")   .addTranslation(Language.RU,"Вы, вероятно, не больны.");
                labelsByAPIName.get("__Covid-19")                   .addTranslation(Language.RU,"Ковид-19");
                labelsByAPIName.get("__Cold")                       .addTranslation(Language.RU,"Простуда");
                labelsByAPIName.get("__Flu")                        .addTranslation(Language.RU,"Грипп");
                labelsByAPIName.get("__World_statistics")           .addTranslation(Language.RU, "всемирная статистика");
                labelsByAPIName.get("__ShareButton")                .addTranslation(Language.RU, "доля!");
                labelsByAPIName.get("__StatisticsButton")           .addTranslation(Language.RU, "все страны");
                labelsByAPIName.get("__Countries")                  .addTranslation(Language.RU, "страны");
                labelsByAPIName.get("__TotalCases")                 .addTranslation(Language.RU, "всего случаев");
                labelsByAPIName.get("__DeathCases")                 .addTranslation(Language.RU, "случаи смерти");
                labelsByAPIName.get("__DeathRate")                  .addTranslation(Language.RU, "Смертность");
                labelsByAPIName.get("__CasesToday")                 .addTranslation(Language.RU, "дела сегодня");
                labelsByAPIName.get("__DeathCasesToday")            .addTranslation(Language.RU, "смерти сегодня");
                labelsByAPIName.get("__TotalCasesByCountry")        .addTranslation(Language.RU , "Всего случаев по стране");
                labelsByAPIName.get("__TotalCasesOverTime")         .addTranslation(Language.RU , "Всего случаев по дате");
                labelsByAPIName.get("__TotalDeathsOverTime")        .addTranslation(Language.RU , "случаи смерти по дате");
                labelsByAPIName.get("__DeathRateOverTime")          .addTranslation(Language.RU , "смертность по дате");
                labelsByAPIName.get("__Sources")                    .addTranslation(Language.RU , "источники");

            } break;
            case AR: {
                labelsByAPIName.get("__FeverQuestion")              .addTranslation(Language.AR , "هل تعاني من الحمى؟");
                labelsByAPIName.get("__DryCough")                   .addTranslation(Language.AR , "هل تعاني من سعال جاف؟");
                labelsByAPIName.get("__BreathShortness")            .addTranslation(Language.AR , "هل تعاني من ضيق التنفس؟");
                labelsByAPIName.get("__RunnyNose")                  .addTranslation(Language.AR , "هل تعاني سيلان الانف؟");
                labelsByAPIName.get("__SoreThroat")                 .addTranslation(Language.AR , "هل تعاني ألم في الحلق؟");
                labelsByAPIName.get("__Headache")                   .addTranslation(Language.AR , "هل تعاني من صداع؟");
                labelsByAPIName.get("__LimbPain")                   .addTranslation(Language.AR , "هل تعاني من ألم في الأطراف؟");
                labelsByAPIName.get("__LostOfTaste")                .addTranslation(Language.AR , "هل تعاني من فقدان الذوق؟");
                labelsByAPIName.get("__Diarrhea")                   .addTranslation(Language.AR , "هل تعاني من الإسهال");
                labelsByAPIName.get("__FeelingWeak")                .addTranslation(Language.AR , "هل تشعر بالضعف بشكل عام؟");
                labelsByAPIName.get("__SevereCourse")               .addTranslation(Language.AR , "هل تزداد الأعراض سوءًا بمرور الوقت؟");
                labelsByAPIName.get("__StartTest")                  .addTranslation(Language.AR , "بدء الاختبار");
                labelsByAPIName.get("__YesButton")                  .addTranslation(Language.AR , "نعم");
                labelsByAPIName.get("__NoButton")                   .addTranslation(Language.AR , "لا");
                labelsByAPIName.get("__ConfirmButton")              .addTranslation(Language.AR , "استمر");
                labelsByAPIName.get("__TotalCasesNumber")           .addTranslation(Language.AR, "حالات الفيروس");
                labelsByAPIName.get("__DeathCasesNumber")           .addTranslation(Language.AR, "الوفيات");
                labelsByAPIName.get("__RecoveredCasesNumber")       .addTranslation(Language.AR, "التعافي");
                labelsByAPIName.get("__Result")                     .addTranslation(Language.AR,"النتيجة");
                labelsByAPIName.get("__ResultDescriptionCovid19")   .addTranslation(Language.AR,"تتشابه أعراضك بشكل كبير مع أعراض Covid-19. يرجى البقاء في المنزل والحفاظ على بعدك عن الآخرين. إذا أصبحت الأعراض حرجة ، يرجى الاتصال بالطبيب. يرجى ملاحظة أن هذا ليس اختبارًا طبيًا بل مقارنة بين أعراض الأمراض الثلاثة.");
                labelsByAPIName.get("__ResultDescriptionCold")      .addTranslation(Language.AR,"تشير أعراضك على الأرجح إلى نزلة برد. ومع ذلك ، لا يزال هناك احتمال أن تكون مصابًا بـ Covid-19. لذلك ، اتبع إجراءات السلامة الرسمية. يرجى ملاحظة أن هذا ليس اختبارًا طبيًا ويقارن فقط أعراض الأمراض الثلاثة.");
                labelsByAPIName.get("__ResultDescriptionFlu")       .addTranslation(Language.AR,"من المرجح أن تكون أعراضك مؤشرًا على الإصابة بالإنفلونزا. ومع ذلك ، لا يزال هناك احتمال أن تكون مصابًا بـ Covid-19. لذلك ، اتبع إجراءات السلامة الرسمية. يرجى ملاحظة أن هذا ليس اختبارًا طبيًا ويقارن فقط أعراض الأمراض الثلاثة.");
                labelsByAPIName.get("__ResultDescriptionNothing")   .addTranslation(Language.AR,"على الغالب أنك لست مريضًا.");
                labelsByAPIName.get("__Covid-19")                   .addTranslation(Language.AR,"Covid-19");
                labelsByAPIName.get("__Cold")                       .addTranslation(Language.AR,"برد");
                labelsByAPIName.get("__Flu")                        .addTranslation(Language.AR,"انفلونزا");
                labelsByAPIName.get("__World_statistics")           .addTranslation(Language.AR, "احصائيات عالمية");
                labelsByAPIName.get("__ShareButton")                .addTranslation(Language.AR, "شارك!");
                labelsByAPIName.get("__StatisticsButton")           .addTranslation(Language.AR, "كل البلدان");
                labelsByAPIName.get("__Countries")                  .addTranslation(Language.AR, "بلدان");
                labelsByAPIName.get("__TotalCases")                 .addTranslation(Language.AR, "مجموع الحالات");
                labelsByAPIName.get("__DeathCases")                 .addTranslation(Language.AR, "الْوَفَيَات");
                labelsByAPIName.get("__DeathRate")                  .addTranslation(Language.AR, "معدل الوفيات");
                labelsByAPIName.get("__CasesToday")                 .addTranslation(Language.AR, "حالات اليوم");
                labelsByAPIName.get("__DeathCasesToday")            .addTranslation(Language.AR, "حالات الوفاة اليوم");
                labelsByAPIName.get("__TotalCasesByCountry")        .addTranslation(Language.AR , "إجمالي الحالات حسب البلد");
                labelsByAPIName.get("__TotalCasesOverTime")         .addTranslation(Language.AR , "إجمالي الحالات حسب التاريخ");
                labelsByAPIName.get("__TotalDeathsOverTime")        .addTranslation(Language.AR , "حالات الوفاة حسب التاريخ");
                labelsByAPIName.get("__DeathRateOverTime")          .addTranslation(Language.AR , "معدل الوفيات حسب التاريخ");
                labelsByAPIName.get("__Sources")                    .addTranslation(Language.AR , "مصادر");

            }
        }
    }

    public Language getSystemLanguage() {
        return systemLanguage;
    }

    private void initSymptoms(){
        symptoms.add(new Symptom("Fever" , labelsByAPIName.get("__FeverQuestion").getLabel(systemLanguage) , 5 , 2 ,5));
        symptoms.add(new Symptom("Dry Cough" , labelsByAPIName.get("__DryCough").getLabel(systemLanguage) , 5 , 4 ,5));
        symptoms.add(new Symptom("Breath Shortness" , labelsByAPIName.get("__BreathShortness").getLabel(systemLanguage) , 2 , -1 ,1));
        symptoms.add(new Symptom("Runny Nose" , labelsByAPIName.get("__RunnyNose").getLabel(systemLanguage) , 1 , 5 ,3));
        symptoms.add(new Symptom("Sore Throat" , labelsByAPIName.get("__SoreThroat").getLabel(systemLanguage) , 2 , 5 ,5));
        symptoms.add(new Symptom("Headache" , labelsByAPIName.get("__Headache").getLabel(systemLanguage) , 2 , 3 ,5));
        symptoms.add(new Symptom("Limb Pain" , labelsByAPIName.get("__LimbPain").getLabel(systemLanguage) , 2 , 3 ,5));
        symptoms.add(new Symptom("Lost Of Taste" , labelsByAPIName.get("__LostOfTaste").getLabel(systemLanguage) , 3 , 1 ,1));
        symptoms.add(new Symptom("Diarrhea" , labelsByAPIName.get("__Diarrhea").getLabel(systemLanguage) , 1 , 1 ,1));
        symptoms.add(new Symptom("Feeling Weak" , labelsByAPIName.get("__FeelingWeak").getLabel(systemLanguage) , 4 , 2 ,5));
        symptoms.add(new Symptom("Severe Course", labelsByAPIName.get("__SevereCourse").getLabel(systemLanguage) , 2 , -1 ,1));
    }
    public Point getWindowSize(){
        return new Point(windowHeight , windowHeight);
    }

    public void setWindowWidth(int windowWidth) {
        this.windowWidth = windowWidth;
    }

    public void setWindowHeight(int windowHeight) {
        this.windowHeight = windowHeight;
    }

    public ArrayList<Symptom> getSymptoms() {
        return symptoms;
    }
    public String getLabel(String apiName){
        return labelsByAPIName.get(apiName).getLabel(systemLanguage);
    }

    public boolean isActivityIsLoading() {
        return activityIsLoading;
    }

    public void setActivityIsLoading(boolean activityIsLoading) {
        this.activityIsLoading = activityIsLoading;
    }

    public int getQuestionCounter() {
        return questionCounter;
    }

    public void setQuestionCounter(int questionCounter) {
        this.questionCounter = questionCounter;
    }

    public int getQuestionMaxCount() {
        return questionMaxCount;
    }

    public void setQuestionMaxCount(int questionMaxCount) {
        this.questionMaxCount = questionMaxCount;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public boolean isAlreadyFadedOutExpansionIcons() {
        return alreadyFadedOutExpansionIcons;
    }

    public void setAlreadyFadedOutExpansionIcons(boolean alreadyFadedOutExpansionIcons) {
        this.alreadyFadedOutExpansionIcons = alreadyFadedOutExpansionIcons;
    }

}
