/*
 * IIconService.java         2014年8月19日 <br/>
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

package cn.lonsun.indicator.internal.service;

import java.util.List;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.exception.BusinessException;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.indicator.internal.vo.IndicatorVO;


/**
 *  指示器服务类
 *	 
 * @date     2014年8月19日 
 * @author 	 yy 
 * @version	 v1.0 
 */
public interface IIndicatorService extends IMockService<IndicatorEO>  {


	/**
	 *
	 */
	public void initParentPath();
	
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
	 * 获取单位unitId下用户uid类型为types的所有权限的主键集合
	 *
	 * @param unitId
	 * @param userId
	 * @param types
	 * @return
	 */
	public List<Long> getIndicators4User(Long unitId,Long userId,String[] types);
	
	/**
	 * 获取用户权限范围内的IndicatorId集合
	 *
	 * @param userId
	 * @param types
	 * @return
	 */
	public List<Long> getIndicatorIdsByUserId(Long userId,String[] types);
	
	/**
	 * 获取角色拥有的权限
	 *
	 * @param roleId
	 * @param types
	 * @return
	 */
	public List<Long> getIndicatorIdsByRoleId(Long roleId,String[] types);
	
	/**
	 * 获取用户在parentId下的子内容
	 * @param parentId
	 * @param types
	 * @param isEnable
	 * @return
	 */
	public List<IndicatorEO> getSystemIndicators(Long parentId,String[] types,Boolean isEnable);
	
	/**
	 * 获取系统管理的服务器部署地址,例如：http://192.168.1.12:8989
	 * @param systemCode
	 * @return
	 */
	public String getHost(String systemCode);
	
	/**
	 * 获取用户在parentId下的子内容
	 * @param userId
	 * @param parentId
	 * @param type
	 * @return
	 */
	public List<IndicatorEO> getSystemIndicators(Long userId,Long parentId,String type);
	
	
	/**
	 * 根据系统编码获取系统快捷方式
	 *
	 * @param systemCode
	 * @return
	 */
	public IndicatorEO getIndicatorBySystemCode(String systemCode);
	/**
	 * 为开发商账号和超级管理员账号获取Indicators
	 *
	 * @param parentId
	 * @param type
	 * @param isOwnedByDeveloper
	 * @return
	 */
	public List<IndicatorEO> getIndicator4DeveloperAndSuperAdmin(Long parentId,String type,boolean isOwnedByDeveloper);
	
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
	 * 获取所有的应用
	 * @return
	 */
	public List<IndicatorEO> getAllShortcuts();
	
	/**
	 * 获取用户在parentId下的子内容
	 *
	 * @param parentId
	 * @param types
	 * @param isEnable
	 * @return
	 */
	public List<IndicatorEO> getAppIndicators(Long parentId,String[] types,Boolean isEnable,Boolean isVisibleBySystemManager);
	
	/**
	 * 获取用户在parentId下的子内容
	 * @param userId
	 * @param organId
	 * @param parentId
	 * @return
	 */
	public List<IndicatorEO> getAppIndicators(Long userId,Long organId,Long parentId,String type);
	
	
	/**
	 * 根据角色获取IndicatorEO列表
	 * @param roleId
	 * @param parentId
	 * @param types
	 * @return
	 */
	public List<IndicatorEO> getIndicatorsByRole(Long roleId,Long parentId,String[] types);
	
	/**
	 * 根据桌面应用编码字符串获取对应的应用
	 * @param userId
	 * @param organId  
	 * @param codes 
	 * @return
	 */
	public List<IndicatorEO> getDesktopIndicators(Long userId,Long organId,String[] codes);
	
    /**
     * 
     * 保存模块、菜单、按钮
     *
     * @author yy
     * @param indicator
     */
    public void save(IndicatorEO indicator);
    
    /**
     * 保存应用快捷方式、菜单、按钮或虚拟权限集合
     *
     * @param indicator
     * @param uris
     */
    public void save(IndicatorEO indicator,String[] uris);
    
    /**
     * 更新应用快捷方式、菜单、按钮或虚拟权限集合
     *
     * @param indicator
     * @param uris
     */
    public void update(IndicatorEO indicator,String[] uris);

   /**
    * 修改应用
    *
    * @param indicator
    */
    public void updateShortCut(IndicatorEO indicator);
    
    /**
     * 保存菜单
     *
     * @author yy
     * @param vo
     */
    public void saveMenu(IndicatorVO vo) throws BusinessException;
    
    /**
     * 
     * 修改菜单
     *
     * @author yy
     * @param indicatorEO
     */
    public void updateMenu(IndicatorVO indicator) throws BusinessException;
    
    /**
     * 添加权限
     *
     * @author yy
     * @param indicatorEO
     */
    public void saveButton(IndicatorVO indicatorVO) throws BusinessException;

    /**
     * 修改权限
     *
     * @author yy
     * @param indicatorEO
     */
    public void updateButton(IndicatorVO indicatorVO) throws BusinessException;
    
    /**
     * 
     * 删除indicator
     *
     * @author yy
     * @param indecatorId
     */
    public void delete(Long indecatorId) throws BusinessException;
    
    /**
     * 通过parentId查询
     *
     * @author yy
     * @param parentId
     * @param isEnable
     * @return
     */
    public List<IndicatorEO> getByParentId(Long parentId, Integer isEnable);

    /**
     * 通过parentId, type查询
     *
     * @author yy
     * @param parentId
     * @param type
     * @param isEnable
     * @return
     */
    public List<IndicatorVO> getMenu(Long parentId, String type, Integer isEnable);
    
    /**
     * 获取button
     *
     * @author yy
     * @param indicatorId
     * @param isEnable
     * @return
     */
    public List<IndicatorVO> getButtons(Long indicatorId, Integer isEnable);
    

    /**
     * 查询所有根节点
     *
     * @author yy
     * @return
     */
    public List<IndicatorEO> getSuperParent();

    /**
     * 根据roleId查询权限
     *
     * @author yy
     * @param roleId
     * @return
     */
    public List<IndicatorVO> getByRole(Long roleId);
    
    /**
     * 获取所有
     *
     * @author yy
     * @return
     */
    public List<IndicatorVO> getAll();
    
    /**
     * getByType
     *
     * @author yy
     * @param type
     * @param isEnable 是否启用
     * @return
     */
    public List<IndicatorVO> getByType(String type, Integer isEnable);

    /**
     * 启用/禁用
     *
     * @author yy
     * @param indicatorId
     */
    public void enable(Long indicatorId) throws BusinessException;

    /**
     * 根据id查询
     *
     * @author yy
     * @param indicatorId
     * @return
     */
    public IndicatorVO getById(Long indicatorId);

	/**
	 * 获取物理数据，无视recordStatus
	 * @param indicatorId
	 * @return
	 */
	public IndicatorEO getPhysicalById(Long indicatorId);
    
    /**
     * 根据id查询IndicatorEO
     *
     * @author yy
     * @param indicatorId
     * @return
     */
    public IndicatorEO getIndicatorEOById(Long indicatorId);

    /**
     * 查询出应用、菜单、权限的排序号
     *
     * @author yy
     * @param type
     * @param parentId
     * @return
     */
    public Integer getMaxSortNum(String type ,Long parentId);

    /**
     * 通过parentId查询权限
     *
     * @author yy
     * @param parentId
     * @return
     */
    public List<IndicatorEO> getButtonByParentId(Long parentId);
    
    /**
     * 获取默认首页菜单
     * @param parentId
     * @return
     */
    public List<IndicatorEO> getIndexIndicators(Long parentId);
    
    /**
     * 根据角色ID获取所有的正常使用的IndicatorId集合
     * @param roleId
     * @return
     */
    public List<Long> getIndicatorIds(Long roleId);

	public  List<IndicatorEO> getAllStieInfo();

}

