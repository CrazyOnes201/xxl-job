package com.xxl.job.spring.boot.autoconfigure;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import com.xxl.job.spring.boot.autoconfigure.entity.Admin;
import com.xxl.job.spring.boot.autoconfigure.entity.Executor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

/**
 * *****************************************
 * **       @author : CrazyWalker         **
 * *****************************************
 * *****************************************
 * ** @date: 周五,04/03 2020 3:56 下午GMT+8**
 * *****************************************
 * *****************************************
 * **     用途:        **
 * *****************************************
 */
@Configuration
@EnableConfigurationProperties({XxlJobProperties.class})
public class XxlJobAutoConfiguration {

    /**
     *
     * @author CrazyWalker
     * @param xxlJobProperties
     * @return
     */
    @Bean
    public XxlJobSpringExecutor xxlJobSpringExecutor(XxlJobProperties xxlJobProperties) {
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAccessToken(xxlJobProperties.getAccessToke());
        xxlJobSpringExecutor.setAdminAddresses(Optional
                .ofNullable(xxlJobProperties.getAdmin()).map(Admin::getAddresses).orElse(null));
        Executor executor = xxlJobProperties.getExecutor();
        if (executor != null) {
            xxlJobSpringExecutor.setAppName(executor.getAppname());
            xxlJobSpringExecutor.setPort(executor.getPort());
            xxlJobSpringExecutor.setIp(executor.getIp());
            xxlJobSpringExecutor.setLogPath(executor.getLogpath());
            xxlJobSpringExecutor.setLogRetentionDays(executor.getLogretentiondays());
        }

        return xxlJobSpringExecutor;
    }
}
