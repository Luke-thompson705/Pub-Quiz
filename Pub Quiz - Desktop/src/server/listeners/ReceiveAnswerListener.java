package server.listeners;

public interface ReceiveAnswerListener {

    void onReceived(String teamId, int questionId ,String answer);

}
