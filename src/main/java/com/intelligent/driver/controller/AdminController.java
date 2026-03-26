package com.intelligent.driver.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import com.intelligent.driver.entity.Admin;
import com.intelligent.driver.exception.BussinessException;
import com.intelligent.driver.responce.R;
import com.intelligent.driver.responce.ResponseCode;
import com.intelligent.driver.service.AdminService;
import com.intelligent.driver.util.CaptchCache;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "管理员信息管理")
@RestController
@RequestMapping("/admin")
public class AdminController {
    @Resource
    private AdminService adminService;

    @Resource
    private CaptchCache captchCache;

    @Operation(summary = "新增管理员")
    @PostMapping("/add")
    @CrossOrigin
    @SaCheckLogin
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "操作成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public R add(@RequestBody Admin admin){
        // 参数校验
        if (admin == null || 
            admin.getUsername() == null || admin.getUsername().trim().isEmpty() ||
            admin.getUserpwd() == null || admin.getUserpwd().trim().isEmpty()) {
            throw new BussinessException(ResponseCode.ERROR);
        }
        
        // 检查用户名是否存在
        LambdaQueryWrapper<Admin> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Admin::getUsername, admin.getUsername().trim());
        long count = adminService.count(lambdaQueryWrapper);
        if (count > 0) {
            throw new BussinessException(ResponseCode.USERNAME_EXIST);
        }

        // 设置默认值
        admin.setUsername(admin.getUsername().trim());
        admin.setUserpwd(admin.getUserpwd().trim());
        
        adminService.save(admin);
        return R.success();
    }

    @Operation(summary = "管理员信息列表")
    @PostMapping("/list")
    @CrossOrigin
    @SaCheckLogin
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public R<PageInfo<Admin>> list(
            @RequestBody(required = false) Admin admin,
            @Parameter(description = "页码", in = ParameterIn.QUERY) 
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量", in = ParameterIn.QUERY) 
            @RequestParam(defaultValue = "10") Integer pageSize){
        
        LambdaQueryWrapper<Admin> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(admin != null && admin.getName() != null, Admin::getName, admin.getName());
        lambdaQueryWrapper.like(admin != null && admin.getTel() != null, Admin::getTel, admin.getTel());
        lambdaQueryWrapper.orderByDesc(Admin::getId);

        PageHelper.startPage(pageNum, pageSize);
        List<Admin> list = adminService.list(lambdaQueryWrapper);
        PageInfo<Admin> pageInfo = new PageInfo<>(list);
        return R.data(pageInfo);
    }

    @Operation(summary = "修改管理员")
    @PostMapping("/update")
    @CrossOrigin
    @SaCheckLogin
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "修改成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public R update(@RequestBody Admin admin){
        if (admin == null || admin.getId() == null) {
            throw new BussinessException(ResponseCode.ERROR);
        }
        
        // 如果要修改用户名，先检查是否重复
        if (admin.getUsername() != null && !admin.getUsername().trim().isEmpty()) {
            LambdaQueryWrapper<Admin> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Admin::getUsername, admin.getUsername().trim())
                            .ne(Admin::getId, admin.getId());
            long count = adminService.count(lambdaQueryWrapper);
            if (count > 0) {
                throw new BussinessException(ResponseCode.USERNAME_EXIST);
            }
            admin.setUsername(admin.getUsername().trim());
        }
        
        adminService.updateById(admin);
        return R.success();
    }

    @Operation(summary = "删除管理员")
    @PostMapping("/del")
    @CrossOrigin
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public R del(@Parameter(description = "ID 列表（逗号分隔）") @RequestParam String ids){
        // 将逗号分隔的字符串转换为 List<Long>
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(String::trim)
                .map(Long::valueOf)
                .collect(Collectors.toList());
        
        adminService.removeByIds(idList);

        // 可选：如果删除所有数据，可以重置自增 ID
        long remainingCount = adminService.count();
        if (remainingCount == 0) {
            adminService.resetAutoIncrement();
        }

        return R.success();
    }


    @Operation(summary = "管理员登录")
    @PostMapping("/login")
    @CrossOrigin
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "登录成功"),
            @ApiResponse(responseCode = "400", description = "验证码错误或账号密码错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public R<Admin> login(@RequestBody Admin admin){

        if(StringUtils.isBlank(admin.getCaptchaId()) || StringUtils.isBlank(admin.getCaptchaCode())){
            throw new BussinessException(ResponseCode.CAPTCHA_ERROR);
        }
        boolean flag = captchCache.validateCaptcha(admin.getCaptchaId(),admin.getCaptchaCode());
        if(!flag){
            throw new BussinessException(ResponseCode.CAPTCHA_ERROR);
        }

        LambdaQueryWrapper<Admin> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Admin::getUsername,admin.getUsername());
        lambdaQueryWrapper.eq(Admin::getUserpwd,admin.getUserpwd());
        Admin admin1 = adminService.getOne(lambdaQueryWrapper);

        if (admin1 == null){
            throw new BussinessException(ResponseCode.USERNAME_USERPED_ERROR);
        }

        StpUtil.login(admin1.getId());
        admin1.setToken(StpUtil.getTokenValue());

        return R.data(admin1);
    }


    @CrossOrigin
    @PostMapping("/loginout")
    @Operation(summary = "退出登录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "退出成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public R logout(){
        StpUtil.logout();
        return R.success();
    }
}



