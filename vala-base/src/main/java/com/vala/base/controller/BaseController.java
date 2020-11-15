package com.vala.base.controller;

import com.vala.base.bean.SearchBean;
import com.vala.base.bean.SearchResult;
import com.vala.base.entity.BaseEntity;
import com.vala.base.entity.FileColumn;
import com.vala.base.entity.TreeEntity;
import com.vala.base.service.BaseService;
import com.vala.base.service.FastDfsService;
import com.vala.commons.bean.KV;
import com.vala.commons.bean.ResponseResult;
import com.vala.commons.util.BeanUtils;
import com.vala.commons.util.Constants;
import com.vala.base.utils.TreeUtils;
import org.apache.commons.collections.ArrayStack;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.ManyToOne;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class BaseController<T extends BaseEntity> extends BaseControllerWraper<T> implements ApplicationListener<ContextRefreshedEvent> {
    public Class<T> domain;
    @Autowired
    public BaseService<T> baseService;

    @Autowired
    public FastDfsService fastDfsService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        domain = (Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        baseService.setDomain(domain);
    }


    @RequestMapping("/input/{type}")
    public ResponseResult input(@PathVariable String type,@RequestPart(value = "file") MultipartFile file) {

        return new ResponseResult("上传成功");
    }

    @RequestMapping("/output/{type}")
    public ResponseResult output(@PathVariable String type,@RequestPart(value = "file") MultipartFile file) {
        return new ResponseResult();
    }



    @Transactional
    @ResponseBody
    @RequestMapping("/set/file/{id}/{field}")
    public ResponseResult dfsFileSet(@PathVariable Integer id, @PathVariable String field, @RequestPart(value = "file") MultipartFile file) throws Exception {
        if(file==null) return new ResponseResult(600,"文件不存在");
        Field f = this.domain.getDeclaredField(field);
        FileColumn annotation = f.getAnnotation(FileColumn.class);
        if(annotation!=null){
            String type = annotation.type();
            String fileName = file.getOriginalFilename();
            String[] strs = this.fastDfsService.getNameAndExtension(fileName);;
            String extension = strs[1];
            String name = strs[0];
            if(StringUtils.isNotEmpty(type)&&!extension.equalsIgnoreCase(type)) return new ResponseResult(600,field+"文件类型不匹配");

            T bean = id==-1? this.baseService.newInstance(): this.baseService.get(id);

            String path = (String) BeanUtils.get(bean, field);
            if(StringUtils.isNotEmpty(path)){
                fastDfsService.delete(path);
            }
            path = fastDfsService.upload(file.getBytes(), extension);
            BeanUtils.set(bean,field,path);

            this.baseService.saveOrUpdate(bean);
            return new ResponseResult(200);
        }else {
            return new ResponseResult(600,field+"不是文件字段");
        }
    }


    @Transactional
    @RequestMapping("/update/many/{field}/{id}")
    public ResponseResult distinct(@PathVariable Integer id, @PathVariable String field , @RequestBody List<BaseEntity> list) throws Exception {

        Field f = domain.getDeclaredField(field);
        if(f.getType().equals(List.class)){
            T bean = this.baseService.get(id);
            if(bean!=null){
                f.set(bean,list);
                this.baseService.saveOrUpdate(bean);
            }else{
                return new ResponseResult(500,"数据未找到!");
            }
        }else{
            return new ResponseResult(500,"字段类型错误!");
        }
        return new ResponseResult(200);
    }


    @RequestMapping("/distinct/{field}")
    public ResponseResult distinct(@PathVariable String field){

        String sql = "SELECT DISTINCT("+field+") FROM " + this.domain.getSimpleName();
        Query nativeQuery = this.baseService.getEntityManager().createQuery(sql);
        List<String> resultList = nativeQuery.getResultList();
        List<KV> list = new ArrayStack();
        for (String s : resultList) {
            KV k = new KV();
            k.setName(s);
            list.add(k);
        }
        return new ResponseResult(list);
    }

    @RequestMapping("/simple")
    public ResponseResult simple(@RequestBody(required=false) T data) throws Exception {

        SearchBean<T> params = new SearchBean<>();
        params.setExact(data);
        SearchResult<T> result = baseService.search(params);
        List<KV> list = new ArrayList<>();
        for (T t : result.getList()) {
            KV k = new KV();
            k.setId(t.getId());
            k.setName(t.getName());
            list.add(k);
        }
        return new ResponseResult(list);
    }

    @ResponseBody
    @RequestMapping("/fields")
    public ResponseResult fields() throws Exception {
        // 搜索前校验
        List<String> list = new ArrayList<>();
        List<Field> completeFields = BeanUtils.getCompleteFields(domain);
        for (Field completeField : completeFields) {
            list.add(completeField.getName());
        }
        return new ResponseResult(list);
    }




    @ResponseBody
    @RequestMapping("/search")
    public ResponseResult search(@RequestBody SearchBean<T> params) throws Exception {
        // 搜索前校验
        String message = this.beforeSearch(params);
        if(message!=null){ // 校验不通过
            return new ResponseResult(400, message);
        }else { // 校验通过
            SearchResult<T> result = baseService.search(params);
            // 更新后校验
            message = this.afterSearch(result);
            return message==null? new ResponseResult(result): new ResponseResult(400, message);
        }
    }

    @ResponseBody
    @RequestMapping("/search/tree")
    public ResponseResult tree(@RequestBody SearchBean<T> params) throws Exception {
        if(TreeEntity.class.isAssignableFrom(domain)){
            SearchBean searchBean = params;
            SearchResult<TreeEntity> result = baseService.search(searchBean);
            List<TreeEntity> results = result.getList();
            List<TreeEntity> treeEntities = TreeUtils.treeBean(results);
            return new ResponseResult(treeEntities);

        }else {
            return new ResponseResult(400, "数据结构不是树形");
        }



    }



    @Transactional
    @ResponseBody
    @RequestMapping("/insert")
    public ResponseResult<T> insert(@RequestBody T ext) throws Exception {
        // 更新前校验
        String message = this.beforeInsert(ext);
        if(message!=null){ // 校验不通过
            return new ResponseResult(400, message);
        }else{ // 校验通过
            T bean = baseService.saveOrUpdate(ext);
            // 更新后校验
            message = this.afterInsert(bean);
            return message==null? new ResponseResult("添加成功", bean): new ResponseResult(400, message);
        }
    }

    @ResponseBody
    @RequestMapping("/get/{id}")
    public ResponseResult<T> get(@PathVariable Integer id) throws Exception {
        T bean  = baseService.get(id);
        bean = afterGet(bean);
        return new ResponseResult<>(bean);
    }

    @Transactional
    @ResponseBody
    @RequestMapping("/get-sub/{id}/{field}")
    public ResponseResult get(@PathVariable Integer id, @PathVariable String field) throws Exception {
        T bean  = baseService.get(id);
        bean = afterGet(bean);
        Field f = this.domain.getField(field);
        Object o = f.get(bean);

        return new ResponseResult(o);
    }

    @ResponseBody
    @RequestMapping("/get/all/{list}")
    public ResponseResult<T> getAll(@PathVariable List<Integer> list) throws Exception {

        List<T> results = new ArrayList<>();
        for (Integer id : list) {
            T bean = this.baseService.get(id);
            results.add(bean);
        }
        return new ResponseResult(results);
    }




    @ResponseBody
    @Transactional
    @RequestMapping("/update")
    public ResponseResult<T> update(@RequestBody T ext) throws Exception {

        Integer id = ext.getId();
        if(id==null){
            return this.insert(ext);
        }else{
            T bean = baseService.get(id);

            if(bean==null)   return new ResponseResult<>(400, "数据不存在");
            // 更新前校验
            String message = beforeUpdate(ext, bean);
            if(message!=null){
                return new ResponseResult<>(400,message);
            }else {
                BeanUtils.extend(domain,bean,ext,ManyToOne.class);
                bean = this.baseService.saveOrUpdate(bean);
                // 更新后校验
                message =  this.afterUpdate(bean);
                return message==null? new ResponseResult("更新成功", bean): new ResponseResult(400, message);
            }
        }
    }

    @RequestMapping("/update/all")
    public ResponseResult updateAll(@RequestBody List<T> list){
        this.baseService.saveAll(list);
        return new ResponseResult(200,"批量更新成功");
    }





    @Transactional
    @ResponseBody
    @RequestMapping("/delete/{id}")
    public ResponseResult<T> delete(@PathVariable Integer id) throws Exception {
        T bean  = baseService.get(id);

//        try {
            this.fastDfsService.deleteBeanFile(bean);
            if(bean==null)   return new ResponseResult<>(400, "数据不存在");
            // 删除前校验
            String message = this.beforeDelete(bean);
            if(message!=null){
                return new ResponseResult<>(400,message);
            }else {
                baseService.delete(id);
                message = this.afterDelete(bean);
                return new ResponseResult<T>(message);
            }
//        } catch (Exception e) {
//            return new ResponseResult<T>(400,e.getMessage());
//        }
    }


    @Transactional
    @ResponseBody
    @RequestMapping("/deleteAll/{ids}")
    public ResponseResult<T> delete(@PathVariable List<Integer> ids) throws Exception {
        int count = 0;
        for (Integer id : ids) {
            if(id==0) continue;

            T bean  = baseService.get(id);
            this.fastDfsService.deleteBeanFile(bean);
            boolean flag = this.baseService.delete(bean);
            if(flag) count++;
        }
        return new ResponseResult<T>(String.format("成功删除 %s 条数据",count));
    }


    @Transactional
    @ResponseBody
    @RequestMapping("/order/{id}/{direction}")
    public ResponseResult<T> order(@PathVariable Integer id, @PathVariable String direction, @RequestBody T ext) throws Exception {

        String conditions = BeanUtils.bean2conditon(ext);
        Integer sid = this.baseService.getSibling(id,direction,conditions);
        if(sid==null){
            String message = null;
            int code = 400;
            if(Constants.SORT_UP.equalsIgnoreCase(direction)){
                message = "已经到达顶端";
            }else if(Constants.SORT_DOWN.equalsIgnoreCase(direction)){
                message = "已经到达底端";
            }else{
                message = "未知的排序参数:" + direction;
            }
            return new ResponseResult<T>(code,message);
        }else{
            this.baseService.order(id,sid);
            return new ResponseResult<T>(String.format("移动成功"));
        }


    }

    /* direction = before / after */
    @Transactional
    @ResponseBody
    @RequestMapping("/place/{draggingId}/{dropId}/{direction}")
    public ResponseResult<T> place(@PathVariable Integer draggingId, @PathVariable Integer dropId, @PathVariable String direction) throws Exception {
        if(TreeEntity.class.isAssignableFrom(domain)){
            T dp = this.baseService.get(dropId);     // 静
            T dg = this.baseService.get(draggingId); // 动

            TreeEntity drop = (TreeEntity) dp;  // 静
            TreeEntity dragging = (TreeEntity) dg;  // 动


            SearchBean<T> search = new SearchBean<>();
            TreeEntity exact = (TreeEntity) this.baseService.newInstance();
            exact.setPid(drop.getPid());    // 查找  drop（静） 的父节点下的所有节点
            search.setExact((T) exact);
            SearchResult<T> res = this.baseService.search(search);
            List<T> list = res.getList();

            List<Date> date = new ArrayList<>();

            int dpIndex = -1 ;
            int dgIndex = -1 ;


            for (int i = 0; i < list.size(); i++) {
                T t =  list.get(i);
                date.add(t.getTimestamp());
                dpIndex = (t.getId().equals(drop.id) && dpIndex==-1) ? i : dpIndex;
                dgIndex = (t.getId().equals(dragging.id) && dgIndex==-1) ? i : dgIndex;
            }






            if(dgIndex!=-1){
                list.remove(dgIndex);
                for (int i = 0; i < list.size(); i++) {
                    T t =  list.get(i);
                    if(t.id.equals(dp.getId())){
                        dpIndex = i;
                        break;
                    }
                }
            }else {
                date.add(dg.getTimestamp());
            }





            if("before".equals(direction)){
                list.add(dpIndex,dg);
            }else {
                list.add(dpIndex+1,dg);
            }




            date.sort(Comparator.comparingLong(Date::getTime).reversed());
            dragging.setPid(drop.getPid());
            for (int i = 0; i < date.size(); i++) {
                Date date1 =  date.get(i);
                T t = list.get(i);
                t.setTimestamp(date1);
                this.baseService.saveOrUpdate(t);
            }




            return new ResponseResult<T>("移动成功",dp);
        }else {
            return new ResponseResult(400, "数据结构不是树形");
        }






    }


    public void setSession(String key, Object value){
        this.session().setAttribute(key,value);
    }
    public Object getSession(String key){
        return this.session().getAttribute(key);
    }

    private HttpSession session(){
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(null != requestAttributes) {
            HttpServletResponse response = requestAttributes.getResponse();
            HttpServletRequest request = requestAttributes.getRequest();
            HttpSession session = request.getSession();
            return session;
        }
        return null;
    }

}
