package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class UiApp extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage stage) throws Exception { 
        controllers.ViewTransitionalModel.getInstance().setPrimaryStage(stage);

        Parent root = FXMLLoader.load(getClass().getResource("/views/TournamentsListView.fxml"));
        stage.setScene(new Scene(root));
        stage.setTitle("Tournaments");
        stage.show();
    } //To run use this in terminal: mvn -DskipTests clean javafx:run 
}
