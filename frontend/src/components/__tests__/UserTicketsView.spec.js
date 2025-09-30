import { describe, it, expect, beforeEach, vi } from "vitest"; //  added vi
import { mount } from "@vue/test-utils";
import UserTicketsView from "@/views/UserTicketsView.vue";

// Mock Vuex store
const mockStore = {
  getters: {
    currentUser: { id: 1, email: "test@example.com" },
  },
  dispatch: () => {},
};

import { useStore } from "vuex";
vi.mock("vuex", () => ({
  useStore: () => mockStore,
}));

// Mock TicketPopup component
vi.mock("@/views/TicketDetailsView.vue", () => ({
  default: { template: "<div class='ticket-popup'></div>" },
}));

describe("UserTicketsView.vue (static tests)", () => {
  let wrapper;

  beforeEach(() => {
    wrapper = mount(UserTicketsView, {
      data() {
        return {
          tickets: [
            {
              id: 1,
              subject: "Test Ticket",
              type: "Application_Status",
              status: "Open",
              description: "Test Description",
              createAt: "2025-09-30",
              updatedAt: "2025-09-30",
              response: "Admin responded",
            },
          ],
          showTicketPopup: false,
          selectedTicketId: null,
          filterStatus: "All",
          sortByLatest: "desc",
        };
      },
    });
  });

  it("renders heading and Raise Ticket button", () => {
    expect(wrapper.find("h2").text()).toContain("My Tickets");
    expect(wrapper.find("button").text()).toContain("Raise Ticket");
  });

  it("renders filter select and sort toggle", () => {
    const selects = wrapper.findAll("select");
    expect(selects.length).toBe(1); // only filter select
    expect(selects[0].element.value).toBe("All");
  });

});
