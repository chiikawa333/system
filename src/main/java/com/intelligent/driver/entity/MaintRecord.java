package com.intelligent.driver.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@TableName("maint_record")
public class MaintRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("appointment_no")
    private String appointmentNo;

    @TableField("maintenance_type")
    private Integer maintenanceType;

    @TableField("city")
    private String city;

    @TableField("service_station_id")
    private Long serviceStationId;

    @TableField("service_station_name")
    private String serviceStationName;

    @TableField("appointment_date")
    private LocalDate appointmentDate;

    @TableField("appointment_time")
    private LocalTime appointmentTime;

    @TableField("vehicle_id")
    private Long vehicleId;

    @TableField("license_plate")
    private String licensePlate;

    @TableField("contact_name")
    private String contactName;

    @TableField("contact_phone")
    private String contactPhone;

    @TableField("repair_plan")
    private String repairPlan;

    @TableField("status")
    private Integer status;

    @TableField("payment_status")
    private Integer paymentStatus;

    @TableField("payment_amount")
    private BigDecimal paymentAmount;

    @TableField("payment_time")
    private LocalTime paymentTime;

    @TableField("actual_repair_time")
    private LocalTime actualRepairTime;

    @TableField("completion_time")
    private LocalDateTime completionTime;

    @TableField("user_id")
    private Long userId;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    @TableField("deleted")
    private Integer deleted;
}
