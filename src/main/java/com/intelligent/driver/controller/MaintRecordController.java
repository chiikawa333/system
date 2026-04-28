package com.intelligent.driver.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import com.intelligent.driver.dto.MaintRecordDTO;
import com.intelligent.driver.entity.MaintRecord;
import com.intelligent.driver.responce.R;
import com.intelligent.driver.service.MaintRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "维保记录管理")
@RestController
@RequestMapping("/maint/record")
@CrossOrigin
public class MaintRecordController {

    @Resource
    private MaintRecordService maintRecordService;

    @Operation(summary = "新增维保预约")
    @PostMapping("/save")
    public R<Boolean> save(@RequestBody MaintRecordDTO dto) {
        boolean result = maintRecordService.saveMaintRecord(dto);
        return result ? R.success("预约成功") : R.fail("预约失败");
    }

    @Operation(summary = "更新维保记录")
    @PutMapping("/update")
    public R<Boolean> update(@RequestBody MaintRecordDTO dto) {
        boolean result = maintRecordService.updateMaintRecord(dto);
        return result ? R.success("更新成功") : R.fail("更新失败");
    }

    @Operation(summary = "获取维保记录列表")
    @GetMapping("/list")
    public R<List<MaintRecord>> getList(
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        List<MaintRecord> list = maintRecordService.getMaintRecords(status);
        return R.data(list);
    }

    @Operation(summary = "获取维保记录详情")
    @GetMapping("/{id}")
    public R<MaintRecord> getById(@PathVariable Long id) {
        MaintRecord record = maintRecordService.getMaintRecordById(id);
        return record != null ? R.data(record) : R.fail("记录不存在");
    }

    @Operation(summary = "删除维保记录")
    @DeleteMapping("/delete/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        boolean result = maintRecordService.deleteMaintRecord(id);
        return result ? R.success("删除成功") : R.fail("删除失败");
    }

    @Operation(summary = "批量删除维保记录")
    @DeleteMapping("/delete/batch")
    public R<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        boolean result = maintRecordService.deleteBatchMaintRecords(ids);
        return result ? R.success("删除成功") : R.fail("删除失败");
    }
}
