/*
 * IIconDao.java         2014年8月19日 <br/>
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

package cn.lonsun.indicator.internal.dao;

import java.util.List;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.indicator.internal.entity.IndicatorEO;


/**
 * 
 *
 * @author xujh
 * @version 1.0
 * 2015年3月12日
 *
 */
public interface IIndicatorDao extends IMockDao<IndicatorEO> {
	
	/**
	 * 根据主键获取host
	 *
	 * @param indicatorId
	 * @return
	 */
	public String getWebServiceHostById(Long indicatorId);
	
	/**
	 * 获取子节点
	 *
	 * @param parentId
	 * @param type
	 * @param isEnable
	 * @return
	 */
	public List<IndicatorEO> getSubIndicatorByType(Long parentId,String type,Boolean isEnable);
	
	/**
	 * 获取单位unitId下用户uid类型为types的Indicator的主键列表
	 *
	 * @param unitId
	 * @param userId
	 * @param types
	 * @return
	 */
	public List<Long> getIndicators4User(Long unitId,Long userId,String[] types);
	
	/**
	 * 为开发商获取Indicators
	 *
	 * @param parentId
	 * @param type
	 * @param isOwnedByDeveloper
	 * @return
	 */
	public List<IndicatorEO> getIndicator4Developer(Long parentId,String type);
	
	/**
	 * 为系统超管获取Indicators
	 *
	 * @param parentId
	 * @param type
	 * @return
	 */
	public List<IndicatorEO> getIndicator4Admin(Long parentId,String type);
	
	/**
	 * 为开发商账号和系统超管获取Indicators
	 *
	 * @param parentId
	 * @param type
	 * @param isOwnedByDeveloper
	 * @return
	 */
	public List<IndicatorEO> getIndicator4DeveloperAndSuperAdmin(Long parentId,String type,boolean isOwnedByDeveloper);
	
	/**
	 * 
	 * 获取系统管理的应用
	 * @param type
	 * @param systemCode
	 * @return
	 */
	public IndicatorEO getIndicator(String type,String systemCode);

	List<IndicatorEO> getAllStieInfo();
	
	/**
	 * 获取用户在parentId下的子内容
	 * @param parentId
	 * @param types
	 * @param isEnable
	 * @return
	 */
	public List<IndicatorEO> getSystemIndicators(Long parentId,String[] types,Boolean isEnable);
	
	/**
	 * 根据角色获取IndicatorEO列表
	 * @param roleId
	 * @param parentId
	 * @param types
	 * @return
	 */
	public List<IndicatorEO> getIndicatorsByRole(Long roleId,Long parentId,String[] types);
	
	/**
	 * 获取用户在parentId下的子内容
	 *
	 * @param parentId
	 * @param types
	 * @param isEnable
	 * @return
	 */
	public List<IndicatorEO> getAppIndicators(Long parentId,String[] types,Boolean isEnable);
	
	/**
	 * 获取parentId下可用的IndicatorEO列表
	 *
	 * @param parentId
	 * @param types
	 * @param isShown4Developer 开发者是否可见
	 * @param isShown4Admin  超级管理员是否可见
	 * @return
	 */
	public List<IndicatorEO> getEnableIndicators(Long parentId,String[] types,Boolean isShown4Developer,Boolean isShown4Admin,Boolean isShown4SystemManager);
	
	/**
	 * 根据角色获取IndicatorEO列表
	 * @param roleIds
	 * @param parentId
	 * @param type
	 * @param isShowInDesktop，不填表示忽略此参数查询
	 * @param isExternal
	 * @return
	 */
	public List<IndicatorEO> getIndicatorsByRole(Long[] roleIds,Long parentId,String type,Boolean isShowInDesktop,Boolean isExternal);
	
	/**
	 * 根据角色获取IndicatorEO列表
	 * @param roleIds
	 * @param parentId
	 * @param type
	 * @return
	 */
	public List<IndicatorEO> getIndicators4SystemManager(Long[] roleIds,Long parentId,String type);
	
	/**
	 * 根据桌面应用编码字符串获取对应的应用
	 * @param roleIds 角色ID集合
	 * @param codes   indicator的code
	 * @return
	 */
	public List<IndicatorEO> getDesktopIndicators(Long[] roleIds,String[] codes);
	
//	/**
//	 * 根据桌面应用编码字符串获取对应的应用
//	 * @param roleIds 角色ID集合
//	 * @param codes   indicator的code
//	 * @return
//	 */
//	public List<IndicatorEO> getDesktopIndicatorsBySql(Long[] roleIds,String[] codes);
	
	
    /**
     * 查询图标分页信息
     *
     * @author yy
     * @param request
     * @return
     */
    public Pagination getPage();

    /**
     * 根据parentId查询
     *
     * @author yy
     * @param parentId
     * @param isEnable
     * @return
     */
    public List<IndicatorEO> getTree(Long parentId, Integer isEnable);
    
    /**
     * 查询菜单
     *
     * @author yy
     * @param parentId
     * @param type
     * @param isEnable
     * @return
     */
    public List<IndicatorEO> getMenu(Long parentId, String[] types, Integer isEnable);

    /**
     * 查询父节点
     *
     * @author yy
     * @return
     */
    public List<IndicatorEO> getSuperParent();

    /**
     * 根据type查询
     *
     * @author yy
     * @param type
     * @param isEnable
     * @return
     */
    public List<IndicatorEO> getByType(String type, Integer isEnable);

    /**
     * 查询出应用、菜单、权限的排序号
     *
     * @author yy
     * @param type
     * @param parentId
     * @return
     */
    public Integer getMaxSortNum(String type,Long parentId);

    /**
     * 根据parentId查询权限
     *
     * @author yy
     * @param parentId
     * @return
     */
    public List<IndicatorEO> getButtonByParentId(Long parentId);
    
    /**
     * 获取父亲主键为parentId的Indicator数量
     * @param parentId
     * @param isEnable 如果为空，那么忽略该查询条件
     * @return
     */
    public long getCountByParentId(Long parentId,Boolean isEnable);
    
    /**
	 * 获取所有的应用
	 * @return
	 */
	public List<IndicatorEO> getAllShortcuts();
	
	 /**
     * 获取默认首页菜单
     * @param parentId
     * @return
     */
    public List<IndicatorEO> getIndexIndicators(Long parentId);

	public List<IndicatorEO> getByParentIdsLike(String parentId);
}

