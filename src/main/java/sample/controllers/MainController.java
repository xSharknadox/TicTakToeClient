package sample.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import sample.dto.GameDTO;

import java.io.IOException;

import static sample.Main.SERVER_PATH;
import static sample.Main.restTemplate;

public class MainController {

    @FXML
    public ImageView mainImage, mainImage2;
    public Label taklabel;
    public Button playButton;
    public Button closeButton;
    public GridPane mainGrid;


    public void loadMainWindowData() {
        Image image = new Image("pepe-or.png");
        mainImage.setImage(image);
        GridPane.setColumnIndex(mainImage, 0);
        GridPane.setRowIndex(mainImage, 0);
        GridPane.setHalignment(mainImage, HPos.RIGHT);

        taklabel.setText("VS");
        taklabel.setFont(Font.font("Verdana", 20));
        taklabel.setAlignment(Pos.CENTER);
        GridPane.setColumnIndex(taklabel, 1);
        GridPane.setRowIndex(taklabel, 0);
        GridPane.setHalignment(taklabel, HPos.CENTER);
        GridPane.setValignment(taklabel, VPos.CENTER);

        image = new Image("pepe-top.png");
        mainImage2.setImage(image);
        GridPane.setColumnIndex(mainImage2, 2);
        GridPane.setRowIndex(mainImage2, 0);
        GridPane.setHalignment(mainImage2, HPos.LEFT);

        GridPane.setColumnIndex(playButton, 0);
        GridPane.setRowIndex(playButton, 1);

        GridPane.setColumnIndex(closeButton, 2);
        GridPane.setRowIndex(closeButton, 1);
    }

    public void play(ActionEvent actionEvent) {
        GameDTO gameDTO = restTemplate.getForObject(SERVER_PATH + "/start_game", GameDTO.class);
        startGame(gameDTO);
    }

    public void closeProgram(ActionEvent actionEvent) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }


    private void startGame(GameDTO gameDTO) {
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("game.fxml"));
            root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("You play by '" + gameDTO.getPlayer().getPlayerSymbol() + "'");
            stage.setScene(new Scene(root, 700, 700));
            stage.show();
            GameController gameController = loader.getController();
            mainGrid.getScene().getWindow().hide();
            gameController.startGame(gameDTO);
            if(gameDTO.getPlayer().getPlayerSymbol().equals("O"))
                gameController.waitForMove();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
