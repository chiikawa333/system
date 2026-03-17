//package com.jiangxijiaoyuan.controller;
//
//import cn.dev33.satoken.annotation.SaCheckLogin;
//import cn.dev33.satoken.stp.StpUtil;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.github.pagehelper.PageHelper;
//import com.github.pagehelper.PageInfo;
//import com.jiangxijiaoyuan.entity.Admin;
//import com.jiangxijiaoyuan.exception.BussinessException;
//import com.jiangxijiaoyuan.responce.R;
//import com.jiangxijiaoyuan.responce.ResponseCode;
//import com.jiangxijiaoyuan.service.AdminService;
//import com.jiangxijiaoyuan.util.CaptchCache;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.annotation.Resource;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//@Tag(name = "管理员信息管理")
//@RestController
//public class AdminController {
//    @Resource
//    private AdminService adminService;
//
//    @Resource
//    private CaptchCache captchCache;
//
//    @Operation(summary = "新增管理员")
//    @PostMapping("/admin/add")
//    @CrossOrigin
//    public R add(@RequestBody Admin admin){
//        LambdaQueryWrapper<Admin> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.eq(Admin::getUsername,admin.getUsername());
//        long count = adminService.count(lambdaQueryWrapper);
//        if (count > 0){
//            throw new BussinessException(ResponseCode.USERNAME_EXIST);
//        }
//
//
//        adminService.save(admin);
//        return R.success();
//    }
//
//    @Operation(summary = "管理员信息列表")
//    @PostMapping("admin/list")
//    @CrossOrigin
//    @SaCheckLogin
//    public R<PageInfo<Admin>> list(@RequestBody Admin admin,@RequestParam Integer pageNum,@RequestParam Integer pageSize){
//        LambdaQueryWrapper<Admin> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.like(admin.getName()!=null,Admin::getName,admin.getName());
//        lambdaQueryWrapper.like(admin.getTel()!=null,Admin::getTel,admin.getTel());
//        lambdaQueryWrapper.orderByDesc(Admin::getId);
//
//        PageHelper.startPage(pageNum,pageSize);
//        List<Admin> list = adminService.list(lambdaQueryWrapper);
//        PageInfo<Admin> pageInfo = new PageInfo(list);
//        return R.data(pageInfo);
//    }
//
//    @Operation(summary = "修改管理员")
//    @PostMapping("/admin/update")
//    @CrossOrigin
//    @SaCheckLogin
//    public R update(@RequestBody Admin admin){
//        adminService.updateById(admin);
//        return R.success();
//    }
//
//    @Operation(summary = "删除管理员")
//    @PostMapping("/admin/del")
//    @CrossOrigin
//    public R del(@RequestParam List<Long> ids){
//        adminService.removeByIds(ids);
//        return R.success();
//    }
//
//    @Operation(summary = "管理员登录")
//    @PostMapping("/admin/login")
//    @CrossOrigin
//    public R<Admin> login(@RequestBody Admin admin){
//
//        if(StringUtils.isBlank(admin.getCaptchaId()) || StringUtils.isBlank(admin.getCaptchaCode())){
//            throw new BussinessException(ResponseCode.CAPTCHA_ERROR);
//        }
//        boolean flag = captchCache.validateCaptcha(admin.getCaptchaId(),admin.getCaptchaCode());
//        if(!flag){
//            throw new BussinessException(ResponseCode.CAPTCHA_ERROR);
//        }
//
//        LambdaQueryWrapper<Admin> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.eq(Admin::getUsername,admin.getUsername());
//        lambdaQueryWrapper.eq(Admin::getUserpwd,admin.getUserpwd());
//        Admin admin1 = adminService.getOne(lambdaQueryWrapper);
//
//        if (admin1 == null){
//            throw new BussinessException(ResponseCode.USERNAME_USERPED_ERROR);
//        }
//
//        StpUtil.login(admin1.getId());
//        admin1.setToken(StpUtil.getTokenValue());
//
//        return R.data(admin1);
//    }
//
//
//    @CrossOrigin
//    @PostMapping("/admin/loginout")
//    public R logout(){
//        StpUtil.logout();
//        return R.success();
//    }
//}

package com.jiangxijiaoyuan.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import com.jiangxijiaoyuan.entity.Admin;
import com.jiangxijiaoyuan.exception.BussinessException;
import com.jiangxijiaoyuan.responce.R;
import com.jiangxijiaoyuan.responce.ResponseCode;
import com.jiangxijiaoyuan.service.AdminService;
import com.jiangxijiaoyuan.util.CaptchCache;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Tag(name = "管理员信息管理")
@RestController
public class AdminController {
    @Resource
    private AdminService adminService;

    @Resource
    private CaptchCache captchCache;

    @Operation(summary = "新增管理员")
    @PostMapping("/admin/add")
    @CrossOrigin
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "操作成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public R add(@RequestBody Admin admin){
        LambdaQueryWrapper<Admin> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Admin::getUsername,admin.getUsername());
        long count = adminService.count(lambdaQueryWrapper);
        if (count > 0){
            throw new BussinessException(ResponseCode.USERNAME_EXIST);
        }


        adminService.save(admin);
        return R.success();
    }

    @Operation(summary = "管理员信息列表")
    @PostMapping("admin/list")
    @CrossOrigin
    @SaCheckLogin
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public R<PageInfo<Admin>> list(@RequestBody Admin admin,@Parameter(description = "页码", in = ParameterIn.QUERY) @RequestParam Integer pageNum,@Parameter(description = "每页数量", in = ParameterIn.QUERY) @RequestParam Integer pageSize){
        LambdaQueryWrapper<Admin> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(admin.getName()!=null,Admin::getName,admin.getName());
        lambdaQueryWrapper.like(admin.getTel()!=null,Admin::getTel,admin.getTel());
        lambdaQueryWrapper.orderByDesc(Admin::getId);

        PageHelper.startPage(pageNum,pageSize);
        List<Admin> list = adminService.list(lambdaQueryWrapper);
        PageInfo<Admin> pageInfo = new PageInfo(list);
        return R.data(pageInfo);
    }

    @Operation(summary = "修改管理员")
    @PostMapping("/admin/update")
    @CrossOrigin
    @SaCheckLogin
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "修改成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public R update(@RequestBody Admin admin){
        adminService.updateById(admin);
        return R.success();
    }

//    @Operation(summary = "删除管理员")
//    @PostMapping("/admin/del")
//    @CrossOrigin
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "删除成功"),
//            @ApiResponse(responseCode = "500", description = "服务器内部错误")
//    })
//    public R del(@Parameter(description = "ID 列表") @RequestParam List<Long> ids){
//        adminService.removeByIds(ids);
//        return R.success();
//    }

    @Operation(summary = "删除管理员")
    @PostMapping("/admin/del")
    @CrossOrigin
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public R del(@Parameter(description = "ID 列表") @RequestParam List<Long> ids){
        adminService.removeByIds(ids);

        // 可选：如果删除所有数据，可以重置自增 ID
         long remainingCount = adminService.count();
         if (remainingCount == 0) {
             adminService.resetAutoIncrement();
         }

        return R.success();
    }


    @Operation(summary = "管理员登录")
    @PostMapping("/admin/login")
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
    @PostMapping("/admin/loginout")
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



