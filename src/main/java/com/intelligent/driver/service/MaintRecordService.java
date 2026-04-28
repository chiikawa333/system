package com.intelligent.driver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.intelligent.driver.dto.MaintRecordDTO;
import com.intelligent.driver.entity.MaintRecord;

import java.util.List;

public interface MaintRecordService extends IService<MaintRecord> {

    boolean saveMaintRecord(MaintRecordDTO dto);

    boolean updateMaintRecord(MaintRecordDTO dto);

    PageInfo<MaintRecord> getMaintRecords(int pageNum, int pageSize, Integer status);

    List<MaintRecord> getMaintRecords(Integer status);

    MaintRecord getMaintRecordById(Long id);

    boolean deleteMaintRecord(Long id);

    boolean deleteBatchMaintRecords(List<Long> ids);

    PageInfo<MaintRecord> getMaintHistory(Long userId, int pageNum, int pageSize);
}
