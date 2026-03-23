package com.jiangxijiaoyuan.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class FeeConfigDTO {
    
    private BigDecimal pricePerHour;
    
    private BigDecimal minFee;
    
    private BigDecimal maxFee;
    
    private Integer freeMinutes;
}
