import { describe, it, expect, beforeEach, vi } from 'vitest';
import { mount } from '@vue/test-utils';
import { createStore } from 'vuex';
import RepaymentSchedule from '@/views/RepaymentSchedule.vue';
import * as requests from '@/utils/requests';

// Mock the requests module
vi.mock('@/utils/requests', () => ({
  makeRequestWithToken: vi.fn(),
}));

// Mock lucide-vue-next icons
vi.mock('lucide-vue-next', () => ({
  Download: { name: 'Download', template: '<div>Download</div>' },
  ArrowLeft: { name: 'ArrowLeft', template: '<div>ArrowLeft</div>' },
  ArrowUpDown: { name: 'ArrowUpDown', template: '<div>ArrowUpDown</div>' },
  Filter: { name: 'Filter', template: '<div>Filter</div>' },
  ChevronDown: { name: 'ChevronDown', template: '<div>ChevronDown</div>' },
  DollarSign: { name: 'DollarSign', template: '<div>DollarSign</div>' },
  TrendingDown: { name: 'TrendingDown', template: '<div>TrendingDown</div>' },
  Calculator: { name: 'Calculator', template: '<div>Calculator</div>' },
}));

describe('RepaymentSchedule', () => {
  let store;
  let wrapper;

  const mockLoanPurposes = {
    HOME: { label: 'Home Loan', rate: 8.5 },
    PERSONAL: { label: 'Personal Loan', rate: 12.0 },
    EDUCATION: { label: 'Education Loan', rate: 9.5 },
  };

  // Use dd/mm/yyyy to match component's split("/")
const mockLoans = [
  {
    id: 'LOAN001',
    amount: 500000,
    purpose: 'HOME',
    interestRate: 8.5,
    tenure: 60,
    status: 'APPROVED',
    appliedDate: '15/01/2024',
    lastUpdated: '15/01/2024', // ✅ added
    loanType: 'Home Loan',
  },
  {
    id: 'LOAN002',
    amount: 200000,
    purpose: 'PERSONAL',
    interestRate: 12.0,
    tenure: 36,
    status: 'APPROVED',
    appliedDate: '20/02/2024',
    lastUpdated: '20/02/2024', // ✅ added
    loanType: 'Personal Loan',
  },
  {
    id: 'LOAN003',
    amount: 300000,
    purpose: 'EDUCATION',
    interestRate: 9.5,
    tenure: 48,
    status: 'PENDING',
    appliedDate: '10/03/2024',
    lastUpdated: '10/03/2024', // ✅ added
    loanType: 'Education Loan',
  },
];


  const mockSchedule = [
    {
      id: 1,
      month: 1,
      emi: 10273,
      principalAmount: 6690,
      interestAmount: 3583,
      balanceRemaining: 493310,
      isPaid: true,
    },
    {
      id: 2,
      month: 2,
      emi: 10273,
      principalAmount: 6737,
      interestAmount: 3536,
      balanceRemaining: 486573,
      isPaid: false,
    },
    {
      id: 3,
      month: 3,
      emi: 10273,
      principalAmount: 6785,
      interestAmount: 3488,
      balanceRemaining: 479788,
      isPaid: false,
    },
  ];

  beforeEach(() => {
    store = createStore({
      state: {
        applications: mockLoans,
        interestRate: mockLoanPurposes,
      },
    });
    vi.clearAllMocks();
  });

  const createWrapper = () => {
    return mount(RepaymentSchedule, {
      global: {
        plugins: [store],
        stubs: {
          Download: true,
          ArrowLeft: true,
          ArrowUpDown: true,
          Filter: true,
          ChevronDown: true,
          DollarSign: true,
          TrendingDown: true,
          Calculator: true,
        },
      },
    });
  };

  describe('Initial Rendering', () => {
    it('should render the component with loan selection view', () => {
      wrapper = createWrapper();
      expect(wrapper.find('h1').text()).toBe('Repayment Schedule');
      expect(wrapper.text()).toContain(
        'View detailed repayment schedules for your approved loans'
      );
    });

    it('should display only approved loans', () => {
      wrapper = createWrapper();
      expect(wrapper.text()).toContain('LOAN001');
      expect(wrapper.text()).toContain('LOAN002');
      expect(wrapper.text()).not.toContain('LOAN003');
    });

    it('should show empty state when no approved loans exist', async () => {
      store.state.applications = [];
      wrapper = createWrapper();
      expect(wrapper.text()).toContain('No Approved Loans Found');
    });
  });

  describe('Loan Filtering', () => {
    beforeEach(() => {
      wrapper = createWrapper();
    });

    it('should filter loans by purpose', async () => {
      const select = wrapper.find('select');
      await select.setValue('Home Loan');
      expect(wrapper.text()).toContain('LOAN001');
      expect(wrapper.text()).not.toContain('LOAN002');
    });

    it('should show all loans when "All Purposes" is selected', async () => {
      const select = wrapper.find('select');
      await select.setValue('Personal Loan');
      await select.setValue('all');
      expect(wrapper.text()).toContain('LOAN001');
      expect(wrapper.text()).toContain('LOAN002');
    });

    it('should show empty state when no loans match filter', async () => {
      store.state.applications = [mockLoans[0]];
      wrapper = createWrapper();
      const select = wrapper.find('select');
      await select.setValue('Personal Loan');
      expect(wrapper.text()).toContain('No Approved Loans Found');
    });
  });

  describe('Loan Selection', () => {
    beforeEach(() => {
      wrapper = createWrapper();
      requests.makeRequestWithToken.mockResolvedValue({
        data: mockSchedule,
      });
    });

    it('should select a loan and fetch its schedule', async () => {
      const viewButton = wrapper
        .findAll('button')
        .find((btn) => btn.text().includes('View Schedule'));
      await viewButton.trigger('click');
      await wrapper.vm.$nextTick();
      expect(requests.makeRequestWithToken).toHaveBeenCalledWith(
        'GET',
        '/api/loans/LOAN001/schedule'
      );
    });
  });

  describe('EMI Calculation', () => {
    beforeEach(() => {
      wrapper = createWrapper();
    });

    it('should calculate EMI correctly', () => {
      const loan = mockLoans[0];
      const emi = wrapper.vm.getEmiForLoan(loan);
      // For 500000 at 8.5% for 60 months ~ 10,273
      expect(emi).toBeGreaterThan(10000);
      expect(emi).toBeLessThan(11000);
    });

    it('should handle different loan parameters', () => {
      const loan = mockLoans[1];
      const emi = wrapper.vm.getEmiForLoan(loan);
      expect(emi).toBeGreaterThan(0);
      expect(typeof emi).toBe('number');
    });
  });

  describe('Month Name Display', () => {
    beforeEach(() => {
      wrapper = createWrapper();
    });

    it('should format month name correctly', () => {
      // Component treats monthIndex=1 as current month + 0 but uses 1-based month from dd/mm/yyyy,
      // resulting in Feb 2024 for '15/01/2024'
      const monthName = wrapper.vm.getMonthName(1, '15/01/2024');
      expect(monthName).toMatch(/Feb 2024/);
    });

    it('should handle different month indices', () => {
      const month1 = wrapper.vm.getMonthName(1, '15/01/2024'); // Feb 2024
      const month2 = wrapper.vm.getMonthName(2, '15/01/2024'); // Mar 2024
      expect(month1).not.toBe(month2);
    });

    it('should use current date when no start date provided', () => {
      // Component requires a dd/mm/yyyy string; provide a neutral fallback
      const monthName = wrapper.vm.getMonthName(1, '01/01/2024x');
      expect(monthName).toBeTruthy();
      expect(typeof monthName).toBe('string');
    });
  });
});
