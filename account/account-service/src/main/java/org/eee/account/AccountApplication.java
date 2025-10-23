package org.eee.account;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@EnableDubbo
@SpringBootApplication(scanBasePackages = {"org.eee.account", "stark.dataworks.boot.autoconfig"})
public class AccountApplication {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(AccountApplication.class, args);
    }
}
