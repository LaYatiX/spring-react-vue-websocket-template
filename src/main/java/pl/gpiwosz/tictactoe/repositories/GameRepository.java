package pl.gpiwosz.tictactoe.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.gpiwosz.tictactoe.model.database.GameEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface GameRepository extends JpaRepository<GameEntity, UUID> {
  List<GameEntity> findAllByPlayer1OrPlayer2(String player1, String player2);
}
