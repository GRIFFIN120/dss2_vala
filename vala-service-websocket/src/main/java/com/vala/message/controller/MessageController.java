package com.vala.message.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vala.base.controller.BaseController;
import com.vala.base.example.ValaEntity;
import com.vala.base.service.BaseService;
import com.vala.commons.bean.ResponseResult;
import com.vala.message.bean.MessageBean;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WebSocketController
 * @author zhengkai.blog.csdn.net
 */
@RestController
@RequestMapping("/message")
public class MessageController extends BaseController<MessageBean>{




    @RequestMapping("/p2p/all")
    public ResponseResult p2pAll(@RequestBody MessageBean msg){
        System.out.println(msg);
        List<MessageBean> list = this.baseService.find(msg);
        int temp = msg.fromId;
        msg.setFromId(msg.toId);
        msg.setToId(temp);
        List<MessageBean> list1 = this.baseService.find(msg);
        list.addAll(list1);
        list.sort((o1, o2) -> new Integer((int) (o1.getTimestamp().getTime()-o2.getTimestamp().getTime())));
        return new ResponseResult(list);
    }

    @RequestMapping("/p2g/all")
    public ResponseResult p2gAll(@RequestBody MessageBean msg){
        msg.setDomain("group");
        List<MessageBean> list = this.baseService.find(msg);
        list.sort((o1, o2) -> new Integer((int) (o1.getTimestamp().getTime()-o2.getTimestamp().getTime())));
        return new ResponseResult(list);
    }

}
