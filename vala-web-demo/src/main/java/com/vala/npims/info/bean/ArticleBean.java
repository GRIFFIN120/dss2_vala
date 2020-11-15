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
public class ArticleBean extends FileEntity {
//    @ApiModelProperty(dataType = "List", example = "[1,2,3]")
//    @JsonBackReference
//    @OneToMany(mappedBy="article", cascade = {CascadeType.REMOVE},fetch = FetchType.LAZY)
//    public List<ArticleImageBean> images = new ArrayList<>();



    @JsonSerialize(using = EntityListSerializer.ListSerializer.class)
    @JsonDeserialize(using = EntityListSerializer.ListDeserializer.class)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="article_domain",
            joinColumns={@JoinColumn(name="article_id")},inverseJoinColumns=
            {@JoinColumn(name="domain_id")}
    )
    public List<DomainBean> domains;



//    @JsonSerialize(using = EntitySetSerializer.SetSerializer.class)
    @JsonDeserialize(using = EntitySetSerializer.SetDeserializer.class)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="article_label",
            joinColumns={@JoinColumn(name="article_id")},inverseJoinColumns=
            {@JoinColumn(name="label_id")}
    )
    public Set<LabelBean> labels;


}
