package com.vala.npims.info.bean;

import com.vala.framework.file.entity.ImageEntity;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@ToString(callSuper = true)
public class ArticleImageBean extends ImageEntity {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="article_id")
    public ArticleBean article;

}
