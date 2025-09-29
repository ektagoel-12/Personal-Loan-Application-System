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

  const mockLoans = [
    {
      id: 'LOAN001',
      amount: 500000,
      purpose: 'HOME',
      interestRate: 8.5,
      tenure: 5,
      status: 'APPROVED',
      appliedDate: '2024-01-15',
      loanType: 'Home Loan',
    },
    {
      id: 'LOAN002',
      amount: 200000,
      purpose: 'PERSONAL',
      interestRate: 12.0,
      tenure: 3,
      status: 'APPROVED',
      appliedDate: '2024-02-20',
      loanType: 'Personal Loan',
    },
    {
      id: 'LOAN003',
      amount: 300000,
      purpose: 'EDUCATION',
      interestRate: 9.5,
      tenure: 4,
      status: 'PENDING',
      appliedDate: '2024-03-10',
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
      expect(wrapper.text()).toContain('View detailed repayment schedules for your approved loans');
    });

    it('should display only approved loans', () => {
      wrapper = createWrapper();
      expect(wrapper.text()).toContain('LOAN001');
      expect(wrapper.text()).toContain('LOAN002');
      expect(wrapper.text()).not.toContain('LOAN003'); // PENDING loan
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
      store.state.applications = [mockLoans[0]]; // Only HOME loan
      wrapper = createWrapper();
      
      const select = wrapper.find('select');
      await select.setValue('Personal Loan');
      
      expect(wrapper.text()).toContain('No Approved Loans Found');
      expect(wrapper.text()).toContain('Try adjusting your filter criteria');
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
      const viewButton = wrapper.findAll('button').find(btn => 
        btn.text().includes('View Schedule')
      );
      
      await viewButton.trigger('click');
      await wrapper.vm.$nextTick();
      
      expect(requests.makeRequestWithToken).toHaveBeenCalledWith(
        'GET',
        '/api/loans/LOAN001/schedule'
      );
    });

    it('should display loan details after selection', async () => {
      const viewButton = wrapper.findAll('button').find(btn => 
        btn.text().includes('View Schedule')
      );
      
      await viewButton.trigger('click');
      await wrapper.vm.$nextTick();
      
      expect(wrapper.text()).toContain('Repayment Schedule - LOAN001');
      expect(wrapper.text()).toContain('Home Loan');
    });

    it('should handle API errors gracefully', async () => {
      requests.makeRequestWithToken.mockRejectedValue(new Error('API Error'));
      const consoleError = vi.spyOn(console, 'error').mockImplementation(() => {});
      
      const viewButton = wrapper.findAll('button').find(btn => 
        btn.text().includes('View Schedule')
      );
      
      await viewButton.trigger('click');
      await wrapper.vm.$nextTick();
      
      expect(consoleError).toHaveBeenCalled();
      consoleError.mockRestore();
    });

    it('should show back button after loan selection', async () => {
      wrapper.vm.selectedLoan = mockLoans[0];
      await wrapper.vm.$nextTick();
      
      const backButton = wrapper.findAll('button').find(btn => 
        btn.text().includes('Back to Loans')
      );
      
      expect(backButton.exists()).toBe(true);
    });

    it('should clear selection when back button is clicked', async () => {
      wrapper.vm.selectedLoan = mockLoans[0];
      wrapper.vm.repaymentSchedule = mockSchedule;
      await wrapper.vm.$nextTick();
      
      const backButton = wrapper.findAll('button').find(btn => 
        btn.text().includes('Back to Loans')
      );
      
      await backButton.trigger('click');
      
      expect(wrapper.vm.selectedLoan).toBeNull();
      expect(wrapper.vm.currentPage).toBe(1);
      expect(wrapper.vm.filterYear).toBe('all');
    });
  });

  describe('EMI Calculation', () => {
    beforeEach(() => {
      wrapper = createWrapper();
    });

    it('should calculate EMI correctly', () => {
      const loan = mockLoans[0];
      const emi = wrapper.vm.getEmiForLoan(loan);
      
      // For 500000 at 8.5% for 5 years
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

  describe('Summary Statistics', () => {
    beforeEach(async () => {
      wrapper = createWrapper();
      wrapper.vm.selectedLoan = mockLoans[0];
      wrapper.vm.repaymentSchedule = mockSchedule;
      await wrapper.vm.$nextTick();
    });

    it('should calculate summary stats correctly', () => {
      const stats = wrapper.vm.summaryStats;
      
      expect(stats).toBeDefined();
      expect(stats.monthlyEmi).toBe(10273);
      expect(stats.totalInterest).toBeGreaterThan(0);
      expect(stats.totalAmount).toBeGreaterThan(500000);
    });

    it('should display summary stats in the UI', () => {
      expect(wrapper.text()).toContain('Monthly EMI');
      expect(wrapper.text()).toContain('Total Interest');
      expect(wrapper.text()).toContain('Total Amount');
    });

    it('should return null when no loan is selected', async () => {
      wrapper.vm.selectedLoan = null;
      await wrapper.vm.$nextTick();
      
      expect(wrapper.vm.summaryStats).toBeNull();
    });
  });

  describe('Schedule Filtering', () => {
    beforeEach(async () => {
      wrapper = createWrapper();
      wrapper.vm.selectedLoan = mockLoans[0];
      wrapper.vm.repaymentSchedule = mockSchedule;
      await wrapper.vm.$nextTick();
    });

    it('should filter schedule by year', async () => {
      const yearSelect = wrapper.findAll('select').find(select => 
        select.html().includes('All Years')
      );
      
      await yearSelect.setValue('1');
      
      const filtered = wrapper.vm.filteredSchedule;
      expect(filtered.every(row => row.month >= 1 && row.month <= 12)).toBe(true);
    });

    it('should show all months when "All Years" is selected', async () => {
      wrapper.vm.filterYear = '1';
      await wrapper.vm.$nextTick();
      
      wrapper.vm.filterYear = 'all';
      await wrapper.vm.$nextTick();
      
      expect(wrapper.vm.filteredSchedule.length).toBe(mockSchedule.length);
    });
  });

  describe('Sorting', () => {
    beforeEach(async () => {
      wrapper = createWrapper();
      wrapper.vm.selectedLoan = mockLoans[0];
      wrapper.vm.repaymentSchedule = mockSchedule;
      await wrapper.vm.$nextTick();
    });

    it('should sort by different fields', () => {
      wrapper.vm.handleSort('emi');
      expect(wrapper.vm.sortField).toBe('emi');
      
      wrapper.vm.handleSort('principalAmount');
      expect(wrapper.vm.sortField).toBe('principalAmount');
      expect(wrapper.vm.sortDirection).toBe('asc');
    });

    it('should handle sort field changes', () => {
      const initialField = wrapper.vm.sortField;
      wrapper.vm.handleSort('interestAmount');
      
      expect(wrapper.vm.sortField).toBe('interestAmount');
      expect(wrapper.vm.sortField).not.toBe(initialField);
    });
  });

  describe('Pagination', () => {
    beforeEach(async () => {
      wrapper = createWrapper();
      wrapper.vm.selectedLoan = mockLoans[0];
      // Create schedule with more than 12 items
      wrapper.vm.repaymentSchedule = Array.from({ length: 25 }, (_, i) => ({
        id: i + 1,
        month: i + 1,
        emi: 10273,
        principalAmount: 6690,
        interestAmount: 3583,
        balanceRemaining: 493310 - (i * 6690),
        isPaid: false,
      }));
      await wrapper.vm.$nextTick();
    });

    it('should paginate results correctly', () => {
      expect(wrapper.vm.paginatedSchedule.length).toBe(12);
      expect(wrapper.vm.totalPages).toBe(3);
    });

    it('should navigate to next page', async () => {
      wrapper.vm.currentPage = 1;
      wrapper.vm.currentPage++;
      await wrapper.vm.$nextTick();
      
      expect(wrapper.vm.currentPage).toBe(2);
      expect(wrapper.vm.paginatedSchedule[0].month).toBe(13);
    });

    it('should navigate to previous page', async () => {
      wrapper.vm.currentPage = 2;
      wrapper.vm.currentPage--;
      await wrapper.vm.$nextTick();
      
      expect(wrapper.vm.currentPage).toBe(1);
    });

    it('should not show pagination for small datasets', async () => {
      wrapper.vm.repaymentSchedule = mockSchedule;
      await wrapper.vm.$nextTick();
      
      expect(wrapper.vm.totalPages).toBe(1);
    });
  });

  describe('Payment Functionality', () => {
    beforeEach(async () => {
      wrapper = createWrapper();
      wrapper.vm.selectedLoan = mockLoans[0];
      wrapper.vm.repaymentSchedule = mockSchedule;
      await wrapper.vm.$nextTick();
      requests.makeRequestWithToken.mockResolvedValue({
        data: mockSchedule,
      });
    });

    it('should only allow paying first unpaid EMI', () => {
      expect(wrapper.vm.canPay(mockSchedule[0])).toBe(false); // Already paid
      expect(wrapper.vm.canPay(mockSchedule[1])).toBe(true); // Next unpaid
      expect(wrapper.vm.canPay(mockSchedule[2])).toBe(false); // Future payment
    });

    it('should mark payment as paid', async () => {
      await wrapper.vm.markAsPaid('LOAN001', 2);
      
      expect(requests.makeRequestWithToken).toHaveBeenCalledWith(
        'POST',
        '/api/loans/LOAN001/schedule/2/pay'
      );
    });

    it('should refresh schedule after payment', async () => {
      await wrapper.vm.markAsPaid('LOAN001', 2);
      
      expect(requests.makeRequestWithToken).toHaveBeenCalledTimes(2);
      expect(requests.makeRequestWithToken).toHaveBeenNthCalledWith(
        2,
        'GET',
        '/api/loans/LOAN001/schedule'
      );
    });

    it('should handle payment errors', async () => {
      requests.makeRequestWithToken.mockRejectedValue(new Error('Payment failed'));
      const consoleError = vi.spyOn(console, 'error').mockImplementation(() => {});
      
      await wrapper.vm.markAsPaid('LOAN001', 2);
      
      expect(consoleError).toHaveBeenCalled();
      consoleError.mockRestore();
    });
  });

  describe('Month Name Display', () => {
    beforeEach(() => {
      wrapper = createWrapper();
    });

    it('should format month name correctly', () => {
      const monthName = wrapper.vm.getMonthName(1, '2024-01-15');
      expect(monthName).toMatch(/Jan 2024/);
    });

    it('should handle different month indices', () => {
      const month1 = wrapper.vm.getMonthName(1, '2024-01-15');
      const month2 = wrapper.vm.getMonthName(2, '2024-01-15');
      
      expect(month1).not.toBe(month2);
    });

    it('should use current date when no start date provided', () => {
      const monthName = wrapper.vm.getMonthName(1);
      expect(monthName).toBeTruthy();
      expect(typeof monthName).toBe('string');
    });
  });

  describe('Core Functionality', () => {
    it('should compute filtered loans correctly', () => {
      wrapper = createWrapper();
      const filtered = wrapper.vm.filteredLoans;
      
      expect(filtered.length).toBe(2); // Only approved loans
      expect(filtered.every(loan => loan.status === 'APPROVED')).toBe(true);
    });

    it('should reset state properly on clear selection', () => {
      wrapper = createWrapper();
      wrapper.vm.selectedLoan = mockLoans[0];
      wrapper.vm.currentPage = 3;
      wrapper.vm.filterYear = '2';
      
      wrapper.vm.clearSelection();
      
      expect(wrapper.vm.selectedLoan).toBeNull();
      expect(wrapper.vm.currentPage).toBe(1);
      expect(wrapper.vm.filterYear).toBe('all');
    });

    it('should handle loan selection and API fetch', async () => {
      wrapper = createWrapper();
      requests.makeRequestWithToken.mockResolvedValue({ data: mockSchedule });
      
      await wrapper.vm.selectLoan(mockLoans[0]);
      
      expect(wrapper.vm.selectedLoan).toEqual(mockLoans[0]);
      expect(wrapper.vm.repaymentSchedule).toEqual(mockSchedule);
      expect(requests.makeRequestWithToken).toHaveBeenCalledWith(
        'GET',
        '/api/loans/LOAN001/schedule'
      );
    });
  });
});