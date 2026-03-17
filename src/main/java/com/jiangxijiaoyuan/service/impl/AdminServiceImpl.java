//package com.jiangxijiaoyuan.service.impl;
//
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.jiangxijiaoyuan.entity.Admin;
//import com.jiangxijiaoyuan.mapper.AdminMapper;
//import com.jiangxijiaoyuan.service.AdminService;
//import org.springframework.stereotype.Service;
//
//@Service
//public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {
//}


package com.jiangxijiaoyuan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiangxijiaoyuan.entity.Admin;
import com.jiangxijiaoyuan.mapper.AdminMapper;
import com.jiangxijiaoyuan.service.AdminService;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Override
    public void resetAutoIncrement() {
        baseMapper.resetAutoIncrement();
    }
}
