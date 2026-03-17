package com.jiangxijiaoyuan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.jiangxijiaoyuan.mapper")

public class AdminsystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminsystemApplication.class, args);
    }

}

