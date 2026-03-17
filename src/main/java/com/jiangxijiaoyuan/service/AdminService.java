//package com.jiangxijiaoyuan.service;
//
//import com.baomidou.mybatisplus.extension.service.IService;
//import com.jiangxijiaoyuan.entity.Admin;
//
//public interface AdminService extends IService<Admin> {
//}
package com.jiangxijiaoyuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiangxijiaoyuan.entity.Admin;

public interface AdminService extends IService<Admin> {

    void resetAutoIncrement();
}
