package com.vala.npims.info.controller;

import com.vala.framework.file.controller.FileBaseController;
import com.vala.npims.info.bean.ImageBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/image")
public class ImageController extends FileBaseController<ImageBean> {
}
