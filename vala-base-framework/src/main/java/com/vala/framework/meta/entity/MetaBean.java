package com.vala.framework.meta.entity;

import com.vala.base.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Entity;

@Data
@Entity
public class MetaBean extends BaseEntity {
    public String prop;
    public String label;
    public Integer width;



    public Boolean sortable;
    public Boolean hideOnList;

    public Boolean readOnly;
    public Boolean hideOnForm;


    public String entity;
    public String type;


    public String tool;

    public String toolParam;
    public String toolParamKey;

    public Boolean upload;
    public Boolean download;


}
