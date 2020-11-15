package com.vala.npims.info.controller;

import com.vala.base.controller.BaseController;
import com.vala.npims.info.bean.LabelBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/label")
public class LabelController extends BaseController<LabelBean> {
}
