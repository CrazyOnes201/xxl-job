package com.xxl.job.spring.boot.autoconfigure.entity;

import java.util.Arrays;

/**
 * *****************************************
 * **       @author : CrazyWalker         **
 * *****************************************
 * *****************************************
 * ** @date: 周六,04/04 2020 7:37 下午GMT+8**
 * *****************************************
 * *****************************************
 * **     用途: 控制台参数类                **
 * *****************************************
 */
public class Admin {

    private String[] addresses;

    public String[] getAddresses() {
        return addresses;
    }

    public void setAddresses(String[] addresses) {
        this.addresses = addresses;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "addresses=" + Arrays.toString(addresses) +
                '}';
    }
}
