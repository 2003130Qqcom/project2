package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * TLias Web 教务管理系统 — Spring Boot 启动入口。
 *
 * <p>自动扫描同级及子包下的 Bean、配置和 Mapper。</p>
 */
@SpringBootApplication
public class TliasWebManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(TliasWebManagementApplication.class, args);
    }
}
