/*
 * PublicClassController.java         2015年12月11日 <br/>
 *
 * Copyright (c) 1994-1999 AnHui LonSun, Inc. <br/>
 * All rights reserved.	<br/>
 *
 * This software is the confidential and proprietary information of AnHui	<br/>
 * LonSun, Inc. ("Confidential Information").  You shall not	<br/>
 * disclose such Confidential Information and shall use it only in	<br/>
 * accordance with the terms of the license agreement you entered into	<br/>
 * with Sun. <br/>
 */

package cn.lonsun.publicInfo.controller;

import javax.annotation.Resource;

import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.SysLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.publicInfo.internal.entity.PublicClassEO;
import cn.lonsun.publicInfo.internal.service.IPublicClassService;

/**
 * 组配分类 <br/>
 *
 * @date 2015年12月11日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Controller
@RequestMapping("/public/class")
public class PublicClassController extends BaseController {

    @Resource
    private IPublicClassService publicClassService;

    /**
     * 转向组配分类首页
     *
     * @author fangtinghua
     * @return
     */
    @RequestMapping("index")
    public String index() {
        return "public/class/index";
    }

    /**
     * 查询组配分类
     *
     * @author fangtinghua
     * @return
     */
    @ResponseBody
    @RequestMapping("getClasss")
    public Object getClasss() {
        return CacheHandler.getList(PublicClassEO.class, CacheGroup.CMS_ALL, AMockEntity.RecordStatus.Normal.toString());
    }

    /**
     * 查询组配分类
     *
     * @author fangtinghua
     * @return
     */
    @ResponseBody
    @RequestMapping("getClasssJSON")
    public Object getClasssJSON() {
        return getObject(CacheHandler.getList(PublicClassEO.class, CacheGroup.CMS_ALL, AMockEntity.RecordStatus.Normal.toString()));
    }

    /**
     * 
     * 新增编辑
     *
     * @author fangtinghua
     * @param publicClassEO
     * @return
     */
    @ResponseBody
    @RequestMapping("saveOrUpdate")
    public Object saveOrUpdate(PublicClassEO publicClassEO) {
        if (StringUtils.isEmpty(publicClassEO.getName())) {
            return ajaxErr("名称不能为空！");
        }
        if (null == publicClassEO.getSortNum()) {
            return ajaxErr("排序号不能为空！");
        }
        String opt = "添加";
        if(publicClassEO.getId()!=null&&publicClassEO.getId()>0){
            opt = "修改";
        }
        publicClassService.saveOrUpdateEntity(publicClassEO);

        SysLog.log("组配分类管理："+opt+"分类（"+publicClassEO.getName()+"）","PublicClassEO", CmsLogEO.Operation.Update.toString());
        return getObject(publicClassEO);
    }

    /**
     * 
     * 删除
     *
     * @author fangtinghua
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping("delete")
    public Object delete(Long id) {
        PublicClassEO publicClassEO = publicClassService.getEntity(PublicClassEO.class,id);
        String name = publicClassEO.getName();
        publicClassService.delete(PublicClassEO.class, id);
        SysLog.log("组配分类管理：删除分类（"+name+"）","PublicClassEO", CmsLogEO.Operation.Delete.toString());
        return getObject();
    }

    /**
     * 
     * 获取对象
     *
     * @author fangtinghua
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping("getPublicClass")
    public Object getPublicClass(Long id) {
        PublicClassEO publicClassEO = null;
        if (null != id) {
            publicClassEO = CacheHandler.getEntity(PublicClassEO.class, id);
        } else {
            publicClassEO = new PublicClassEO();
        }
        return publicClassEO;
    }
}