package com.golwado.coronavirusscanner.Model;

import java.util.ArrayList;
import java.util.List;

public class Quiz {
    private int currentQuestionIndex = 0;
    private List<Question> questions = new ArrayList<>();
    private float[] results;
    public Quiz(){
        initQuestions();
    }

    private void initQuestions(){
        for(Symptom symptom : Controller.getInstance().getSymptoms()){
            questions.add(new Question(symptom.getQuestion()));
        }
    }

    public Question getCurrentQuestion(){
        return questions.get(currentQuestionIndex);
    }
    public Question getNextQuestion(){
        if(currentQuestionIndex == questions.size() -1)
            return null;
        else{
            currentQuestionIndex++;
            return questions.get(currentQuestionIndex);
        }
    }

    public Question getPreviousQuestion(){
        if(currentQuestionIndex == 0)
            return null;
        else{
            currentQuestionIndex--;
            return questions.get(currentQuestionIndex);
        }
    }

    public void calculateResult(){
        ArrayList<Symptom> symptoms = Controller.getInstance().getSymptoms();
        Symptom[] symptomsArr       = new Symptom[symptoms.size()];
        int[] covidPersonalSymptoms = new int[symptoms.size()];
        int[] coldPersonalSymptoms = new int[symptoms.size()];
        int[] fluPersonalSymptoms = new int[symptoms.size()];

        for(int i=0; i< symptoms.size(); i++) {
            boolean confirmed = questions.get(i).getSelectedAnswer();
            if(confirmed) {
                covidPersonalSymptoms[i]    =   symptoms.get(i).getCovidWeight();
                coldPersonalSymptoms[i]     =   symptoms.get(i).getColdWeight();
                fluPersonalSymptoms[i]      =   symptoms.get(i).getFluWeight();

            } else {
                covidPersonalSymptoms[i]    =   -symptoms.get(i).getCovidWeight();
                coldPersonalSymptoms[i]     =   -symptoms.get(i).getColdWeight();
                fluPersonalSymptoms[i]      =   -symptoms.get(i).getFluWeight();
            }
        }

        float[] distances = calculateDistances(symptoms.toArray(symptomsArr) , covidPersonalSymptoms , coldPersonalSymptoms , fluPersonalSymptoms);
        results = new float[3];
        results[0] = (1.f - remap(0 , 19.697716f , distances[0])) * 100f;
        results[1] = (1.f - remap(0 , 19.697716f , distances[1])) * 100f;
        results[2] = (1.f - remap(0 , 25.53429f , distances[2])) * 100f;
    }


    private float remap(float min , float max , float x){
        return (x - min) / (max - min);
    }


    private float[] calculateDistances(Symptom[] sourceArr , int[] covidPersonalSymptoms , int[] coldPersonalSymptoms , int[] fluPersonalSymptoms){
        float[] underRootSums = {0,0,0};
        for(int i = 0; i < sourceArr.length ;i++){
            underRootSums[0] += ((sourceArr[i].getCovidWeight() - covidPersonalSymptoms[i]) * (sourceArr[i].getCovidWeight() - covidPersonalSymptoms[i]));
            underRootSums[1] += ((sourceArr[i].getColdWeight() - coldPersonalSymptoms[i]) * (sourceArr[i].getColdWeight() - coldPersonalSymptoms[i]));
            underRootSums[2] += ((sourceArr[i].getFluWeight() - fluPersonalSymptoms[i]) * (sourceArr[i].getFluWeight() - fluPersonalSymptoms[i]));
        }
        for(int i = 0 ; i < underRootSums.length ; i++){
            underRootSums[i] = ((float) Math.sqrt(underRootSums[i]));
        }
        return underRootSums;
    }

    public float[] getResults() {
        return results;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public int getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }
}
