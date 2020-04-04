package com.xxl.job.spring.boot.autoconfigure;

import com.xxl.job.spring.boot.autoconfigure.entity.Admin;
import com.xxl.job.spring.boot.autoconfigure.entity.Executor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * *****************************************
 * **       @author : CrazyWalker         **
 * *****************************************
 * *****************************************
 * ** @date: 周五,04/03 2020 3:59 下午GMT+8**
 * *****************************************
 * *****************************************
 * **     用途: xxl-job配置                **
 * *****************************************
 */
@ConfigurationProperties(prefix = XxlJobProperties.PREFIX)
public class XxlJobProperties {

    static final String PREFIX = "xxl";

    private String accessToke;

    private Executor executor;

    private Admin admin;

    public String getAccessToke() {
        return accessToke;
    }

    public void setAccessToke(String accessToke) {
        this.accessToke = accessToke;
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    @Override
    public String toString() {
        return "XxlJobProperties{" +
                "accessToke='" + accessToke + '\'' +
                ", executor=" + executor +
                ", admin=" + admin +
                '}';
    }
}
