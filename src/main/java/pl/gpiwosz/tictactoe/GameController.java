package pl.gpiwosz.tictactoe;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.gpiwosz.tictactoe.model.Game;
import pl.gpiwosz.tictactoe.model.GameRequest;
import pl.gpiwosz.tictactoe.model.MoveRequest;
import pl.gpiwosz.tictactoe.model.database.GameEntity;
import pl.gpiwosz.tictactoe.model.database.UserEntity;
import pl.gpiwosz.tictactoe.repositories.GameRepository;
import pl.gpiwosz.tictactoe.repositories.UserRepository;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@ResponseBody
public class GameController {

  private final GameService gameService;
  private final GameRepository gameRepository;
  private final UserRepository userRepository;

  GameController(GameService gameService, GameRepository gameRepository, UserRepository userRepository) {
    this.gameService = gameService;
    this.gameRepository = gameRepository;
    this.userRepository = userRepository;
  }

  @GetMapping("/api/gamesHistory")
  public List<GameEntity> gamesHistory(@RequestParam("username") String username) {
    return this.gameRepository.findAllByPlayer1OrPlayer2(username, username);
  }

  @MessageMapping("/initPrincipal")
  public void greeting(String message, @Header("simpSessionId") String sessionId, Principal principal) {
    log.info("Received greeting message {} from {} with sessionId {}", message, principal.getName(), sessionId);
    gameService.addUserName(principal.getName());
    if(!this.userRepository.findByUsername(message).isPresent())
      this.userRepository.save(new UserEntity(UUID.randomUUID().toString(), message));
  }

  @MessageMapping("/newGame")
  @SendTo("/app/games")
  public List<Game> newGame(Game game, Principal principal) {
    log.info("Game {} ", game);
    return gameService.addNewGame(game, principal.getName());
  }

  @MessageMapping("/getGames")
  @SendTo("/app/games")
  public List<Game> games(String message) {
    return gameService.getActiveGames();
  }

  @MessageMapping("/joinGame")
  @SendTo("/app/games")
  public List<Game> games(GameRequest game, Principal principal) {
    return gameService.joinGame(game, principal);
  }

  @MessageMapping("/leaveGame")
  @SendTo("/app/games")
  public List<Game> leaveGame(Game game, Principal principal) {
    return gameService.leaveGame(game, principal);
  }

  @MessageMapping("/makeMove")
  @SendTo("/app/games")
  public List<Game> makeMove(MoveRequest move) {
    return gameService.makeMove(move);
  }
}
