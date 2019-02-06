package menuapp;

import datebase.DbHelper;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import server.Server;

public class MenuApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("newMenu.fxml"));

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Quiz Desktop");
        primaryStage.show();
        DbHelper.clearPlayerTable();
        primaryStage.setResizable(false);

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                Server.getInstance().stop();
            }
        });
    }

    public static void main(String[] args){
        launch(args);
    }
}
