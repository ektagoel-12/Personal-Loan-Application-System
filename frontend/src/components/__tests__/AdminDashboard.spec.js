import { mount, flushPromises } from "@vue/test-utils";
import { createStore } from "vuex";
import { createWebHistory } from "vue-router";
import { describe, it, expect, vi, beforeEach } from "vitest";
import AdminDashboard from "../../views/AdminDashboard.vue";

const push=vi.fn();
vi.mock("vue-router", () => ({
  useRouter: () => ({
    push,
  }),
}));

import { useRouter } from "vue-router";

describe("AdminDashboard.vue", () => {
  let store, mockRouter;
  let applicationsData; // mutable data for getters

  beforeEach(async () => {
    applicationsData = []; // reset before each test

    store = createStore({
      state: {
        interestRate: {
          HOME: { label: "Home Loan" },
          CAR: { label: "Car Loan" }
        }
      },
      getters: {
        stats: () => ({}),
        applications: () => applicationsData
      },
      actions: {
        fetchDashboardData: vi.fn()
      }
    });

    mockRouter = useRouter(); 
  });

  it("calls fetchDashboardData on mount", () => {
    mount(AdminDashboard, {
      global: { plugins: [store, mockRouter] }
    });

    expect(store._actions.fetchDashboardData).toBeTruthy();
  });

  it("renders applications once data is fetched", async () => {
    applicationsData.push({
      id: 42,
      name: "Pooja Bhat",
      amount: 400000.0,
      income: 105000.0,
      creditScore: 740,
      loanType: null,
      applicationDate: "2025-09-16",
      status: "PENDING"
    });

    const wrapper = mount(AdminDashboard, {
      global: { plugins: [store, mockRouter] }
    });

    await flushPromises();

    expect(wrapper.text()).toContain("Pooja Bhat");
    expect(wrapper.text()).toContain("₹400,000");
  });

  it("navigates to edit when Edit clicked", async () => {
    applicationsData.push({
      id: 42,
      name: "Pooja Bhat",
      amount: 400000.0,
      income: 105000.0,
      creditScore: 740,
      loanType: "HOME",
      applicationDate: "2025-09-16",
      status: "PENDING"
    });

    const wrapper = mount(AdminDashboard, {
      global: { plugins: [store, mockRouter] }
    });

    await flushPromises();

    await wrapper.find("tbody tr button").trigger("click");
  expect(push).toHaveBeenCalledWith("/admin/loans/42");
  });
});
