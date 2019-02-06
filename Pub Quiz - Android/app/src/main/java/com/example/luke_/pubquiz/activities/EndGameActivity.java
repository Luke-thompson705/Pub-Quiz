package com.example.luke_.pubquiz.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.luke_.pubquiz.R;
import com.example.luke_.pubquiz.Team;
import com.example.luke_.pubquiz.client.Client;
import com.example.luke_.pubquiz.client.IClient;
import com.example.luke_.pubquiz.client.Listener.ReceiveCurrentPointsListener;
import com.example.luke_.pubquiz.client.Listener.SummaryListener;
import com.example.luke_.pubquiz.utils.Utils;

public class EndGameActivity extends AppCompatActivity implements ReceiveCurrentPointsListener, SummaryListener{

    //UI
    private TextView pointsTextView;
    private TextView positionTextView;
    private IClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);

        positionTextView = (TextView) findViewById(R.id.posTextView);
        pointsTextView = (TextView) findViewById(R.id.pointsTextView);

        client =  Client.getInstance();

        client.addReceiveCurrentPointsListener(this);
        client.addSummaryListener(this);


        client.getCurrentPoints(Team.getInstance().getId());
        client.getCurrentPosition(Team.getInstance().getId());

    }


    @Override
    public void onReceivePoints(final int points) {
        runOnUiThread(new Runnable() {
            public void run() {
                pointsTextView.setText("You scored: " + String.valueOf(points));
            }
        });
    }

    @Override
    public void onSummary(final int pos) {
        runOnUiThread(new Runnable() {
            public void run() {
                positionTextView.setText("You came: " + Utils.Ordinals(pos));
            }
        });
    }

    public void finishOnClick(View view){
        startActivity(new Intent(EndGameActivity.this, MainActivity.class));
        Client.getInstance().disconnect();

    }

    @Override
    protected void onDestroy() {
        client.removeReceiveCurrentPointsListener(this);
        client.removeSummaryListener(this);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        client.removeReceiveCurrentPointsListener(this);
        client.removeSummaryListener(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        client.addReceiveCurrentPointsListener(this);
        client.addSummaryListener(this);
        super.onPause();
    }

}
