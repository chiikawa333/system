package com.intelligent.driver.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.intelligent.driver.dto.CarData;
import com.intelligent.driver.dto.HistoryData;
import com.intelligent.driver.entity.CarTrajectory;

import java.util.List;

public interface CarTrajectoryService extends IService<CarTrajectory> {

    boolean saveCarData(CarData carData);

    boolean batchSaveCarData(List<CarData> carDataList);

    Page<CarTrajectory> getAllTrajectories(int pageNum, int pageSize);

    CarTrajectory getLatestPosition(String carId);

    Page<HistoryData> getHistoryTrajectory(String carId, int pageNum, int pageSize);

    Page<CarTrajectory> getRunningCars(int pageNum, int pageSize);

    void resetAutoIncrement();

    boolean deleteById(Long id);

    boolean deleteBatch(List<Long> ids);

    void deleteAll();
}
