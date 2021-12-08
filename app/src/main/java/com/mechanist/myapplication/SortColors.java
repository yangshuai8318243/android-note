package com.mechanist.myapplication;

/**
 * 按颜色进行排序
 * 有三种颜色的球，算法的目标是将这三种球按颜色顺序正确地排列。
 * 它其实是三向切分快速排序的一种变种，在三向切分快速排序中，
 * 每次切分都将数组分成三个区间：小于切分元素、等于切分元素、大于切分元素，
 * 而该算法是将数组分成三个区间：等于红色、等于白色、等于蓝色。
 */
public class SortColors {

    public void sortColors(int[] nums) {
        int zero = -1, one = 0, two = nums.length;
        while (one < two) {
            if (nums[one] == 0) {
                swap(nums, ++zero, one++);
            } else if (nums[one] == 2) {
                swap(nums, --two, one);
            } else {
                ++one;
            }
        }
    }

    private void swap(int[] nums, int i, int j) {
        int t = nums[i];
        nums[i] = nums[j];
        nums[j] = t;
    }
    public int maxProfit(int[] prices) {
        int n = prices.length;
        if (n == 0) return 0;
        int soFarMin = prices[0];
        int max = 0;
        for (int i = 1; i < n; i++) {
            if (soFarMin > prices[i]) soFarMin = prices[i];
            else max = Math.max(max, prices[i] - soFarMin);
        }
        return max;
    }
}
