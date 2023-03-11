package com.example.demo;

import com.example.demo.errors.OutOfboundExp;


public class SinIntegral {
    private double ans;

    public SinIntegral(double left, double right) throws OutOfboundExp {
        if (right < left) {
            throw new OutOfboundExp("Wrong borders");
        }
        double h = Math.abs((double) (right - left) / 100);
        ans = (Math.sin(left) + Math.sin(right)) / 2;
        for (double i = left + h; i <= right - h + 0.00000001; i += h) {
            ans += Math.sin(i);
        }
        ans *= h;
        ans = (double) Math.round(ans * 10000) / 10000;
    }

    public double getAns() {
        return ans;
    }

    public void setAns(double ans) {
        this.ans = ans;
    }
}
