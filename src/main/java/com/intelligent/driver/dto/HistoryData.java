package com.intelligent.driver.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class HistoryData {

    private String carId;

    private String xCoordinate;

    private String yCoordinate;
    
    private BigDecimal speed;
    
    private Integer seconds;
    
    private String interval;
}
