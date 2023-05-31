package org.example.config;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CuratorConfig {
    @Autowired
    WrapperZK wrapperZk;

    @Bean(initMethod = "start")
    public CuratorFramework curatorFramework() {


        return CuratorFrameworkFactory.newClient(
                wrapperZk.getConnectString(),
                wrapperZk.getSessionTimeoutMs(),
                wrapperZk.getConnectionTimeoutMs(),
                new RetryNTimes(wrapperZk.getRetryCount(), wrapperZk.getElapsedTimeMs()));
//        return CuratorFrameworkFactory.builder()
//                .connectString(wrapperZk.getConnectString()) // 链接地址
//                .sessionTimeoutMs(wrapperZk.getSessionTimeoutMs()) // #会话超时时间设置
//                .connectionTimeoutMs(wrapperZk.getConnectionTimeoutMs()) // #连接超时时间
//                .retryPolicy(new RetryNTimes(wrapperZk.getRetryCount(), wrapperZk.getElapsedTimeMs())) // 重试策略
//                .namespace("/test") // 命名空间后续所有请求前面都会加上/test
//                .build();
    }
}
