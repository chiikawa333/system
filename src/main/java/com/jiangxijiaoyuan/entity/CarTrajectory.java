package com.jiangxijiaoyuan.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("car_detail")
public class CarTrajectory {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("car_id")
    private String carId;

    @TableField("plate_number")
    private String plateNumber;

    @TableField("vehicle_type")
    private Integer vehicleType;

    @TableField("entry_time")
    private LocalDateTime entryTime;

    @TableField("exit_time")
    private LocalDateTime exitTime;

    @TableField("x_coordinate")
    private String xCoordinate;

    @TableField("y_coordinate")
    private String yCoordinate;

    @TableField("`interval`")
    private String interval;

    @TableField("speed")
    private BigDecimal speed;

    @TableField("status")
    private Integer status;

    @TableField(value = "deleted", fill = FieldFill.INSERT)
    @TableLogic
    private Integer deleted;
}
