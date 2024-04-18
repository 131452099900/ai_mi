package com.ai.common.core;

import java.util.ArrayList;
import java.util.List;

public class U {
    public static void main(String[] args) {
        new U().coinChange(new int[]{2}, 3);
    }
    public int coinChange(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        for (int i = 0; i < dp.length; i++) {
            dp[i] = Integer.MAX_VALUE;
        }
        for (int i = 0; i < dp.length; i++) {
            for (int j = coins.length - 1; j >= 0; j--) {
                if (i - coins[j] == 0) {
                    dp[i] = 1;
                } else if (i - coins[j] > 0) {
                    dp[i] = Math.min(dp[i - coins[j]] + 1, dp[i]);
                } else {
                    continue;
                }
            }
        }
        return dp[amount];
    }
}
