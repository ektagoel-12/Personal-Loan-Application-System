import { makeRequestWithToken } from "@/utils/requests";
import { createStore } from "vuex";

const store = createStore({
  state() {
    return {
      count: 0,
      user: JSON.parse(localStorage.getItem('currUser')) || null,
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
      applications: [],
      tickets: [], // added tickets state
      allTickets: [],
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
      if (app) app.status = status;
    },

    UPDATE_CURR_USER(state, payload) {
      state.user = payload;
    },

    GET_LOANS_USER(state, payload) {
      state.applications = payload;
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

    // Tickets mutations
    SET_TICKETS(state, tickets) {
      state.tickets = tickets;
    },
     SET_ALL_TICKETS(state, tickets) {
      state.allTickets = tickets;
    },
      ADD_TICKET(state, ticket) {
    state.tickets.push(ticket);
  },
    UPDATE_TICKET_STATUS(state, { id, status }) {
    const ticket = state.allTickets.find(t => t.id === id);
    if (ticket) ticket.status = status;
  },
  UPDATE_TICKET_RESPONSE(state, { id, response }) {
    const ticket = state.allTickets.find(t => t.id === id);
    if (ticket) ticket.response = response;
  },
  },

  actions: {
    async fetchDashboardData({ commit }) {
      commit("SET_LOADING", true);
      await new Promise((resolve) => setTimeout(resolve, 500));
      commit("SET_LOADING", false);
    },

    updateApplicationStatus({ commit }, { id, status }) {
      commit("UPDATE_APPLICATION_STATUS", { id, status });
    },

    setCurrentUser({ commit }, payload) {
      commit("UPDATE_CURR_USER", payload);
    },

    async getAllLoans({ commit, state }) {
      const id = state.user.role === 'ADMIN' ? '' : '/user/' + state.user.id;
      const response = await makeRequestWithToken('GET', `/api/loans${id}`);
      const loans = response.data.map((loan) => {
        const principal = loan.amount;
        const monthlyRate = loan.rateOfInterest / 12 / 100;  
        const tenureMonths = loan.tenure;                    
        loan.emi = Math.round((principal * monthlyRate * Math.pow(1 + monthlyRate, tenureMonths)) /
                  (Math.pow(1 + monthlyRate, tenureMonths) - 1));
        loan.interestRate = loan.rateOfInterest;
        loan.appliedDate = new Date(loan.applicationDate).toLocaleDateString();
        loan.lastUpdated = new Date(loan.lastUpdated).toLocaleDateString();
        loan.remarkedBy = loan.remarksBy;
        return loan;
      });
      commit("GET_LOANS_USER", loans);
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

    // Tickets actions
    async fetchTickets({ commit }, userEmail) {
      try {
        const endpoint = '/ticket/user/email/' + userEmail;
        const response = await makeRequestWithToken("GET", endpoint);
        commit("SET_TICKETS", response.data || []);
      } catch (err) {
        console.error("Error fetching tickets:", err);
      }
    },
     async fetchAllTickets({ commit }) {
      try {
        const endpoint = '/ticket/admin';
        const response = await makeRequestWithToken("GET", endpoint);
        // console.log(response.data)
        commit("SET_ALL_TICKETS", response.data || []);
      } catch (err) {
        console.error("Error fetching tickets:", err);
      }
    },
      async submitTicket({ commit }, ticketData) {
    try {
      const response = await makeRequestWithToken("POST", "/ticket/user", ticketData);
      commit("ADD_TICKET", response.data); // Save ticket in Vuex state
      return response;
    } catch (err) {
      throw err;
    }
  },
  async updateTicketStatus({ commit }, { id, status }) {
    try {
      await makeRequestWithToken("PATCH", `/ticket/admin/${id}/${status}`);
      commit("UPDATE_TICKET_STATUS", { id, status });
    } catch (err) {
      throw err;
    }
  },
  async updateTicketResponse({ commit }, { id, response }) {
    try {
      const res = await makeRequestWithToken("PATCH", `/ticket/admin/${id}/response`, { response });
      commit("UPDATE_TICKET_RESPONSE", { id, response: res.data.response });
    } catch (err) {
      throw err;
    }
  },
  },

  getters: {
    stats: (state) => state.stats,
    applications: (state) => state.applications,
    tickets: (state) => state.tickets,
    allTickets: (state) => state.allTickets,
    filteredApplications: (state) => {
      return state.applications.filter((app) => {
        const matchesSearch =
          state.searchTerm === "" ||
          app.applicant?.toLowerCase().includes(state.searchTerm.toLowerCase()) ||
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
