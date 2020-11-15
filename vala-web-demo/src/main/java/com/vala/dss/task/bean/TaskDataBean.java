package com.vala.dss.task.bean;

import com.vala.base.entity.BaseEntity;
import com.vala.base.entity.TreeEntity;
import lombok.Data;

import javax.persistence.Entity;

@Entity
@Data
public class TaskDataBean extends BaseEntity {

    // public String domain

    private Integer scaleId;
    private Integer unitId;

    private Integer taskId;

    private Integer userId;


}
