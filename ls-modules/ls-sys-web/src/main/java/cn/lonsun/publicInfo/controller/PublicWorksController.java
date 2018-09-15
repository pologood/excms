/*
 * PublicWorksController.java         2016年9月22日 <br/>
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

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.publicInfo.internal.entity.PublicLeadersEO;
import cn.lonsun.publicInfo.internal.entity.PublicWorksEO;
import cn.lonsun.publicInfo.internal.service.IPublicLeadersService;
import cn.lonsun.publicInfo.internal.service.IPublicWorksService;
import cn.lonsun.publicInfo.vo.PublicLeadersQueryVO;
import cn.lonsun.publicInfo.vo.PublicWorksQueryVO;
import cn.lonsun.util.LoginPersonUtil;

/**
 * 每日工作动态 <br/>
 * 
 * @date 2016年9月22日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Controller
@RequestMapping("/public/works")
public class PublicWorksController extends BaseController {

    @Resource
    private IPublicWorksService publicWorksService;
    @Resource
    private IPublicLeadersService publicLeadersService;

    /**
     * 转向工作动态首页
     * 
     * @author fangtinghua
     * @return
     */
    @RequestMapping("index")
    public String index() {
        return "public/works/index";
    }

    /**
     * 转向工作动态管理首页
     * 
     * @author fangtinghua
     * @return
     */
    @RequestMapping("indexMgr")
    public String indexMgr() {
        return "public/works/index_mgr";
    }

    /**
     * 转向工作动态编辑页面
     * 
     * @author fangtinghua
     * @return
     */
    @RequestMapping("edit")
    public String edit(PublicLeadersQueryVO queryVO, ModelMap modelMap) {
        // 查询领导
        // 设置状态，查询启用的领导
        queryVO.setSiteId(LoginPersonUtil.getSiteId());// 暂无作用
        queryVO.setStatus(PublicLeadersEO.Status.Normal.toString());
        queryVO.setOrganId(LoginPersonUtil.getUnitId());
        List<PublicLeadersEO> leadersList = publicLeadersService.getPublicLeadersList(queryVO);
        modelMap.put("leadersList", leadersList);
        // 默认当前时间
        modelMap.put("date", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));
        return "public/works/edit";
    }

    /**
     * 转向工作动态编辑页面
     * 
     * @author fangtinghua
     * @return
     */
    @RequestMapping("editMgr")
    public String editMgr(PublicLeadersQueryVO queryVO, ModelMap modelMap) {
        // 查询领导
        // 设置状态，查询启用的领导
        queryVO.setSiteId(LoginPersonUtil.getSiteId());// 暂无作用
        queryVO.setStatus(PublicLeadersEO.Status.Normal.toString());
        List<PublicLeadersEO> leadersList = publicLeadersService.getPublicLeadersList(queryVO);
        modelMap.put("leadersList", leadersList);
        // 默认当前时间
        modelMap.put("date", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));
        return "public/works/edit";
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
    public Object getPage(PublicWorksQueryVO queryVO) {
        queryVO.setOrganId(LoginPersonUtil.getUnitId());
        return publicWorksService.getPagination(queryVO);
    }

    /**
     * 获取分页
     * 
     * @author fangtinghua
     * @param queryVO
     * @return
     */
    @ResponseBody
    @RequestMapping("getMgrPage")
    public Object getMgrPage(PublicWorksQueryVO queryVO) {
        return publicWorksService.getPagination(queryVO);
    }

    /**
     * 新增编辑
     * 
     * @author fangtinghua
     * @param publicWorksEO
     * @return
     */
    @ResponseBody
    @RequestMapping("saveOrUpdate")
    public Object saveOrUpdate(PublicWorksEO publicWorksEO) {
        if (null == publicWorksEO.getLeadersId()) {
            return ajaxErr("单位领导不能为空！");
        }
        if (StringUtils.isEmpty(publicWorksEO.getJobContent())) {
            return ajaxErr("工作内容不能为空！");
        }
        publicWorksService.saveOrUpdateEntity(publicWorksEO);
        return getObject();
    }

    /**
     * 更新状态
     * 
     * @author fangtinghua
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping("updateStatus")
    public Object updateStatus(Long id) {
        PublicWorksEO publicWorksEO = publicWorksService.getEntity(PublicWorksEO.class, id);
        publicWorksEO.setEnable(!publicWorksEO.getEnable());
        publicWorksService.updateEntity(publicWorksEO);
        return getObject();
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
        publicWorksService.delete(PublicWorksEO.class, id);
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
    @RequestMapping("getPublicWorks")
    public Object getPublicWorks(Long id) {
        PublicWorksEO publicWorksEO = null;
        if (null != id) {
            publicWorksEO = publicWorksService.getEntity(PublicWorksEO.class, id);
        } else {
            publicWorksEO = new PublicWorksEO();
        }
        return publicWorksEO;
    }
}