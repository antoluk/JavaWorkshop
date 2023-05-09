package com.example.demo.logic;


public class SinIntegralLogic {
    public static double counting(double left, double right) {
        double h = Math.abs((right - left) / 100);
        double ans = (Math.sin(left) + Math.sin(right)) / 2;
        for (double i = left + h; i <= right - h + 0.00000001; i += h) {
            ans += Math.sin(i);
        }
        ans *= h;
        ans = (double) Math.round(ans * 10000) / 10000;
        return ans;
    }

}
