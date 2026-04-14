package com.intelligent.driver.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class MaintRecordDTO {

    private Long id;

    private String appointmentNo;

    private Integer maintenanceType;

    private String city;

    private Long serviceStationId;

    private String serviceStationName;

    private String appointmentDate;

    private String appointmentTime;

    private Long vehicleId;

    private String licensePlate;

    private String contactName;

    private String contactPhone;

    private String repairPlan;

    private Integer status;

    private Integer paymentStatus;

    private BigDecimal paymentAmount;

    private String paymentTime;

    private String actualRepairTime;

    private String completionTime;

    private Long userId;
}
