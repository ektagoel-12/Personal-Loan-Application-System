// src/store/dashboard.js
import { createStore } from "vuex";

const store = createStore({
  state: {
    stats: {
      totalApplications: 1250,
      growth: 12,
      approvalRate: 78,
      approvalGrowth: 5,
      pending: 45,
      avgIncome: 125000,
      monthlyTrends: {
        months: ["Jan", "Feb", "Mar", "Apr", "May", "Jun"],
        values: [70, 90, 100, 110, 120, 130],
      },
      statusDistribution: {
        approved: 78,
        pending: 15,
        rejected: 7,
      },
    },
    applications: [
      {
        id: "L001",
        applicant: "John Smith",
        amount: 250000,
        income: 80000,
        creditScore: 720,
        purpose: "Home Purchase",
        date: "2024-01-10",
        status: "pending",
      },
      {
        id: "L002",
        applicant: "Sarah Johnson",
        amount: 150000,
        income: 95000,
        creditScore: 680,
        purpose: "Business Expansion",
        date: "2024-01-09",
        status: "pending",
      },
      {
        id: "L003",
        applicant: "Mike Davis",
        amount: 75000,
        income: 60000,
        creditScore: 750,
        purpose: "Personal",
        date: "2024-01-08",
        status: "pending",
      },
    ],
    loading: false,
  },
  mutations: {
    SET_LOADING(state, val) {
      state.loading = val;
    },
    UPDATE_APPLICATION_STATUS(state, { id, status }) {
      const app = state.applications.find(app => app.id === id);
      if (app) {
        app.status = status;
        console.log(`Application ${id} status updated to ${status}`);
      }
    }

  },
  actions: {
    async fetchDashboardData({ commit }) {
      commit("SET_LOADING", true);
      await new Promise((resolve) => setTimeout(resolve, 500));
      commit("SET_LOADING", false);
    },
    updateApplicationStatus({ commit }, payload) {
      commit("UPDATE_APPLICATION_STATUS", payload);
    },
  },
  getters: {
    stats: (state) => state.stats,
    applications: (state) => state.applications,
    isLoading: (state) => state.loading,
    
  },
});

export default store;
