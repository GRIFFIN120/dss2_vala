package com.vala.npims.info.controller;

import com.vala.framework.file.controller.FileBaseController;
import com.vala.npims.info.bean.VideoBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/video")
public class VideoController extends FileBaseController<VideoBean> {
}
