/*
 * BusinessTypeController.java         2014年9月15日 <br/>
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

package cn.lonsun.type.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.exception.BusinessException;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.type.internal.entity.BusinessTypeEO;
import cn.lonsun.type.internal.service.IBusinessTypeService;


/**
 * 业务对象类型控制器
 *	 
 * @date     2014年9月15日 
 * @author 	 yy	 
 * @version	 v1.0 
 */
@Controller
@RequestMapping(value="businessType")
public class BusinessTypeController extends BaseController {
    @Autowired
    private IBusinessTypeService businessTypeService;
    
    /**
     * 根据Id查询
     * 
     * @author yy
     * @return
     */
    @RequestMapping("getById")
    @ResponseBody
    public Object getById(Long id) {
        BusinessTypeEO b = new BusinessTypeEO();
        if(null!=id) {
            b = businessTypeService.getEntity(BusinessTypeEO.class, id);
        }
        return getObject(b);
    }
    
    /**
     * 添加类型
     * 
     * @author yy
     * @param role
     */
    @RequestMapping("save")
    @ResponseBody
    public Object save(BusinessTypeEO type) {
        businessTypeService.save(type);
        return this.getObject(type);
    }
    
    /**
     * 修改类型
     * 
     * @author yy
     * @param role
     */
    @RequestMapping("update")
    @ResponseBody
    public Object update(BusinessTypeEO type) {
        businessTypeService.update(type);
        return this.getObject(type);
    }
    
    /**
     * 根据业务类型，模块子类型查询
     *
     * @author yy
     * @param type
     * @param caseCode
     * @return
     */
    @RequestMapping("getTreeByTypeWithCaseCode")
    @ResponseBody
    public Object getTreeByTypeWithCaseCode(String type, String caseCode) {
        return this.getObject(businessTypeService.getTreeByTypeWithCaseCode(type, caseCode));
    }
    
    /**
     * 删除
     * @author yy
     * @param 
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public Object delete(Long businessTypeId) {
        try {
            businessTypeService.delete(businessTypeId);
        } catch (BusinessException e) {
            e.printStackTrace();
            throw new BaseRunTimeException(TipsMode.Key.toString(),e.getKey());
        }
        return this.getObject();
    }
}

