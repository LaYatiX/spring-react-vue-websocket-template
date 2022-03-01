package pl.gpiwosz.tictactoe;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import pl.gpiwosz.tictactoe.model.Game;
import pl.gpiwosz.tictactoe.model.GameRequest;
import pl.gpiwosz.tictactoe.model.MoveRequest;
import pl.gpiwosz.tictactoe.model.Player;
import pl.gpiwosz.tictactoe.model.database.GameEntity;
import pl.gpiwosz.tictactoe.repositories.GameRepository;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Data
@Slf4j
@Service
public class GameService {
  private final SimpMessagingTemplate simpMessagingTemplate;
  private final GameRepository gameRepository;
  private static final String SIMP_SESSION_ID = "simpSessionId";
  private static final String WS_MESSAGE_TRANSFER_DESTINATION = "/topic/greetings";
  private static final String WS_CURRENT_GAME_DESTINATION = "/app/currentGame";
  private static final String WS_GAME_RESULT_DESTINATION = "/app/gameResult";
  private List<String> userNames = new ArrayList<>();
  private List<Game> activeGames = new ArrayList<>();

  GameService(SimpMessagingTemplate simpMessagingTemplate, GameRepository gameRepository) {
    this.simpMessagingTemplate = simpMessagingTemplate;
    this.gameRepository = gameRepository;
  }

  public void addUserName(String username) {
    userNames.add(username);
  }

  /**
   * Handles WebSocket connection events
   */
  @EventListener(SessionConnectEvent.class)
  public void handleWebsocketConnectListener(SessionConnectEvent event) {
    log.info(String.format("WebSocket connection established for sessionID %s",
      getSessionIdFromMessageHeaders(event)));
  }

  /**
   * Handles WebSocket disconnection events
   */
  @EventListener(SessionDisconnectEvent.class)
  public void handleWebsocketDisconnectListener(SessionDisconnectEvent event) {
    log.info(String.format("WebSocket connection closed for sessionID %s",
      getSessionIdFromMessageHeaders(event)));
  }

  private String getSessionIdFromMessageHeaders(SessionDisconnectEvent event) {
    Map<String, Object> headers = event.getMessage().getHeaders();
    return Objects.requireNonNull(headers.get(SIMP_SESSION_ID)).toString();
  }

  private String getSessionIdFromMessageHeaders(SessionConnectEvent event) {
    Map<String, Object> headers = event.getMessage().getHeaders();
    return Objects.requireNonNull(headers.get(SIMP_SESSION_ID)).toString();
  }

  public List<Game> addNewGame(Game game, String name) {
    game.getPlayer1().setPrincipalId(name);
    this.activeGames.add(Game.builder()
      .id(game.getId())
      .title(game.getTitle())
      .moves(game.getMoves())
      .player1(game.getPlayer1())
      .player2(game.getPlayer2())
      .status(game.getStatus())
      .build()
    );
    this.sendCurrentGame(game);
    return this.activeGames;
  }

  public List<Game> leaveGame(Game game, Principal principal) {
    Optional<Game> optionalGame = this.activeGames.stream().filter(g -> g.getId().equals(game.getId())).findFirst();
    Game currentGame;
    if (optionalGame.isPresent())
      currentGame = optionalGame.get();
    else return this.activeGames;

    int index = this.activeGames.indexOf(currentGame);
    if (currentGame.getPlayer1() != null && currentGame.getPlayer1().getPrincipalId().equals(principal.getName())) {
      currentGame.setPlayer1(null);
    }
    if (currentGame.getPlayer2() != null && currentGame.getPlayer2().getPrincipalId().equals(principal.getName())) {
      currentGame.setPlayer2(null);
    }
    this.activeGames.set(index, currentGame);
    this.sendCurrentGame(currentGame);
    return this.activeGames;
  }

  private void sendCurrentGame(Game game) {
    if (game.getPlayer1() != null)
      simpMessagingTemplate.convertAndSendToUser(game.getPlayer1().getPrincipalId(), WS_CURRENT_GAME_DESTINATION, game);
    if (game.getPlayer2() != null)
      simpMessagingTemplate.convertAndSendToUser(game.getPlayer2().getPrincipalId(), WS_CURRENT_GAME_DESTINATION, game);
  }

  private void sendGameResult(Game game, Integer player1Result, Integer player2Result) {
    if (game.getPlayer1() != null)
      simpMessagingTemplate.convertAndSendToUser(game.getPlayer1().getPrincipalId(), WS_GAME_RESULT_DESTINATION, player1Result);
    if (game.getPlayer2() != null)
      simpMessagingTemplate.convertAndSendToUser(game.getPlayer2().getPrincipalId(), WS_GAME_RESULT_DESTINATION, player2Result);
  }

  public List<Game> joinGame(GameRequest gameRequest, Principal principal) {
    Game currentGame = this.activeGames.stream().filter(g -> g.getId().equals(gameRequest.getGame().getId())).findFirst().get();
    int index = this.activeGames.indexOf(currentGame);
    if (currentGame.getPlayer1() == null) {
      currentGame.setPlayer1(new Player(gameRequest.getUsername(), principal.getName()));
    } else if (currentGame.getPlayer2() == null) {
      currentGame.setPlayer2(new Player(gameRequest.getUsername(), principal.getName()));
    }
    this.activeGames.set(index, currentGame);
    this.sendCurrentGame(currentGame);
    return this.activeGames;
  }

  public List<Game> makeMove(MoveRequest move) {
    Optional<Game> optionalGame = this.activeGames.stream().filter(g -> g.getId().equals(move.getGame().getId())).findFirst();
    Game currentGame;
    if (optionalGame.isPresent()) {
      currentGame = optionalGame.get();
      int index = this.activeGames.indexOf(currentGame);
      currentGame.getMoves().add(move.getMove());
      this.activeGames.set(index, currentGame);
      this.sendCurrentGame(currentGame);

      boolean gameWon = checkWinConditions(move.getMove().getPositions());
      boolean gameEnd = Arrays.stream(move.getMove().getPositions()).reduce(0, (acc, curr) -> curr.equals(-1) ? ++acc : acc).equals(0);
      if (gameWon) {
        boolean player1Won = move.getUsername().equals(currentGame.getPlayer1().getUsername());
        this.sendGameResult(currentGame, player1Won ? 1 : 0, !player1Won ? 1 : 0);
        gameEnd = true;
      }
      if (!gameWon && gameEnd) {
        this.sendGameResult(currentGame, -1, -1);
      }
      if (gameEnd) {
        boolean player1Won = move.getUsername().equals(currentGame.getPlayer1().getUsername());
        currentGame.setStatus("DONE");
        gameRepository.save(GameEntity.builder()
          .id(currentGame.getId())
          .title(currentGame.getTitle())
          .player1(currentGame.getPlayer1().getUsername())
          .player2(currentGame.getPlayer2().getUsername())
          .status(currentGame.getStatus())
          .moves(JSONArray.toJSONString(currentGame.getMoves()))
          .playerWin(!gameWon ? "" : player1Won ? currentGame.getPlayer1().getUsername() : currentGame.getPlayer2().getUsername())
          .build());
      }
    }
    return this.activeGames;
  }

  private boolean checkWinConditions(Integer[] positions) {
    return (
      (positions[0].equals(positions[1]) && positions[0].equals(positions[2]) && positions[0] != -1) // górna linia
        ||
        (positions[3].equals(positions[4]) && positions[3].equals(positions[5]) && positions[3] != -1) // środkowa linia
        ||
        (positions[6].equals(positions[7]) && positions[6].equals(positions[8]) && positions[6] != -1) // dolna linia
        ||
        (positions[0].equals(positions[3]) && positions[0].equals(positions[6]) && positions[0] != -1) // lewia pionowa linia
        ||
        (positions[1].equals(positions[4]) && positions[1].equals(positions[7]) && positions[1] != -1) // środkowa pionowa linia
        ||
        (positions[2].equals(positions[5]) && positions[2].equals(positions[8]) && positions[2] != -1) // prawa pionowa linia
        ||
        (positions[0].equals(positions[4]) && positions[0].equals(positions[8]) && positions[0] != -1) // skos 1
        ||
        (positions[2].equals(positions[4]) && positions[2].equals(positions[6]) && positions[2] != -1) // skos 2
    );
  }
}
