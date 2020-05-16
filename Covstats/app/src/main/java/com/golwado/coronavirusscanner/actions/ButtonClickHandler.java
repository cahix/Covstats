package com.golwado.coronavirusscanner.actions;

import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.golwado.coronavirusscanner.Activities.MainActivity;
import com.golwado.coronavirusscanner.Activities.QuestionsActivity;
import com.golwado.coronavirusscanner.Activities.ResultActivity;
import com.golwado.coronavirusscanner.Design.Customization;
import com.golwado.coronavirusscanner.Model.Controller;
import com.golwado.coronavirusscanner.Model.Question;
import com.golwado.coronavirusscanner.Model.Quiz;
import com.golwado.coronavirusscanner.R;

public class ButtonClickHandler implements View.OnClickListener{
    public enum ACTION_TYPE {
        Test_Start,
        Result,
        onTouchFeedBack,
        AnswerQuestionYes,
        AnswerQuestionNo,
        PreviousQuestion,
        goHome,
    }
    private ACTION_TYPE actionType;

    public ButtonClickHandler(ACTION_TYPE actionType){
        this.actionType = actionType;
    }

    @Override
    public void onClick(View v) {
        switch (actionType){
            case Test_Start: onTestStart();
                break;
            case AnswerQuestionYes:onQuestionYesAnswer();
                break;
            case AnswerQuestionNo:onQuestionNoAnswer();
                break;
            case PreviousQuestion: onPreviousQuestion();
                break;
            case goHome: onGoHome();
                break;
        }
    }

    public void setOnTouchFeedBack(final Button button, final Context context){
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    button.setBackgroundResource(R.drawable.roundedbutton_grey);
                    button.setTextColor(context.getResources().getColor(R.color.whiteColor));
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    button.setBackgroundResource(R.drawable.roundedbutton);
                    button.setTextColor(context .getResources().getColor(R.color.mainColor));
                }
                return false;
            }
        });
    }

    public void setOnTouchFeedBackBlueTheme(final Button button, final Context context){
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    Customization.setDarkBlueButtonTheme(button,context);
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    Customization.setLightBlueButtonTheme(button,context);
                }
                return false;
            }
        });
    }



    private void onGoHome(){
        QuestionsActivity instance = QuestionsActivity.getInstance();
        Intent intent = new Intent(instance,MainActivity.class);
        Controller.getInstance().setActivityIsLoading(true);
        instance.startActivity(intent);
    }

    private void onTestStart(){
        Controller controller = Controller.getInstance();
        MainActivity instance = MainActivity.getInstance();
        Intent intent = new Intent(instance, QuestionsActivity.class);
        controller.setQuiz(new Quiz());
        controller.setQuestionCounter(1);
        controller.setActivityIsLoading(true);
        instance.startActivity(intent);
    }

    private void onResult(float[] results){
        Intent intent = new Intent(MainActivity.getInstance() , ResultActivity.class);
        intent.putExtra("results" , results);
        Controller.getInstance().setActivityIsLoading(true);
        MainActivity.getInstance().startActivity(intent);
    }


    private void onQuestionYesAnswer(){
        QuestionsActivity instance = QuestionsActivity.getInstance();
        Controller controller = Controller.getInstance();
        Quiz quiz = controller.getQuiz();
        Question currentQuestion = quiz.getCurrentQuestion();
        currentQuestion.answerQuestion(true);
        Question nextQuestion = quiz.getNextQuestion();
        controller.setQuestionCounter(quiz.getCurrentQuestionIndex()+1);
        instance.getCircularProgressBar().setProgress((int)Math.round(100.0 / controller.getQuestionMaxCount() * controller.getQuestionCounter()));
        instance.getProgressCounterTextView().setText((controller.getQuestionCounter())+ "/"+controller.getQuestionMaxCount());
        if(nextQuestion != null) {
            instance.getQuestionTextView().setText(nextQuestion.getContext());
        } else {
            quiz.calculateResult();
            onResult(quiz.getResults());
            instance.overridePendingTransition(0,0);
        }
        Customization.setBlueButtonTheme(instance.getYesButton(),instance);
        Customization.setBlueButtonTheme(instance.getNoButton(),instance);
    }

    private void onQuestionNoAnswer(){
        QuestionsActivity instance = QuestionsActivity.getInstance();
        Controller controller = Controller.getInstance();
        Quiz quiz = controller.getQuiz();
        Question currentQuestion = quiz.getCurrentQuestion();
        currentQuestion.answerQuestion(false);
        Question nextQuestion = quiz.getNextQuestion();
        controller.setQuestionCounter(quiz.getCurrentQuestionIndex()+1);
        instance.getCircularProgressBar().setProgress((int)Math.round(100.0 / controller.getQuestionMaxCount() * controller.getQuestionCounter()));
        instance.getProgressCounterTextView().setText((controller.getQuestionCounter())+ "/"+controller.getQuestionMaxCount());
        if(nextQuestion != null) {
            instance.getQuestionTextView().setText(nextQuestion.getContext());
        } else {
            quiz.calculateResult();
            onResult(quiz.getResults());
            instance.overridePendingTransition(0,0);
        }
        Customization.setBlueButtonTheme(instance.getYesButton(),instance);
        Customization.setBlueButtonTheme(instance.getNoButton(),instance);
    }

    public void onPreviousQuestion(){
        QuestionsActivity instance = QuestionsActivity.getInstance();
        Controller controller = Controller.getInstance();
        Quiz quiz = controller.getQuiz();
        Question previousQuestion = quiz.getPreviousQuestion();
        if(previousQuestion != null) {
            instance.getQuestionTextView().setText(previousQuestion.getContext());
            controller.setQuestionCounter(controller.getQuestionCounter()-1);
            instance.getCircularProgressBar().setProgress((int)Math.round(100.0 / controller.getQuestionMaxCount() * controller.getQuestionCounter()));
            instance.getProgressCounterTextView().setText((controller.getQuestionCounter())+ "/"+controller.getQuestionMaxCount());
        }
        else {
            Intent intent = new Intent(instance,MainActivity.class);
            instance.startActivity(intent);
        }
        Customization.setBlueButtonTheme(instance.getYesButton(),instance);
        Customization.setBlueButtonTheme(instance.getNoButton(),instance);
    }
}
