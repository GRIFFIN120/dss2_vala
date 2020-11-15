package com.vala.dss.task.bean;

import com.vala.base.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Entity;

@Entity
@Data
public class TaskDataItemBean extends BaseEntity {

    private String category;
    private Float value;

    private Integer dataId;
}
