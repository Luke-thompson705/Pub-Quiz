package menuapp;

import menuapp.Listener.GameEndListener;
import menuapp.Listener.RoundEndListener;

public interface IQuizModel {


    void nextQuestion();

    Question getCurrentQuestion();

    int getTotalQuestionsInCurrentRound();

    Boolean checkAnswer(int questionId, String answer);

    void setGameState(int state);

    int getGameState();

    int getTeamScore(String id);

    void addRoundEndListener(RoundEndListener listener);

    void removeRoundEndListener(RoundEndListener listener);

    void addGameEndListener(GameEndListener listener);

    void removeGameEndListener(GameEndListener listener);
}
