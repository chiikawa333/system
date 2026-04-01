package com.intelligent.driver.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("market_tasks")
public class MarketTask {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer publisherId;

    private String publisherName;

    private String title;

    private String content;

    private String targetRole;

    private String status;

    private LocalDateTime createTime;

    private LocalDateTime deadline;

    private Integer priority;
}
