import { mount } from "@vue/test-utils";
import { describe, it, expect, vi, beforeEach } from "vitest";
import LoanApplication from "../../views/LoanForm.vue";

// Mock router
vi.mock("@/router", () => ({
  default: { push: vi.fn() }
}));

// Mock Vuex store
const mockDispatch = vi.fn();
const mockStore = {
  state: {
    user: { id: 1, name: "John Doe", email: "john@example.com", creditScore: 720 }
  },
  dispatch: mockDispatch
};

vi.mock("vuex", () => ({
  useStore: () => mockStore
}));

describe("LoanApplication.vue", () => {
  let wrapper;

  beforeEach(() => {
    wrapper = mount(LoanApplication, {
      global: {
        stubs: ["Landmark", "ScrollText"]
      }
    });
    mockDispatch.mockClear();
  });

  it("starts at step 1", () => {
    expect(wrapper.vm.step).toBe(1);
  });

  it("moves to next step if validation passes", () => {
    wrapper.vm.loan.amount = 50000;
    wrapper.vm.loan.tenure = 2;
    wrapper.vm.loan.purpose = "HOME_LOAN";

    wrapper.vm.nextStep();
    expect(wrapper.vm.step).toBe(2);
  });

  it("does not move to next step if validation fails", () => {
    wrapper.vm.loan.amount = 0; // invalid
    wrapper.vm.loan.tenure = 0;

    wrapper.vm.nextStep();
    expect(wrapper.vm.step).toBe(1);
  });

  it("moves to previous step", () => {
    wrapper.vm.step = 3;
    wrapper.vm.prevStep();
    expect(wrapper.vm.step).toBe(2);
  });

  it("calculates correct interest rate", () => {
    wrapper.vm.loan.purpose = "PERSONAL_LOAN";
    expect(wrapper.vm.interestRate).toBe(10);
  });

  it("calculates EMI correctly", () => {
    wrapper.vm.loan.amount = 120000;
    wrapper.vm.loan.tenure = 1;
    wrapper.vm.loan.purpose = "PERSONAL_LOAN"; // 10% interest

    expect(wrapper.vm.emi).toBeGreaterThan(0);
  });

  it("computes eligibility score based on income and credit score", () => {
    wrapper.vm.loan.income = 40000;
    wrapper.vm.loan.creditScore = 720;
    wrapper.vm.loan.amount = 100000;

    const score = wrapper.vm.eligibilityScore;
    expect(score).toBeGreaterThan(0);
    expect(score).toBeLessThanOrEqual(100);
  });

  it("rejects invalid email in step 3", () => {
    wrapper.vm.step = 3;
    wrapper.vm.userDetails.fullName = "John";
    wrapper.vm.userDetails.email = "invalid-email";
    wrapper.vm.userDetails.phoneNo = "1234567890";
    wrapper.vm.userDetails.address = "Somewhere";

    expect(wrapper.vm.validateStep()).toBe(false);
  });

  it("rejects invalid phone number in step 3", () => {
    wrapper.vm.step = 3;
    wrapper.vm.userDetails.fullName = "John";
    wrapper.vm.userDetails.email = "john@example.com";
    wrapper.vm.userDetails.phoneNo = "123"; // invalid
    wrapper.vm.userDetails.address = "Somewhere";

    expect(wrapper.vm.validateStep()).toBe(false);
  });

  it("dispatches addApplication on applyLoan", () => {
    wrapper.vm.applyLoan();
    expect(mockDispatch).toHaveBeenCalledWith("addApplication", wrapper.vm.loan);
  });
});
