package com.jiangxijiaoyuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiangxijiaoyuan.dto.CarData;
import com.jiangxijiaoyuan.dto.HistoryData;
import com.jiangxijiaoyuan.entity.CarTrajectory;
import com.jiangxijiaoyuan.mapper.CarTrajectoryMapper;
import com.jiangxijiaoyuan.service.CarTrajectoryService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarTrajectoryServiceImpl extends ServiceImpl<CarTrajectoryMapper, CarTrajectory>
        implements CarTrajectoryService {

    @Resource
    private CarTrajectoryMapper carTrajectoryMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveCarData(CarData carData) {
        CarTrajectory trajectory = new CarTrajectory();
        trajectory.setCarId(carData.getCarId());
        trajectory.setPlateNumber(carData.getPlateNumber());
        trajectory.setVehicleType(carData.getVehicleType() != null ? carData.getVehicleType() : 1);
        trajectory.setXCoordinate(carData.getXCoordinate());
        trajectory.setYCoordinate(carData.getYCoordinate());
        trajectory.setInterval(carData.getInterval());
        trajectory.setSpeed(carData.getSpeed());
        trajectory.setStatus(carData.getStatus() != null ? carData.getStatus() : 1);

        if (carData.getStatus() != null && carData.getStatus() == 1) {
            trajectory.setEntryTime(LocalDateTime.now());
        }

        return save(trajectory);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchSaveCarData(List<CarData> carDataList) {
        List<CarTrajectory> trajectories = carDataList.stream().map(carData -> {
            CarTrajectory trajectory = new CarTrajectory();
            trajectory.setCarId(carData.getCarId());
            trajectory.setPlateNumber(carData.getPlateNumber());
            trajectory.setVehicleType(carData.getVehicleType() != null ? carData.getVehicleType() : 1);
            trajectory.setXCoordinate(carData.getXCoordinate());
            trajectory.setYCoordinate(carData.getYCoordinate());
            trajectory.setInterval(carData.getInterval());
            trajectory.setSpeed(carData.getSpeed());
            trajectory.setStatus(carData.getStatus() != null ? carData.getStatus() : 1);

            if (carData.getStatus() != null && carData.getStatus() == 1) {
                trajectory.setEntryTime(LocalDateTime.now());
            }

            return trajectory;
        }).collect(Collectors.toList());

        return saveBatch(trajectories);
    }

    @Override
    public Page<CarTrajectory> getAllTrajectories(int pageNum, int pageSize) {
        Page<CarTrajectory> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<CarTrajectory> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(CarTrajectory::getId);
        return page(page, wrapper);
    }

    @Override
    public CarTrajectory getLatestPosition(String carId) {
        LambdaQueryWrapper<CarTrajectory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CarTrajectory::getCarId, carId)
                .orderByDesc(CarTrajectory::getId)
                .last("LIMIT 1");
        return getOne(wrapper);
    }

    @Override
    public Page<HistoryData> getHistoryTrajectory(String carId, int pageNum, int pageSize) {
        Page<CarTrajectory> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<CarTrajectory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CarTrajectory::getCarId, carId)
                .orderByDesc(CarTrajectory::getId);

        Page<CarTrajectory> trajectoryPage = page(page, wrapper);

        Page<HistoryData> historyPage = new Page<>(pageNum, pageSize);
        historyPage.setTotal(trajectoryPage.getTotal());

        List<HistoryData> historyDataList = trajectoryPage.getRecords().stream().map(trajectory -> {
            HistoryData data = new HistoryData();
            data.setCarId(trajectory.getCarId());
            data.setXCoordinate(trajectory.getXCoordinate());
            data.setYCoordinate(trajectory.getYCoordinate());
            data.setSpeed(trajectory.getSpeed());
            data.setInterval(trajectory.getInterval());

            if (trajectory.getEntryTime() != null) {
                long seconds = java.time.Duration.between(
                        trajectory.getEntryTime(),
                        LocalDateTime.now()
                ).getSeconds();
                data.setSeconds((int) seconds);
            } else {
                data.setSeconds(0);
            }

            return data;
        }).collect(Collectors.toList());

        historyPage.setRecords(historyDataList);
        return historyPage;
    }

    @Override
    public Page<CarTrajectory> getRunningCars(int pageNum, int pageSize) {
        Page<CarTrajectory> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<CarTrajectory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CarTrajectory::getStatus, 1)
                .orderByDesc(CarTrajectory::getId);
        return page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetAutoIncrement() {
        carTrajectoryMapper.resetAutoIncrement();
    }
}
