package com.vala.dss.task.controller;

import com.vala.base.controller.BaseController;
import com.vala.base.entity.TreeEntity;
import com.vala.base.utils.TreeUtils;
import com.vala.commons.bean.ResponseResult;
import com.vala.commons.util.BeanUtils;
import com.vala.commons.util.Constants;
import com.vala.dss.task.bean.TaskBean;
import com.vala.dss.task.bean.TaskTreeEntity;
import com.vala.framework.data.bean.DataFrameBean;
import com.vala.framework.file.controller.FileBaseController;
import com.vala.framework.user.entity.UserBasic;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/task")
public class TaskController extends FileBaseController<TaskBean> {

    @RequestMapping("/userTasks")
    public ResponseResult userTasks() throws Exception {
        Object UID = this.getSession("UID");
        if(UID!=null){
            Integer uid = (Integer) UID;
            EntityManager entityManager = this.baseService.getEntityManager();
            Query nativeQuery = entityManager.createNativeQuery("select task_id from task_user where user_id=" + uid);
            List<Integer> resultList = nativeQuery.getResultList();

            List<TaskBean> results = new ArrayList<>();
            for (Integer id : resultList) {
                TaskBean taskBean = this.baseService.get(id);
                results.add(taskBean);
            }
            return new ResponseResult(results);
        }else{
            return new ResponseResult(400,"登录失效，请重新登录");
        }

    }


    @Transactional
    @RequestMapping("/changeState/{taskId}/{state}")
    public ResponseResult setTaskData(@PathVariable Integer taskId, @PathVariable String state) throws Exception {
        TaskBean task = this.baseService.get(taskId);
        if(state.equalsIgnoreCase("weight")){
            // 删掉当前所有的weight树
            this.removeMainTree(taskId);

            // 获得决策者的树
            Integer uid = task.getUid();
            TaskTreeEntity treeEntity = new TaskTreeEntity();
            treeEntity.setUserId(uid);
            treeEntity.setTaskId(taskId);

            List<TaskTreeEntity> list = this.baseService.find(treeEntity);

            List<TaskTreeEntity> tree = TreeUtils.treeBean(list);
            TaskTreeEntity root = new TaskTreeEntity();
            root.setChildren(tree);
            root.setId(0);
            root.setTaskId(taskId);

            this.copyMainTree(root,uid);


            List<UserBasic> experts = task.getExperts();
            for (UserBasic expert : experts) {
                this.copyMainTree(root,expert.id);
            }

        }else if(state.equalsIgnoreCase("tree")){
            removeMainTree(taskId);
        }

        task.setState(state);
        this.baseService.saveOrUpdate(task);
        return new ResponseResult(200);
    }
    void removeMainTree(Integer taskId){
        EntityManager entityManager = this.baseService.getEntityManager();
        Query query = entityManager.createQuery("delete from TaskTreeEntity where domain = ?1 and taskId = ?2 ");
        query.setParameter(1,"weight");
        query.setParameter(2,taskId);
        query.executeUpdate();
    }

    private void copyMainTree(TaskTreeEntity parent, Integer uid ){
        List<TaskTreeEntity> copyTree = new ArrayList<>();
        List<TaskTreeEntity> children = parent.getChildren();
        for (TaskTreeEntity treeEntity : children) {
            TaskTreeEntity copyNode = new TaskTreeEntity();
            copyNode.setName(treeEntity.name);
            copyNode.setDataId(treeEntity.dataId);
            copyNode.setPid(parent.id);
            copyNode.setTaskId(parent.taskId);
            copyNode.setUserId(uid);
            copyNode.setDomain("weight");
            copyNode.setChildren(treeEntity.getChildren());
            copyTree.add(copyNode);
        }
        this.baseService.saveOrUpdate(copyTree);
        for (TaskTreeEntity treeEntity : copyTree) {
            copyMainTree(treeEntity,uid);
        }
    }


    @Override
    public String beforeUpdate(TaskBean ext, TaskBean bean) {
        Integer viewId = ext.getViewId();
        if(viewId!=null){
            DataFrameBean dataFrameBean = this.baseService.get(DataFrameBean.class, viewId);
            ext.setDomain(dataFrameBean.getDomain());
            ext.setScaleId(dataFrameBean.getScaleId());
        }
        return super.beforeUpdate(ext, bean);
    }



    @Override
    public String beforeInsert(TaskBean ext) {
        Object UID = this.getSession("UID");
        if(UID!=null){
            Integer uid = (Integer) UID;
            UserBasic userBasic = this.baseService.get(UserBasic.class, uid);
            ext.setUser(userBasic);
            ext.setUid(uid);
            ext.setState("prepare");
            ext.setDate(Constants.DATE_FORMAT.format(new Date()));

            Integer viewId = ext.getViewId();
            if(viewId==null) return "请选择任务视图";
            DataFrameBean dataFrameBean = this.baseService.get(DataFrameBean.class, viewId);
            if(dataFrameBean==null) return "任务视图不存在";
            ext.setDomain(dataFrameBean.getDomain());
            ext.setScaleId(dataFrameBean.getScaleId());

            return super.beforeInsert(ext);
        }else{
            return "用户未登录！";
        }
    }

}
