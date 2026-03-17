package com.jiangxijiaoyuan.component;

import com.jiangxijiaoyuan.service.ParkingLotService;
import jakarta.annotation.Resource;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 数据初始化器
 * 应用启动时自动初始化模拟数据
 */
@Component
public class DataInitializer implements ApplicationRunner {

    @Resource
    private ParkingLotService parkingLotService;

    @Override
    public void run(ApplicationArguments args) {
        // 应用启动时自动初始化停车场模拟数据
        parkingLotService.initMockData();
    }
}
