package com.intelligent.driver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@TableName("fatigue_detection")
public class FatigueDetection {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("timestamp")
    @JsonProperty("timestamp")
    private String timestamp;

    @TableField("status")
    @JsonProperty("status")
    private String status;

    @TableField("yawn_count")
    @JsonProperty("yawn_count")
    private String yawnCount;

    @TableField("yawn_frequency")
    @JsonProperty("yawn_frequency")
    private String yawnFrequency;

    @TableField("eye_close_duration")
    @JsonProperty("eye_close_duration")
    private String eyeCloseDuration;

    @TableField("damage_types")
    @JsonProperty("damage_types")
    private String damageTypes;
}
