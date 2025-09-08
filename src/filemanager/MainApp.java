package filemanager;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private FileManagerView fileManagerView;

    @Override
    public void start(Stage primaryStage) {
        fileManagerView = new FileManagerView();
        Scene scene = new Scene(fileManagerView.getMainLayout(), 800, 600);
        primaryStage.setTitle("Gestionnaire de Fichiers Favoris");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}