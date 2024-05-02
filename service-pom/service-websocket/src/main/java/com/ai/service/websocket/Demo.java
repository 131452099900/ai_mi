package com.ai.service.websocket;

import java.util.ArrayList;
import java.util.List;

class Solution {
    public static void main(String[] args) {
        new Solution().searchRange(new int[]{1, 4}, 4);
    }
    public int[] searchRange(int[] nums, int target) {
        if (nums.length == 0) {
            return new int[]{-1, -1};
        }
        int start = 0;
        int end = nums.length - 1;
        int mid = (start + end) / 2;;
        while (start < end) {
            if (nums[mid] > target) {
                end = mid - 1;
            } else if (nums[mid] < target) {
                start = mid + 1;
            } else {
                break;
            }
            mid = (start + end) / 2;
        }


        if (nums[mid] != target) {
            return new int[]{-1, -1};
        }

        int[] res = new int[]{mid, mid};
        int qT = mid;
        int hT = mid;
        while (qT >= 0 && nums[qT] == target) {
            res[0] = Math.min(res[0], qT);
            qT--;
        }

        while (hT < nums.length  && nums[hT] == target) {
            res[1] = Math.max(res[1], hT);
            hT++;
        }

        return res;
    }
}