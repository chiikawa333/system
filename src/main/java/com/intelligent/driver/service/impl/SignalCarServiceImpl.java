package com.intelligent.driver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.intelligent.driver.entity.SignalCar;
import com.intelligent.driver.mapper.SignalCarMapper;
import com.intelligent.driver.service.SignalCarService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class SignalCarServiceImpl extends ServiceImpl<SignalCarMapper, SignalCar> implements SignalCarService {

    private static final int MIN_INTERVAL_SECONDS = 6;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public boolean saveSignalCar(SignalCar signalCar) {
        SignalCar latestRecord = getLatestByCarIdAndColor(signalCar.getCarId(), signalCar.getLightColor());
        
        if (latestRecord != null) {
            try {
                LocalDateTime lastTime = LocalDateTime.parse(latestRecord.getRecordTime(), FORMATTER);
                LocalDateTime currentTime = LocalDateTime.parse(signalCar.getRecordTime(), FORMATTER);
                long secondsDiff = java.time.Duration.between(lastTime, currentTime).getSeconds();
                
                if (secondsDiff < MIN_INTERVAL_SECONDS) {
                    System.out.println("跳过重复信号 - 车辆: " + signalCar.getCarId() 
                        + ", 颜色: " + signalCar.getLightColor() 
                        + ", 距离上次: " + secondsDiff + "秒");
                    return true;
                }
            } catch (Exception e) {
                System.err.println("时间解析失败，直接保存: " + e.getMessage());
            }
        }
        
        return save(signalCar);
    }

    private SignalCar getLatestByCarIdAndColor(String carId, String lightColor) {
        LambdaQueryWrapper<SignalCar> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SignalCar::getCarId, carId)
               .eq(SignalCar::getLightColor, lightColor)
               .orderByDesc(SignalCar::getRecordTime)
               .last("LIMIT 1");
        return getOne(wrapper);
    }

    @Override
    public boolean batchSaveSignalCar(List<SignalCar> signalCarList) {
        return saveBatch(signalCarList);
    }

    @Override
    public Page<SignalCar> getAllRecords(int pageNum, int pageSize) {
        Page<SignalCar> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SignalCar> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SignalCar::getRecordTime);
        return page(page, wrapper);
    }

    @Override
    public List<SignalCar> getByCarId(String carId) {
        LambdaQueryWrapper<SignalCar> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SignalCar::getCarId, carId);
        wrapper.orderByDesc(SignalCar::getRecordTime);
        return list(wrapper);
    }

    @Override
    public List<SignalCar> getByLightColor(String lightColor) {
        LambdaQueryWrapper<SignalCar> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SignalCar::getLightColor, lightColor);
        wrapper.orderByDesc(SignalCar::getRecordTime);
        return list(wrapper);
    }

    @Override
    public boolean deleteById(String id) {
        return removeById(id);
    }

    @Override
    public boolean deleteBatch(List<String> ids) {
        return removeByIds(ids);
    }

    @Override
    public void deleteAll() {
        remove(null);
    }
}
