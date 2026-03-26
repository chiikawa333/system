package com.intelligent.driver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.intelligent.driver.mapper")

public class AdminsystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminsystemApplication.class, args);
    }

}

