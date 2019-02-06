package menuapp;


import datebase.DbHelper;
import menuapp.Listener.GameEndListener;
import menuapp.Listener.RoundEndListener;

import java.util.ArrayList;


public class QuizModel implements IQuizModel{

private int currentQuestion = 1;
private int currentRound = 1;
private int totalQuestionsInRound;
private int totalQuestions;
private int gameState = GameState.WAITING_STATE;

private ArrayList<RoundEndListener> roundEndListeners = new ArrayList<>();
private ArrayList<GameEndListener> gameEndListeners = new ArrayList<>();


    public QuizModel() {
        totalQuestionsInRound = DbHelper.getQuestionTotalByRound(currentRound);
        totalQuestions = DbHelper.getQuestionTotalQuestions();
    }

    public void setGameState(int state){
        gameState = state;
    }

    public Boolean checkAnswer(int questionId, String answer){
        boolean correct = false;
        Question question = DbHelper.getQuestion(questionId);
        if(String.valueOf(question.getAnswer().toLowerCase().charAt(0)).equals(answer.toLowerCase())){
            correct = true;
        }
        return correct;
    }

    public void nextQuestion(){
        if(currentQuestion == totalQuestionsInRound && currentQuestion < totalQuestions) {
            currentRound++;
            totalQuestionsInRound += DbHelper.getQuestionTotalByRound(currentRound);
            //Notify listeners end of the round
            for(RoundEndListener listener: roundEndListeners){
                listener.onRoundEnd();
            }

        }else if(currentQuestion == totalQuestions) {
            //END GAME
            for(GameEndListener listener: gameEndListeners){
                listener.onGameEnd();
            }
        }
        currentQuestion++;
    }

    public int getTeamScore(String id){
        return  DbHelper.getTeam(id).getScore();
    }


    public ArrayList<Question> getAllQuestions(){
        return DbHelper.getAllQuestions();
    }

    public int getCurrentRound() {
        return currentRound;
    }

    @Override
    public int getTotalQuestionsInCurrentRound(){
        return DbHelper.getQuestionTotalByRound(currentRound);
    }

    public Question getCurrentQuestion(){
        return DbHelper.getQuestion(currentQuestion);
    }

    public int getGameState() {
        return gameState;
    }

    @Override
    public void addRoundEndListener(RoundEndListener listener) {
        roundEndListeners.add(listener);
    }

    @Override
    public void removeRoundEndListener(RoundEndListener listener) {
        roundEndListeners.remove(listener);
    }

    @Override
    public void addGameEndListener(GameEndListener listener) {
        gameEndListeners.add(listener);
    }

    @Override
    public void removeGameEndListener(GameEndListener listener) {
        gameEndListeners.remove(listener);
    }
}
