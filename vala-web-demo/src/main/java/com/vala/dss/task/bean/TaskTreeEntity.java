package com.vala.dss.task.bean;

import com.vala.base.entity.TreeEntity;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;

@Entity
@Data
@ToString(callSuper = true)
public class TaskTreeEntity extends TreeEntity {

    // string nodeType
    public Integer userId; ///

    public Integer dataId;

    public Integer taskId;  ///

    public Double weight;

    public Double version;


}
