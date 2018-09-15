/*
 * JobController.java         2016年3月24日 <br/>
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

package cn.lonsun.system.job.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.job.internal.entity.ScheduleJobEO;
import cn.lonsun.job.internal.vo.ScheduleJobQueryVO;
import cn.lonsun.job.service.IScheduleJobService;
import cn.lonsun.job.util.QuartzManagerUtil;
import cn.lonsun.system.datadictionary.internal.entity.DataDictEO;
import cn.lonsun.system.datadictionary.internal.entity.DataDictItemEO;
import cn.lonsun.util.DataDictionaryUtil;
import cn.lonsun.util.LoginPersonUtil;

/**
 * 任务管理 <br/>
 *
 * @date 2016年3月24日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Controller
@RequestMapping("/job")
public class JobController extends BaseController {

    @Resource
    private IScheduleJobService scheduleJobService;

    /**
     * 转向任务管理首页
     *
     * @author fangtinghua
     * @return
     */
    @RequestMapping("index")
    public String index() {
        return "system/job/index";
    }

    /**
     * 转向任务管理编辑页面
     *
     * @author fangtinghua
     * @return
     */
    @RequestMapping("edit")
    public String edit(ModelMap modelMap) {
        boolean isRoot = LoginPersonUtil.isRoot();
        if (!isRoot) {
            DataDictEO dataDictEO = CacheHandler.getEntity(DataDictEO.class, CacheGroup.CMS_CODE, IScheduleJobService.jobType);
            if (null != dataDictEO) {
                modelMap.put("jobTypeList", CacheHandler.getList(DataDictItemEO.class, CacheGroup.CMS_PARENTID, dataDictEO.getDictId()));
            }
        }
        modelMap.put("isRoot", isRoot);
        modelMap.put("default", DataDictionaryUtil.getDefuatItem(IScheduleJobService.jobType, null));
        return "system/job/edit";
    }

    /**
     * 获取分页
     *
     * @author fangtinghua
     * @param queryVO
     * @return
     */
    @ResponseBody
    @RequestMapping("getPage")
    public Object getPage(ScheduleJobQueryVO queryVO) {
        queryVO.setSiteId(LoginPersonUtil.getSiteId());
        return scheduleJobService.getPagination(queryVO);
    }

    /**
     * 新增编辑
     *
     * @author fangtinghua
     * @param scheduleJobEO
     * @return
     */
    @ResponseBody
    @RequestMapping("saveOrUpdate")
    public Object saveOrUpdate(ScheduleJobEO scheduleJobEO) {
        if (StringUtils.isEmpty(scheduleJobEO.getName())) {
            return ajaxErr("名称不能为空！");
        }
        if (StringUtils.isEmpty(scheduleJobEO.getType())) {
            return ajaxErr("类型不能为空！");
        }
        if (StringUtils.isEmpty(scheduleJobEO.getClazz())) {
            return ajaxErr("类路径不能为空！");
        }
        if (StringUtils.isEmpty(scheduleJobEO.getCronExpression())) {
            return ajaxErr("时间表达式不能为空！");
        }
        if (!QuartzManagerUtil.isValidExpression(scheduleJobEO.getCronExpression())) {
            return ajaxErr("时间表达式格式不正确！");
        }
        scheduleJobService.saveEntity(scheduleJobEO);
        return getObject(scheduleJobEO);
    }

    /**
     * 删除
     *
     * @author fangtinghua
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping("delete")
    public Object delete(Long id) {
        scheduleJobService.delete(ScheduleJobEO.class, id);
        return getObject();
    }

    /**
     * 获取对象
     *
     * @author fangtinghua
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping("getScheduleJob")
    public Object getScheduleJob(Long id) {
        ScheduleJobEO scheduleJobEO = null;
        if (null != id) {
            scheduleJobEO = scheduleJobService.getEntity(ScheduleJobEO.class, id);
        } else {
            scheduleJobEO = new ScheduleJobEO();
        }
        return scheduleJobEO;
    }

    /**
     * 暂停
     *
     * @author fangtinghua
     * @param scheduleJobEO
     * @return
     */
    @ResponseBody
    @RequestMapping("pauseJob")
    public Object pauseJob(ScheduleJobEO scheduleJobEO) {
        return getObject(scheduleJobService.pauseJob(scheduleJobEO));
    }

    /**
     * 恢复
     *
     * @author fangtinghua
     * @param scheduleJobEO
     * @return
     */
    @ResponseBody
    @RequestMapping("resumeJob")
    public Object resumeJob(ScheduleJobEO scheduleJobEO) {
        return getObject(scheduleJobService.resumeJob(scheduleJobEO));
    }

    /**
     * 立即运行
     *
     * @author fangtinghua
     * @param scheduleJobEO
     * @return
     */
    @ResponseBody
    @RequestMapping("triggerJob")
    public Object triggerJob(ScheduleJobEO scheduleJobEO) {
        return getObject(scheduleJobService.triggerJob(scheduleJobEO));
    }

    /**
     * 验证表达式
     *
     * @author fangtinghua
     * @param cronExpression
     * @return
     */
    @ResponseBody
    @RequestMapping("validExpression")
    public Object validExpression(String cronExpression) {
        Map<String, String> resultMap = new HashMap<String, String>();
        boolean result = QuartzManagerUtil.isValidExpression(cronExpression);
        if (!result) {
            resultMap.put("error", "时间表达式格式不正确");
        }
        return resultMap;
    }

    /**
     * 验证类
     *
     * @author fangtinghua
     * @param clazz
     * @return
     */
    @ResponseBody
    @RequestMapping("validClazz")
    public Object validClazz(String clazz) {
        Map<String, String> resultMap = new HashMap<String, String>();
        try {
            Class.forName(clazz);
        } catch (Throwable e) {
            resultMap.put("error", "Java类不存在或路径不正确");
        }
        return resultMap;
    }
}