package datebase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class dbConnection {

    private static final String SQCONN = "jdbo:sqlite:quiz.sqlite";

    public static Connection getConnection() throws SQLException{
        try{
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection("jdbc:sqlite:quiz.sqlite");
        }catch (Exception exc){
            exc.printStackTrace();
        }
        return null;
    }

}
