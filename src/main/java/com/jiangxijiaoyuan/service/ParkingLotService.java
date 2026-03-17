package com.jiangxijiaoyuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jiangxijiaoyuan.entity.ParkingLot;

import java.math.BigDecimal;
import java.util.List;

public interface ParkingLotService extends IService<ParkingLot> {

    /**
     * 获取附近停车场列表
     * @param latitude 纬度
     * @param longitude 经度
     * @param radius 半径（公里）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 停车场分页数据
     */
    PageInfo<ParkingLot> getNearbyParkingLots(BigDecimal latitude, BigDecimal longitude,
                                              Double radius, Integer pageNum, Integer pageSize);

    /**
     * 根据条件搜索停车场
     * @param keyword 搜索关键词
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 停车场分页数据
     */
    PageInfo<ParkingLot> searchParkingLots(String keyword, Integer pageNum, Integer pageSize);

    /**
     * 初始化模拟数据
     */
    void initMockData();
}
