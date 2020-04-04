package com.xxl.job.spring.boot.autoconfigure.entity;

/**
 * *****************************************
 * **       @author : CrazyWalker         **
 * *****************************************
 * *****************************************
 * ** @date: 周六,04/04 2020 7:20 下午GMT+8**
 * *****************************************
 * *****************************************
 * **     用途: 执行器参数类                **
 * *****************************************
 */
public class Executor {

    private String appname;

    private String ip;

    private Integer port;

    private String logpath;

    private Integer logretentiondays;

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getLogpath() {
        return logpath;
    }

    public void setLogpath(String logpath) {
        this.logpath = logpath;
    }

    public Integer getLogretentiondays() {
        return logretentiondays;
    }

    public void setLogretentiondays(Integer logretentiondays) {
        this.logretentiondays = logretentiondays;
    }

    @Override
    public String toString() {
        return "Executor{" +
                "appname='" + appname + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", logpath='" + logpath + '\'' +
                ", logretentiondays='" + logretentiondays + '\'' +
                '}';
    }
}
