package com.vala.npims.info.controller;

import com.vala.framework.file.controller.FileBaseController;
import com.vala.npims.info.bean.ArticleImageBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/article-image")
public class ArticleImageController extends FileBaseController<ArticleImageBean> {
}
