package com.golwado.coronavirusscanner.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.golwado.coronavirusscanner.Model.Quiz;
import com.golwado.coronavirusscanner.Model.Controller;
import com.golwado.coronavirusscanner.R;
import com.golwado.coronavirusscanner.actions.ButtonClickHandler;

public class QuestionsActivity extends AppCompatActivity {
    private static QuestionsActivity instance;
    private TextView questionTextView;
    private TextView progressCounterTextView;
    private Quiz quiz;
    private Button yesButton;
    private Button noButton;
    private Button homeButton;
    private ProgressBar circularProgressBar;


    public static QuestionsActivity getInstance(){
        return instance;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_constraint_questions);
        quiz = Controller.getInstance().getQuiz();
        Controller.getInstance().setQuestionMaxCount(quiz.getQuestions().size());
        getComponents();
        addTranslatedTexts();
        questionTextView.setText(quiz.getCurrentQuestion().getContext());
        addListeners();
        setUpProgressBar();
        Controller.getInstance().setActivityIsLoading(false);
    }

    protected void setUpProgressBar(){
        circularProgressBar     = findViewById(R.id.circularProgressBar);
        progressCounterTextView = findViewById(R.id.text_view_progress_counter);
        circularProgressBar.setProgress(Controller.getInstance().getQuestionCounter());
        progressCounterTextView.setText(Controller.getInstance().getQuestionCounter() +"/"+Controller.getInstance().getQuestionMaxCount());
    }

    protected void addListeners(){
        ButtonClickHandler bch = new ButtonClickHandler(ButtonClickHandler.ACTION_TYPE.onTouchFeedBack);
        yesButton.setOnClickListener(new ButtonClickHandler(ButtonClickHandler.ACTION_TYPE.AnswerQuestionYes));
        bch.setOnTouchFeedBack(yesButton,this);
        noButton.setOnClickListener(new ButtonClickHandler(ButtonClickHandler.ACTION_TYPE.AnswerQuestionNo));
        bch.setOnTouchFeedBack(noButton,this);
        homeButton.setOnClickListener(new ButtonClickHandler(ButtonClickHandler.ACTION_TYPE.goHome));
        homeButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    Drawable mIcon= ContextCompat.getDrawable(getApplicationContext(), R.drawable.cancel);
                    mIcon.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.grayColor), PorterDuff.Mode.MULTIPLY);
                    homeButton.setBackground(mIcon);
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    Drawable mIcon= ContextCompat.getDrawable(getApplicationContext(), R.drawable.cancel);
                    mIcon.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.whiteColor), PorterDuff.Mode.MULTIPLY);
                    homeButton.setBackground(mIcon);
                }
                return false;
            }
        });


    }

    @Override
    public void onBackPressed() {
        ButtonClickHandler bch = new ButtonClickHandler(ButtonClickHandler.ACTION_TYPE.PreviousQuestion);
        bch.onPreviousQuestion();
    }

    private void getComponents () {
        questionTextView        = findViewById(R.id.text_view_question_constraint);
        yesButton               = findViewById(R.id.button_yes);
        noButton                = findViewById(R.id.button_no);
        homeButton = findViewById(R.id.button_home);
    }

    protected void addTranslatedTexts(){
        yesButton.setText(Controller.getInstance().getLabel("__YesButton"));
        noButton.setText(Controller.getInstance().getLabel("__NoButton"));
    }

    public TextView getQuestionTextView() {
        return questionTextView;
    }

    public Quiz getQuiz() {
        return quiz;
    }


    public TextView getProgressCounterTextView() {
        return progressCounterTextView;
    }

    public Button getYesButton() {
        return yesButton;
    }

    public Button getNoButton() {
        return noButton;
    }

    public ProgressBar getCircularProgressBar() {
        return circularProgressBar;
    }

}

