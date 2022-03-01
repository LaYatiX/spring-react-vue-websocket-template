import Vue from 'vue';
import Vuex from 'vuex';

Vue.use(Vuex);

export interface Move {
  positions: number[];
  username: string;
}
export interface Player {
  username: string;
  principalId: string;
}

export interface Game {
  id: string;
  title: string;
  player1: Player;
  player2: Player;
  moves: Move[];
  status: 'NEW' | 'GAME_IN_PROGRESS' | 'DONE';
}

export interface DialogData {
  gameResult: number;
  open: boolean;
}

export interface State {
  dialog: DialogData;
  currentUser: string;
  currentGame: Game;
  games: Game[];
}

export interface GameListItem {
  color: string;
  subtitle: string;
  title: string;
  playerCount: number;
}

const store = new Vuex.Store<State>({
  state: {
    dialog: {
      open: false,
      gameResult: -2,
    },
    currentUser: '',
    games: [],
    currentGame: {
      id: '',
      title: '',
      player1: {
        username: '',
        principalId: '',
      },
      player2: {
        username: '',
        principalId: '',
      },
      status: 'NEW',
      moves: [
        {
          username: '',
          positions: [-1, -1, -1, -1, -1, -1, -1, -1, -1],
        },
      ],
    },
  },
  actions: {
    tileClicked({ commit, state, getters }, positions) {
      commit('setCurrentGame', {
        ...state.currentGame,
        moves: [
          ...state.currentGame.moves,
          { username: state.currentUser, positions: positions },
        ],
      } as Game);
    },
  },
  mutations: {
    setCurrentGame(state, game: Game) {
      state.currentGame = game;
    },
    setGames(state, games: Game[]) {
      state.games = games;
    },
    setCurrentUser(state, user: string) {
      state.currentUser = user;
    },
    setDialog(state, dialogData: DialogData) {
      state.dialog = dialogData;
    },
  },
  getters: {
    currentGame: (state) => {
      return state.currentGame;
    },
    currentUser: (state) => {
      return state.currentUser;
    },
    currentGameMoves: (state) => {
      return state.currentGame.moves[state.currentGame.moves.length - 1]
        .positions;
    },
    games: (state) => {
      return state.games;
    },
    gamesList: (state): GameListItem[] => {
      return state.games.map((game) => {
        // @ts-ignore
        const playerCount = !!game.player1 + !!game.player2;
        return {
          game,
          color: game.status !== 'NEW' || playerCount === 2 ? 'red' : 'green',
          title: game.title,
          playerCount: playerCount,
          subtitle: 'Ilość graczy: ' + playerCount,
        };
      });
    },
    blocked: function (state) {
      if (state.currentGame)
        return (
          state.currentGame.moves[state.currentGame.moves.length - 1]
            .username === state.currentUser
        );
      else return true;
    },
    dialog: function (state) {
      return state.dialog;
    },
  },
});

import { WebsocketService } from '@/services/websocket-service';
const webSocketService = WebsocketService.getInstance();
const webSocketGamesEndpoint = '/app/games';
const webSocketCurrentGameEndpoint = '/user/app/currentGame';
const webSocketGreetingsSendEndpoint = '/app/getGames';
const webSocketGameResultEndpoint = '/user/app/gameResult';

webSocketService.connect(
  () => {
    if (webSocketService) {
      console.log('games', 'subscribe');
      webSocketService.subscribe(webSocketGamesEndpoint, (games) => {
        console.log(JSON.parse(games.body), 'receivedGames');
        store.commit('setGames', JSON.parse(games.body));
      });
      webSocketService.subscribe(webSocketCurrentGameEndpoint, (game) => {
        console.log(JSON.parse(game.body), 'currentGame');
        store.commit('setCurrentGame', JSON.parse(game.body));
      });
      webSocketService.subscribe(webSocketGameResultEndpoint, (gameResult) => {
        store.commit('setDialog', {
          open: true,
          gameResult: JSON.parse(gameResult.body),
        } as DialogData);
        console.log(JSON.parse(gameResult.body), 'gameResult');
      });
    }

    webSocketService.sendMessage(
      webSocketGreetingsSendEndpoint,
      JSON.stringify('games')
    );
  },
  () => {},
  () => {}
);

export { webSocketService };
export default store;
