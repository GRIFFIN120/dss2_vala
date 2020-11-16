package com.vala.npims.info.controller;

import com.vala.framework.file.controller.FileBaseController;
import com.vala.npims.info.bean.PanoramaBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/panorama")
public class PanoramaController extends FileBaseController<PanoramaBean> {
}
