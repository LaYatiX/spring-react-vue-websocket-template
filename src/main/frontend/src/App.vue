<template>
  <v-app>
    <v-app-bar app color="primary" dark>
      <div
        class="
          d-flex
          align-center
          text-h5
          font-weight-bold
          justify-space-between
        "
      >
        spring-react-vue-websocket-template - Tic Tac Toe
      </div>
      <v-spacer></v-spacer>

      <span v-if="username">
        <v-btn text
          >Grasz jako: <strong>{{ username }}</strong></v-btn
        >
      </span>

      <v-btn target="_blank" text>
        <span class="mr-2">{{
          connected ? `Połączono z serwerem gry` : 'Brak połączenia z serwerem'
        }}</span>
        <v-icon>mdi-connection</v-icon>
      </v-btn>
    </v-app-bar>

    <v-main class="center">
      <v-container>
        <router-view />
      </v-container>
    </v-main>
    <Dialog></Dialog>
  </v-app>
</template>

<script>
import Vue from 'vue';
import { mapGetters, mapActions, mapMutations } from 'vuex';
import { webSocketService } from "@/store";
import Dialog from "@/components/Dialog";

export default Vue.extend({
  name: 'App',
  components: { Dialog },
  data: () => ({
    service: webSocketService
  }),
  mounted() {
    if (!this.username || !this.$router.history.current.fullPath) {
      this.$router.push('/');
    }
  },
  computed: {
    ...mapGetters({
      username: 'currentUser',
    }),
    connected: function () {
      return this.service.isConnected;
    },
  },
  methods: {
    ...mapMutations({
      setGames: 'setGames',
      setCurrentGame: 'setCurrentGame',
    }),
  },
});
</script>
<style>
.center {
  display: flex;
  flex-direction: row;
  align-items: center;
}
</style>
