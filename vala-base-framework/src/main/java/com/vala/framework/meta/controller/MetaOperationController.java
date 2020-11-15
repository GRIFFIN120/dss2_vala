package com.vala.framework.meta.controller;

import com.vala.base.controller.BaseController;
import com.vala.framework.meta.entity.MetaOperationBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/meta-operation")
public class MetaOperationController extends BaseController<MetaOperationBean> {



}
