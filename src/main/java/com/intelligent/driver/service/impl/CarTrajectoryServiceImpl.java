package com.intelligent.driver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.intelligent.driver.dto.CarData;
import com.intelligent.driver.dto.HistoryData;
import com.intelligent.driver.entity.CarTrajectory;
import com.intelligent.driver.mapper.CarTrajectoryMapper;
import com.intelligent.driver.service.CarTrajectoryService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
        System.out.println("=== 接收到小车位置数据 ===");
        System.out.println("carId: " + carData.getCarId());
        System.out.println("plateNumber: " + carData.getPlateNumber());
        System.out.println("xCoordinate: '" + carData.getXCoordinate() + "'");
        System.out.println("yCoordinate: '" + carData.getYCoordinate() + "'");
        System.out.println("interval: '" + carData.getInterval() + "'");
        System.out.println("speed: " + carData.getSpeed());
        System.out.println("status: " + carData.getStatus());
        System.out.println("seconds: " + carData.getSeconds());
        
        CarTrajectory trajectory = new CarTrajectory();
        trajectory.setCarId(carData.getCarId());
        trajectory.setPlateNumber(carData.getPlateNumber());
        trajectory.setVehicleType(carData.getVehicleType() != null ? carData.getVehicleType() : 1);
        trajectory.setXCoordinate(carData.getXCoordinate() != null && !carData.getXCoordinate().isEmpty() ? carData.getXCoordinate() : "0");
        trajectory.setYCoordinate(carData.getYCoordinate() != null && !carData.getYCoordinate().isEmpty() ? carData.getYCoordinate() : "0");
        trajectory.setInterval(carData.getInterval() != null && !carData.getInterval().isEmpty() ? carData.getInterval() : "0");
        trajectory.setSpeed(carData.getSpeed() != null ? carData.getSpeed() : new BigDecimal("0"));
        trajectory.setStatus(carData.getStatus() != null ? carData.getStatus() : 1);

        if (carData.getStatus() != null && carData.getStatus() == 1) {
            trajectory.setEntryTime(LocalDateTime.now());
        }

        boolean result = save(trajectory);
        System.out.println("保存结果：" + result);
        System.out.println("生成的轨迹 ID: " + trajectory.getId());
        System.out.println("保存的 xCoordinate: " + trajectory.getXCoordinate());
        System.out.println("保存的 yCoordinate: " + trajectory.getYCoordinate());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchSaveCarData(List<CarData> carDataList) {
        List<CarTrajectory> trajectories = carDataList.stream().map(carData -> {
            System.out.println("批量保存 - 处理车辆：" + carData.getCarId());
            System.out.println("  xCoordinate: '" + carData.getXCoordinate() + "'");
            System.out.println("  yCoordinate: '" + carData.getYCoordinate() + "'");
            
            CarTrajectory trajectory = new CarTrajectory();
            trajectory.setCarId(carData.getCarId());
            trajectory.setPlateNumber(carData.getPlateNumber());
            trajectory.setVehicleType(carData.getVehicleType() != null ? carData.getVehicleType() : 1);
            trajectory.setXCoordinate(carData.getXCoordinate() != null && !carData.getXCoordinate().isEmpty() ? carData.getXCoordinate() : "0");
            trajectory.setYCoordinate(carData.getYCoordinate() != null && !carData.getYCoordinate().isEmpty() ? carData.getYCoordinate() : "0");
            trajectory.setInterval(carData.getInterval() != null && !carData.getInterval().isEmpty() ? carData.getInterval() : "0");
            trajectory.setSpeed(carData.getSpeed() != null ? carData.getSpeed() : new BigDecimal("0"));
            trajectory.setStatus(carData.getStatus() != null ? carData.getStatus() : 1);

            if (carData.getStatus() != null && carData.getStatus() == 1) {
                trajectory.setEntryTime(LocalDateTime.now());
            }

            return trajectory;
        }).collect(Collectors.toList());

        boolean result = saveBatch(trajectories);
        System.out.println("批量保存结果：" + result);
        return result;
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(Long id) {
        return removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        return removeByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll() {
        remove(null);
        resetAutoIncrement();
    }
}
