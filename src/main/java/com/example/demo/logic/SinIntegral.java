package com.example.demo.logic;

import com.example.demo.errors.OutOfboundExp;
import com.example.demo.logic.SinIntegralLogic;


public class SinIntegral {
    private double ans;

    public SinIntegral(double left, double right) throws OutOfboundExp {
        if (right < left) {
            throw new OutOfboundExp("Wrong borders");
        }
        ans = SinIntegralLogic.counting(left, right);
    }

    public double getAns() {
        return ans;
    }

    public void setAns(double ans) {
        this.ans = ans;
    }
}
