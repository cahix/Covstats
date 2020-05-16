package com.golwado.coronavirusscanner.Model;

public class Question {
    private boolean selectedAnswer   = false;
    private String context          = "";

    public Question(String context){
        this.context = context;
    }

    public void answerQuestion(boolean selectedAnswer){
        this.selectedAnswer = selectedAnswer;
    }

    public String getContext() {
        return context;
    }

    public boolean getSelectedAnswer() {
        return selectedAnswer;
    }
}
