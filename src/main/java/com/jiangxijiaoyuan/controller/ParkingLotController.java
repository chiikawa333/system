package com.jiangxijiaoyuan.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import com.jiangxijiaoyuan.entity.ParkingLot;
import com.jiangxijiaoyuan.responce.R;
import com.jiangxijiaoyuan.service.ParkingLotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "停车场信息管理")
@RestController
@RequestMapping("/parking")
@CrossOrigin
public class ParkingLotController {

    @Resource
    private ParkingLotService parkingLotService;
    
    @Operation(summary = "获取附近停车场列表")
    @GetMapping("/list/nearby")
    @CrossOrigin
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public R<PageInfo<ParkingLot>> getNearbyParkingLots(
            @Parameter(description = "纬度", example = "39.90872", in = ParameterIn.QUERY)
            @RequestParam(required = false, defaultValue = "39.90872") BigDecimal latitude,

            @Parameter(description = "经度", example = "116.46953", in = ParameterIn.QUERY)
            @RequestParam(required = false, defaultValue = "116.46953") BigDecimal longitude,

            @Parameter(description = "搜索半径（公里）", example = "5.0", in = ParameterIn.QUERY)
            @RequestParam(required = false, defaultValue = "5.0") Double radius,

            @Parameter(description = "页码", example = "1", in = ParameterIn.QUERY)
            @RequestParam(defaultValue = "1") Integer pageNum,

            @Parameter(description = "每页数量", example = "10", in = ParameterIn.QUERY)
            @RequestParam(defaultValue = "10") Integer pageSize) {

        PageInfo<ParkingLot> pageInfo = parkingLotService.getNearbyParkingLots(
                latitude, longitude, radius, pageNum, pageSize);
        return R.data(pageInfo);
    }

    @Operation(summary = "搜索停车场")
    @GetMapping("/search")
    @CrossOrigin
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public R<PageInfo<ParkingLot>> searchParkingLots(
            @Parameter(description = "搜索关键词", example = "万达", in = ParameterIn.QUERY)
            @RequestParam(required = false) String keyword,

            @Parameter(description = "页码", example = "1", in = ParameterIn.QUERY)
            @RequestParam(defaultValue = "1") Integer pageNum,

            @Parameter(description = "每页数量", example = "10", in = ParameterIn.QUERY)
            @RequestParam(defaultValue = "10") Integer pageSize) {

        PageInfo<ParkingLot> pageInfo = parkingLotService.searchParkingLots(keyword, pageNum, pageSize);
        return R.data(pageInfo);
    }

    @Operation(summary = "获取停车场详情")
    @GetMapping("/detail/{id}")
    @CrossOrigin
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "停车场不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public R<ParkingLot> getParkingLotDetail(
            @Parameter(description = "停车场 ID", example = "1", in = ParameterIn.PATH)
            @PathVariable Long id) {

        ParkingLot parkingLot = parkingLotService.getById(id);
        if (parkingLot == null) {
            return R.fail("停车场不存在");
        }
        return R.data(parkingLot);
    }

    @Operation(summary = "初始化模拟数据（仅用于测试）")
    @PostMapping("/init/mock")
    @CrossOrigin
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "初始化成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public R<Map<String, Object>> initMockData() {
        parkingLotService.initMockData();

        Map<String, Object> result = new HashMap<>();
        result.put("message", "模拟数据初始化成功");
        result.put("count", parkingLotService.count());

        return R.data(result);
    }

    @Operation(summary = "获取所有停车场（管理员专用）")
    @PostMapping("/admin/list")
    @CrossOrigin
    @SaCheckLogin
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public R<PageInfo<ParkingLot>> getAllParkingLots(
            @RequestBody(required = false) ParkingLot parkingLot,
            @Parameter(description = "页码", example = "1", in = ParameterIn.QUERY)
            @RequestParam(defaultValue = "1") Integer pageNum,

            @Parameter(description = "每页数量", example = "10", in = ParameterIn.QUERY)
            @RequestParam(defaultValue = "10") Integer pageSize) {

        PageInfo<ParkingLot> pageInfo = parkingLotService.searchParkingLots("", pageNum, pageSize);
        return R.data(pageInfo);
    }

    @Operation(summary = "停车场信息列表")
    @GetMapping("/list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public R<PageInfo<ParkingLot>> getParkingLots(
            @Parameter(description = "搜索关键词", in = ParameterIn.QUERY)
            @RequestParam(required = false) String keyword,
            
            @Parameter(description = "状态", in = ParameterIn.QUERY)
            @RequestParam(required = false) Integer status,
            
            @Parameter(description = "页码", example = "1", in = ParameterIn.QUERY)
            @RequestParam(defaultValue = "1") Integer pageNum,
            
            @Parameter(description = "每页数量", example = "10", in = ParameterIn.QUERY)
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        PageInfo<ParkingLot> pageInfo = parkingLotService.getParkingLots(
                keyword, status, pageNum, pageSize);
        return R.data(pageInfo);
    }

    @Operation(summary = "创建停车场")
    @PostMapping("/add")
    @SaCheckLogin
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "创建成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public R<Boolean> add(@RequestBody ParkingLot parkingLot) {
        boolean result = parkingLotService.save(parkingLot);
        return result ? R.success() : R.fail();
    }

    @Operation(summary = "更新停车场")
    @PostMapping("/update")
    @SaCheckLogin
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public R<Boolean> update(@RequestBody ParkingLot parkingLot) {
        boolean result = parkingLotService.updateById(parkingLot);
        return result ? R.success() : R.fail();
    }

    @Operation(summary = "删除停车场")
    @DeleteMapping("/delete/{id}")
    @SaCheckLogin
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public R<Boolean> delete(@PathVariable Long id) {
        boolean result = parkingLotService.removeById(id);
        return result ? R.success() : R.fail();
    }
}

