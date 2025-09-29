import { describe, it, beforeEach, expect, vi } from "vitest";
import { mount } from "@vue/test-utils";
import { createStore } from "vuex";
import { createMemoryHistory, createRouter } from "vue-router";
import RaiseTicketView from "@/views/RaiseTicketView.vue";

// ✅ Mock toast
vi.mock("vue-toastification", () => ({
  useToast: () => ({
    error: vi.fn(),
    success: vi.fn(),
  }),
}));

// ✅ Mock routes
const routes = [{ path: "/user-ticket-view", component: { template: "<div>User Tickets</div>" } }];
const router = createRouter({
  history: createMemoryHistory(),
  routes,
});

describe("RaiseTicketView.vue", () => {
  let store;
  let actions;

  beforeEach(async () => {
    actions = {
      submitTicket: vi.fn().mockResolvedValue(true),
    };

    store = createStore({
      getters: {
        currentUser: () => ({ id: 1, name: "Test User" }),
      },
      actions,
    });

    router.push("/");
    await router.isReady();
  });

  it("renders form title", () => {
    const wrapper = mount(RaiseTicketView, {
      global: {
        plugins: [store, router],
      },
    });

    expect(wrapper.find("h2").text()).toBe("Raise a Support Ticket");
  });

  // it("shows validation error if fields are empty", async () => {
  //   const toastMock = {
  //     error: vi.fn(),
  //     success: vi.fn(),
  //   };
  //   vi.mocked(require("vue-toastification").useToast).mockReturnValue(toastMock);

  //   const wrapper = mount(RaiseTicketView, {
  //     global: {
  //       plugins: [store, router],
  //     },
  //   });

  //   await wrapper.find("form").trigger("submit.prevent");

  //   expect(toastMock.error).toHaveBeenCalledWith(" Please fill out all fields before submitting.");
  // });

  // it("dispatches submitTicket and redirects on success", async () => {
  //   const toastMock = {
  //     error: vi.fn(),
  //     success: vi.fn(),
  //   };
  //   vi.mocked(require("vue-toastification").useToast).mockReturnValue(toastMock);

  //   const wrapper = mount(RaiseTicketView, {
  //     global: {
  //       plugins: [store, router],
  //     },
  //   });

  //   // Fill form
  //   await wrapper.find("select").setValue("Application_Status");
  //   await wrapper.find("input[type='text']").setValue("Test Subject");
  //   await wrapper.find("textarea").setValue("This is a test description.");

  //   // Submit form
  //   await wrapper.find("form").trigger("submit.prevent");

  //   expect(actions.submitTicket).toHaveBeenCalled();
  //   expect(toastMock.success).toHaveBeenCalledWith("Ticket submitted successfully!");
  //   expect(wrapper.vm.$router.currentRoute.value.path).toBe("/user-ticket-view");
  // });
});
