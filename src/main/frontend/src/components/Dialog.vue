<template>
  <v-dialog
    v-model="dialog.open"
    width="500"
  >
    <v-card>
      <v-card-title>
        <p v-if="dialog.gameResult === -1">Remis</p>
        <p v-if="dialog.gameResult === 0">Przegrana</p>
        <p v-if="dialog.gameResult === 1">Wygrana</p>
      </v-card-title>
      <v-card-subtitle>
        Koniec gry!
      </v-card-subtitle>
      <v-card-text>

      </v-card-text>

      <v-divider></v-divider>

      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn
          color="primary"
          text
          @click="closeDialog()"
        >
          Ok
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import { mapGetters, mapMutations } from "vuex";

export default {
  name: "Dialog",
  computed: {
    ...mapGetters({
      dialog: 'dialog',
    }),
  },
  methods: {
    ...mapMutations({
       setDialog: 'setDialog'
   }),
    closeDialog() {
      this.$router.push('/games')
      this.setDialog({open: false, gameResult: -2})
    }
  }
};
</script>

<style scoped>

</style>
