package com.intelligent.driver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("signal_car")
public class SignalCar {

    @TableId(type = IdType.INPUT)
    private String id;

    @TableField("light_color")
    private String lightColor;

    @TableField("car_id")
    private String carId;

    @TableField("vehicle_status")
    private String vehicleStatus;

    @TableField("record_time")
    private String recordTime;
}
