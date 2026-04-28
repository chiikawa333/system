package com.intelligent.driver.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class MaintRecordDTO {

    private Long id;

    private String appointmentNo;

    private Integer maintenanceType;

    private String city;

    private Long serviceStationId;

    private String serviceStationName;

    private LocalDate appointmentDate;

    private LocalTime appointmentTime;

    private Long vehicleId;

    private String licensePlate;

    private String contactName;

    private String contactPhone;

    private String repairPlan;

    private Integer status;

    private Integer paymentStatus;

    private BigDecimal paymentAmount;

    private LocalTime paymentTime;

    private LocalTime actualRepairTime;

    private LocalDateTime completionTime;

    private Long userId;
}
