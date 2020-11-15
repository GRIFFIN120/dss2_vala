package com.vala.message.bean;

import com.vala.base.entity.BaseEntity;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;

@Entity
@Data
@ToString
public class MessageBean extends BaseEntity {

    public Integer fromId;
    public Integer toId;
    public String content;
    public Integer taskId;

}
