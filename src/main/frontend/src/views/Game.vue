<template>
  <div class="game">

    <v-card elevation="2" outlined shaped>
      <v-card-title>Gra gracza: </v-card-title>
      <v-card-subtitle
        >{{blocked ? 'Oczekiwanie na ruch drugiego gracza' : 'Twój ruch'}}</v-card-subtitle
      >
      <v-card-text>
        <div :class="{board: true, blocked}">
          <v-sheet
            class="tile"
            v-for="(tile, index) in tiles"
            :key="index"
            color="white"
            elevation="1"
            height="170"
            rounded
            width="170"
            @click="tileClicked(tile, index)"
          >
            <v-icon v-if="tile === 0">mdi-window-close</v-icon>
            <v-icon v-if="tile === 1">mdi-checkbox-blank-circle-outline</v-icon>
          </v-sheet>
        </div>
      </v-card-text>
      <v-card-actions>
        <v-btn color="primary" text @click="exitGame()">Wyjdź z gry</v-btn>
        <v-spacer></v-spacer>
        Gracz nr 1: {{currentGame.player1 ? currentGame.player1.username : 'Oczekiwanie na dołączenie'}} <br>
        Gracz nr 2: {{currentGame.player2 ? currentGame.player2.username : 'Oczekiwanie na dołączenie'}}
      </v-card-actions>
    </v-card>
  </div>
</template>

<script>
import { mapActions, mapGetters } from 'vuex';
import { webSocketService } from "@/store";

export default {
  name: 'game',
  components: {},
  data() {
    return {
      rules: [(value) => !!value || 'Pole wymagane'],
    };
  },
  computed: {
    ...mapGetters({
      currentGame: 'currentGame',
      tiles: 'currentGameMoves',
      username: 'currentUser',
      blocked: 'blocked',
    }),
  },
  methods: {
    ...mapActions({}),
    exitGame() {
      webSocketService.sendMessage(
        '/app/leaveGame',
        JSON.stringify(this.currentGame)
      );
      this.$router.push('/games');
    },
    tileClicked(tile, index) {
      if(tile !== -1) return
      const symbol = this.currentGame.player1.username === this.username ? 1 : 0;
      webSocketService.sendMessage(
        '/app/makeMove',
        JSON.stringify({
          game: this.currentGame,
          move: {
            positions: this.currentGame.moves[this.currentGame.moves.length - 1].positions.map((position, idx) => idx === index ? symbol : position),
            username: this.username
          },
          username: this.username
        })
      );
    },
  },
};
</script>

<style lang="less">
.game {
  width: 575px;
  margin: 0 auto;
}

.board {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
}

.tile {
  margin: 5px;
  cursor: pointer;
  transition: all 300ms ease;
  padding: 15px;
  &:hover {
    background-color: #2196f3 !important;
    opacity: 0.9;
    i.v-icon {
      color: white;
    }
  }
  i.v-icon {
    color: #2196f3;
    font-size: 9rem;
  }
}
.blocked {
  pointer-events: none;
  filter: blur(2px);
}
</style>
