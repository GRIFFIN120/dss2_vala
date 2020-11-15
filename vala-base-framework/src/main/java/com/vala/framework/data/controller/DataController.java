package com.vala.framework.data.controller;

import com.vala.base.controller.BaseController;
import com.vala.framework.data.bean.DataBean;
import com.vala.framework.menu.entity.MenuItem;
import com.vala.framework.user.entity.UserBasic;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/data")
public class DataController extends BaseController<DataBean> {
    @Override
    public String beforeInsert(DataBean ext) {
        Object UID = this.getSession("UID");
        if(UID!=null){
            Integer uid = (Integer) UID;
            UserBasic userBasic = this.baseService.get(UserBasic.class, uid);
            ext.setUser(userBasic);
            ext.setUid(uid);
            return super.beforeInsert(ext);
        }else{
            return "用户未登录！";
        }

    }
}
