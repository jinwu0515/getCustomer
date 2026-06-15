package com.huoke.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("ai_follow_up_scripts")
public class AiFollowUpScript {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("lead_id")
    private Long leadId;

    @TableField("service_name")
    private String serviceName;

    private String content;

    private String model;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
