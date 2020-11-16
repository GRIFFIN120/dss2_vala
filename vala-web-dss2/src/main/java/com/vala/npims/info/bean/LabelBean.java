package com.vala.npims.info.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vala.base.entity.BaseEntity;
import com.vala.npims.info.bean.ArticleBean;
import com.vala.npims.info.bean.ImageBean;
import com.vala.npims.info.bean.PanoramaBean;
import com.vala.npims.info.bean.VideoBean;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import java.util.List;

@Data
@Entity
@ToString(callSuper = true)
public class LabelBean extends BaseEntity {

    @JsonIgnore
    @ManyToMany(mappedBy = "labels", fetch = FetchType.LAZY)
    public List<ArticleBean> articles;

    @JsonIgnore
    @ManyToMany(mappedBy = "labels", fetch = FetchType.LAZY)
    public List<ImageBean> images;

    @JsonIgnore
    @ManyToMany(mappedBy = "labels", fetch = FetchType.LAZY)
    public List<VideoBean> videos;

    @JsonIgnore
    @ManyToMany(mappedBy = "labels", fetch = FetchType.LAZY)
    public List<PanoramaBean> panoramas;


}
