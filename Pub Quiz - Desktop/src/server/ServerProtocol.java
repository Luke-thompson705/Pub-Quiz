package server;

import datebase.DbHelper;

import java.sql.SQLException;

public class ServerProtocol {
    public static final int WAITING = 0;
    public static final int ADDTEAM = 1;
    public static final int STARTNEWGAME = 2;
    public static final int GETQUESTION = 3;

    public void processInput(String input) {
        int state = Integer.parseInt(input.split(",")[0]);
        String message = input.split(",")[1];
        switch (state) {
            case WAITING:
                System.out.println("Connected to game! please enter a team name!");
                break;
            case ADDTEAM:
                String id = input.split(",")[2];
                String teamName = message;
                System.out.println("Adding new team: " + teamName);
                try {
                    DbHelper.insertTeamIntoDatabase(id, teamName);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case STARTNEWGAME:
                break;
            case GETQUESTION:
                System.out.println("PING");
                break;
        }

    }
}
