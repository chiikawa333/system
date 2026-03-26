package com.intelligent.driver.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import com.intelligent.driver.dto.CarData;
import com.intelligent.driver.dto.HistoryData;
import com.intelligent.driver.entity.CarTrajectory;
import com.intelligent.driver.responce.R;
import com.intelligent.driver.service.CarTrajectoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "车辆轨迹管理")
@RestController
@RequestMapping("/car/trajectory")
@CrossOrigin
public class CarTrajectoryController {

    @Resource
    private CarTrajectoryService carTrajectoryService;

    @Operation(summary = "保存单个小车位置")
    @PostMapping("/save")
    public R<Boolean> save(@RequestBody CarData carData) {
        boolean result = carTrajectoryService.saveCarData(carData);
        return result ? R.success() : R.fail();
    }

    @Operation(summary = "批量保存小车位置")
    @PostMapping("/save/batch")
    public R<Boolean> batchSave(@RequestBody List<CarData> carDataList) {
        boolean result = carTrajectoryService.batchSaveCarData(carDataList);
        return result ? R.success() : R.fail();
    }

    @Operation(summary = "获取所有小车轨迹")
    @GetMapping("/all")
    public R<PageInfo<CarTrajectory>> getAll(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<CarTrajectory> page = carTrajectoryService.getAllTrajectories(pageNum, pageSize);
        PageInfo<CarTrajectory> pageInfo = new PageInfo<>(page.getRecords());
        pageInfo.setTotal(page.getTotal());
        return R.data(pageInfo);
    }

    @Operation(summary = "获取小车最新位置")
    @GetMapping("/latest/{carId}")
    public R<CarTrajectory> getLatest(@PathVariable String carId) {
        CarTrajectory trajectory = carTrajectoryService.getLatestPosition(carId);
        return trajectory != null ? R.data(trajectory) : R.fail("未找到车辆信息");
    }

    @Operation(summary = "获取小车历史轨迹")
    @GetMapping("/history/{carId}")
    public R<PageInfo<HistoryData>> getHistory(
            @PathVariable String carId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<HistoryData> page = carTrajectoryService.getHistoryTrajectory(carId, pageNum, pageSize);
        PageInfo<HistoryData> pageInfo = new PageInfo<>(page.getRecords());
        pageInfo.setTotal(page.getTotal());
        return R.data(pageInfo);
    }

    @Operation(summary = "获取所有运行中的小车")
    @GetMapping("/running")
    @SaCheckLogin
    public R<PageInfo<CarTrajectory>> getRunning(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<CarTrajectory> page = carTrajectoryService.getRunningCars(pageNum, pageSize);
        PageInfo<CarTrajectory> pageInfo = new PageInfo<>(page.getRecords());
        pageInfo.setTotal(page.getTotal());
        return R.data(pageInfo);
    }

    @Operation(summary = "删除单个小车轨迹")
    @DeleteMapping("/delete/{id}")
    public R<Boolean> deleteById(@PathVariable Long id) {
        boolean result = carTrajectoryService.deleteById(id);
        return result ? R.success() : R.fail("删除失败");
    }

    @Operation(summary = "批量删除小车轨迹")
    @DeleteMapping("/delete/batch")
    public R<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        boolean result = carTrajectoryService.deleteBatch(ids);
        return result ? R.success() : R.fail("删除失败");
    }

    @Operation(summary = "清空所有小车轨迹数据")
    @DeleteMapping("/delete/all")
    public R<Boolean> deleteAll() {
        carTrajectoryService.deleteAll();
        return R.success("清空成功");
    }
}
