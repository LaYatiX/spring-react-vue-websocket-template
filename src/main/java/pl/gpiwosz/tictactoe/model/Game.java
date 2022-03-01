package pl.gpiwosz.tictactoe.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Game {
  String id;
  String title;
  Player player1;
  Player player2;
  List<Move> moves;
  String status;
}
