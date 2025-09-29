import { describe, it, expect, beforeEach, vi } from 'vitest';
import { mount } from '@vue/test-utils';
import { nextTick } from 'vue';
import EMICalculator from '../../views/EmiCalculator.vue'; // Adjust path as needed

// Mock vue-echarts to avoid chart rendering issues in tests
vi.mock('vue-echarts', () => ({
  default: {
    name: 'VChart',
    template: '<div class="mock-chart"></div>',
    props: ['option', 'autoresize']
  }
}));

describe('EMI Calculator', () => {
  let wrapper;

  beforeEach(() => {
    wrapper = mount(EMICalculator);
  });

  describe('Initial State', () => {
    it('should initialize with default values', () => {
      expect(wrapper.vm.loanAmount).toBe(500000);
      expect(wrapper.vm.interestRate).toBe(8.5);
      expect(wrapper.vm.tenure).toBe(15);
    });

    it('should calculate EMI on mount', () => {
      expect(wrapper.vm.emiDetails.emi).toBeGreaterThan(0);
      expect(wrapper.vm.emiDetails.totalAmount).toBeGreaterThan(0);
      expect(wrapper.vm.emiDetails.totalInterest).toBeGreaterThan(0);
      expect(wrapper.vm.emiDetails.principalAmount).toBe(500000);
    });
  });

  describe('EMI Calculation Logic', () => {
    it('should calculate EMI correctly for loan amount 500000, rate 8.5%, tenure 15 years', () => {
      wrapper.vm.loanAmount = 500000;
      wrapper.vm.interestRate = 8.5;
      wrapper.vm.tenure = 15;
      wrapper.vm.calculateEMI();

      // Formula: EMI = [P x R x (1+R)^N] / [(1+R)^N-1]
      // P = 500000, R = 8.5/12/100 = 0.00708, N = 180
      // Expected EMI ≈ 4924 (actual calculated value)
      expect(wrapper.vm.emiDetails.emi).toBeCloseTo(4924, 0);
    });

    it('should calculate total amount correctly', () => {
      wrapper.vm.loanAmount = 1000000;
      wrapper.vm.interestRate = 10;
      wrapper.vm.tenure = 20;
      wrapper.vm.calculateEMI();

      // Total amount should equal EMI * number of months (with small rounding tolerance)
      const expectedTotal = wrapper.vm.emiDetails.emi * 20 * 12;
      expect(Math.abs(wrapper.vm.emiDetails.totalAmount - expectedTotal)).toBeLessThan(100);
    });

    it('should calculate total interest correctly', () => {
      wrapper.vm.loanAmount = 300000;
      wrapper.vm.interestRate = 7;
      wrapper.vm.tenure = 10;
      wrapper.vm.calculateEMI();

      const expectedInterest = wrapper.vm.emiDetails.totalAmount - 300000;
      expect(wrapper.vm.emiDetails.totalInterest).toBe(expectedInterest);
    });

    it('should handle minimum loan amount', () => {
      wrapper.vm.loanAmount = 50000;
      wrapper.vm.interestRate = 6;
      wrapper.vm.tenure = 1;
      wrapper.vm.calculateEMI();

      expect(wrapper.vm.emiDetails.emi).toBeGreaterThan(0);
      expect(wrapper.vm.emiDetails.principalAmount).toBe(50000);
    });

    it('should handle maximum values', () => {
      wrapper.vm.loanAmount = 10000000;
      wrapper.vm.interestRate = 20;
      wrapper.vm.tenure = 30;
      wrapper.vm.calculateEMI();

      expect(wrapper.vm.emiDetails.emi).toBeGreaterThan(0);
      expect(wrapper.vm.emiDetails.totalAmount).toBeGreaterThan(10000000);
    });

    it('should round EMI to nearest integer', () => {
      wrapper.vm.loanAmount = 555555;
      wrapper.vm.interestRate = 8.75;
      wrapper.vm.tenure = 12;
      wrapper.vm.calculateEMI();

      expect(Number.isInteger(wrapper.vm.emiDetails.emi)).toBe(true);
      expect(Number.isInteger(wrapper.vm.emiDetails.totalAmount)).toBe(true);
      expect(Number.isInteger(wrapper.vm.emiDetails.totalInterest)).toBe(true);
    });
  });

  describe('Watchers', () => {
    it('should recalculate EMI when loan amount changes', async () => {
      const initialEMI = wrapper.vm.emiDetails.emi;
      
      wrapper.vm.loanAmount = 1000000;
      await nextTick();

      expect(wrapper.vm.emiDetails.emi).not.toBe(initialEMI);
      expect(wrapper.vm.emiDetails.emi).toBeGreaterThan(initialEMI);
    });

    it('should recalculate EMI when interest rate changes', async () => {
      const initialEMI = wrapper.vm.emiDetails.emi;
      
      wrapper.vm.interestRate = 12;
      await nextTick();

      expect(wrapper.vm.emiDetails.emi).not.toBe(initialEMI);
      expect(wrapper.vm.emiDetails.emi).toBeGreaterThan(initialEMI);
    });

    it('should recalculate EMI when tenure changes', async () => {
      const initialEMI = wrapper.vm.emiDetails.emi;
      
      wrapper.vm.tenure = 5;
      await nextTick();

      expect(wrapper.vm.emiDetails.emi).not.toBe(initialEMI);
      expect(wrapper.vm.emiDetails.emi).toBeGreaterThan(initialEMI);
    });

    it('should recalculate when multiple parameters change', async () => {
      wrapper.vm.loanAmount = 750000;
      wrapper.vm.interestRate = 9.5;
      wrapper.vm.tenure = 20;
      await nextTick();

      expect(wrapper.vm.emiDetails.principalAmount).toBe(750000);
      expect(wrapper.vm.emiDetails.emi).toBeGreaterThan(0);
    });
  });

  describe('Computed Properties', () => {
    it('should calculate interest percentage correctly', () => {
      wrapper.vm.loanAmount = 500000;
      wrapper.vm.interestRate = 10;
      wrapper.vm.tenure = 20;
      wrapper.vm.calculateEMI();

      const expected = Math.round(
        (wrapper.vm.emiDetails.totalInterest / wrapper.vm.emiDetails.totalAmount) * 100
      );
      expect(wrapper.vm.interestPercentage).toBe(expected);
    });

    it('should return 0 for interest percentage when total amount is 0', () => {
      wrapper.vm.emiDetails.totalAmount = 0;
      wrapper.vm.emiDetails.totalInterest = 0;

      expect(wrapper.vm.interestPercentage).toBe(0);
    });

    it('should generate pie chart data correctly', () => {
      wrapper.vm.calculateEMI();

      const pieData = wrapper.vm.pieOption.series[0].data;
      expect(pieData).toHaveLength(2);
      expect(pieData[0].name).toBe('Principal');
      expect(pieData[0].value).toBe(wrapper.vm.emiDetails.principalAmount);
      expect(pieData[1].name).toBe('Interest');
      expect(pieData[1].value).toBe(wrapper.vm.emiDetails.totalInterest);
    });

    it('should generate amortization data with correct number of points', () => {
      wrapper.vm.tenure = 10;
      wrapper.vm.calculateEMI();

      const amortData = wrapper.vm.amortizationData;
      // Should have tenure + 1 points (year 0 to year 10)
      expect(amortData.length).toBe(11);
      expect(amortData[0].year).toBe(0);
      expect(amortData[10].year).toBe(10);
    });

    it('should show decreasing balance in amortization data', () => {
      wrapper.vm.calculateEMI();
      const amortData = wrapper.vm.amortizationData;

      for (let i = 1; i < amortData.length; i++) {
        expect(amortData[i].remaining).toBeLessThanOrEqual(amortData[i - 1].remaining);
      }
    });

    it('should have near-zero balance at end of tenure', () => {
      wrapper.vm.calculateEMI();
      const amortData = wrapper.vm.amortizationData;
      const lastBalance = amortData[amortData.length - 1].remaining;

      // Should be very close to 0 (allowing for rounding)
      expect(lastBalance).toBeLessThan(100);
    });

    it('should generate line chart data correctly', () => {
      wrapper.vm.calculateEMI();
      const lineData = wrapper.vm.lineOption;

      expect(lineData.xAxis.data).toHaveLength(wrapper.vm.tenure + 1);
      expect(lineData.series[0].data).toHaveLength(wrapper.vm.tenure + 1);
    });
  });

  describe('UI Interactions', () => {
    it('should update loan amount via input', async () => {
      const input = wrapper.find('input[type="number"]');
      await input.setValue(750000);

      expect(wrapper.vm.loanAmount).toBe(750000);
    });

    it('should trigger recalculation on button click', async () => {
      const initialEMI = wrapper.vm.emiDetails.emi;
      const button = wrapper.find('button');
      
      // Change a value to see recalculation
      wrapper.vm.loanAmount = 750000;
      await button.trigger('click');

      // Verify EMI was recalculated (should be different after loan amount change)
      expect(wrapper.vm.emiDetails.emi).not.toBe(initialEMI);
    });

    it('should display formatted EMI value', () => {
      wrapper.vm.calculateEMI();
      const emiText = wrapper.text();
      
      expect(emiText).toContain('₹');
      expect(emiText).toContain(wrapper.vm.emiDetails.emi.toLocaleString());
    });

    it('should display tenure in months', async () => {
      wrapper.vm.tenure = 10;
      await nextTick(); // Wait for DOM update
      const text = wrapper.text();
      
      expect(text).toContain('120 months');
    });
  });

  describe('Edge Cases', () => {
    it('should handle zero interest rate gracefully', () => {
      wrapper.vm.loanAmount = 500000;
      wrapper.vm.interestRate = 0;
      wrapper.vm.tenure = 10;
      
      // With 0% interest, EMI should be principal / months
      expect(() => wrapper.vm.calculateEMI()).not.toThrow();
    });

    it('should handle very short tenure', () => {
      wrapper.vm.loanAmount = 100000;
      wrapper.vm.interestRate = 10;
      wrapper.vm.tenure = 1;
      wrapper.vm.calculateEMI();

      expect(wrapper.vm.emiDetails.emi).toBeGreaterThan(8000);
    });

    it('should handle very long tenure', () => {
      wrapper.vm.loanAmount = 500000;
      wrapper.vm.interestRate = 8;
      wrapper.vm.tenure = 30;
      wrapper.vm.calculateEMI();

      expect(wrapper.vm.emiDetails.emi).toBeLessThan(5000);
    });

    it('should maintain precision in calculations', () => {
      wrapper.vm.loanAmount = 123456;
      wrapper.vm.interestRate = 7.89;
      wrapper.vm.tenure = 13;
      wrapper.vm.calculateEMI();

      expect(wrapper.vm.emiDetails.emi).toBeGreaterThan(0);
      expect(wrapper.vm.emiDetails.totalAmount).toBeGreaterThan(wrapper.vm.loanAmount);
    });
  });

  describe('Data Validation', () => {
    it('should ensure total amount equals principal plus interest', () => {
      wrapper.vm.calculateEMI();

      const sum = wrapper.vm.emiDetails.principalAmount + wrapper.vm.emiDetails.totalInterest;
      expect(wrapper.vm.emiDetails.totalAmount).toBe(sum);
    });

    it('should ensure EMI times months equals total amount', () => {
      wrapper.vm.calculateEMI();

      const calculatedTotal = wrapper.vm.emiDetails.emi * wrapper.vm.tenure * 12;
      // Allow reasonable rounding difference (up to 100 due to rounding in EMI calculation)
      expect(Math.abs(calculatedTotal - wrapper.vm.emiDetails.totalAmount)).toBeLessThan(100);
    });

    it('should have positive values for all calculations', () => {
      wrapper.vm.calculateEMI();

      expect(wrapper.vm.emiDetails.emi).toBeGreaterThan(0);
      expect(wrapper.vm.emiDetails.totalAmount).toBeGreaterThan(0);
      expect(wrapper.vm.emiDetails.totalInterest).toBeGreaterThan(0);
      expect(wrapper.vm.emiDetails.principalAmount).toBeGreaterThan(0);
    });
  });
});