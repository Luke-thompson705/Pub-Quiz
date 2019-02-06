package menuapp;

import datebase.DbHelper;
import datebase.dbConnection;
import server.IServer;
import server.Server;
import server.listeners.ReceiveAnswerListener;
import server.listeners.TeamListener;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MenuModel implements TeamListener {

    private Connection connection;
    private IServer server;
    private int currentQuestion;
    private int currentRound;

    public MenuModel()
    {
        server = Server.getInstance();

        server.addTeamListener(this);

        currentQuestion = 1;
        currentRound = 1;

        try
        {
            this.connection = dbConnection.getConnection();
            connection.setAutoCommit(false);
        }
        catch (SQLException ex)
        {
            Logger.getLogger(MenuModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (this.connection == null) {
            System.exit(1);
        }
    }

    @Override
    public void onAddTeam(String id, String name) {
        try {
            DbHelper.insertTeamIntoDatabase(id, name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public boolean isDatabaseConnected()
    {
        return this.connection != null;
    }

    /*
    @Override
    public void onReceivedRequest(String id, String answer) {
        /*
        String[] questionInfo = DbHelper.getQuestion(currentQuestion, currentRound);
        String questionAnswer = questionInfo[1];
        answer = answer.toLowerCase();
        questionAnswer = String.valueOf(questionAnswer.charAt(0));
        questionAnswer = questionAnswer.toLowerCase();
        if(answer.equals(questionAnswer)){
            DbHelper.updateTeamScore(id);
            System.out.println("Correct answer");
        }else {
            System.out.println("Incorrect answer");
        }
        currentQuestion++;

    }
    */
}
