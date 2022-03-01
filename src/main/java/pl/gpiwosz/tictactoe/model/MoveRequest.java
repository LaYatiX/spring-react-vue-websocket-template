package pl.gpiwosz.tictactoe.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MoveRequest {
  Game game;
  Move move;
  String username;
}
