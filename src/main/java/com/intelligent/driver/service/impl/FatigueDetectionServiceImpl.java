package com.intelligent.driver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.intelligent.driver.entity.FatigueDetection;
import com.intelligent.driver.mapper.FatigueDetectionMapper;
import com.intelligent.driver.service.FatigueDetectionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FatigueDetectionServiceImpl extends ServiceImpl<FatigueDetectionMapper, FatigueDetection> implements FatigueDetectionService {

    @Override
    public boolean saveDetection(FatigueDetection detection) {
        return save(detection);
    }

    @Override
    public boolean batchSaveDetection(List<FatigueDetection> detectionList) {
        return saveBatch(detectionList);
    }

    @Override
    public Page<FatigueDetection> getAllRecords(int pageNum, int pageSize) {
        Page<FatigueDetection> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<FatigueDetection> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(FatigueDetection::getTimestamp);
        return page(page, wrapper);
    }

    @Override
    public List<FatigueDetection> getByStatus(String status) {
        LambdaQueryWrapper<FatigueDetection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FatigueDetection::getStatus, status);
        wrapper.orderByDesc(FatigueDetection::getTimestamp);
        return list(wrapper);
    }

    @Override
    public List<FatigueDetection> getByTimeRange(String startTime, String endTime) {
        LambdaQueryWrapper<FatigueDetection> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(FatigueDetection::getTimestamp, startTime);
        wrapper.le(FatigueDetection::getTimestamp, endTime);
        wrapper.orderByDesc(FatigueDetection::getTimestamp);
        return list(wrapper);
    }

    @Override
    public List<FatigueDetection> getByDamageTypes(String damageTypes) {
        LambdaQueryWrapper<FatigueDetection> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(FatigueDetection::getDamageTypes, damageTypes);
        wrapper.orderByDesc(FatigueDetection::getTimestamp);
        return list(wrapper);
    }

    @Override
    public boolean deleteById(Long id) {
        return removeById(id);
    }

    @Override
    public boolean deleteBatch(List<Long> ids) {
        return removeByIds(ids);
    }

    @Override
    public void deleteAll() {
        remove(null);
    }
}
