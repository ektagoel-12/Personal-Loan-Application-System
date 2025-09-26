import { createStore } from "vuex";

const store = createStore({
  state() {
    return {
      count: 0,
      user: JSON.parse(localStorage.getItem('currUser')),
      
      stats: {
        totalApplications: 1250,
        approvalRate: 78,
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
      applications: [
        {
          id: "LA2024001",
          applicant: "Sarah Johnson",
          creditScore: 680,
          income: 95000,
          amount: 250000,
          purpose: "Home Loan",
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
          purpose: "Business Loan",
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
          purpose: "Business Loan",
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
      loading: false,
    };
  },
  mutations: {
    SET_LOADING(state, val) {
      state.loading = val;
    },
    UPDATE_APPLICATION(state, { id, payload }) {
    const idx = state.applications.findIndex(app => app.id === id);
    if (idx !== -1) {
      state.applications[idx] = {
        ...state.applications[idx],
        ...payload
      };
    }
  },
    UPDATE_CURR_USER(state, payload) {
      state.user = payload
    },
    SET_SELECTED_APPLICATION(state, app) {
      state.selectedApplication = app;
    },

    SET_FILTERS(state, { searchTerm, statusFilter, dateRange }) {
      if (searchTerm !== undefined) state.searchTerm = searchTerm;
      if (statusFilter !== undefined) state.statusFilter = statusFilter;
      if (dateRange !== undefined) state.dateRange = dateRange;
    },

    ADD_APPLICATION(state, application) {
      state.applications.push(application);
    },

    REMOVE_APPLICATION(state, id) {
      state.applications = state.applications.filter((app) => app.id !== id);
    },
  },
  actions: {
    async fetchDashboardData({ commit }) {
      commit("SET_LOADING", true);
      await new Promise((resolve) => setTimeout(resolve, 500));
      commit("SET_LOADING", false);
    },
    updateApplicationStatus({ commit },{id, payload}) {
      commit("UPDATE_APPLICATION", {id, payload});
    },
    setCurrentUser({commit},payload){
      commit("UPDATE_CURR_USER",payload);
    },
    setCurrentUser({ commit }, payload) {
      commit("UPDATE_CURR_USER", payload);
    },

    selectApplication({ commit }, app) {
      commit("SET_SELECTED_APPLICATION", app);
    },

    applyFilters({ commit }, filters) {
      commit("SET_FILTERS", filters);
    },

    addApplication({ commit }, application) {
      commit("ADD_APPLICATION", application);
    },

    removeApplication({ commit }, id) {
      commit("REMOVE_APPLICATION", id);
    },
    fetchLoanById({ state, commit }, id) {
      const loan = state.applications.find((app) => app.id === id) || null;
      commit("SET_SELECTED_APPLICATION", loan);
      return loan;
    },
  },
  getters: {
    stats: (state) => state.stats,
    applications: (state) => state.applications,

    filteredApplications: (state) => {
      return state.applications.filter((app) => {
        const matchesSearch =
          state.searchTerm === "" ||
          app.applicant.toLowerCase().includes(state.searchTerm.toLowerCase()) ||
          app.id.toLowerCase().includes(state.searchTerm.toLowerCase());

        const matchesStatus =
          state.statusFilter === "all" ||
          app.status.toLowerCase() === state.statusFilter.toLowerCase();

        const matchesDate =
          (!state.dateRange.from ||
            new Date(app.appliedDate) >= new Date(state.dateRange.from)) &&
          (!state.dateRange.to ||
            new Date(app.appliedDate) <= new Date(state.dateRange.to));

        return matchesSearch && matchesStatus && matchesDate;
      });
    },
    isLoading: (state) => state.loading,
    isLoggedIn: (state) => state.user,
    currentUser: (state) => state.user,
    selectedApplication: (state) => state.selectedApplication,
  },
});

export default store;
