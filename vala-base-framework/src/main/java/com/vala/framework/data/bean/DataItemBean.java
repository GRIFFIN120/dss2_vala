package com.vala.framework.data.bean;

import com.vala.base.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Entity;

@Entity
@Data
public class DataItemBean extends BaseEntity {

    private String category;
    private Double value;

    private Integer dataId;

}
