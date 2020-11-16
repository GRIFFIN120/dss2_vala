package com.vala.dss.task.controller;

import com.vala.base.controller.BaseController;
import com.vala.commons.bean.ResponseResult;
import com.vala.dss.task.bean.TaskBean;
import com.vala.dss.task.bean.TaskDataBean;
import com.vala.framework.user.entity.UserBasic;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.List;

@RestController
@RequestMapping("/task-data")
public class TaskDataController extends BaseController<TaskDataBean> {


}
