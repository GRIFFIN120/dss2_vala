package com.vala.dss.task.controller;

import com.vala.base.bean.SearchBean;
import com.vala.base.bean.SearchResult;
import com.vala.base.controller.BaseController;
import com.vala.base.utils.TreeUtils;
import com.vala.commons.bean.ResponseResult;
import com.vala.dss.task.bean.TaskBean;
import com.vala.dss.task.bean.TaskDataBean;
import com.vala.dss.task.bean.TaskTreeEntity;
import com.vala.framework.data.bean.DataBean;
import com.vala.framework.data.bean.DataItemBean;
import com.vala.framework.user.entity.UserBasic;
import org.springframework.data.domain.Example;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.*;

@RestController
@RequestMapping("/task-tree")
public class TaskTreeController extends BaseController<TaskTreeEntity> {

    @RequestMapping("/getResult/{taskId}/{userId}/{nodeId}")
    public ResponseResult getResult(@PathVariable Integer taskId, @PathVariable Integer userId, @PathVariable Integer nodeId) throws Exception {
        TaskTreeEntity exact = new TaskTreeEntity();
        exact.setTaskId(taskId);
        exact.setUserId(userId);
        exact.setDomain("weight");
        SearchBean<TaskTreeEntity> searchBean = new SearchBean<>();
        searchBean.setExact(exact);
        SearchResult<TaskTreeEntity> search = this.baseService.search(searchBean);
        List<TaskTreeEntity> list = search.getList();
        for (TaskTreeEntity taskTreeEntity : list) {
            Double weight = taskTreeEntity.getWeight();
            if(weight==null) weight =0.0;
            taskTreeEntity.setWeight(weight/100);
        }

        // 从根结点开始构建树
        List<TaskTreeEntity> children = TreeUtils.treeBean(search.getList());
        TaskTreeEntity root = new TaskTreeEntity();
        root.setId(0);
        root.setWeight(1.0);
        root.setChildren(children);



        // 查找选中的节点
        TaskTreeEntity node = this.getNode(root, nodeId);

        // 以选中节点为根，向下赋权
        node.setWeight(1.0);
        this.weight(root);

        // 查找赋权后的数据节点（末端节点）
        List<TaskTreeEntity> data = new ArrayList<>();
        this.findData(node,data);

        // 获得加权求和的值
        Map<String, Double> map = this.getData(data);

        // 归一化
        Double max = 0.0;
        for (String s : map.keySet()) {
            Double val = map.get(s);
            max = val>max? val : max;
        }
        List<DataItemBean> results = new ArrayList<>();
        for (String s : map.keySet()) {
            Double val = map.get(s)/max;
            DataItemBean item = new DataItemBean();
            item.setCategory(s);
            item.setValue(val);
            results.add(item);
        }
        return new ResponseResult(results);

    }



    private Map<String,Double> getData(List<TaskTreeEntity> data){
        Map<String,Double> map = new LinkedHashMap<>();
        for (TaskTreeEntity datum : data) {
            Integer dataId = datum.getDataId();
            data(dataId,map);
        }
        return map;
    }

    private void data(Integer dataId,Map<String,Double> map){

        DataItemBean exact = new DataItemBean();
        exact.setDataId(dataId);
        List<DataItemBean> all = this.baseService.getRepo().findAll(Example.of(exact));
        for (DataItemBean item : all) {
            String cat = item.getCategory();
            Double val =item.getValue();
            val = val==null ? 0 : val;
            Double temp = map.get(cat);
            temp = temp==null? 0: temp;
            map.put(cat,(val+temp));
        }
    }




    private void findData(TaskTreeEntity node, List<TaskTreeEntity> data){
        if(node.dataId!=null){
            data.add(node);
            return;
        }
        List<TaskTreeEntity> children = node.getChildren();
        for (TaskTreeEntity child : children) {
            findData(child,data);
        }
    }


    public TaskTreeEntity getNode(TaskTreeEntity node,Integer id){
        if(id.equals(node.getId())) return node;
        List<TaskTreeEntity> children = node.getChildren();
        for (TaskTreeEntity child : children) {
            TaskTreeEntity find = getNode(child, id);
            if(find!=null){
                return find;
            }
        }
        return null;
    }

    public void weight(TaskTreeEntity node){
        Double weight = node.getWeight();
        List<TaskTreeEntity> children = node.getChildren();
        for (TaskTreeEntity child : children) {
            child.setWeight(child.getWeight()*weight);
            weight(child);
        }
    }


    @Override
    public String beforeInsert(TaskTreeEntity ext) {
        Object UID = this.getSession("UID");
        if(UID!=null){
            Integer uid = (Integer) UID;
            UserBasic userBasic = this.baseService.get(UserBasic.class, uid);
            ext.setUserId(uid);
            return super.beforeInsert(ext);
        }else{
            return "登录已失效，请重新登录";
        }

    }

}
