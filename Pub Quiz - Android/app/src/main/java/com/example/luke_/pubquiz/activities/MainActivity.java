package com.example.luke_.pubquiz.activities;

import android.content.Intent;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.luke_.pubquiz.GameState;
import com.example.luke_.pubquiz.R;
import com.example.luke_.pubquiz.Team;
import com.example.luke_.pubquiz.client.Client;
import com.example.luke_.pubquiz.client.Listener.ConnectionListener;
import com.example.luke_.pubquiz.client.IClient;
import com.example.luke_.pubquiz.client.Listener.ConnectionTimeOutListener;
import com.example.luke_.pubquiz.client.Listener.ReconnectListener;

public class MainActivity extends AppCompatActivity implements ConnectionListener, ReconnectListener, ConnectionTimeOutListener {

    private Button connectButton;
    private IClient client;
    private String phoneID;
    private TextView connectionPromptTextView;
    private ProgressBar connectionSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectButton = (Button)findViewById(R.id.connectBttn);
        connectionPromptTextView = (TextView) findViewById(R.id.connectionPromptTextView);
        connectionSpinner = (ProgressBar) findViewById(R.id.connectionSpinner);
        client = Client.getInstance();

        connectionSpinner.setVisibility(View.INVISIBLE);

        phoneID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Team.getInstance().setId(phoneID);

        client.addConnectionListener(this);
        client.addReconnectListener(this);
        client.addConnectionTimeOutListener(this);
    }

    public void connectOnClick(final View view){
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                client.connect("192.168.0.55", 8080);
            }
        };
        new Thread(runnable).start();

        connectButton.setEnabled(false);
        connectButton.setVisibility(View.INVISIBLE);
        connectionSpinner.setVisibility(View.VISIBLE);

        connectionPromptTextView.setText("Attempting to connect to the game!");

    }



    @Override
    public void onConnected() {
        startActivity(new Intent(MainActivity.this, TeamActivity.class));
    }


    @Override
    public void onReconnect(int state) {
        if(state == GameState.WAITING_STATE){
            startActivity(new Intent(MainActivity.this, TeamActivity.class));
        }
        else if(state == GameState.GAME_STATE){
            startActivity(new Intent(MainActivity.this, QuizActivity.class));
        }else if(state == GameState.SUMMARY_STATE){
            startActivity(new Intent(MainActivity.this, SummaryActivity.class));
        }else if(state == GameState.END_STATE){
            startActivity(new Intent(MainActivity.this, EndGameActivity.class));

        }
    }

    @Override
    public void onConnectionTimeout() {
        runOnUiThread(new Runnable() {
            public void run() {
                connectButton.setEnabled(true);
                connectButton.setVisibility(View.VISIBLE);
                connectionSpinner.setVisibility(View.INVISIBLE);
                connectionPromptTextView.setText("Could not connect to the game!");
            }
        });
    }

    @Override
    protected void onPause() {
        client.removeConnectionListener(this);
        client.removeReconnectListener(this);
        client.removeConnectionTimeOutListener(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        client.addConnectionListener(this);
        client.addReconnectListener(this);
        client.addConnectionTimeOutListener(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        client.removeConnectionListener(this);
        client.removeReconnectListener(this);
        client.removeConnectionTimeOutListener(this);
        super.onDestroy();
    }
}
