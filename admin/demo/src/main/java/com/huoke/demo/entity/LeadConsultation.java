package com.huoke.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("lead_consultations")
public class LeadConsultation {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("service_name")
    private String serviceName;

    @TableField("budget_range")
    private String budgetRange;

    private String urgency;

    @TableField("customer_type")
    private String customerType;

    @TableField("source_channel")
    private String sourceChannel;

    @TableField("intent_score")
    private Integer intentScore;

    @TableField("intent_level")
    private String intentLevel;

    @TableField("follow_status")
    private String followStatus;
}
