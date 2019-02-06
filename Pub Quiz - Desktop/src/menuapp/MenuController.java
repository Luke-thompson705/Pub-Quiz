package menuapp;

import datebase.DbHelper;
import datebase.Team;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.sql.SQLException;
import java.util.*;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import menuapp.Listener.GameEndListener;
import menuapp.Listener.RoundEndListener;
import server.ClientMessage;
import server.IServer;
import server.Server;
import server.listeners.*;


public class MenuController implements Initializable , TeamListener, ReceiveAnswerListener, ClientConnectListener, QuestionRequestListener, ClientReconnectListener, ClientDisconnectListener, RoundEndListener, ReceiveCurrentPositionListener, ReceivePointsListener ,GameEndListener  {

    //Model
    private IQuizModel quizModel = new QuizModel();

    private IServer server;

    //FXML
    @FXML
    private Label roundLabel;
    @FXML
    private Label totalQuestionsLabel;
    @FXML
    private Label inputTypeLabel;
    @FXML
    private Label timerLabel;

    @FXML
    private Button startRoundButton;
    @FXML
    private Button hostQuizButton;
    @FXML
    private Button QuestionsButton;

    @FXML
    private TextArea questionTextArea;

    @FXML
    private ListView teamListView;


    private Question currentQuestion;

    private ArrayList<Label> teamLabels = new ArrayList<>();

    //timer for questions
    Timer questionTimer;
    TimerTask countDownTimerTask;
    int elapsedTime;
    Boolean roundEnd = false;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        server = Server.getInstance();

        //Add sever listeners
        server.addTeamListener(this);
        server.addReceiveAnswerListener(this);
        server.addClientConnectListener(this);
        server.addClientReconnectListener(this);
        server.addClientDisconnectListener(this);
        server.addRequestListener(this);
        server.addReceiveCurrentPositionListener(this);
        server.addReceivePointsListener(this);

        //Add model listeners
        quizModel.addRoundEndListener(this);
        quizModel.addGameEndListener(this);

        questionTextArea.setWrapText(true);
        questionTextArea.setEditable(false);

        currentQuestion = quizModel.getCurrentQuestion();
        showQuestion();
    }


    @FXML
    public void onHostAction(ActionEvent event) throws SQLException {
        Runnable r = () -> server.start();
        new Thread(r).start();
        hostQuizButton.setDisable(true);
    }

    @FXML
    public void onStartRoundAction(ActionEvent event) throws SQLException {
        if(hostQuizButton.isDisable()) {
            roundEnd = false;
            quizModel.setGameState(GameState.GAME_STATE);
            server.sendGameState(quizModel.getGameState());
            sendQuestion();
            questionCountdownTimer();
            startRoundButton.setDisable(true);
        }
    }

    @FXML
    public void onQuestionAction(ActionEvent event){

    }


    @Override
    public void onAddTeam(String id, String name) {
        Team newTeam = DbHelper.getTeam(id);
        Label teamLabel = new Label(newTeam.getName());
        teamLabel.setFont(new Font(16));
        Platform.runLater(() -> teamListView.getItems().add(teamLabel));
        teamLabels.add(teamLabel);
    }


    @Override
    public void onReceived(String teamId, int questionId, String answer){
        if(quizModel.checkAnswer(questionId, answer)){
            DbHelper.updateTeamScore(teamId);
        }
    }

    private void showQuestion() {
        Platform.runLater(() -> {
            if(currentQuestion != null) {
                //Top
                roundLabel.setText("Round: " + currentQuestion.getRound());
                totalQuestionsLabel.setText("Total Questions in Round: " + quizModel.getTotalQuestionsInCurrentRound());
                inputTypeLabel.setText("Input Type: Alphabet");

                //Bottom
                questionTextArea.setText("Question: " + "\n" + currentQuestion.getQuestion() + "\n\n" + "Answer: " + "\n" + currentQuestion.getAnswer());
                timerLabel.setText(String.valueOf(quizModel.getCurrentQuestion().getTime()));
            }else {
                //Top
                roundLabel.setText("Round: END");
                totalQuestionsLabel.setText("Total Questions: END");
                inputTypeLabel.setText("Input Type: Alphabet");

                //Bottom
                questionTextArea.setText("END OF QUIZ!");
                timerLabel.setText("END");
            }
        });
    }

    @Override
    public void onConnected() {

    }

    private void questionCountdownTimer(){
        if(currentQuestion != null) {
            questionTimer = new Timer();
            elapsedTime = currentQuestion.getTime();
            countDownTimerTask = new TimerTask() {
                public void run() {
                    if (!roundEnd) {
                        Platform.runLater(() -> timerLabel.setText(String.valueOf(elapsedTime)));
                        elapsedTime -= 1;
                        if (elapsedTime <= 0) {
                            server.requestAnswers();
                            quizModel.nextQuestion();
                            sendQuestion();
                            pauseTimer();
                            questionCountdownTimer();
                        }
                    } else {
                        pauseTimer();
                    }
                }
            };
            questionTimer.scheduleAtFixedRate(countDownTimerTask, 500, 1000);
        }
    }

    private void sendQuestion(){
        currentQuestion = quizModel.getCurrentQuestion();
        showQuestion();
        if(currentQuestion != null) {
            server.sendQuestion(currentQuestion.getId(), currentQuestion.getQuestion(), currentQuestion.getTime());
        }
    }

    //Needs for individual client
    @Override
    public void onRequest() {
        server.sendQuestion(currentQuestion.getId(), currentQuestion.getQuestion(),elapsedTime);
    }

    @Override
    public void onReconnect(String id) {
        server.sendToClient(id, ClientMessage.RECONNECTED, String.valueOf(quizModel.getGameState()));

        if(DbHelper.checkTeamExist(id)){
            Label teamLabel = new Label(DbHelper.getTeam(id).getName());
            teamLabels.add(teamLabel);
            server.sendGameStateToClient(id, quizModel.getGameState());
            Platform.runLater(() -> teamListView.getItems().add(teamLabel));
        }
    }

    @Override
    public void onDisconnected(String id) {
        if (id != null) {
            String teamName = DbHelper.getTeam(id).getName();
            if(teamName != null) {
                Iterator<Label> teamLabelIterator = teamLabels.iterator();
                while (teamLabelIterator.hasNext()) {
                    Label teamLabel = teamLabelIterator.next();
                    if (teamLabel.getText().equals(teamName)) {
                        Platform.runLater(() -> teamListView.getItems().remove(teamLabel));
                        teamLabelIterator.remove();
                    }
                }
            }
        }
    }

    //Save some code by having this - repeated.
    private void addTeamtoLabel(String name){

    }

    @Override
    public void onRoundEnd() {
        roundEnd = true;
        pauseTimer();
        quizModel.setGameState(GameState.SUMMARY_STATE);
        server.sendGameState(quizModel.getGameState());
        Platform.runLater(() -> timerLabel.setText(String.valueOf(currentQuestion.getTime())));
        startRoundButton.setDisable(false);
    }

    private void pauseTimer(){
        questionTimer.cancel();
        questionTimer.purge();
        countDownTimerTask.cancel();
    }

    @Override
    public void onReceivedRequest(String id) {
        server.sendRoundSummary(id);
    }

    @Override
    public void onGameEnd() {
        roundEnd = true;
        pauseTimer();
        quizModel.setGameState(GameState.END_STATE);
        server.sendGameState(quizModel.getGameState());
        startRoundButton.setDisable(false);
    }


    @Override
    public void onRecievePoints(String id) {
        server.sendToClient(id,ClientMessage.RECIEVEPOINTS, String.valueOf(quizModel.getTeamScore(id)));
    }
}
