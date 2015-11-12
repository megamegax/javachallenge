package hu.javachallenge;

import hu.javachallenge.main.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Main extends Application {

    private MainController mainController;
    private Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.stage = primaryStage;
        repalceSceneContentToMain();
        stage.show();
    }

    private Parent repalceSceneContentToMain() throws IOException {
        URL location = getClass().getResource("/layouts/main.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(location);
        fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
        Parent page = fxmlLoader.load();
        mainController = fxmlLoader.getController();
        Scene scene = stage.getScene();
        if (scene == null) {
            scene = new Scene(page);
            stage.setScene(scene);
        } else {
            stage.getScene().setRoot(page);
        }
        stage.setTitle("JavaChallenge");
        stage.sizeToScene();
        return page;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
