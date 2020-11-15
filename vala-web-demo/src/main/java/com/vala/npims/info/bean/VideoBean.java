package com.vala.npims.info.bean;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vala.base.convertor.EntityListSerializer;
import com.vala.base.convertor.EntitySetSerializer;
import com.vala.framework.file.entity.FileEntity;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Data
@ToString(callSuper = true)
public class VideoBean extends FileEntity {

    @JsonSerialize(using = EntityListSerializer.ListSerializer.class)
    @JsonDeserialize(using = EntityListSerializer.ListDeserializer.class)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="video_domain",
            joinColumns={@JoinColumn(name="video_id")},inverseJoinColumns=
            {@JoinColumn(name="domain_id")}
    )
    public List<DomainBean> domains;



//    @JsonSerialize(using = EntitySetSerializer.SetSerializer.class)
    @JsonDeserialize(using = EntitySetSerializer.SetDeserializer.class)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="video_label",
            joinColumns={@JoinColumn(name="video_id")},inverseJoinColumns=
            {@JoinColumn(name="label_id")}
    )
    public Set<LabelBean> labels;

}
