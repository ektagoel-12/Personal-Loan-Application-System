import { describe, it, expect, beforeEach, vi } from "vitest";
import { mount } from "@vue/test-utils";
import RaiseTicketView from "@/views/RaiseTicketView.vue";

// Mock Vuex useStore
vi.mock("vuex", () => ({
  useStore: () => mockStore,
}));

let mockStore;

describe("RaiseTicketView.vue (static tests)", () => {
  beforeEach(() => {
    mockStore = {
      getters: {
        applications: [{ id: 101 }, { id: 202 }],
        currentUser: { id: 1 },
      },
      dispatch: vi.fn(),
    };
  });

  const mountComponent = () =>
    mount(RaiseTicketView, {
      global: {},
    });

  it("renders heading", () => {
    const wrapper = mountComponent();
    expect(wrapper.find("h2").text()).toBe("Raise a Support Ticket");
  });

  it("renders all form fields", () => {
    const wrapper = mountComponent();

    // Use order-based selection since v-model isn’t in the DOM
    const selects = wrapper.findAll("select");
    const inputs = wrapper.findAll("input[type='text']");
    const textareas = wrapper.findAll("textarea");

    expect(selects.length).toBeGreaterThanOrEqual(2); // type + loan
    expect(inputs.length).toBe(1); // subject
    expect(textareas.length).toBe(1); // description
    expect(wrapper.find("button[type='submit']").text()).toBe("Submit Ticket");
  });

  it("renders request type options correctly", () => {
    const wrapper = mountComponent();
    const typeSelect = wrapper.findAll("select")[0]; // first select is type

    const options = typeSelect.findAll("option");
    const values = options.slice(1).map((o) => o.text());

    expect(values).toEqual([
      "Application Status",
      "Document Upload Issue",
      "Emi Query",
      "Loan Closure",
      "Others",
    ]);
  });

  it("renders applications from store as loan options", () => {
    const wrapper = mountComponent();
    const loanSelect = wrapper.findAll("select")[1]; // second select is loan

    const options = loanSelect.findAll("option");
    const values = options.slice(1).map((o) => o.text());

    expect(values).toEqual(["101", "202"]);
  });
});
