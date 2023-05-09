package com.example.demo.logic;

import com.example.demo.exeptions.OutOfboundExp;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class SinIntegral {
    @Id
    private Long id;
    private double ans;

    public SinIntegral(double left, double right) throws OutOfboundExp {
        ans = SinIntegralLogic.counting(left, right);
    }


    public SinIntegral() {
        this.ans = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getAns() {
        return ans;
    }

}
