import { createStore } from 'vuex';

const store = createStore({
  state() {
    return {
      count: 0,
      user: null
    };
  },
  mutations: {
    increment(state) {
      state.count++;
    },
    setUser(state, user) {
      state.user = user;
    }
  },
  actions: {
    async fetchUser({ commit }) {
      const user = await fetch('/api/user').then(res => res.json());
      commit('setUser', user);
    }
  },
  getters: {
    doubleCount: (state) => state.count * 2,
    isLoggedIn: (state) => !!state.user
  }
});

export default store;
