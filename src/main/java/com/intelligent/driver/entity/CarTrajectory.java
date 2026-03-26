package com.intelligent.driver.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("xCoordinate")
    private String xCoordinate;

    @TableField("y_coordinate")
    @JsonProperty("yCoordinate")
    private String yCoordinate;

    @TableField("`interval`")
    private String interval;

    @TableField(exist = false)
    private Integer seconds;

    @TableField("speed")
    private BigDecimal speed;

    @TableField("status")
    private Integer status;

}
