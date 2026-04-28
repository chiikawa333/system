package com.intelligent.driver.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.intelligent.driver.entity.SignalCar;
import com.intelligent.driver.responce.R;
import com.intelligent.driver.service.SignalCarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "信号车管理")
@RestController
@RequestMapping("/api/signal-car")
public class SignalCarController {

    @Autowired
    private SignalCarService signalCarService;

    @Operation(summary = "保存信号车记录")
    @PostMapping("/save")
    public R<Boolean> save(@RequestBody SignalCar signalCar) {
        boolean success = signalCarService.saveSignalCar(signalCar);
        return success ? R.data(true) : R.fail("保存失败");
    }

    @Operation(summary = "批量保存信号车记录")
    @PostMapping("/batch-save")
    public R<Boolean> batchSave(@RequestBody List<SignalCar> signalCarList) {
        boolean success = signalCarService.batchSaveSignalCar(signalCarList);
        return success ? R.data(true) : R.fail("批量保存失败");
    }

    @Operation(summary = "获取信号车记录列表")
    @GetMapping("/list")
    public R<Page<SignalCar>> getList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<SignalCar> page = signalCarService.getAllRecords(pageNum, pageSize);
        return R.data(page);
    }

    @Operation(summary = "根据车辆ID查询信号车记录")
    @GetMapping("/car/{carId}")
    public R<List<SignalCar>> getByCarId(@PathVariable String carId) {
        List<SignalCar> list = signalCarService.getByCarId(carId);
        return R.data(list);
    }

    @Operation(summary = "根据信号灯颜色查询信号车记录")
    @GetMapping("/color/{lightColor}")
    public R<List<SignalCar>> getByLightColor(@PathVariable String lightColor) {
        List<SignalCar> list = signalCarService.getByLightColor(lightColor);
        return R.data(list);
    }

    @Operation(summary = "删除信号车记录")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable String id) {
        boolean success = signalCarService.deleteById(id);
        return success ? R.data(true) : R.fail("删除失败");
    }

    @Operation(summary = "批量删除信号车记录")
    @DeleteMapping("/batch")
    public R<Boolean> deleteBatch(@RequestBody List<String> ids) {
        boolean success = signalCarService.deleteBatch(ids);
        return success ? R.data(true) : R.fail("批量删除失败");
    }

    @Operation(summary = "清空所有信号车记录")
    @DeleteMapping("/all")
    public R<Boolean> deleteAll() {
        signalCarService.deleteAll();
        return R.data(true);
    }
}
