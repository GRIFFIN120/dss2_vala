package com.vala.framework.data.controller;

import com.vala.base.controller.BaseController;
import com.vala.commons.bean.ResponseResult;
import com.vala.commons.util.Constants;
import com.vala.framework.data.bean.DataBean;
import com.vala.framework.data.bean.DataFrameBean;
import com.vala.framework.data.bean.DataItemBean;
import com.vala.framework.user.entity.UserBasic;
import com.vala.framework.utils.ExcelUtils;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/data-frame")
public class DataFrameController extends BaseController<DataFrameBean> {

    @Transactional
    @RequestMapping(value="/file/{domain}/{scaleId}/{create}", produces = "application/json;charset=UTF-8")
    public ResponseResult file(@RequestPart(value = "file") MultipartFile file, @PathVariable String domain, @PathVariable Integer scaleId, @PathVariable Boolean create) throws Exception {

        Integer uid = (Integer) this.getSession("UID");
        if(uid==null){
            return new ResponseResult(500, "登录已失效，请重新登录");
        }
        String fileName = file.getOriginalFilename();
        String[] ars = ExcelUtils.getNameAndExtension(fileName);
        List<Object[]> read = ExcelUtils.read(file.getInputStream(), ars[1]);

        for (Object[] objects : read) {
            System.out.println(ArrayUtils.toString(objects));
        }


        String frameName = ars[0];
        String date = Constants.DATE_FORMAT.format(new Date());



        Map<String, Double[]> map = new LinkedHashMap<>();
        Class catClass = String.class;
        if(domain.equals("series")) catClass = Date.class;
        if(domain.equals("number")) catClass = Double.class;

        for (int i = 1; i < read.size(); i++) {
            Object[] objects =  read.get(i);
            Object first = objects[0];
            if(first==null){
                String msg = String.format("第%s行的类别项（第1列）不能为空，请修改！",(i+1));
                return new ResponseResult(400, msg);
            }else if(!first.getClass().isAssignableFrom(catClass)){
                String msg = String.format("第%s行的类别项（第1列）不符合视图结构要求，请修改！！",(i+1));
                return new ResponseResult(400, msg);
            }

            Double[] numbers = new Double[objects.length-1];
            for (int j = 1; j < objects.length; j++) {
                Object num =  objects[j];
                if(num!=null&&!num.getClass().isAssignableFrom(Double.class)){
                    String msg = String.format("%s行%s列不是数字，请修改！",(i+1),(j+1));
                    return new ResponseResult(400, msg);
                }else{
                    numbers[j-1] = (Double) num;
                }
            }

            String category = first.toString();
            if(domain.equals("series")) category = Constants.DATE_FORMAT.format(first);
            map.put(category, numbers);
        }

        UserBasic user = this.baseService.get(UserBasic.class, uid);

        List<DataBean> dataList = new ArrayList<>();
        Object[] titles = read.get(0);
        for (int j = 1; j < titles.length; j++) {
            Object temp =  titles[j];
            if(temp==null){
                String msg = String.format("首行的第%s列为标题列，不能为空，请修改！",(j+1));
                return new ResponseResult(400, msg);
            }

            String title = temp.toString();
            DataBean data = new DataBean();
            data.setName(title);
            data.setDomain(domain);
            data.setScaleId(scaleId);
            data.setDate(date);
            if(create){
                data.setDescription("由视图\""+frameName+"\"导入");
            }else{
                data.setDescription("由外部文件\""+frameName+"\"导入");
            }


            data.setUid(uid);
            data.setUser(user);
            dataList.add(data);
        }



        this.baseService.saveOrUpdate(dataList);

        for (int i = 0; i < dataList.size(); i++) {
            DataBean dataBean =  dataList.get(i);
            saveDataItems(map,i,dataBean.getId());
        }


        if(create){
            DataFrameBean frame = new DataFrameBean();
            frame.setName(frameName);
            frame.setDomain(domain);
            frame.setDate(date);
            frame.setScaleId(scaleId);
            frame.setDescription("由外部文件导入");
            frame.setUid(uid);
            frame.setUser(user);
            frame.setData(dataList);
            this.baseService.saveOrUpdate(frame);
        }


        return new ResponseResult(200, "数据上传成功");
    }



    private void saveDataItems(Map<String, Double[]> map, int index, int dataId){

        Date date = new Date();
        long time = date.getTime();
        int i = 0;
        List<DataItemBean> list = new ArrayList<>();
        for (String category : map.keySet()) {
            Double[] ds = map.get(category);
            Double value = ds[index];
            DataItemBean item = new DataItemBean();
            item.setCategory(category);
            item.setValue(value);
            item.setDataId(dataId);
            item.setTimestamp(new Date(time+1000*i++));
            list.add(item);

        }
        this.baseService.saveOrUpdate(list);
    }

    @RequestMapping("/getData/{id}")
    public ResponseResult getData(@PathVariable Integer id) throws Exception {
        DataFrameBean dataFrameBean = this.baseService.get(id);
        return new ResponseResult(dataFrameBean.getData());
    }

    @Override
    public String beforeInsert(DataFrameBean ext) {
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
