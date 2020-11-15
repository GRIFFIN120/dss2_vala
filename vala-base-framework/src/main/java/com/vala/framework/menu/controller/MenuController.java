package com.vala.framework.menu.controller;

import com.vala.base.bean.SearchBean;
import com.vala.base.controller.BaseController;
import com.vala.base.utils.TreeUtils;
import com.vala.commons.bean.ResponseResult;
import com.vala.framework.menu.entity.MenuItem;
import com.vala.framework.user.entity.RoleBasic;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/menu")
public class MenuController extends BaseController<MenuItem> {


    @Transactional
    @RequestMapping("/roleMenu/{roles}")
    public ResponseResult roleMenu(@PathVariable List<Integer> roles){

        Map<Integer, MenuItem> map = new LinkedHashMap<>();

        for (Integer role : roles) {
            RoleBasic roleBasic = this.baseService.get(RoleBasic.class, role);
            List<MenuItem> menus = roleBasic.getMenus();
            for (MenuItem menu : menus) {
                map.put(menu.getId(),menu);
            }
        }

        List<MenuItem> list = new ArrayList<>();
        for (Integer integer : map.keySet()) {
            MenuItem menuItem = map.get(integer);
            list.add(menuItem);
        }

        List<MenuItem> menuItems = TreeUtils.treeBean(list);


        return new ResponseResult(menuItems);
    }



}
