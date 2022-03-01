<template>
  <div class="history">
    <v-card elevation="2" outlined shaped>
      <v-card-title>Historia rozegranych gier</v-card-title>
      <v-card-text>
        <v-btn v-if="games.length === 0">Brak rozegranych gier</v-btn>
        <v-list>
          <v-list-item v-for="game in games" :key="game.title">
            <v-list-item-avatar>
              <v-icon :class="game.color" dark>mdi-controller-classic</v-icon>
            </v-list-item-avatar>

            <v-list-item-content>
              <v-list-item-title v-text="game.title"></v-list-item-title>
              <v-list-item-subtitle
                v-text="game.subtitle"
              ></v-list-item-subtitle>
            </v-list-item-content>
          </v-list-item>
        </v-list>
      </v-card-text>
      <v-card-actions>
        <v-btn color="primary" text @click="goToGames()">
          Wróć do listy gier
        </v-btn>
      </v-card-actions>
    </v-card>
  </div>
</template>

<script>
import { mapGetters } from "vuex";

export default {
  name: 'history',
  components: {},
  data() {
    return {
      games: [

      ],
    };
  },
  computed: {
    ...mapGetters({
      username: 'currentUser',
    }),
  },
  methods: {
    goToGames() {
      this.$router.push('/games');
    },
  },
  mounted: function () {
    fetch('/api/gamesHistory?username=' + this.username)
      .then((value) => value.json())
      .then((value) => {
        this.games = value.map((game) => {
          return {
            color: game.playerWin === '' ? 'orange' : game.playerWin === this.username ? 'green' : 'red',
            subtitle: game.playerWin === '' ? 'Remis' : game.playerWin === this.username ? 'Wygrana' : 'Przegrana',
            title: game.title
          };
        });
        console.log(value);
      });
  },
};
</script>
