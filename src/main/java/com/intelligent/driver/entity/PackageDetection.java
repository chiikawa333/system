package com.intelligent.driver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@TableName("package_detection")
public class PackageDetection {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("status")
    @JsonProperty("status")
    private String status;

    @TableField("damage_types")
    @JsonProperty("damage_types")
    private String damageTypes;

    @TableField("timestamp")
    @JsonProperty("timestamp")
    private String timestamp;
}
