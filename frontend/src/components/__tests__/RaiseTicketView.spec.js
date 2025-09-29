import { mount } from "@vue/test-utils";
import { describe, it, expect, vi, beforeEach } from "vitest";
import SupportTicketForm from "../../views/RaiseTicketView.vue";

// Mock Vuex store
const mockDispatch = vi.fn();
const mockStore = {
  getters: {
    currentUser: { id: 123 },
  },
  dispatch: mockDispatch,
};

function mountComponent() {
  return mount(SupportTicketForm, {
    global: {
      mocks: {
        $store: mockStore, // For Options API
      },
      provide: {
        store: mockStore, // For useStore() in Composition API
      },
    },
  });
}

describe("SupportTicketForm.vue", () => {
  beforeEach(() => {
    mockDispatch.mockReset();
  });

  it("renders form fields correctly", () => {
    const wrapper = mountComponent();
    expect(wrapper.find("h2").text()).toBe("Raise a Support Ticket");
    expect(wrapper.find("select").exists()).toBe(true);
    expect(wrapper.find("input[type='text']").exists()).toBe(true);
    expect(wrapper.find("textarea").exists()).toBe(true);
  });

  it("shows alert if required fields are empty", async () => {
    const wrapper = mountComponent();

    // Mock window.alert
    const alertSpy = vi.spyOn(window, "alert").mockImplementation(() => {});

    await wrapper.find("form").trigger("submit.prevent");

    expect(alertSpy).toHaveBeenCalledWith("⚠️ Please fill out all fields before submitting.");

    alertSpy.mockRestore();
  });

  it("dispatches submitTicket on valid form submission", async () => {
    const wrapper = mountComponent();

    // Fill out fields
    await wrapper.find("select").setValue("Application_Status");
    await wrapper.find("input[type='text']").setValue("Test Subject");
    await wrapper.find("textarea").setValue("This is a test description");

    mockDispatch.mockResolvedValueOnce({ success: true });

    // Mock alert
    const alertSpy = vi.spyOn(window, "alert").mockImplementation(() => {});

    await wrapper.find("form").trigger("submit.prevent");

    expect(mockDispatch).toHaveBeenCalledWith("submitTicket", {
      type: "Application_Status",
      subject: "Test Subject",
      description: "This is a test description",
      userId: 123,
      LoanId: null,
    });

    expect(alertSpy).toHaveBeenCalledWith("Ticket submitted successfully! 🎉");

    alertSpy.mockRestore();
  });

  it("shows error alert if dispatch fails", async () => {
    const wrapper = mountComponent();

    // Fill out fields
    await wrapper.find("select").setValue("Loan_Closure");
    await wrapper.find("input[type='text']").setValue("Loan Issue");
    await wrapper.find("textarea").setValue("Details about loan issue");

    mockDispatch.mockRejectedValueOnce(new Error("API error"));

    const alertSpy = vi.spyOn(window, "alert").mockImplementation(() => {});

    await wrapper.find("form").trigger("submit.prevent");

    expect(alertSpy).toHaveBeenCalledWith("Failed to submit ticket. Please try again.");

    alertSpy.mockRestore();
  });
});
