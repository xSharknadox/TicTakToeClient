package sample.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import sample.dto.GameDTO;
import sample.dto.PlayerMove;

import java.io.IOException;

import static sample.Main.SERVER_PATH;
import static sample.Main.restTemplate;

public class GameController {

    @FXML
    public GridPane gameGrid;

    private Button[][] field;
    private Image playerOneImage, playerTwoImage;
    private GameDTO gameResponce;

    public void startGame(GameDTO gameDTO) {
        gameResponce = gameDTO;

        playerOneImage = new Image("pepe-or.png");
        playerTwoImage = new Image("pepe-top.png");
        int x = gameDTO.getField()[0].length;
        int y = gameDTO.getField().length;

        final int prefSize = Math.min((700 / x), (700 / y));
        field = new Button[x][y];
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                final Button button = new Button();
                button.setId(i + "," + j);
                button.setPrefSize(prefSize, prefSize);
                button.setOnAction(event -> {
                    updateField(gameResponce.getField(), prefSize);
                    if (gameResponce.isGameEnded()) {
                        String winner = restTemplate.postForObject(SERVER_PATH + "/winner", gameResponce.getPlayer(), String.class);
                        endGame(event, prefSize, winner);
                    }
                    String id = ((Button) event.getSource()).getId();
                    String[] coordinates = id.split(",");
                    gameResponce = restTemplate.postForObject(SERVER_PATH + "/make_move", new PlayerMove(gameResponce.getPlayer(), Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1])), GameDTO.class);
                    updateField(gameResponce.getField(), prefSize);
                    waitForMove();
                    updateField(gameResponce.getField(), prefSize);
                    if (gameResponce.isGameEnded()) {
                        String winner = restTemplate.postForObject(SERVER_PATH + "/winner", gameResponce.getPlayer(), String.class);
                        endGame(event, prefSize, winner);
                    }
                });
                field[i][j] = button;
                gameGrid.add(field[i][j], i, j);
            }
        }
    }

    public void waitForMove() {
        while (!gameResponce.getNextMove().equals(gameResponce.getPlayer().getPlayerSymbol())) {
            long start = System.currentTimeMillis();
            System.out.println(start);
            long end;
            do {
                end = System.currentTimeMillis();
            }
            while (end - start <= 1000);
            gameResponce = restTemplate.postForObject(SERVER_PATH + "/game_progress", gameResponce.getPlayer(), GameDTO.class);
            if (gameResponce.isGameEnded()) {
                break;
            }
        }
    }

    private void endGame(ActionEvent event, int prefSize, String winner) {
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("winner.fxml"));
            root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Pepe vs Pepe");
            stage.setScene(new Scene(root, 300, 320));
            stage.show();
            WinnerController winnerController = loader.getController();
            winnerController.winnerInit(getPlayerImage(prefSize, winner));
            ((Node) (event.getSource())).getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateField(String[][] responceField, int prefSize) {
        for (int i = 0; i < responceField.length; i++) {
            for (int j = 0; j < responceField[i].length; j++) {
                System.out.println(responceField[i][j]);
                setPlayerMoveImage(prefSize, (Button) gameGrid.getScene().lookup("#" + i + "," + j), responceField[i][j]);
            }
        }
    }

    private void setPlayerMoveImage(int prefSize, Button button, String symbol) {
        Image playerImage = getPlayerImage(prefSize, symbol);
        ImageView imageView = new ImageView(playerImage);
        imageView.setFitHeight(prefSize);
        imageView.setFitWidth(prefSize);
        button.setStyle("-fx-padding: 0;\n" +
                "    -fx-border-style: none;\n" +
                "    -fx-border-width: 0;\n" +
                "    -fx-border-insets: 0;");
        button.setGraphic(imageView);
    }

    private Image getPlayerImage(int prefSize, String symbol) {
        Image playerImage;
        switch (symbol) {
            case "X":
                playerImage = playerOneImage;
                break;
            case "O":
                playerImage = playerTwoImage;
                break;
            default:
                playerImage = null;
                break;
        }
        return playerImage;
    }
}
