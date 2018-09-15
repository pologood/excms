/*
 * IIndicatorService.java         2015年8月25日 <br/>
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

package cn.lonsun.rbac.indicator.service;

import java.util.List;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.rbac.indicator.entity.MenuEO;

/**
 * 指示器管理 <br/>
 *
 * @date 2015年8月25日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public interface IIndicatorService extends IMockService<IndicatorEO> {

    /**
     * 
     * 根据角色权限获取菜单
     *
     * @author fangtinghua
     */
    public List<MenuEO> getMenu(boolean all, Long roleId);

    /**
     * 
     * 根据菜单获取按钮列表
     *
     * @author fangtinghua
     */
    public Pagination getButton(PageQueryVO vo, Long indicatorId);

    /**
     * 
     * 保存菜单、按钮、站点、栏目
     *
     * @author fangtinghua
     * @param indicator
     */
    public void save(IndicatorEO indicator);

    /**
     * 根据id查询
     *
     * @author fangtinghua
     * @param indicatorId
     * @return
     */
    public IndicatorEO getById(Long indicatorId);

    /**
     * 通过parentId查询
     *
     * @author yy
     * @param parentId
     * @param isEnable
     * @return
     */
    public List<IndicatorEO> getByParentId(Long parentId);

    /**
     * 
     * 删除indicator
     *
     * @author fangtinghua
     * @param indicatorId
     */
    public void delete(Long indicatorId);

    /**
     * 
     * 删除indicator
     *
     * @author fangtinghua
     * @param ids
     */
    public void delete(Long[] ids);

    public void updateList(List<IndicatorEO> list);
}