package server;

import server.listeners.*;

public interface IServer {

    void start();

    void stop();

    void sendQuestion(int questionId, String question, int time);

    void requestAnswers();

    void sendRoundSummary(String id);

    void sendGameState(int state);

    void sendGameStateToClient(String id, int state);

    void sendToClient(String id, int protocol, String message);

    void addClientConnectListener(ClientConnectListener listener);

    void removeClientConnectListener(ClientConnectListener listener);

    void addClientDisconnectListener(ClientDisconnectListener listener);

    void removeClientDisconnectListener(ClientDisconnectListener listener);

    void addClientReconnectListener(ClientReconnectListener listener);

    void removeClientReconnectListener(ClientReconnectListener listener);

    void addTeamListener(TeamListener listener);

    void removeTeamListener(TeamListener listener);

    void addReceiveAnswerListener(ReceiveAnswerListener listener);

    void removeReceiveAnwserListener(ReceiveAnswerListener listener);

    void addRequestListener(QuestionRequestListener listener);

    void removeRequestListener(QuestionRequestListener listener);

    void addGameFinishListener(GameFinishListener listener);

    void removeGameFinishListener(GameFinishListener listener);

    void addReceiveCurrentPositionListener(ReceiveCurrentPositionListener listener);

    void removeReceiveCurrentPositionListener(ReceiveCurrentPositionListener listener);

    void addReceivePointsListener(ReceivePointsListener listener);

    void removeReceivePointsListener(ReceivePointsListener listener);


}
