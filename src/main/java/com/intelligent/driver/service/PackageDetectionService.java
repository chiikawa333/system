package com.intelligent.driver.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.intelligent.driver.entity.PackageDetection;

import java.util.List;

public interface PackageDetectionService extends IService<PackageDetection> {

    boolean saveDetection(PackageDetection detection);

    boolean batchSaveDetection(List<PackageDetection> detectionList);

    Page<PackageDetection> getAllRecords(int pageNum, int pageSize);

    List<PackageDetection> getByStatus(String status);

    List<PackageDetection> getByTimeRange(String startTime, String endTime);

    List<PackageDetection> getByDamageTypes(String damageTypes);

    boolean deleteById(Long id);

    boolean deleteBatch(List<Long> ids);

    void deleteAll();
}
