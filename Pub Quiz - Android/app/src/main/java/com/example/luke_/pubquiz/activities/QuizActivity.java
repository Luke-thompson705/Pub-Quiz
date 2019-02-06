package com.example.luke_.pubquiz.activities;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.example.luke_.pubquiz.GameState;
import com.example.luke_.pubquiz.GridAdapter;
import com.example.luke_.pubquiz.ProgressBarAnimation;
import com.example.luke_.pubquiz.Question;
import com.example.luke_.pubquiz.R;
import com.example.luke_.pubquiz.Team;
import com.example.luke_.pubquiz.client.Client;
import com.example.luke_.pubquiz.client.IClient;
import com.example.luke_.pubquiz.client.Listener.DisconnectListener;
import com.example.luke_.pubquiz.client.Listener.GameStateListener;
import com.example.luke_.pubquiz.client.Listener.QuestionListener;
import com.example.luke_.pubquiz.client.Listener.RequestAnswerListener;

import me.grantland.widget.AutofitTextView;

public class QuizActivity extends AppCompatActivity implements QuestionListener, RequestAnswerListener, GameStateListener, DisconnectListener {


    private IClient client;

    private AutofitTextView questionTextArea;

    private GridView gridView;

    private Question currentQuestion;

    static final String[] alphabet = new String[] {
            "A", "B", "C", "D", "E",
            "F", "G", "H", "I", "J",
            "K", "L", "M", "N", "O",
            "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "", "Y", "Z", ""};

    static final String[] numbers = new String[] {
            "0", "1", "2", "3", "4",
            "5", "6", "7", "8", "9" };


    private String selectedLetter;

    private ObjectAnimator progressAnimator;


    //count time timer
    ProgressBar progressBar;
    CountDownTimer countDownTimer;

    private int previousSelectedPosition = -1;
    private View previousSelectedView;
    private View selectedView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        gridView = (GridView) findViewById(R.id.quizGridView);

        client = Client.getInstance();
        client.addQuestionListener(this);
        client.addRequestAnswerListener(this);
        client.addGameStateListener(this);
        client.addDisconnectListener(this);

        questionTextArea = (AutofitTextView) findViewById(R.id.questionAreaTextView);

        gridView.setAdapter(new GridAdapter(alphabet, this));


        progressBar = (ProgressBar)findViewById(R.id.progressbar);
        progressBar.setProgress(0);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                String letter = (String) gridView.getItemAtPosition(position);
                if(!letter.equals("")) {
                    selectedLetter = letter;
                    selectedView = v;
                    selectedView.setSelected(true);
                    previousSelectedView = gridView.getChildAt(previousSelectedPosition);
                    if (previousSelectedPosition != -1) {
                        previousSelectedView.setSelected(false);
                    }
                    previousSelectedPosition = position;

                }
            }
        });

        client.requestQuestion();
    }
    @Override
    public void onNewQuestion(Question question) {
        currentQuestion = question;
        selectedLetter = " ";
        runOnUiThread(new Runnable() {
            public void run(){
                if(selectedView != null) {
                    selectedView.setSelected(false);
                }
                questionTextArea.setText(currentQuestion.getQuestion());
                ProgressBarAnimation anim = new ProgressBarAnimation(progressBar, 100, 0);
                anim.setDuration(currentQuestion.getTime()*1000);
                progressBar.startAnimation(anim);
            }
        });
    }

    @Override
    public void onAnswerRequest() {
        client.submitAnswer(Team.getInstance().getId(),currentQuestion.getId(), selectedLetter);
    }

    @Override
    public void onStateChange(int state) {
        if(state == GameState.SUMMARY_STATE){
            startActivity(new Intent(QuizActivity.this, SummaryActivity.class));
        }else if(state == GameState.END_STATE){
            startActivity(new Intent(QuizActivity.this, EndGameActivity.class));
        }
    }

    @Override
    public void onDisconnect() {
        startActivity(new Intent(QuizActivity.this, MainActivity.class));
    }

    @Override
    protected void onDestroy() {
        client.removeQuestionListener(this);
        client.removeRequestAnswerListener(this);
        client.removeGameStateListener(this);
        client.removeDisconnectListener(this);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        client.removeQuestionListener(this);
        client.removeRequestAnswerListener(this);
        client.removeGameStateListener(this);
        client.removeDisconnectListener(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        client.addQuestionListener(this);
        client.addRequestAnswerListener(this);
        client.addGameStateListener(this);
        client.addDisconnectListener(this);
        super.onPause();
    }
}
