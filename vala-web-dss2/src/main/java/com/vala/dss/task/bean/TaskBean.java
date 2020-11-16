package com.vala.dss.task.bean;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vala.base.convertor.EntityListSerializer;
import com.vala.base.entity.BaseEntity;
import com.vala.framework.data.bean.DataFrameBean;
import com.vala.framework.file.entity.FileEntity;
import com.vala.framework.user.entity.UserBasic;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@ToString(callSuper = true)
public class TaskBean extends FileEntity {

    private String state;

    public Integer scaleId;

    // 创建者ID (用于查询)
    private Integer uid;
    // 创建者（用于展示——访问控制，避免展示所有用户）
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="user_id")
    private UserBasic user;

    private Integer viewId;

    // 用户列表
    @Fetch(FetchMode.SUBSELECT)
    @JsonSerialize(using = EntityListSerializer.ListSerializer.class)
    @JsonDeserialize(using = EntityListSerializer.ListDeserializer.class)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="task_user",
            joinColumns={@JoinColumn(name="task_id")},
            inverseJoinColumns={@JoinColumn(name="user_id")}
    )
    public List<UserBasic> experts;

    @Transient
    public List<Integer> data;

}
