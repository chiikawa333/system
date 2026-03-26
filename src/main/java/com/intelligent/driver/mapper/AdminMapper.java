//package com.intelligent.driver.mapper;
//
//import com.baomidou.mybatisplus.core.mapper.BaseMapper;
//import entity.com.intelligent.driver.Admin;
//
///**
// *extends BaseMapper自实现CRUD
// * 泛型确定数据库
// **/
//
//public interface AdminMapper extends BaseMapper<Admin> {
//}

package com.intelligent.driver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.intelligent.driver.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AdminMapper extends BaseMapper<Admin> {

    @Update("ALTER TABLE admin AUTO_INCREMENT = 1")
    void resetAutoIncrement();
}

