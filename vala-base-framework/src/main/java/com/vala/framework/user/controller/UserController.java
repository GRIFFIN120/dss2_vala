package com.vala.framework.user.controller;

import com.vala.base.controller.BaseController;
import com.vala.commons.bean.ResponseResult;
import com.vala.framework.user.entity.UserBasic;
import com.vala.framework.utils.JwtUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController<UserBasic> {

    @RequestMapping("/login")
    public ResponseResult<UserBasic> login(@RequestBody UserBasic user) throws Exception {
        UserBasic userBasic = this.baseService.get(user);
        if(userBasic!=null){
            String token = JwtUtils.sign(user.getUsername(),user.getId());
            userBasic.setToken(token);
            System.out.println(userBasic.getId());
            this.setSession("UID",userBasic.getId());
            System.out.println(this.getSession("UID"));
            return new ResponseResult("登录成功", userBasic);
        }else {
            return new ResponseResult(500,"用户不存在或密码不正确");
        }
    }

    @RequestMapping("/logout")
    public ResponseResult login() throws Exception {
        return new ResponseResult(200,"已登出系统，欢迎再次使用");
    }
    @RequestMapping("/changePassword")
    public ResponseResult changePassword(@RequestBody Map<String, Object> data) throws Exception {
        Integer id = (Integer) data.get("id");
        String former = (String) data.get("former");
        String current = (String) data.get("current");
        System.out.println(id+"  "+former);
        UserBasic user = new UserBasic();
        user.setId(id);
        user.setPassword(former);
        UserBasic userBasic = this.baseService.get(user);
        if(userBasic!=null){
            userBasic.setPassword(current);
            this.baseService.saveOrUpdate(userBasic);
            return new ResponseResult(200,"密码修改成功！");
        }else{
            return new ResponseResult(500,"密码错误！");
        }
    }


    @RequestMapping("/signup")
    public ResponseResult signup(@RequestBody UserBasic user) throws Exception {
        UserBasic temp = new UserBasic();
        String username = user.getUsername();
        temp.setUsername(username);


        UserBasic userBasic = this.baseService.get(temp);
        if(userBasic!=null){
            return new ResponseResult(500,"邮箱"+username+"已经被注册，请使用其他邮箱注册。");
        }else{
            this.baseService.saveOrUpdate(user);
            return new ResponseResult(200,"注册成功!");
        }
    }


//    @Override
//    public UserBasic afterGet(UserBasic bean) {
//        bean.setPassword(null);
//        return bean;
//    }
}
