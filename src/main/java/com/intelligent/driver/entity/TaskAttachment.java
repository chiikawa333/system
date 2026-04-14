package com.intelligent.driver.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("task_attachments")
public class TaskAttachment {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer taskId;

    private Integer senderId;

    private String senderName;

    private String senderRole;

    private String messageType;

    private String messageContent;

    private String fileName;

    private String filePath;

    private Long fileSize;

    private LocalDateTime createTime;


}
