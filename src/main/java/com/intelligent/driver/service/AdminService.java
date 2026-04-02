package com.intelligent.driver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.intelligent.driver.entity.Admin;

public interface AdminService extends IService<Admin> {

    void resetAutoIncrement();
}
