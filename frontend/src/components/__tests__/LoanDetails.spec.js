import { mount } from "@vue/test-utils";
import { describe, it, expect, vi, beforeEach } from "vitest";
import LoanReview from "@/views/LoanDetail.vue";

// Mock vue-router
const push = vi.fn();
const back = vi.fn();
vi.mock("vue-router", () => ({
  useRouter: () => ({ push, back }),
  useRoute: () => ({ params: { id: 123 } }),
}));

// Mock Vuex composable
const mockDispatch = vi.fn().mockResolvedValue();
const mockState = {
  user: { name: "Admin User" },
  interestRate: { 
    HOME: { label: "Home Loan" }, 
    PERSONAL: { label: "Personal Loan" },
    CAR: { label: "Car Loan" },
    null: { label: "N/A" } 
  },
};
const mockGetters = {
  selectedApplication: (id) => ({
    id,
    name: "John Doe",
    amount: 500000,
    income: 80000,
    creditScore: 750,
    loanType: "HOME",
    applicationDate: "2024-01-15",
    status: "PENDING",
  }),
};

// Mock Vuex useStore
vi.mock("vuex", () => ({
  useStore: () => ({
    dispatch: mockDispatch,
    state: mockState,
    getters: mockGetters,
  }),
}));

describe("LoanReview.vue", () => {
  beforeEach(() => {
    push.mockClear();
    back.mockClear();
    mockDispatch.mockClear();
  });

  it("renders loan details correctly", () => {
    const wrapper = mount(LoanReview, {
      global: {
        stubs: ['Search'],
      },
    });

    expect(wrapper.text()).toContain("Loan Application #123");
    expect(wrapper.text()).toContain("John Doe");
    expect(wrapper.text()).toContain("₹5,00,000");
    expect(wrapper.text()).toContain("₹80,000");
    expect(wrapper.text()).toContain("750");
    expect(wrapper.text()).toContain("Home Loan");
    expect(wrapper.text()).toContain("2024-01-15");
    expect(wrapper.text()).toContain("PENDING");
  });

  it("submits approval review successfully", async () => {
    const wrapper = mount(LoanReview, {
      global: { stubs: ['Search'] },
    });

    await wrapper.find("textarea").setValue("Good credit score and income");

    const buttons = wrapper.findAll("button");
    const approveButton = buttons.find(btn => btn.text().includes("Approve"));
    await approveButton.trigger("click");

    expect(mockDispatch).toHaveBeenCalledWith("updateApplicationStatus", {
      id: 123,
      payload: expect.objectContaining({
        status: "APPROVED",
        reviewedBy: "Admin User",
        reviewRemarks: "Good credit score and income",
        reviewedAt: expect.any(String),
      }),
    });
    expect(push).toHaveBeenCalledWith("/admin");
  });

  it("handles failed review submission", async () => {
    mockDispatch.mockRejectedValueOnce(new Error("API Error"));
    const consoleSpy = vi.spyOn(console, "error").mockImplementation(() => {});
    
    const wrapper = mount(LoanReview, {
      global: { stubs: ['Search'] },
    });

    await wrapper.find("textarea").setValue("Test remark");
    const buttons = wrapper.findAll("button");
    const approveButton = buttons.find(btn => btn.text().includes("Approve"));
    await approveButton.trigger("click");

    expect(wrapper.text()).toContain("Failed to update application");
    expect(consoleSpy).toHaveBeenCalled();
    
    consoleSpy.mockRestore();
  });

  it("goes back when back button clicked", async () => {
    const wrapper = mount(LoanReview, {
      global: { stubs: ['Search'] },
    });

    await wrapper.find("button.back-btn").trigger("click");
    expect(back).toHaveBeenCalled();
  });
});