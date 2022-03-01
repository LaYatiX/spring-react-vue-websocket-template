package pl.gpiwosz.tictactoe.model;

import lombok.Data;

@Data
public class GameRequest {
  Game game;
  String username;
}
