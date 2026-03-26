package com.intelligent.driver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.intelligent.driver.entity.CarTrajectory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CarTrajectoryMapper extends BaseMapper<CarTrajectory> {

    @Update("ALTER TABLE car_detail AUTO_INCREMENT = 1")
    void resetAutoIncrement();

    List<CarTrajectory> selectRunningCars(@Param("status") Integer status);
}
