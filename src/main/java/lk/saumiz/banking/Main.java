package lk.saumiz.banking;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lk.saumiz.banking.db.DBConnection;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(getClass().getResource("/view/bank-theme.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setTitle("Smart Banking Management System");
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    @Override
    public void stop() {
        // release pooled DB connections cleanly when the app closes
        DBConnection.getInstance().shutdown();
    }

    public static void main(String[] args) {
        launch(args);
    }
}