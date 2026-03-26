package com.intelligent.driver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.intelligent.driver.entity.ParkingLot;
import com.intelligent.driver.mapper.ParkingLotMapper;
import com.intelligent.driver.service.ParkingLotService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ParkingLotServiceImpl extends ServiceImpl<ParkingLotMapper, ParkingLot> implements ParkingLotService {

    @Resource
    private ParkingLotMapper parkingLotMapper;

    @Override
    public PageInfo<ParkingLot> getNearbyParkingLots(BigDecimal latitude, BigDecimal longitude,
                                                     Double radius, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<ParkingLot> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ParkingLot::getStatus, 1);
        lambdaQueryWrapper.orderByAsc(ParkingLot::getId);

        PageHelper.startPage(pageNum, pageSize);
        List<ParkingLot> list = parkingLotMapper.selectList(lambdaQueryWrapper);

        return new PageInfo<>(list);
    }

    @Override
    public PageInfo<ParkingLot> searchParkingLots(String keyword, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<ParkingLot> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ParkingLot::getStatus, 1);
        lambdaQueryWrapper.and(wrapper ->
                wrapper.like(ParkingLot::getName, keyword)
                        .or()
                        .like(ParkingLot::getAddress, keyword)
        );
        lambdaQueryWrapper.orderByDesc(ParkingLot::getId);

        PageHelper.startPage(pageNum, pageSize);
        List<ParkingLot> list = parkingLotMapper.selectList(lambdaQueryWrapper);

        return new PageInfo<>(list);
    }

    @Override
    public PageInfo<ParkingLot> getParkingLots(String keyword, Integer status, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<ParkingLot> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        
        // 关键词搜索
        if (StringUtils.isNotBlank(keyword)) {
            lambdaQueryWrapper.and(wrapper ->
                    wrapper.like(ParkingLot::getName, keyword)
                            .or()
                            .like(ParkingLot::getAddress, keyword)
                            .or()
                            .like(ParkingLot::getDescription, keyword)
            );
        }
        
        // 状态过滤
        if (status != null) {
            lambdaQueryWrapper.eq(ParkingLot::getStatus, status);
        }
        
        lambdaQueryWrapper.orderByDesc(ParkingLot::getId);

        PageHelper.startPage(pageNum, pageSize);
        List<ParkingLot> list = parkingLotMapper.selectList(lambdaQueryWrapper);

        return new PageInfo<>(list);
    }

    @Override
    public void initMockData() {
        // 检查是否已有数据
        long count = parkingLotMapper.selectCount(null);
        if (count > 0) {
            return; // 已有数据，不重复插入
        }

        // 创建模拟停车场数据
        List<ParkingLot> mockData = List.of(
                createParkingLot(1L, "万达广场地下停车场", "北京市朝阳区建国路 93 号",
                        new BigDecimal("39.90872"), new BigDecimal("116.46953"),
                        500, 120, new BigDecimal("10.00"),
                        LocalTime.of(0, 0), LocalTime.of(23, 59),
                        "010-12345678", "充电桩，无障碍，监控",
                        new BigDecimal("4.5")),

                createParkingLot(2L, "国贸商城停车场", "北京市朝阳区建国门外大街 1 号",
                        new BigDecimal("39.90801"), new BigDecimal("116.45932"),
                        800, 350, new BigDecimal("15.00"),
                        LocalTime.of(6, 0), LocalTime.of(22, 0),
                        "010-87654321", "充电桩，VIP 专区，洗车服务",
                        new BigDecimal("4.8")),

                createParkingLot(3L, "三里屯太古里停车场", "北京市朝阳区三里屯路 19 号",
                        new BigDecimal("39.93654"), new BigDecimal("116.45432"),
                        300, 80, new BigDecimal("12.00"),
                        LocalTime.of(8, 0), LocalTime.of(22, 0),
                        "010-11223344", "无障碍，监控，餐饮",
                        new BigDecimal("4.3")),

                createParkingLot(4L, "北京站停车场", "北京市东城区毛家湾胡同甲 13 号",
                        new BigDecimal("39.90403"), new BigDecimal("116.42788"),
                        200, 50, new BigDecimal("8.00"),
                        LocalTime.of(0, 0), LocalTime.of(23, 59),
                        "010-66554433", "监控，24 小时营业",
                        new BigDecimal("4.0")),

                createParkingLot(5L, "奥林匹克公园停车场", "北京市朝阳区北辰东路 15 号",
                        new BigDecimal("39.99234"), new BigDecimal("116.39765"),
                        1000, 600, new BigDecimal("6.00"),
                        LocalTime.of(6, 0), LocalTime.of(21, 0),
                        "010-88997766", "充电桩，无障碍，大型车专用",
                        new BigDecimal("4.6")),

                createParkingLot(6L, "中关村广场停车场", "北京市海淀区中关村大街 1 号",
                        new BigDecimal("39.98123"), new BigDecimal("116.31654"),
                        400, 150, new BigDecimal("10.00"),
                        LocalTime.of(7, 0), LocalTime.of(22, 0),
                        "010-62345678", "充电桩，监控",
                        new BigDecimal("4.2")),

                createParkingLot(7L, "西单大悦城停车场", "北京市西城区西单北大街 131 号",
                        new BigDecimal("39.91234"), new BigDecimal("116.37432"),
                        350, 100, new BigDecimal("12.00"),
                        LocalTime.of(9, 0), LocalTime.of(22, 0),
                        "010-66112233", "VIP 专区，洗车服务",
                        new BigDecimal("4.4")),

                createParkingLot(8L, "首都机场 P3 停车场", "北京市朝阳区首都机场 1 号航站楼",
                        new BigDecimal("40.08011"), new BigDecimal("116.58512"),
                        2000, 1500, new BigDecimal("5.00"),
                        LocalTime.of(0, 0), LocalTime.of(23, 59),
                        "010-64567890", "充电桩，无障碍，长期停车优惠",
                        new BigDecimal("4.7"))
        );

        // 批量插入模拟数据
        saveBatch(mockData);
    }

    private ParkingLot createParkingLot(Long id, String name, String address,
                                        BigDecimal latitude, BigDecimal longitude,
                                        Integer totalSpaces, Integer availableSpaces,
                                        BigDecimal pricePerHour,
                                        LocalTime openingTime, LocalTime closingTime,
                                        String contactPhone, String facilities,
                                        BigDecimal rating) {
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.setId(id);
        parkingLot.setName(name);
        parkingLot.setAddress(address);
        parkingLot.setLatitude(latitude);
        parkingLot.setLongitude(longitude);
        parkingLot.setTotalSpaces(totalSpaces);
        parkingLot.setAvailableSpaces(availableSpaces);
        parkingLot.setPricePerHour(pricePerHour);
        parkingLot.setOpeningTime(openingTime);
        parkingLot.setClosingTime(closingTime);
        parkingLot.setContactPhone(contactPhone);
        parkingLot.setFacilities(facilities);
        parkingLot.setStatus(1);
        parkingLot.setRating(rating);
        parkingLot.setDescription(name + "提供安全便捷的停车服务");
        parkingLot.setCreateTime(LocalDateTime.now());
        parkingLot.setUpdateTime(LocalDateTime.now());
        return parkingLot;
    }
}

