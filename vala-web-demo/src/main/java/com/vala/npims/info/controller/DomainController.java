package com.vala.npims.info.controller;

import com.vala.base.controller.BaseController;
import com.vala.npims.info.bean.DomainBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/domain")
public class DomainController extends BaseController<DomainBean> {
}
