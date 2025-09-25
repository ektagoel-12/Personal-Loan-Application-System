import { createStore } from "vuex";

const store = createStore({
  state() {
    return {
      // your counter + user state
      count: 0,
      user: null,
      
      //stats
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
        }
      },
      // loan application state
      applications: [
        {
          id: "LA2024001",
          applicant: "Sarah Johnson",
          creditScore: 680,
          income: 95000,
          amount: 250000,
          purpose: "Home Purchase",
          status: "PENDING",
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
          applicant: "Mike Davis",
          creditScore: 750,
          income: 60000,
          amount: 150000,
          purpose: "Business Expansion",
          status: "REJECTED",
          appliedDate: "2024-01-10",
          lastUpdated: "2024-01-12",
          emi: 12000,
          interestRate: 9.0,
          tenure: 15,
          remarks: "Additional income documents required.",
          progress: 65,
        },{
          id: "LA20240232",
          applicant: "Mike Davis",
          creditScore: 750,
          income: 60000,
          amount: 150000,
          purpose: "Business Expansion",
          status: "PENDING",
          appliedDate: "2025-09-24",
          lastUpdated: "2024-01-12",
          emi: 12000,
          interestRate: 9.0,
          tenure: 15,
          remarks: "Additional income documents required.",
          progress: 65,
        }
      ],
      searchTerm: "",
      statusFilter: "all",
      dateRange: { from: null, to: null },
      selectedApplication: null,
    };
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
    },
    UPDATE_CURR_USER(state,payload){
      state.user = payload
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
    setCurrentUser({commit},payload){
      commit("UPDATE_CURR_USER",payload);
    }
  },
  getters: {
    stats: (state) => state.stats,
    applications: (state) => state.applications,
    isLoading: (state) => state.loading,
    isLoggedIn: (state) => state.user,
    currentUser: (state) => state.user
  },
});

export default store;
