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

        // 创建模拟仓库数据
        List<ParkingLot> mockData = List.of(
                createParkingLot(1L, "华东智能仓储中心", "上海市浦东新区临港新城物流园区 A1 栋",
                        new BigDecimal("31.08972"), new BigDecimal("121.86953"),
                        5000, 1200, new BigDecimal("10.00"),
                        LocalTime.of(0, 0), LocalTime.of(23, 59),
                        "021-58123456", "自动化分拣，冷链存储，24小时监控，叉车通道",
                        new BigDecimal("4.8")),

                createParkingLot(2L, "华北物流配送基地", "天津市滨海新区保税区物流大道 88 号",
                        new BigDecimal("39.08801"), new BigDecimal("117.75932"),
                        8000, 3500, new BigDecimal("8.00"),
                        LocalTime.of(6, 0), LocalTime.of(22, 0),
                        "022-25876543", "智能货架，恒温仓，装卸平台，GPS定位",
                        new BigDecimal("4.6")),

                createParkingLot(3L, "华南电商仓储枢纽", "广东省广州市白云区空港物流园 B3 区",
                        new BigDecimal("23.33654"), new BigDecimal("113.25432"),
                        6000, 1800, new BigDecimal("12.00"),
                        LocalTime.of(8, 0), LocalTime.of(22, 0),
                        "020-86112233", "快速分拣，包装服务，快递对接，安防系统",
                        new BigDecimal("4.7")),

                createParkingLot(4L, "西南物资储备仓库", "四川省成都市双流区航空港物流园区 C5 栋",
                        new BigDecimal("30.50403"), new BigDecimal("103.92788"),
                        4000, 1500, new BigDecimal("9.00"),
                        LocalTime.of(0, 0), LocalTime.of(23, 59),
                        "028-85665544", "大宗货物存储，防潮防火，铁路专线",
                        new BigDecimal("4.5")),

                createParkingLot(5L, "华中智能制造仓库", "湖北省武汉市东西湖区保税物流中心 D2 区",
                        new BigDecimal("30.69234"), new BigDecimal("114.19765"),
                        7000, 2600, new BigDecimal("11.00"),
                        LocalTime.of(6, 0), LocalTime.of(21, 0),
                        "027-83997766", "AGV机器人，智能盘点，温湿度控制",
                        new BigDecimal("4.9")),

                createParkingLot(6L, "东北冷链物流基地", "辽宁省沈阳市浑南区现代物流园 E1 栋",
                        new BigDecimal("41.78123"), new BigDecimal("123.51654"),
                        3000, 800, new BigDecimal("13.00"),
                        LocalTime.of(7, 0), LocalTime.of(22, 0),
                        "024-23623456", "冷冻冷藏，生鲜专区，温度监控，快速配送",
                        new BigDecimal("4.4")),

                createParkingLot(7L, "西北综合仓储中心", "陕西省西安市国际港务区物流大道 F6 区",
                        new BigDecimal("34.41234"), new BigDecimal("109.07432"),
                        5500, 2100, new BigDecimal("7.00"),
                        LocalTime.of(9, 0), LocalTime.of(22, 0),
                        "029-86611223", "中欧班列对接，跨境仓储，保税功能",
                        new BigDecimal("4.3")),

                createParkingLot(8L, "京东亚洲一号智能仓", "江苏省昆山市花桥经济开发区 G8 栋",
                        new BigDecimal("31.28011"), new BigDecimal("121.08512"),
                        10000, 5000, new BigDecimal("15.00"),
                        LocalTime.of(0, 0), LocalTime.of(23, 59),
                        "0512-57456789", "全自动分拣，无人搬运，AI调度，当日达",
                        new BigDecimal("4.9"))
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

