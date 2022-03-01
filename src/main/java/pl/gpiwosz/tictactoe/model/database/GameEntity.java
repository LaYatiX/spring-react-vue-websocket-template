package pl.gpiwosz.tictactoe.model.database;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameEntity {
  @Id
  private String id;
  private String title;
  private String player1;
  private String player2;
  private String status;
  @Column(length = 9000, columnDefinition = "TEXT")
  private String moves;
  private String playerWin;
}
