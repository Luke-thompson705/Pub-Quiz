package com.example.luke_.pubquiz.activities;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.luke_.pubquiz.GameState;
import com.example.luke_.pubquiz.R;
import com.example.luke_.pubquiz.Team;
import com.example.luke_.pubquiz.client.Client;
import com.example.luke_.pubquiz.client.Listener.DisconnectListener;
import com.example.luke_.pubquiz.client.Listener.GameStateListener;
import com.example.luke_.pubquiz.client.IClient;
import com.example.luke_.pubquiz.client.Listener.ReconnectListener;
import com.example.luke_.pubquiz.client.Listener.TeamListener;

public class TeamActivity extends AppCompatActivity implements GameStateListener, TeamListener, DisconnectListener {

    //UI
    private EditText teamEditText;
    private ProgressBar spinner;
    private Button confirmButton;
    private TextView teamTextView;

    private IClient client;
    private Team team;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        teamEditText = (EditText) findViewById(R.id.teamEditText);
        spinner = (ProgressBar) findViewById(R.id.connectionSpinner);
        confirmButton = (Button) findViewById(R.id.confirmButton);
        teamTextView = (TextView) findViewById(R.id.teamTextView);

        spinner.setVisibility(View.INVISIBLE);

        team = Team.getInstance();

        //Listeners
        client = Client.getInstance();
        client.addGameStateListener(this);
        client.addTeamListener(this);
        client.addDisconnectListener(this);

        //Check team
        client.checkTeamExist(Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID));

    }

    public void confirmTeamOnClick(final View view){
        client.submitTeamName(Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID), teamEditText.getText().toString());
        client.checkTeamExist(Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID));
    }



    private void waitForGameState(final String teamName){
        runOnUiThread(new Runnable() {
            public void run(){
                teamEditText.setText(teamName);
                confirmButton.setVisibility(View.INVISIBLE);
                confirmButton.setEnabled(false);
                spinner.setVisibility(View.VISIBLE);
                teamTextView.setText("Waiting for game to start!");
                teamEditText.setEnabled(false);
            }
        });
    }

    @Override
    public void teamExist(String name) {
        team.init(Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID), name);
        waitForGameState(team.getName());
    }

    @Override
    public void onStateChange(int state) {
        if(state == GameState.GAME_STATE){
            startActivity(new Intent(TeamActivity.this, QuizActivity.class));
        }
    }

    @Override
    public void onDisconnect() {
        startActivity(new Intent(TeamActivity.this, MainActivity.class));
    }

    @Override
    protected void onDestroy() {
        client.removeGameStateListener(this);
        client.removeTeamListener(this);
        client.removeDisconnectListener(this);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        client.removeGameStateListener(this);
        client.removeTeamListener(this);
        client.removeDisconnectListener(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        client.addGameStateListener(this);
        client.addTeamListener(this);
        client.addDisconnectListener(this);
        super.onPause();
    }
}
