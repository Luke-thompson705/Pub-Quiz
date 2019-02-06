package datebase;

import menuapp.MenuModel;
import menuapp.Question;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DbHelper {


    private static Connection openConnection(){
        Connection connection = null;
        try {
            connection = dbConnection.getConnection();
            connection.setAutoCommit(false);
        } catch (SQLException ex) {
            Logger.getLogger(MenuModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return connection;
    }

    public static void insertTeamIntoDatabase(String id, String teamName) throws SQLException {
        Connection connection = openConnection();
        PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO players(id ,team_name) VALUES(?,?)");
        statement.setString(1, id);
        statement.setString(2, teamName);
        try {
            statement.executeUpdate();
            connection.commit();
            System.out.println(teamName + " is added to the database");
        } catch (Exception e) {
            System.out.println("Can't add same team to the database");
        }
    }

    public static Question getQuestion(int questionID) {
        Connection connection = openConnection();
        Question question = null;
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM questions;");

            while (rs.next()) {
                int id = rs.getInt("id");
                if(id == questionID){
                    int questionId = rs.getInt("id");
                    String currentQuestion = rs.getString("question");
                    String answer = rs.getString("answer");
                    int currentRound = rs.getInt("round");
                    int time = rs.getInt("time");
                    question = new Question(questionId,currentQuestion,answer,currentRound,time);
                }
            }
            rs.close();
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            Logger.getLogger(MenuModel.class.getName()).log(Level.SEVERE, null, ex);
        }

        return question;
    }

    public static boolean checkTeamExist(String id) {
        Connection connection = openConnection();;
        Boolean exist = false;
        String queryId;
        try {

            Statement statement = connection.createStatement();

            ResultSet rs = statement.executeQuery("SELECT * FROM players;");

            while (rs.next()) {
                queryId = rs.getString("id");
                System.out.println(queryId + " " + id);
                if (queryId.equals(id))
                    exist = true;
            }
            rs.close();
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            Logger.getLogger(MenuModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return exist;
    }

    public static void updateTeamScore(String id) {
        Connection connection = openConnection();
        int queryScore = 0;

        String sql = "UPDATE players SET score = ? "
                + "WHERE id = ?";
        try {

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM players;");
            while (rs.next()) {
                queryScore = rs.getInt("score");
            }
            PreparedStatement pstmt = connection.prepareStatement(sql);
            {
                queryScore = queryScore+1;
                pstmt.setInt(1, queryScore);
                pstmt.setString(2, id);
                pstmt.executeUpdate();
                connection.commit();
            }
            rs.close();
            statement.close();
            pstmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Question> getAllQuestions(){
        Connection connection = openConnection();
        ArrayList<Question> questions = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM questions;");

            while (rs.next()) {
                int id = rs.getInt("id");
                String question = rs.getString("question");
                String answer = rs.getString("answer");
                int round = rs.getInt("round");
                int time = rs.getInt("time");
                questions.add(new Question(id,question,answer,round,time));
            }

            rs.close();
            statement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questions;
    }

    public static Team getTeam(String id) {
        Connection connection = openConnection();
        Team team = null;
        String queryId;
        String teamName;
        int score;
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM players;");
            while (rs.next()) {
                queryId = rs.getString("id");
                teamName = rs.getString("team_name");
                score = rs.getInt("score");
                if (queryId.equals(id))
                    team = new Team(id,teamName,score);
            }
            rs.close();
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            Logger.getLogger(MenuModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return team;
    }

    public static ArrayList<Team> getAllTeams() {
        Connection connection = openConnection();
        Team team;
        String queryId;
        String teamName;
        int score;
        ArrayList<Team> teamScores = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM players;");
            while (rs.next()) {
                queryId = rs.getString("id");
                teamName = rs.getString("team_name");
                score = rs.getInt("score");
                team = new Team(queryId,teamName,score);
                teamScores.add(team);
            }
            rs.close();
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            Logger.getLogger(MenuModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return teamScores;
    }

    public static int getTotalAmountRounds(int round){
        return 0;
    }

    public static int getQuestionTotalByRound(int round){
        int counter = 0;
        Connection connection = openConnection();
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery( "SELECT round FROM questions;");
            while (rs.next()) {
                int roundNumber = rs.getInt("round");
                if(roundNumber == round){
                    counter++;
                }
            }
            rs.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return counter;
    }

    public static int getQuestionTotalQuestions(){
        int counter = 0;
        Connection connection = openConnection();
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery( "SELECT round FROM questions;");
            while (rs.next()) {
                counter++;
            }
            rs.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return counter;
    }

    public static void clearPlayerTable(){
        Connection connection = openConnection();

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM players;");
            statement.close();
            connection.commit();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}

