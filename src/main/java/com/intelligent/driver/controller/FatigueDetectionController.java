package com.intelligent.driver.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.intelligent.driver.entity.FatigueDetection;
import com.intelligent.driver.responce.R;
import com.intelligent.driver.service.FatigueDetectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "疲劳检测管理")
@RestController
@RequestMapping("/api/fatigue")
public class FatigueDetectionController {

    @Autowired
    private FatigueDetectionService fatigueDetectionService;

    @Operation(summary = "保存疲劳检测记录")
    @PostMapping("/save")
    public R<Boolean> save(@RequestBody FatigueDetection detection) {
        boolean success = fatigueDetectionService.saveDetection(detection);
        return success ? R.data(true) : R.fail("保存失败");
    }

    @Operation(summary = "批量保存疲劳检测记录")
    @PostMapping("/batch-save")
    public R<Boolean> batchSave(@RequestBody List<FatigueDetection> detectionList) {
        boolean success = fatigueDetectionService.batchSaveDetection(detectionList);
        return success ? R.data(true) : R.fail("批量保存失败");
    }

    @Operation(summary = "获取疲劳检测记录列表")
    @GetMapping("/list")
    public R<Page<FatigueDetection>> getList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<FatigueDetection> page = fatigueDetectionService.getAllRecords(pageNum, pageSize);
        return R.data(page);
    }

    @Operation(summary = "根据状态查询疲劳检测记录")
    @GetMapping("/status/{status}")
    public R<List<FatigueDetection>> getByStatus(@PathVariable String status) {
        List<FatigueDetection> list = fatigueDetectionService.getByStatus(status);
        return R.data(list);
    }

    @Operation(summary = "根据时间范围查询疲劳检测记录")
    @GetMapping("/time-range")
    public R<List<FatigueDetection>> getByTimeRange(
            @RequestParam String startTime,
            @RequestParam String endTime) {
        List<FatigueDetection> list = fatigueDetectionService.getByTimeRange(startTime, endTime);
        return R.data(list);
    }

    @Operation(summary = "根据损伤类型查询疲劳检测记录")
    @GetMapping("/damage-types")
    public R<List<FatigueDetection>> getByDamageTypes(@RequestParam String damageTypes) {
        List<FatigueDetection> list = fatigueDetectionService.getByDamageTypes(damageTypes);
        return R.data(list);
    }

    @Operation(summary = "删除疲劳检测记录")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        boolean success = fatigueDetectionService.deleteById(id);
        return success ? R.data(true) : R.fail("删除失败");
    }

    @Operation(summary = "批量删除疲劳检测记录")
    @DeleteMapping("/batch")
    public R<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        boolean success = fatigueDetectionService.deleteBatch(ids);
        return success ? R.data(true) : R.fail("批量删除失败");
    }

    @Operation(summary = "清空所有疲劳检测记录")
    @DeleteMapping("/all")
    public R<Boolean> deleteAll() {
        fatigueDetectionService.deleteAll();
        return R.data(true);
    }
}
