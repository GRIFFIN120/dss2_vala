package com.vala.dss.task.controller;

import com.vala.base.controller.BaseController;
import com.vala.dss.task.bean.TaskBean;
import com.vala.dss.task.bean.TaskDataItemBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/task-data-item")
public class TaskDataItemController extends BaseController<TaskDataItemBean> {


}
