//package com.intelligent.driver.service.impl;
//
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import entity.com.intelligent.driver.Admin;
//import mapper.com.intelligent.driver.AdminMapper;
//import service.com.intelligent.driver.AdminService;
//import org.springframework.stereotype.Service;
//
//@Service
//public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {
//}


package com.intelligent.driver.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.intelligent.driver.entity.Admin;
import com.intelligent.driver.mapper.AdminMapper;
import com.intelligent.driver.service.AdminService;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Override
    public void resetAutoIncrement() {
        baseMapper.resetAutoIncrement();
    }
}
