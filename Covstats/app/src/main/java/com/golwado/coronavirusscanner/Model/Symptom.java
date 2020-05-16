package com.golwado.coronavirusscanner.Model;

public class Symptom {
    private final String name;
    private final String question;
    private final int covidWeight;
    private final int coldWeight;
    private final int fluWeight;

    public Symptom(String name, String question, int covidWeight, int coldWeight, int fluWeight) {
        this.name = name;
        this.question = question;
        this.covidWeight = covidWeight;
        this.coldWeight = coldWeight;
        this.fluWeight = fluWeight;
    }

    public String getName() {
        return name;
    }

    public String getQuestion() {
        return question;
    }

    public int getCovidWeight() {
        return covidWeight;
    }

    public int getColdWeight() {
        return coldWeight;
    }

    public int getFluWeight() {
        return fluWeight;
    }

    public int getTotalWeight(){
        return covidWeight + coldWeight + fluWeight;
    }
}
