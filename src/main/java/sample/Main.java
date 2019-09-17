package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import sample.controllers.MainController;

public class Main extends Application {

    public static final String SERVER_PATH = "http://192.168.0.105:8080";
    public static final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void start(Stage primaryStage) throws Exception {
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("main.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Pepe vs Pepe");
        primaryStage.setScene(new Scene(root, 500, 400));
        primaryStage.show();
        MainController mainController = fxmlLoader.getController();
        mainController.loadMainWindowData();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
