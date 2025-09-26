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
    UPDATE_APPLICATION_STATUS(state, { id, status }) {
      const app = state.applications.find(app => app.id === id);
      if (app) {
        app.status = status;
      }
    },
    UPDATE_CURR_USER(state,payload){
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
      await new Promise((resolve) => setTimeout(resolve, 500));
      commit("SET_LOADING", false);
    },
    updateApplicationStatus({ commit },{id, payload}) {
      commit("UPDATE_APPLICATION", {id, payload});
    },
    setCurrentUser({commit},payload){
      commit("UPDATE_CURR_USER",payload);
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

    async addApplication({ commit }, application) {
      const response = await makeRequestWithToken('POST','/api/loans',application);
      if(response.status == 200){
        commit("ADD_APPLICATION", response.data);
      }else{
        alert("Failed to add loan")
        console.log(response)
      }
      
    },

    removeApplication({ commit }, id) {
      commit("REMOVE_APPLICATION", id);
    },
    fetchLoanById({ state, commit }, id) {
      const loan = state.applications.find((app) => app.id === id) || null;
      commit("SET_SELECTED_APPLICATION", loan);
      return loan;
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
    isLoading: (state) => state.loading,
    isLoggedIn: (state) => state.user,
    currentUser: (state) => state.user,
    selectedApplication: (state) => state.selectedApplication,
  },
});

export default store;
