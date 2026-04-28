package com.intelligent.driver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.intelligent.driver.dto.MaintRecordDTO;
import com.intelligent.driver.entity.MaintRecord;
import com.intelligent.driver.mapper.MaintRecordMapper;
import com.intelligent.driver.service.MaintRecordService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MaintRecordServiceImpl extends ServiceImpl<MaintRecordMapper, MaintRecord>
        implements MaintRecordService {

    @Resource
    private MaintRecordMapper maintRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveMaintRecord(MaintRecordDTO dto) {
        MaintRecord record = new MaintRecord();
        BeanUtils.copyProperties(dto, record);

        if (record.getAppointmentNo() == null) {
            record.setAppointmentNo(generateAppointmentNo());
        }
        if (record.getStatus() == null) {
            record.setStatus(1);
        }
        if (record.getPaymentStatus() == null) {
            record.setPaymentStatus(0);
        }
        if (record.getDeleted() == null) {
            record.setDeleted(0);
        }
        if (record.getRepairPlan() != null && record.getRepairPlan().trim().isEmpty()) {
            record.setRepairPlan(null);
        }

        return save(record);
    }

    private String generateAppointmentNo() {
        String dateStr = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = count() + 1;
        return "MR" + dateStr + String.format("%04d", count);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMaintRecord(MaintRecordDTO dto) {
        MaintRecord record = new MaintRecord();
        BeanUtils.copyProperties(dto, record);
        return updateById(record);
    }

    @Override
    public PageInfo<MaintRecord> getMaintRecords(int pageNum, int pageSize, Integer status) {
        LambdaQueryWrapper<MaintRecord> wrapper = new LambdaQueryWrapper<>();

        if (status != null) {
            wrapper.eq(MaintRecord::getStatus, status);
        }

        wrapper.eq(MaintRecord::getDeleted, 0)
                .orderByDesc(MaintRecord::getId);

        PageHelper.startPage(pageNum, pageSize);
        List<MaintRecord> list = maintRecordMapper.selectList(wrapper);

        return new PageInfo<>(list);
    }

    @Override
    public List<MaintRecord> getMaintRecords(Integer status) {
        LambdaQueryWrapper<MaintRecord> wrapper = new LambdaQueryWrapper<>();

        if (status != null) {
            wrapper.eq(MaintRecord::getStatus, status);
        }

        wrapper.eq(MaintRecord::getDeleted, 0)
                .orderByDesc(MaintRecord::getId);

        return list(wrapper);
    }

    @Override
    public MaintRecord getMaintRecordById(Long id) {
        LambdaQueryWrapper<MaintRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MaintRecord::getId, id)
                .eq(MaintRecord::getDeleted, 0);
        return getOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteMaintRecord(Long id) {
        MaintRecord record = getById(id);
        if (record != null) {
            record.setDeleted(1);
            return updateById(record);
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteBatchMaintRecords(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }

        List<MaintRecord> records = listByIds(ids);
        for (MaintRecord record : records) {
            record.setDeleted(1);
        }

        return updateBatchById(records);
    }

    @Override
    public PageInfo<MaintRecord> getMaintHistory(Long userId, int pageNum, int pageSize) {
        LambdaQueryWrapper<MaintRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MaintRecord::getUserId, userId)
                .eq(MaintRecord::getDeleted, 0)
                .in(MaintRecord::getStatus, 2, 3, 4, 5)
                .orderByDesc(MaintRecord::getId);

        PageHelper.startPage(pageNum, pageSize);
        List<MaintRecord> list = maintRecordMapper.selectList(wrapper);

        return new PageInfo<>(list);
    }
}
