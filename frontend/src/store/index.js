import { createStore } from "vuex";

const store = createStore({
  state() {
    return {
      // your counter + user state
      count: 0,
      user: null,

      // loan application state
      applications: [
        {
          id: "LA2024001",
          amount: 250000,
          purpose: "Home Purchase",
          status: "APPROVED",
          appliedDate: "2024-01-05",
          lastUpdated: "2024-01-08",
          emi: 15000,
          interestRate: 8.5,
          tenure: 20,
          remarks: "Application approved after document verification.",
          progress: 100,
        },
        {
          id: "LA2024002",
          amount: 150000,
          purpose: "Business Expansion",
          status: "UNDER_REVIEW",
          appliedDate: "2024-01-10",
          lastUpdated: "2024-01-12",
          emi: 12000,
          interestRate: 9.0,
          tenure: 15,
          remarks: "Additional income documents required.",
          progress: 65,
        },
      ],
      searchTerm: "",
      statusFilter: "all",
      dateRange: { from: null, to: null },
      selectedApplication: null,
    };
  },
  mutations: {
    // --- counter + user ---
    increment(state) {
      state.count++;
    },
    setUser(state, user) {
      state.user = user;
    },

    // --- loan applications ---

  },
  actions: {
    async fetchUser({ commit }) {
      const user = await fetch("/api/user").then((res) => res.json());
      commit("setUser", user);
    },
  },
  getters: {

    isLoggedIn: (state) => !!state.user,
  },
});

export default store;
