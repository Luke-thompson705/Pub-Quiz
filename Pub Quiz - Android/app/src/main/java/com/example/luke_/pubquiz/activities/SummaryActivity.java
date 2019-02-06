package com.example.luke_.pubquiz.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.luke_.pubquiz.R;
import com.example.luke_.pubquiz.Team;
import com.example.luke_.pubquiz.client.Client;
import com.example.luke_.pubquiz.client.IClient;
import com.example.luke_.pubquiz.client.Listener.DisconnectListener;
import com.example.luke_.pubquiz.client.Listener.GameStateListener;
import com.example.luke_.pubquiz.client.Listener.SummaryListener;
import com.example.luke_.pubquiz.utils.Utils;

public class SummaryActivity extends AppCompatActivity implements SummaryListener, DisconnectListener, GameStateListener{


    //UI
    private TextView roundPosition;
    private IClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        roundPosition = (TextView) findViewById(R.id.roundPosition);

        client = Client.getInstance();
        client.addSummaryListener(this);
        client.addDisconnectListener(this);
        client.addGameStateListener(this);
        client.getCurrentPosition(Team.getInstance().getId());

    }

    @Override
    public void onSummary(final int pos) {
        runOnUiThread(new Runnable() {
            public void run() {
                roundPosition.setText(Utils.Ordinals(pos));
            }
        });
    }

    @Override
    public void onDisconnect() {
        startActivity(new Intent(SummaryActivity.this, MainActivity.class));

    }

    @Override
    public void onStateChange(int state) {
    }

    @Override
    protected void onDestroy() {
        client.removeSummaryListener(this);
        client.removeDisconnectListener(this);
        client.removeGameStateListener(this);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        client.removeSummaryListener(this);
        client.removeDisconnectListener(this);
        client.removeGameStateListener(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        client.addSummaryListener(this);
        client.addDisconnectListener(this);
        client.addGameStateListener(this);
        super.onPause();
    }
}
