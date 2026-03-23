package com.jiangxijiaoyuan.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("fee_config")
public class FeeConfig {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("parking_lot_id")
    private Long parkingLotId;

    @TableField("price_per_hour")
    private BigDecimal pricePerHour;

    @TableField("min_fee")
    private BigDecimal minFee;

    @TableField("max_fee")
    private BigDecimal maxFee;

    @TableField("free_minutes")
    private Integer freeMinutes;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
