package com.intelligent.driver.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.intelligent.driver.entity.PackageDetection;
import com.intelligent.driver.responce.R;
import com.intelligent.driver.service.PackageDetectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "包裹检测管理")
@RestController
@RequestMapping("/api/package")
public class PackageDetectionController {

    @Autowired
    private PackageDetectionService packageDetectionService;

    @Operation(summary = "保存包裹检测记录")
    @PostMapping("/save")
    public R<Boolean> save(@RequestBody PackageDetection detection) {
        boolean success = packageDetectionService.saveDetection(detection);
        return success ? R.data(true) : R.fail("保存失败");
    }

    @Operation(summary = "批量保存包裹检测记录")
    @PostMapping("/batch-save")
    public R<Boolean> batchSave(@RequestBody List<PackageDetection> detectionList) {
        boolean success = packageDetectionService.batchSaveDetection(detectionList);
        return success ? R.data(true) : R.fail("批量保存失败");
    }

    @Operation(summary = "获取包裹检测记录列表")
    @GetMapping("/list")
    public R<Page<PackageDetection>> getList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<PackageDetection> page = packageDetectionService.getAllRecords(pageNum, pageSize);
        return R.data(page);
    }

    @Operation(summary = "根据状态查询包裹检测记录")
    @GetMapping("/status/{status}")
    public R<List<PackageDetection>> getByStatus(@PathVariable String status) {
        List<PackageDetection> list = packageDetectionService.getByStatus(status);
        return R.data(list);
    }

    @Operation(summary = "根据时间范围查询包裹检测记录")
    @GetMapping("/time-range")
    public R<List<PackageDetection>> getByTimeRange(
            @RequestParam String startTime,
            @RequestParam String endTime) {
        List<PackageDetection> list = packageDetectionService.getByTimeRange(startTime, endTime);
        return R.data(list);
    }

    @Operation(summary = "根据破损类型查询包裹检测记录")
    @GetMapping("/damage-types")
    public R<List<PackageDetection>> getByDamageTypes(@RequestParam String damageTypes) {
        List<PackageDetection> list = packageDetectionService.getByDamageTypes(damageTypes);
        return R.data(list);
    }

    @Operation(summary = "删除包裹检测记录")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        boolean success = packageDetectionService.deleteById(id);
        return success ? R.data(true) : R.fail("删除失败");
    }

    @Operation(summary = "批量删除包裹检测记录")
    @DeleteMapping("/batch")
    public R<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        boolean success = packageDetectionService.deleteBatch(ids);
        return success ? R.data(true) : R.fail("批量删除失败");
    }

    @Operation(summary = "清空所有包裹检测记录")
    @DeleteMapping("/all")
    public R<Boolean> deleteAll() {
        packageDetectionService.deleteAll();
        return R.data(true);
    }
}
