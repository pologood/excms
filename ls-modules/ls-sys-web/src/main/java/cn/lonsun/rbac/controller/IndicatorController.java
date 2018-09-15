/*
 * IIndicatorController.java         2015年8月26日 <br/>
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

package cn.lonsun.rbac.controller;

import javax.annotation.Resource;

import cn.lonsun.system.role.internal.entity.RbacMenuUserHideEO;
import cn.lonsun.system.role.internal.service.IMenuUserHideService;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.rbac.indicator.service.IIndicatorService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.shiro.web.filter.mgt.DefaultFilter.user;

/**
 * 指示器控制 <br/>
 *
 * @date 2015年8月26日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Controller("ex_8_IndicatorController")
@RequestMapping("/system/indicator")
public class IndicatorController extends BaseController {

    @Resource(name = "ex_8_IndicatorServiceImpl")
    private IIndicatorService indicatorService;

    @Autowired
    private IMenuUserHideService menuUserHideService;

    /**
     * 
     * 新增编辑
     *
     * @author fangtinghua
     * @param indicatorEO
     * @return
     */
    @ResponseBody
    @RequestMapping("saveOrUpdate")
    public Object saveOrUpdate(IndicatorEO indicatorEO) {
        if (StringUtils.isEmpty(indicatorEO.getName())) {
            return ajaxErr("名称不能为空！");
        }
        if (null == indicatorEO.getSortNum()) {
            return ajaxErr("排序号不能为空！");
        }
        indicatorService.save(indicatorEO);
        return getObject(indicatorEO);
    }

    /**
     * 
     * 删除
     *
     * @author fangtinghua
     * @param indicatorEO
     * @return
     */
    @ResponseBody
    @RequestMapping("delete")
    public Object delete(@RequestParam("ids[]") Long[] ids) {
        if (null == ids || ids.length == 0) {
            return ajaxErr("Id不能为空！");
        }
        indicatorService.delete(ids);
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
    @RequestMapping("getIndicator")
    public Object getIndicator(Long id) {
        IndicatorEO indicatorEO = null;
        if (null != id) {
            indicatorEO = indicatorService.getById(id);
            Map<String,Object> params = new HashMap<String, Object>();
            params.put("menuId",indicatorEO.getIndicatorId());
            List<RbacMenuUserHideEO> userlist =  menuUserHideService.getEntities(RbacMenuUserHideEO.class,params);
            if(null != userlist && !userlist.isEmpty()) {
                String json = JSONObject.toJSONString(userlist);
                indicatorEO.setUsersList(json);
                String userNames = null;
                for(RbacMenuUserHideEO eo : userlist) {
                    if(null == userNames) {
                        userNames = eo.getName();
                    } else {
                        userNames += "," + eo.getName();
                    }
                }
                indicatorEO.setUserNames(userNames);
            }
        } else {
            indicatorEO = new IndicatorEO();
        }

        return indicatorEO;
    }
}