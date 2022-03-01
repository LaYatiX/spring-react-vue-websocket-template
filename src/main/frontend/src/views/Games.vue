<template>
  <div class="games">
    <v-card elevation="2" outlined shaped>
      <v-card-title>Lista aktywnych gier</v-card-title>
      <v-card-subtitle>Kliknij dołącz lub stwórz nową</v-card-subtitle>
      <v-card-text>
        <v-list>
          <v-btn v-if="!games.length">Brak aktywych gier</v-btn>
          <v-list-item v-for="game in games" :key="game.id">
            <v-list-item-avatar>
              <v-icon
                :class="game.color"
                dark
                v-text="'mdi-controller-classic'"
              ></v-icon>
            </v-list-item-avatar>

            <v-list-item-content>
              <v-list-item-title v-text="game.title"></v-list-item-title>
              <v-list-item-subtitle
                v-text="game.subtitle"
              ></v-list-item-subtitle>
            </v-list-item-content>

            <v-list-item-action>
              <v-btn fab dark small color="primary" :disabled="game.playerCount === 2" @click="joinGame(game)">
                <v-icon dark> mdi-play </v-icon>
              </v-btn>
            </v-list-item-action>
          </v-list-item>
        </v-list>
      </v-card-text>
      <v-card-actions>
        <v-btn color="primary" text @click="goToHistory()">
          Historia gier
        </v-btn>
        <v-spacer></v-spacer>
        <v-btn color="primary" elevation="2" fab @click="addGame()"
          ><v-icon>mdi-plus</v-icon></v-btn
        >
      </v-card-actions>
    </v-card>
  </div>
</template>

<script>
import { mapGetters, mapActions, mapMutations } from 'vuex';
import { v4 } from 'uuid';
import { webSocketService } from "@/store";

export default {
  name: 'games',
  data: () => ({
  }),
  computed: {
    ...mapGetters({
      games: 'gamesList',
      username: 'currentUser',
    }),
  },
  components: {},
  methods: {
    goToHistory() {
      this.$router.push('/history');
    },
    addGame() {
      const game = {
        id: v4(),
        title: 'Gra gracza: ' + this.username,
        status: 'NEW',
        moves: [
          {
            username: this.username,
            positions: [-1, -1, -1, -1, -1, -1, -1, -1, -1],
          },
        ],
        player1: {
          username: this.username,
          principalId: '',
        },
      };
      webSocketService.sendMessage(
        '/app/newGame',
        JSON.stringify(game)
      );
      this.$router.push('/game');
    },
    joinGame(game) {
      webSocketService.sendMessage(
        '/app/joinGame',
        JSON.stringify({ game: game.game, username: this.username })
      );
      this.$router.push('/game');
    },
  },
};
</script>
