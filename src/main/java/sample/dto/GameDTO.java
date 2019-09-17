package sample.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GameDTO {
    private Player player;
    private String[][] field;
    private String nextMove;
    private boolean gameEnded;
}
