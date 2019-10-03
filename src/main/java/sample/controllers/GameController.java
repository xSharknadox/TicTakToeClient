package sample.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import sample.dto.GameDTO;
import sample.dto.Player;
import sample.dto.PlayerMove;

import java.io.IOException;

import static sample.Main.SERVER_PATH;
import static sample.Main.restTemplate;

public class GameController implements EventHandler<ActionEvent> {

    @FXML
    public GridPane gameGrid;

    private Button[][] field;
    private Image playerOneImage, playerTwoImage;
    private GameDTO gameResponce;
    private int prefSize;
    private Player player;
    private boolean canMakeMove = false;

    public void startGame(GameDTO gameDTO) {
        gameResponce = gameDTO;
        player = gameDTO.getPlayer();

        playerOneImage = new Image("pepe-or.png");
        playerTwoImage = new Image("pepe-top.png");
        int x = gameDTO.getField()[0].length;
        int y = gameDTO.getField().length;

        prefSize = Math.min((700 / x), (700 / y));
        field = new Button[x][y];
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                final Button button = new Button();
                button.setId(i + "," + j);
                button.setPrefSize(prefSize, prefSize);
                button.setOnAction(this);
                field[i][j] = button;
                gameGrid.add(field[i][j], i, j);
            }
        }

        if(player.getPlayerSymbol().equals("X")){
            canMakeMove = true;
        } else {
            Platform.runLater(this::waitForMove);
        }
    }

    public void endGame(String winner) {
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("winner.fxml"));
            root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Pepe vs Pepe");
            stage.setScene(new Scene(root, 300, 320));
            stage.show();
            WinnerController winnerController = loader.getController();
            winnerController.winnerInit(getPlayerImage(winner));
            gameGrid.getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateField(String[][] responseField) {
        for (int i = 0; i < responseField.length; i++) {
            for (int j = 0; j < responseField[i].length; j++) {
                setPlayerMoveImage(prefSize, (Button) gameGrid.getScene().lookup("#" + i + "," + j), responseField[i][j]);
            }
        }
    }

    private void setPlayerMoveImage(int prefSize, Button button, String symbol) {
        Image playerImage = getPlayerImage(symbol);
        ImageView imageView = new ImageView(playerImage);
        imageView.setFitHeight(prefSize);
        imageView.setFitWidth(prefSize);
        button.setStyle("-fx-padding: 0;\n" +
                "    -fx-border-style: none;\n" +
                "    -fx-border-width: 0;\n" +
                "    -fx-border-insets: 0;");
        button.setGraphic(imageView);
    }

    private Image getPlayerImage(String symbol) {
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

    @Override
    public void handle(ActionEvent actionEvent) {
        if (canMakeMove) {
            String id = ((Button) actionEvent.getSource()).getId();
            String[] coordinates = id.split(",");
            gameResponce = restTemplate.postForObject(SERVER_PATH + "/make_move", new PlayerMove(gameResponce.getPlayer(), Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1])), GameDTO.class);
            updateField(gameResponce.getField());
            canMakeMove = false;
            Platform.runLater(this::waitForMove);
        }
    }

    private void waitForMove() {
        GameDTO gameDTO;

        while (!canMakeMove) {
            waitBeforeSendRequest();
            gameDTO = restTemplate.postForObject(SERVER_PATH + "/game_progress", player, GameDTO.class);

            if (gameDTO.isGameEnded()) {
                String winner = restTemplate.postForObject(SERVER_PATH + "/winner", gameDTO.getPlayer(), String.class);
                endGame(winner);
                break;
            }

            updateField(gameDTO.getField());
            if (gameDTO.getNextMove().equals(player.getPlayerSymbol())) {
                canMakeMove = true;
            }
        }

    }

    private void waitBeforeSendRequest() {
        long start = System.currentTimeMillis();
        long end;
        do {
            end = System.currentTimeMillis();
        }
        while (end - start <= 500);
    }
}
