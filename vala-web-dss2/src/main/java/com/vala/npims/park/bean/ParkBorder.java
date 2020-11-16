package com.vala.npims.park.bean;

import com.vala.base.entity.FileColumn;
import com.vala.framework.file.entity.ImageEntity;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;

@Entity
@Data
@ToString(callSuper = true)
public class ParkBorder extends ImageEntity {

    public Integer parkId;

    public Integer polygonStyleId;

    @FileColumn // 路径
    public String path;

    @FileColumn(type="txt")  // 主页
    public String home;
}
