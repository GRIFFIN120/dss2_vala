package com.vala.npims.park.bean;

import com.vala.base.entity.FileColumn;
import com.vala.framework.file.entity.ImageEntity;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;

@Entity
@Data
@ToString(callSuper = true)
public class Park extends ImageEntity {

    public Integer mapStyleId;

    public String parkType;

    @FileColumn(type="txt")
    public String home;

}
