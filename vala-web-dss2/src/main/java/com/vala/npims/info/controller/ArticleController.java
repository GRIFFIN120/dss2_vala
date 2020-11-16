package com.vala.npims.info.controller;

import com.vala.framework.file.controller.FileBaseController;
import com.vala.npims.info.bean.ArticleBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/article")
public class ArticleController extends FileBaseController<ArticleBean> {
//    @Override
//    public ArticleBean afterGet(ArticleBean bean) {
//        System.out.println(bean.getImages().size());
//        for (ArticleImageBean image : bean.images) {
//            System.out.println(image.fileName);
//        }
//        return super.afterGet(bean);
//    }
}
