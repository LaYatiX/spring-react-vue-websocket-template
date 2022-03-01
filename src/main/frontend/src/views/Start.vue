<template>
  <div class="start">
    <v-card elevation="2" outlined shaped>
      <v-card-title>Witaj w grze Kółko i Krzyżyk</v-card-title>
      <v-card-subtitle>Podaj swój nick żeby zacząć</v-card-subtitle>
      <v-card-text>
        <v-text-field
          label="Podaj nazwę gracza"
          :rules="rules"
          v-model="username"
          name="username"
          autofocus
          hide-details="auto"
        ></v-text-field>
      </v-card-text>
      <v-card-actions>
        <v-btn
          color="primary"
          text
          :disabled="invalidInput"
          @click="enterGame()"
        >
          Przejdź to gry
        </v-btn>
      </v-card-actions>
    </v-card>
  </div>
</template>

<script>
import { mapGetters, mapActions, mapMutations } from 'vuex';
import store, { webSocketService } from "@/store";

export default {
  name: 'start',
  components: {},
  data() {
    return {
      username: '',
      rules: [(value) => !!value || 'Pole wymagane'],
    };
  },
  computed: {
    invalidInput: function () {
      return this.username.length === 0;
    },
  },
  methods: {
    ...mapMutations({
      setCurrentUser: 'setCurrentUser',
    }),
    enterGame() {
      const webSocketInitSendEndpoint = '/app/initPrincipal';
      this.setCurrentUser(this.username);
      webSocketService.sendMessage(
        webSocketInitSendEndpoint,
        JSON.stringify(this.username)
      );
      this.$router.push('games');
    },
  },
};
</script>
