import { makeRequestWithToken } from "@/utils/requests";
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
      // loan application state
      applications: [],
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
    UPDATE_DASHBOARD_DATA(state, payload) {
      state.stats = payload.stats;
      state.applications = payload.applications;
    },
    UPDATE_APPLICATION(state, { id, payload }) {
      const idx = state.applications.findIndex(app => app.id === id);
      if (idx !== -1) {
        state.applications[idx] = payload;
      }
    },
    UPDATE_CURR_USER(state, payload) {
      state.user = payload
    },
    GET_LOANS_USER(state,payload){
      state.applications = payload
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
  try {
    const res = await makeRequestWithToken("GET", "/admin");
    const stats = res.data.stats;

    // Transform monthlyTrends
    let months = [];
    let values = [];
    if (stats.monthlyTrends?.length) {
      stats.monthlyTrends.forEach((trend) => {
        const month = Object.keys(trend)[0];
        months.push(month);
        values.push(trend[month]);
      });
    }

    // Transform statusDistribution
    const statusDist = stats.statusDistribution || {};
    const statusDistribution = {
      approved: statusDist.APPROVED || 0,
      pending: statusDist.PENDING || 0,
      rejected: statusDist.REJECTED || 0,
    };

    // Commit transformed stats
    commit("UPDATE_DASHBOARD_DATA", {
      stats: {
        ...stats,
        monthlyTrends: { months, values },
        statusDistribution,
      },
      applications: res.data.pendingApplications,
    });
  } catch (err) {
    console.error("Failed to fetch dashboard data", err);
  } finally {
    commit("SET_LOADING", false);
  }
    },

    async updateApplicationStatus({ commit }, { id, payload }) {
      try {
    const res = await makeRequestWithToken("PUT", `/admin/loans/${id}/status`, payload);
    console.log("Updated loan:", res.data);
    commit("UPDATE_APPLICATION", { id, payload: res.data });
  } catch (err) {
    console.error("Failed to update application status", err);
  }
    },

    setCurrentUser({commit},payload){
      commit("UPDATE_CURR_USER",payload);
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

    async fetchLoanById({ state, commit }, id) {
      try {
        const res = await makeRequestWithToken("GET", `/admin/loans/${id}`);
        commit("SET_SELECTED_APPLICATION", res.data);
        return res.data;
      } catch (err) {
        console.error("Failed to fetch loan by ID", err);
        return null;
      }
    },

    async getAllLoans({commit}){
      const id = this.state.user.role === 'ADMIN' ? '' : '/user/' +this.state.user.id
      const response = await makeRequestWithToken('GET',`/api/loans${id}`)
      const loans = response.data.map( (loan)=>{
        let principal = loan.amount;
        let monthlyRate = loan.rateOfInterest / 12 / 100;  
        let tenureMonths = loan.tenure;                    
 
        loan.emi = Math.round((principal * monthlyRate * Math.pow(1 + monthlyRate, tenureMonths)) /
                  (Math.pow(1 + monthlyRate, tenureMonths) - 1));
        loan.interestRate = loan.rateOfInterest
        loan.appliedDate = new Date(loan.applicationDate).toLocaleDateString();
        loan.lastUpdated = new Date(loan.lastUpdated).toLocaleDateString();
        loan.remarkedBy = loan.remarksBy
        return loan;
      })
      console.log(loans)
      commit("GET_LOANS_USER",loans)
    }
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
    stats: (state) => state.stats,
    applications: (state) => state.applications,
    isLoading: (state) => state.loading,
    isLoggedIn: (state) => state.user,
    currentUser: (state) => state.user,
    selectedApplication: (state) => state.selectedApplication,
  },
});

export default store;