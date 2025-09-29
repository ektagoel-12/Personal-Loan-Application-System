import { describe, it, beforeEach, expect, vi } from "vitest";
import { mount } from "@vue/test-utils";
import LoanForm from "../../views/LoanForm.vue";
import { createStore } from "vuex";
import { createMemoryHistory, createRouter } from "vue-router";

// Mock toast
vi.mock("vue-toastification", () => ({
  useToast: () => ({
    error: vi.fn(),
    success: vi.fn(),
  }),
}));

// Mock routes
const routes = [{ path: "/loan", component: { template: "<div>Loan Page</div>" } }];
const router = createRouter({
  history: createMemoryHistory(),
  routes,
});

describe("LoanForm.vue", () => {
  let store;

  beforeEach(async () => {
    store = createStore({
      state: {
        user: {
          id: 1,
          name: "Test User",
          email: "test@example.com",
          creditScore: 750,
        },
        interestRate: {
          Personal: { label: "Personal Loan", rate: 12 },
          Home: { label: "Home Loan", rate: 8 },
        },
      },
      actions: {
        addApplication: vi.fn(),
      },
    });

    // Ensure router is ready
    router.push("/");
    await router.isReady();
  });

  it("renders form title", () => {
    const wrapper = mount(LoanForm, {
      global: {
        plugins: [store, router],
      },
    });

    expect(wrapper.find("h2").text()).toBe("Loan Application");
  });

  // it("shows validation error if loan amount is empty", async () => {
  //   const wrapper = mount(LoanForm, {
  //     global: {
  //       plugins: [store, router],
  //     },
  //   });

  //   // Click next without entering amount
  //   await wrapper.find("button:has-text('Next')").trigger("click");

  //   expect(wrapper.text()).toContain("Step 1 of 4"); // still in step 1
  // });

  // it("navigates after successful application", async () => {
  //   const wrapper = mount(LoanForm, {
  //     global: {
  //       plugins: [store, router],
  //     },
  //   });

  //   // Step 1: Loan details
  //   await wrapper.find("input[type='number']").setValue(100000); // loan amount
  //   await wrapper.find("select").setValue(5); // tenure
  //   await wrapper.find("select:last-of-type").setValue("Personal"); // purpose
  //   await wrapper.find("button:has-text('Next')").trigger("click");

  //   // Step 2: Financial details
  //   await wrapper.find("input[type='number']").setValue(50000); // income
  //   await wrapper.findAll("input[type='number']")[1].setValue(750); // credit score
  //   await wrapper.find("select:last-of-type").setValue("Salaried");
  //   await wrapper.find("button:has-text('Next')").trigger("click");

  //   // Step 3: Contact details
  //   const inputs = wrapper.findAll("input");
  //   await inputs[0].setValue("Test User"); // full name
  //   await inputs[1].setValue("test@example.com"); // email
  //   await inputs[2].setValue("9876543210"); // phone
  //   await wrapper.find("textarea").setValue("Test Address");
  //   await wrapper.find("button:has-text('Next')").trigger("click");

  //   // Step 4: Apply
  //   await wrapper.find("button:has-text('Apply')").trigger("click");

  //   // Should redirect to /loan
  //   expect(wrapper.vm.$router.currentRoute.value.path).toBe("/loan");
  // });
});
