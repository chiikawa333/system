package com.jiangxijiaoyuan.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CarData {
    
    private String carId;
    
    private String plateNumber;
    
    private Integer vehicleType;
    
    private String xCoordinate;
    
    private String yCoordinate;
    
    private String interval;
    
    private BigDecimal speed;
    
    private Integer status;
    
    private Integer seconds;
}
