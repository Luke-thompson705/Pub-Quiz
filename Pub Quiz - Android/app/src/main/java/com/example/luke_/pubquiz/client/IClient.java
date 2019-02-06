package com.example.luke_.pubquiz.client;

import com.example.luke_.pubquiz.client.Listener.ConnectionListener;
import com.example.luke_.pubquiz.client.Listener.ConnectionTimeOutListener;
import com.example.luke_.pubquiz.client.Listener.DisconnectListener;
import com.example.luke_.pubquiz.client.Listener.GameStateListener;
import com.example.luke_.pubquiz.client.Listener.QuestionListener;
import com.example.luke_.pubquiz.client.Listener.ReceiveCurrentPointsListener;
import com.example.luke_.pubquiz.client.Listener.ReconnectListener;
import com.example.luke_.pubquiz.client.Listener.RequestAnswerListener;
import com.example.luke_.pubquiz.client.Listener.SummaryListener;
import com.example.luke_.pubquiz.client.Listener.TeamListener;

public interface IClient {

    void connect(String host, int port);

    void disconnect();

    void submitTeamName(String id, String name);

    void getCurrentPosition(String id);

    void getCurrentPoints(String id);

    void submitAnswer(String teamId, int questionID ,String answer);

    void checkTeamExist(String id);

    void requestQuestion();

    void addConnectionListener(ConnectionListener listener);

    void removeConnectionListener(ConnectionListener listener);

    void addGameStateListener(GameStateListener listener);

    void removeGameStateListener(GameStateListener listener);

    void addQuestionListener(QuestionListener listener);

    void removeQuestionListener(QuestionListener listener);

    void addRequestAnswerListener(RequestAnswerListener listener);

    void removeRequestAnswerListener(RequestAnswerListener listener);

    void addReconnectListener(ReconnectListener listener);

    void removeReconnectListener(ReconnectListener listener);

    void addTeamListener(TeamListener listener);

    void removeTeamListener(TeamListener listener);

    void addSummaryListener(SummaryListener listener);

    void removeSummaryListener(SummaryListener listener);

    void addReceiveCurrentPointsListener(ReceiveCurrentPointsListener listener);

    void removeReceiveCurrentPointsListener(ReceiveCurrentPointsListener listener);

    void addConnectionTimeOutListener(ConnectionTimeOutListener listener);

    void removeConnectionTimeOutListener(ConnectionTimeOutListener listener);

    void addDisconnectListener(DisconnectListener listener);

    void removeDisconnectListener(DisconnectListener listener);
}
