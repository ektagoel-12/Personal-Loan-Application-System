import { makeRequestWithToken } from "@/utils/requests";
import { createStore } from "vuex";

const store = createStore({
  state() {
    return {

      count: 0,
      user: JSON.parse(localStorage.getItem('currUser')),
      
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
      applications: [],
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
      }
    },
    UPDATE_CURR_USER(state,payload){
      state.user = payload
    },
    GET_LOANS_USER(state,payload){
      state.applications = payload
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
    isLoading: (state) => state.loading,
    isLoggedIn: (state) => state.user,
    currentUser: (state) => state.user
  },
});

export default store;
