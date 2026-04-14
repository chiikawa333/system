package com.intelligent.driver.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.intelligent.driver.dto.MaintRecordDTO;
import com.intelligent.driver.entity.MaintRecord;

import java.util.List;

public interface MaintRecordService extends IService<MaintRecord> {

    boolean saveMaintRecord(MaintRecordDTO dto);

    boolean updateMaintRecord(MaintRecordDTO dto);

    Page<MaintRecord> getMaintRecords(int pageNum, int pageSize, Integer status);

    MaintRecord getMaintRecordById(Long id);

    boolean deleteMaintRecord(Long id);

    boolean deleteBatchMaintRecords(List<Long> ids);

    Page<MaintRecord> getMaintHistory(Long userId, int pageNum, int pageSize);
}
