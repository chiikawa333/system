package com.intelligent.driver.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.intelligent.driver.entity.FatigueDetection;

import java.util.List;

public interface FatigueDetectionService extends IService<FatigueDetection> {

    boolean saveDetection(FatigueDetection detection);

    boolean batchSaveDetection(List<FatigueDetection> detectionList);

    Page<FatigueDetection> getAllRecords(int pageNum, int pageSize);

    List<FatigueDetection> getByStatus(String status);

    List<FatigueDetection> getByTimeRange(String startTime, String endTime);

    List<FatigueDetection> getByDamageTypes(String damageTypes);

    boolean deleteById(Long id);

    boolean deleteBatch(List<Long> ids);

    void deleteAll();
}
