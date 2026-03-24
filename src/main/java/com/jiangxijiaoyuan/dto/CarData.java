package com.jiangxijiaoyuan.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CarData {
    
    private String carId;
    
    private String plateNumber;
    
    private Integer vehicleType;
    
    @JsonProperty("xCoordinate")
    private String xCoordinate;
    
    @JsonProperty("yCoordinate")
    private String yCoordinate;
    
    private String interval;
    
    private BigDecimal speed;
    
    private Integer status;
    
    private Integer seconds;
}
