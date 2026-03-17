package com.jiangxijiaoyuan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
@TableName("parking_lots")
@Schema(description = "停车场信息实体类")
public class ParkingLot {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键 ID")
    private Long id;

    @Schema(description = "停车场名称")
    private String name;

    @Schema(description = "详细地址")
    private String address;

    @Schema(description = "纬度")
    private BigDecimal latitude;

    @Schema(description = "经度")
    private BigDecimal longitude;

    @Schema(description = "总车位数")
    private Integer totalSpaces;

    @Schema(description = "剩余车位数")
    private Integer availableSpaces;

    @Schema(description = "每小时价格（元）")
    private BigDecimal pricePerHour;

    @Schema(description = "营业时间 - 开始")
    private LocalTime openingTime;

    @Schema(description = "营业时间 - 结束")
    private LocalTime closingTime;

    @Schema(description = "联系电话")
    private String contactPhone;

    @Schema(description = "设施（充电桩、无障碍等）")
    private String facilities;

    @Schema(description = "状态（0：停用，1：启用）")
    private Integer status;

    @Schema(description = "评分")
    private BigDecimal rating;

    @Schema(description = "图片 URL")
    private String imageUrl;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "创建时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createTime;

    @Schema(description = "更新时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updateTime;

}
