package sample.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;


public class WinnerController {

    @FXML
    public ImageView winnerImage;
    public Label winnerText;
    public GridPane winnerGrid;
    public Button playAgainButton;

    public void winnerInit(Image image) {
        winnerImage.setImage(image);
        GridPane.setColumnIndex(winnerImage, 0);
        GridPane.setRowIndex(winnerImage, 0);
        GridPane.setHalignment(winnerImage, HPos.CENTER);

        winnerText.setAlignment(Pos.CENTER);
        winnerText.setFont(Font.font("Verdana", 18));
        GridPane.setColumnIndex(winnerText, 0);
        GridPane.setRowIndex(winnerText, 1);
        GridPane.setHalignment(winnerText, HPos.CENTER);
        GridPane.setHgrow(winnerText, Priority.ALWAYS);

        GridPane.setColumnIndex(playAgainButton, 0);
        GridPane.setRowIndex(playAgainButton, 2);
        GridPane.setHalignment(playAgainButton, HPos.CENTER);
    }

    public void playAgain(ActionEvent actionEvent) {
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("main.fxml"));
            root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Pepe vs Pepe");
            stage.setScene(new Scene(root, 500, 500));
            stage.show();
            ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
            MainController mainController = loader.getController();
            mainController.loadMainWindowData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
