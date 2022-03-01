import Vue from 'vue';
import VueRouter from 'vue-router';
import Start from '../views/Start.vue';
import Games from '../views/Games.vue';
import History from '../views/History.vue';
import Game from '../views/Game.vue';

Vue.use(VueRouter);

const routes = [
  {
    path: '/',
    name: 'start',
    component: Start,
  },
  {
    path: '/games',
    name: 'games',
    component: Games,
  },
  {
    path: '/history',
    name: 'history',
    component: History,
  },
  {
    path: '/game',
    name: 'game',
    component: Game,
  },
];

const router = new VueRouter({
  routes,
});

export default router;
