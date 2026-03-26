package com.intelligent.driver.component;

import com.intelligent.driver.service.ParkingLotService;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 数据初始化器
 * 应用启动时自动初始化模拟数据
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Resource
    private ParkingLotService parkingLotService;

    @Override
    public void run(String... args) {
        // 应用启动时自动初始化停车场模拟数据
        parkingLotService.initMockData();
    }
}
