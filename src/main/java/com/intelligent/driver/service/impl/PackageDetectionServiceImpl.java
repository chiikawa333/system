package com.intelligent.driver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.intelligent.driver.entity.PackageDetection;
import com.intelligent.driver.mapper.PackageDetectionMapper;
import com.intelligent.driver.service.PackageDetectionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PackageDetectionServiceImpl extends ServiceImpl<PackageDetectionMapper, PackageDetection> implements PackageDetectionService {

    @Override
    public boolean saveDetection(PackageDetection detection) {
        return save(detection);
    }

    @Override
    public boolean batchSaveDetection(List<PackageDetection> detectionList) {
        return saveBatch(detectionList);
    }

    @Override
    public Page<PackageDetection> getAllRecords(int pageNum, int pageSize) {
        Page<PackageDetection> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PackageDetection> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(PackageDetection::getTimestamp);
        return page(page, wrapper);
    }

    @Override
    public List<PackageDetection> getByStatus(String status) {
        LambdaQueryWrapper<PackageDetection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PackageDetection::getStatus, status);
        wrapper.orderByDesc(PackageDetection::getTimestamp);
        return list(wrapper);
    }

    @Override
    public List<PackageDetection> getByTimeRange(String startTime, String endTime) {
        LambdaQueryWrapper<PackageDetection> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(PackageDetection::getTimestamp, startTime);
        wrapper.le(PackageDetection::getTimestamp, endTime);
        wrapper.orderByDesc(PackageDetection::getTimestamp);
        return list(wrapper);
    }

    @Override
    public List<PackageDetection> getByDamageTypes(String damageTypes) {
        LambdaQueryWrapper<PackageDetection> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(PackageDetection::getDamageTypes, damageTypes);
        wrapper.orderByDesc(PackageDetection::getTimestamp);
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
