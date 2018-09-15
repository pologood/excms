/*
 * IconServiceImpl.java         2014年8月19日 <br/>
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

package cn.lonsun.indicator.internal.service.impl;

import java.util.*;

import cn.lonsun.core.base.util.ArrayUtil;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.rbac.utils.PinYinUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.entity.AMockEntity.RecordStatus;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.exception.BusinessException;
import cn.lonsun.indicator.internal.dao.IIndicatorDao;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.indicator.internal.service.IIndicatorService;
import cn.lonsun.indicator.internal.vo.IndicatorVO;
import cn.lonsun.rbac.internal.entity.IndicatorPermissionEO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.entity.ResourceEO;
import cn.lonsun.rbac.internal.entity.RoleAssignmentEO;
import cn.lonsun.rbac.internal.entity.RoleEO;
import cn.lonsun.rbac.internal.entity.RoleRelationEO;
import cn.lonsun.rbac.internal.entity.UserEO;
import cn.lonsun.rbac.internal.service.IIndicatorPermissionService;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.rbac.internal.service.IResourceService;
import cn.lonsun.rbac.internal.service.IRoleAssignmentService;
import cn.lonsun.rbac.internal.service.IRoleRelationService;
import cn.lonsun.rbac.internal.service.IRoleService;
import cn.lonsun.rbac.internal.service.IUserService;
import cn.lonsun.rbac.utils.AdminUidVO;

/**
 * 指示器service
 * 
 * @date 2014年8月19日
 * @author yy
 * @version v1.0
 */
@Service("indicatorService")
public class IndicatorServiceImpl extends MockService<IndicatorEO> implements
		IIndicatorService {
	@Autowired
	private IIndicatorDao indicatorDao;
	@Autowired
	private IIndicatorPermissionService indicatorPermissionService;
	@Autowired
	private IResourceService resourceService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IOrganService organServie;
	@Autowired
	private IUserService userService;
	@Autowired
	private IRoleAssignmentService roleAssignmentService;
	@Autowired
	private IRoleRelationService roleRelationService;

	@Override
	public String getWebServiceHostById(Long indicatorId) {
		return indicatorDao.getWebServiceHostById(indicatorId);
	}

	@Override
	public List<IndicatorEO> getSubIndicatorByType(Long parentId, String type,
			Boolean isEnable) {
		return indicatorDao.getSubIndicatorByType(parentId, type, isEnable);
	}
	@Override
	public List<IndicatorEO> getAllStieInfo() {
		return indicatorDao.getAllStieInfo();
	}

	@Override
	public List<IndicatorEO> getEnableIndicators(Long parentId, String[] types,
			Boolean isShown4Developer, Boolean isShown4Admin,
			Boolean isShown4SystemManager) {
		return indicatorDao.getEnableIndicators(parentId, types,
				isShown4Developer, isShown4Admin, isShown4SystemManager);
	}

	@Override
	public List<Long> getIndicators4User(Long unitId, Long userId,
			String[] types) {
		return indicatorDao.getIndicators4User(unitId, userId, types);
	}

	@Override
	public List<Long> getIndicatorIdsByUserId(Long userId, String[] types) {
		// 1.根据unitId和userId获取用户直接绑定的系统角色--因为只有系统角色才有管理的绑定关系
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", userId);
		map.put("roleType", RoleEO.Type.System.toString());
		map.put("recordStatus", RecordStatus.Normal.toString());
		List<RoleAssignmentEO> ras = roleAssignmentService.getEntities(RoleAssignmentEO.class, map);
		if (ras == null || ras.size() <= 0) {
			return null;
		}
		Long[] roleIds = new Long[ras.size()];
		for (int i = 0; i < ras.size(); i++) {
			RoleAssignmentEO ra = ras.get(i);
			roleIds[i] = ra.getRoleId();
		}
		// 2.根据角色管理关系，获取用户可管理的角色
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("roleId", roleIds);
		params.put("recordStatus", RecordStatus.Normal.toString());
		List<RoleRelationEO> rrs = roleRelationService.getEntities(RoleRelationEO.class, params);
		// 如果没有角色管理关系，那么说明当前用户无添加角色权限的权限
		if (rrs == null || rrs.size() <= 0) {
			return null;
		}
		// 获取不重复的被管理角色主键集合
		List<Long> targetRoleIds = new ArrayList<Long>();
		for (RoleRelationEO rr : rrs) {
			if (!targetRoleIds.contains(rr.getTargetRoleId())) {
				targetRoleIds.add(rr.getTargetRoleId());
			}
		}
		// 3.通过用户可管理的角色获取可管理的权限
		Map<String, Object> pparams = new HashMap<String, Object>();
		pparams.put("roleId", targetRoleIds);
		pparams.put("indicatorType", types);
		pparams.put("recordStatus", RecordStatus.Normal.toString());
		List<IndicatorPermissionEO> ips = indicatorPermissionService
				.getEntities(IndicatorPermissionEO.class, pparams);
		//获取又有的权限主键
		List<Long> indicatorIds = null;
		if(ips!=null&&ips.size()>0){
			indicatorIds = new ArrayList<Long>();
			for(IndicatorPermissionEO ip:ips){
				Long indicatorId = ip.getIndicatorId();
				if(!indicatorIds.contains(indicatorId)){
					indicatorIds.add(indicatorId);
				}
			}
		}
		return indicatorIds;
	}
	
	@Override
	public List<Long> getIndicatorIdsByRoleId(Long roleId,String[] types){
		// 通过用户角色获取可管理的权限
		Map<String, Object> pparams = new HashMap<String, Object>();
		pparams.put("roleId", roleId);
		pparams.put("indicatorType", types);
		pparams.put("recordStatus", RecordStatus.Normal.toString());
		List<IndicatorPermissionEO> ips = indicatorPermissionService.getEntities(IndicatorPermissionEO.class, pparams);
		//获取又有的权限主键
		List<Long> indicatorIds = null;
		if(ips!=null&&ips.size()>0){
			indicatorIds = new ArrayList<Long>();
			for(IndicatorPermissionEO ip:ips){
				Long indicatorId = ip.getIndicatorId();
				if(!indicatorIds.contains(indicatorId)){
					indicatorIds.add(indicatorId);
				}
			}
		}
		return indicatorIds;
	}

	@Override
	public List<IndicatorVO> getAll() {
		List<IndicatorVO> indicatorVOs = new ArrayList<IndicatorVO>();
		List<IndicatorEO> indicatorEOs = indicatorDao.getEntitiesByHql(
				"from IndicatorEO t", new Object[] {});
		for (IndicatorEO indicator : indicatorEOs) {
			IndicatorVO indicatorVO = new IndicatorVO();
			AppUtil.copyProperties(indicatorVO, indicator);
			indicatorVO.setIsEnable(indicator.getIsEnable() ? "1" : "0");
			indicatorVO.setIsShowSons(indicator.getIsShowSons() ? "1" : "0");
			indicatorVO.setId(indicator.getIndicatorId());
			indicatorVO.setPid(indicator.getParentId());
			indicatorVO.setIsParent(getByParentId(indicator.getIndicatorId(),
					null).size() > 0);
			indicatorVOs.add(indicatorVO);
		}
		return indicatorVOs;
	}

	@Override
	public List<IndicatorVO> getByType(String type, Integer isEnable) {
		List<IndicatorVO> list = new ArrayList<IndicatorVO>();
		List<IndicatorEO> indicatorEOs = indicatorDao.getByType(type, isEnable);
		for (IndicatorEO eo : indicatorEOs) {
			IndicatorVO vo = new IndicatorVO();
			AppUtil.copyProperties(vo, eo);
			vo.setIsEnable(eo.getIsEnable() ? "1" : "0");
			//vo.setIsShowSons(eo.getIsShowSons() ? "1" : "0");
			vo.setId(eo.getIndicatorId());
			vo.setPid(eo.getParentId());
			if (type.equals(IndicatorVO.Type.Menu.toString())) {
				vo.setIsParent(getMenu(eo.getIndicatorId(), type, isEnable)
						.size() > 0);
			} else {
				vo.setIsParent(getByParentId(eo.getIndicatorId(), isEnable)
						.size() > 0);
			}
			list.add(vo);
		}
		return list;
	}

	@Override
	public void save(IndicatorEO indicator) {
		setParentIdAndName(indicator);
		saveEntity(indicator);
	}
	@Override
	public void updateEntity(IndicatorEO indicatorEO) {
		super.updateEntity(indicatorEO);
		setParentIdAndName(indicatorEO);
	}

	@Override
	public void save(IndicatorEO indicator, String[] uris) {
		// 保存自身的uri
		Long resourceId = null;
		if (!StringUtils.isEmpty(indicator.getUri())) {
			ResourceEO r = new ResourceEO();
			r.setUri(indicator.getUri());
			// r.setIndicatorId(indicatorId);
			resourceId = resourceService.saveEntity(r);
		}
		// 保存indicator
		indicator.setResourceId(resourceId);
		// 更新父亲的isParent
		if (indicator.getParentId() != null) {
			updateIsParent(indicator.getParentId(), 1);
			//获取所有父节点的名称和id
			setParentIdAndName(indicator);
		}
		Long indicatorId = saveEntity(indicator);
		// 保存资源uri
		if (uris != null && uris.length > 0) {
			List<ResourceEO> res = new ArrayList<ResourceEO>(uris.length);
			for (String uri : uris) {
				uri = uri.trim();
				if (!StringUtils.isEmpty(uri)) {
					ResourceEO r = new ResourceEO();
					r.setUri(uri);
					r.setIndicatorId(indicatorId);
					res.add(r);
				}
			}
			resourceService.saveEntities(res);
		}
	}

	/**
	 * 只有新增的时候才会执行
	 * @param indicator
	 */
	private void setParentIdAndName(IndicatorEO indicator){
		//新增时才设置当前节点的拼音，类似于id,不允许修改
		setPinyin(indicator);

		if (indicator.getParentId() != null && indicator.getParentId().longValue() != 0) {
			List<String> parentIds = new ArrayList<String>();
			List<String> parentNames = new ArrayList<String>();
			List<String> parentNamesPinyin = new ArrayList<String>();
			getParentIdAndName(indicator.getParentId(), parentIds, parentNames, parentNamesPinyin);
			StringBuilder ids = new StringBuilder();
			StringBuilder names = new StringBuilder();
			StringBuilder namesPinyin = new StringBuilder();
			for (int i = parentIds.size() -1; i >= 0; i--) {
				ids.append(parentIds.get(i)).append(",");
				names.append(parentNames.get(i)).append(">");
				namesPinyin.append(parentNamesPinyin.get(i)).append("/");
			}
			if(ids.length() <= 1){
				indicator.setParentIds("");
				indicator.setParentNames("");
				indicator.setParentNamesPinyin("");
			}else{
				indicator.setParentIds(ids.substring(0, ids.length() - 1));
				indicator.setParentNames(names.substring(0, names.length() - 1));
				indicator.setParentNamesPinyin(namesPinyin.substring(0, namesPinyin.length() - 1));
			}
		}
//		if(indicator.getIndicatorId() == null || indicator.getIndicatorId().longValue() == 0){
//			return;
//		}
//		List<IndicatorEO> list = indicatorDao.getByParentIdsLike(indicator.getIndicatorId().toString());
//		if(list == null){
//			return;
//		}
//		//如果存在子节点则更新子节点
//		for(IndicatorEO eo : list){
//			if(StringUtils.isEmpty(eo.getParentIds()) || StringUtils.isEmpty(eo.getParentNames())){
//				continue;
//			}
//			int index = ArrayUtils.indexOf(eo.getParentIds().split(","), indicator.getIndicatorId().toString());
//			String[] name = eo.getParentNames().split(">");
//			name[index] = indicator.getName();
//			eo.setParentNames(StringUtils.join(name,">"));
//
//			String[] namePinyin = eo.getParentNamesPinyin().split("/");
//			namePinyin[index] = indicator.getNamePinyin();
//			eo.setParentNamesPinyin(StringUtils.join(namePinyin,"/"));
//		}
//		indicatorDao.update(list);
	}

	/**
	 * 设置拼音，如果存在首字母相同，则后一个字使用全拼，如果所有结果尝试后均为已存在，则抛出异常
	 * @param eo
	 */
	private void setPinyin(IndicatorEO eo){
		List<String> pinyins = PinYinUtil.cn2SpellList(eo.getName());
		String[] pinyinFirst = PinYinUtil.getFirstSpellArr(pinyins);
		setPinyinloop(eo, pinyins, pinyinFirst, 0);
		StringBuilder sb = new StringBuilder();
		for(String p : pinyinFirst){
			sb.append(p);
		}
		eo.setNamePinyin(sb.toString());
	}

	private void setPinyinloop(IndicatorEO eo, List<String> pinyins, String[] pinyinFirst, int index){
		//同一目录下验证拼音是否重复
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("parentId", eo.getParentId());
		StringBuilder sb = new StringBuilder();
		for(String p : pinyinFirst){
			sb.append(p);
		}
		map.put("namePinyin", sb.toString());
		map.put("recordStatus", RecordStatus.Normal.toString());
		List<IndicatorEO> exit = indicatorDao.getEntities(IndicatorEO.class, map);
		if((exit == null || exit.isEmpty())) {
			return;
		}
		//如果查询到拼音属于当前记录，则直接返回
		if(eo.getIndicatorId() != null && exit.get(0).getIndicatorId().longValue() == eo.getIndicatorId().longValue()){
			//解决更新报错，若缓存中已存在则无法将其他eo保存到数据库
			BeanUtils.copyProperties(eo, exit.get(0));
			eo = exit.get(0);
			return;
		}
		//如果使用全拼依然有存在的目录，则提示错误
		if(index == pinyinFirst.length || index == pinyins.size() ){
			throw new BaseRunTimeException("存在拼音相同的栏目！");
		}
		pinyinFirst[pinyinFirst.length - index - 1] = pinyins.get(pinyins.size() - index - 1);
		setPinyinloop(eo, pinyins, pinyinFirst, index + 1);
	}

	@Override
	public void update(IndicatorEO indicator, String[] uris) {
		// 更新indicator
		setParentIdAndName(indicator);
		updateEntity(indicator);
		Long indicatorId = indicator.getIndicatorId();
		// 获取Indicator原有的资源列表
		List<ResourceEO> srcRes = resourceService
				.getResourcesByIndicatorId(indicatorId);
		Map<String, ResourceEO> map = null;
		if (srcRes != null) {
			map = new HashMap<String, ResourceEO>(srcRes.size());
			for (ResourceEO re : srcRes) {
				map.put(re.getUri(), re);
			}
		}
		// 保存资源uri
		if (uris != null && uris.length > 0) {
			List<ResourceEO> addRes = new ArrayList<ResourceEO>();
			List<ResourceEO> updateRes = new ArrayList<ResourceEO>();
			for (String uri : uris) {
				uri = uri.trim();
				if (!StringUtils.isEmpty(uri)) {
					ResourceEO srcRe = map.get(uri);
					// 需要新增
					if (srcRe == null) {
						ResourceEO r = new ResourceEO();
						r.setUri(uri);
						r.setIndicatorId(indicatorId);
						addRes.add(r);
					} else {
						srcRe.setUri(uri);
						updateRes.add(srcRe);
						// 移除，剩余的表示被删除的
						srcRes.remove(srcRe);
					}
				}
			}
			if (addRes != null && addRes.size() > 0) {
				resourceService.saveEntities(addRes);
			}
			if (updateRes != null && updateRes.size() > 0) {
				resourceService.updateEntities(updateRes);
			}
			if (srcRes != null && srcRes.size() > 0) {
				resourceService.delete(srcRes);
			}
		}
	}

	@Override
	public void updateShortCut(IndicatorEO indicator) {
		setParentIdAndName(indicator);
		updateEntity(indicator);
		// 如果uri改变，那么需要同步跟新ResourceEO
		Long id = indicator.getIndicatorId();
		List<ResourceEO> resources = resourceService
				.getResourcesByIndicatorId(id);
		if (resources != null && resources.size() > 0) {
			for (ResourceEO r : resources) {
				r.setUri(indicator.getUri());
			}
			resourceService.updateResourcesWithIndicatorId(id, resources);
		}
	}

	@Override
	public void saveMenu(IndicatorVO vo) throws BusinessException {
		IndicatorEO eo = new IndicatorEO();
		Boolean isIndex = vo.getIsIndex();
		if (eo.getIsIndex() == null
				|| eo.getIsIndex().booleanValue() != isIndex.booleanValue()) {
			// 每个应用的默认首页只允许有一个，如果设置为默认首页，那么已设置的默认首页将被更新为非默认首页
			if (isIndex) {
				Long parentId = vo.getParentId();
				List<IndicatorEO> indicators = getIndexIndicators(parentId);
				if (indicators != null && indicators.size() > 0) {
					for (IndicatorEO i : indicators) {
						if (i.getIsIndex() == null || i.getIsIndex()) {
							i.setIsIndex(Boolean.FALSE);
							updateEntity(i);
						}
					}
				}
			}
			eo.setIsIndex(isIndex);
		}
		AppUtil.copyProperties(eo, vo);
		eo.setName(vo.getName());
		eo.setUri(vo.getUri());
		eo.setIsEnable(vo.getIsEnable().equals("1"));
		eo.setIsShowSons(vo.getIsShowSons().equals("1"));
		boolean isShown4Developer = vo.getIsShown4Developer().equals("1");
		eo.setIsShown4Developer(isShown4Developer);
		boolean isShown4Admin = vo.getIsShown4Admin().equals("1");
		eo.setIsShown4Admin(isShown4Admin);
		boolean isShown4SystemManager = vo.getIsShown4SystemManager().equals(
				"1");
		eo.setIsShown4SystemManager(isShown4SystemManager);
		boolean isShown4ExternalUser = vo.getIsShown4ExternalUser().equals("1");
		eo.setIsShown4ExternalUser(isShown4ExternalUser);
		setParentIdAndName(eo);
		//
		Long indicatorId = indicatorDao.save(eo);
		// 更新父亲的isParent
		updateIsParent(vo.getParentId(), 1);
		if (null != vo.getUri()) {
			ResourceEO r = new ResourceEO();
			r.setUri(vo.getUri());
			r.setIsEnable(vo.getIsEnable().equals("1"));
			r.setIndicatorId(indicatorId);
			Long resourceId = resourceService.saveEntity(r);
			eo.setResourceId(resourceId);
			indicatorDao.update(eo);
		}
	}

	@Override
	public void updateMenu(IndicatorVO vo) throws BusinessException {
		IndicatorEO eo = getEntity(IndicatorEO.class, vo.getIndicatorId());
		Boolean isIndex = vo.getIsIndex();
		if (eo.getIsIndex() == null
				|| eo.getIsIndex().booleanValue() != isIndex.booleanValue()) {
			// 每个应用的默认首页只允许有一个，如果设置为默认首页，那么已设置的默认首页将被更新为非默认首页
			if (isIndex) {
				Long parentId = vo.getParentId();
				List<IndicatorEO> indicators = getIndexIndicators(parentId);
				if (indicators != null && indicators.size() > 0) {
					for (IndicatorEO i : indicators) {
						// 标志是需要更新
						boolean isNeedUpdate = false;
						if (i.getIsIndex() == null || i.getIsIndex()) {
							i.setIsIndex(Boolean.FALSE);
							isNeedUpdate = true;
						}
						// 开发商不可见，那么设置所有的子菜单和按钮为管理员不可见
						String s = vo.getIsShown4Developer();
						if (s != null && "0".equals(s)) {
							i.setIsShown4Developer(Boolean.FALSE);
							isNeedUpdate = true;
						}
						// 管理员不可见，那么设置所有的子菜单和按钮为管理员不可见
						s = vo.getIsShown4Admin();
						if (s != null && "0".equals(s)) {
							i.setIsShown4Admin(Boolean.FALSE);
							isNeedUpdate = true;
						}
						if (isNeedUpdate) {
							updateEntity(i);
						}
					}
				}
			}
			eo.setIsIndex(isIndex);
		}
		AppUtil.copyProperties(eo, vo);
		setParentIdAndName(eo);
		indicatorDao.update(eo);
		eo.setName(vo.getName());
		eo.setUri(vo.getUri());
		// 是否启用
		boolean isEnable = vo.getIsEnable().equals("1");
		eo.setIsEnable(isEnable);
		eo.setIsShowSons(vo.getIsShowSons().equals("1"));
		boolean isShown4Developer = vo.getIsShown4Developer().equals("1");
		eo.setIsShown4Developer(isShown4Developer);
		boolean isShown4Admin = vo.getIsShown4Admin().equals("1");
		eo.setIsShown4Admin(isShown4Admin);
		boolean isShown4SystemManager = vo.getIsShown4SystemManager().equals(
				"1");
		eo.setIsShown4SystemManager(isShown4SystemManager);
		boolean isShown4User = vo.getIsShown4ExternalUser().equals("1");
		eo.setIsShown4ExternalUser(isShown4User);
		// 当是否启用修改后，需要更新父Indicator的isParent属性
		if (isEnable != eo.getIsEnable()) {
			updateIsParent(eo.getParentId());
		}
		List<ResourceEO> resourceEOs = resourceService
				.getResourcesByIndicatorId(vo.getIndicatorId());
		resourceService.updateResourcesWithIndicatorId(vo.getIndicatorId(),
				resourceEOs);
	}

	@Override
	public List<IndicatorEO> getByParentId(Long parentId, Integer isEnable) {
		return indicatorDao.getTree(parentId, isEnable);
	}

	@Override
	public List<IndicatorVO> getMenu(Long parentId, String type,
			Integer isEnable) {
		List<IndicatorVO> list = new ArrayList<IndicatorVO>();
		if (null == parentId || null == type) {
			return list;
		}
		List<IndicatorEO> eos = indicatorDao.getMenu(parentId,
				new String[] { type }, isEnable);
		for (IndicatorEO eo : eos) {
			IndicatorVO vo = new IndicatorVO();
			AppUtil.copyProperties(vo, eo);
			vo.setIsEnable(eo.getIsEnable() ? "1" : "0");
			vo.setIsShowSons(eo.getIsShowSons() ? "1" : "0");
			vo.setId(eo.getIndicatorId());
			vo.setPid(eo.getParentId());
			vo.setIsParent(indicatorDao.getMenu(eo.getIndicatorId(),
					new String[] { type }, isEnable).size() > 0);
			list.add(vo);
		}
		return list;
	}

	@Override
	public List<IndicatorVO> getButtons(Long parentId, Integer isEnable) {
		List<IndicatorVO> list = new ArrayList<IndicatorVO>();
		String[] types = new String[] {
				IndicatorEO.Type.ToolBarButton.toString(),
				IndicatorEO.Type.Other.toString() };
		List<IndicatorEO> indicatorEOs = indicatorDao.getMenu(parentId, types,
				isEnable);
		for (IndicatorEO indicatorEO : indicatorEOs) {
			IndicatorVO indicatorVO = new IndicatorVO();
			AppUtil.copyProperties(indicatorVO, indicatorEO);
			indicatorVO.setIsEnable(indicatorEO.getIsEnable() ? "1" : "0");
			indicatorVO.setIsShowSons(indicatorEO.getIsShowSons() ? "1" : "0");
			indicatorVO.setId(indicatorEO.getIndicatorId());
			indicatorVO.setPid(indicatorEO.getParentId());
			indicatorVO.setIsParent(getByParentId(indicatorEO.getIndicatorId(),
					isEnable).size() > 0);
			list.add(indicatorVO);
		}
		return list;
	}

	@Override
	public void delete(Long indicatorId) {
		List<IndicatorPermissionEO> indicatorPermissions = indicatorPermissionService
				.getByIndicatorId(indicatorId);
		if (null != indicatorPermissions && indicatorPermissions.size() > 0) {
			indicatorPermissionService.delete(indicatorPermissions);
		}
		List<IndicatorEO> indicators = getByParentId(indicatorId, null);
		if (null != indicators && indicators.size() > 0) {
			for (IndicatorEO indicatorEO : indicators) {
				List<ResourceEO> resources = resourceService
						.getResourcesByIndicatorId(indicatorEO.getIndicatorId());
				resourceService.delete(resources);
			}
			indicatorDao.delete(indicators);
		}
		IndicatorEO indicatorEO = indicatorDao.getEntity(IndicatorEO.class,
				indicatorId);
		// 逻辑删除
		delete(indicatorEO);
		// 更新父Indicator的isParent属性
		if (indicatorEO.getParentId() != null) {
			updateIsParent(indicatorEO.getParentId());
		}
	}

	@Override
	public List<IndicatorEO> getSuperParent() {
		return indicatorDao.getSuperParent();
	}

	@Override
	public List<IndicatorVO> getByRole(Long roleId) {
		List<IndicatorVO> indicatorVOs = new ArrayList<IndicatorVO>();
		List<IndicatorPermissionEO> ips = indicatorPermissionService
				.getByRoleId(roleId);
		for (IndicatorPermissionEO indicatorPermissionEO : ips) {
			IndicatorVO indicatorVO = new IndicatorVO();

			IndicatorEO indicator = indicatorDao.getEntity(IndicatorEO.class,
					indicatorPermissionEO.getIndicatorId());
			AppUtil.copyProperties(indicatorVO, indicator);
			indicatorVO.setId(indicator.getIndicatorId());
			indicatorVO.setPid(indicator.getParentId());
			indicatorVO
					.setIsParent(getByParentId(indicator.getIndicatorId(), 1)
							.size() > 0);
			indicatorVO.setChecked(true);
			indicatorVOs.add(indicatorVO);
		}

		return indicatorVOs;
	}

	@Override
	public void saveButton(IndicatorVO indicatorVO) throws BusinessException {
		IndicatorEO indicatorEO = new IndicatorEO();
		AppUtil.copyProperties(indicatorEO, indicatorVO);
		indicatorEO.setIsEnable(indicatorVO.getIsEnable().equals("1"));
		setParentIdAndName(indicatorEO);
		Long indicatorId = indicatorDao.save(indicatorEO);
		// 更新父亲的isParent
		// updateIsParent(indicatorVO.getParentId(),1);
		resourceService.saveResourcesWithIndicatorId(indicatorId,
				indicatorVO.getResources());
	}

	@Override
	public void updateButton(IndicatorVO indicatorVO) throws BusinessException {
		IndicatorEO indicatorEO = new IndicatorEO();
		AppUtil.copyProperties(indicatorEO, indicatorVO);
		boolean isEnable = indicatorVO.getIsEnable().equals("1");
		indicatorEO.setIsEnable(isEnable);
		setParentIdAndName(indicatorEO);
		indicatorDao.update(indicatorEO);
		// 当是否启用修改后，需要更新父Indicator的isParent属性
		if (isEnable != indicatorEO.getIsEnable()) {
			updateIsParent(indicatorEO.getParentId());
		}
		resourceService.updateResourcesWithIndicatorId(
				indicatorVO.getIndicatorId(), indicatorVO.getResources());
	}

	@Override
	public void enable(Long indicatorId) throws BusinessException {
		IndicatorEO indicatorEO = indicatorDao.getEntity(IndicatorEO.class,
				indicatorId);
		if (indicatorEO.getIsEnable()) {
			indicatorEO.setIsEnable(false);
			resourceService.saveEnable(indicatorId, false);
		} else {
			indicatorEO.setIsEnable(true);
			resourceService.saveEnable(indicatorId, true);
		}
		indicatorDao.update(indicatorEO);
	}

	@Override
	public IndicatorVO getById(Long indicatorId) {
		IndicatorVO indicatorVO = null;
		IndicatorEO indicator = indicatorDao.getEntity(IndicatorEO.class,
				indicatorId);
		if (indicator != null) {
			indicatorVO = new IndicatorVO();
			AppUtil.copyProperties(indicatorVO, indicator);
			indicatorVO.setIsEnable(indicator.getIsEnable() ? "1" : "0");
			indicatorVO.setIsShowSons(indicator.getIsShowSons() ? "1" : "0");
			indicatorVO
					.setIsShown4Developer(indicator.getIsShown4Developer() ? "1"
							: "0");
			indicatorVO.setIsShown4Admin(indicator.getIsShown4Admin() ? "1"
					: "0");
			indicatorVO.setIsShown4SystemManager(indicator
					.getIsShown4SystemManager() ? "1" : "0");
			indicatorVO.setIsShown4ExternalUser(indicator
					.getIsShown4ExternalUser() ? "1" : "0");
			indicatorVO.setHasMessage(indicator.getHasMessage() ? "1" : "0");
			indicatorVO.setId(indicator.getIndicatorId());
			indicatorVO.setPid(indicator.getParentId());
			Boolean isShowInDeskTop = indicator.getIsShowInDesktop();
			if (isShowInDeskTop != null) {
				indicatorVO
						.setIsShowInDesktop(indicator.getIsShowInDesktop() ? "1"
								: "0");
			}
			indicatorVO.setIsParent(getByParentId(indicator.getIndicatorId(),
					null).size() > 0);
			// // 查询资源
			// List<ResourceEO> resourceEOs = resourceService
			// .getResourcesByIndicatorId(indicatorId);
			// ResourceEO resourceEO =
			// resourceService.getEntity(ResourceEO.class,
			// indicator.getResourceId());
			// if (null != resourceEOs && resourceEOs.size() > 0) {
			// resourceEOs.remove(resourceEO);
			// indicatorVO.setResources(resourceEOs);
			// }
		}
		return indicatorVO;
	}

	@Override
	public IndicatorEO getPhysicalById(Long indicatorId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("indicatorId", indicatorId);
		return indicatorDao.getEntity(IndicatorEO.class, map);
	}

	@Override
	public IndicatorEO getIndicatorEOById(Long indicatorId) {
		return indicatorDao.getEntity(IndicatorEO.class, indicatorId);
	}

	@Override
	public Integer getMaxSortNum(String type, Long parentId) {
		Integer i = indicatorDao.getMaxSortNum(type, parentId);
		if (null == i) {
			i = 0;
		}
		return i + 2;
	}

	@Override
	public List<IndicatorEO> getButtonByParentId(Long parentId) {

		return indicatorDao.getButtonByParentId(parentId);
	}

	@Override
	public List<IndicatorEO> getSystemIndicators(Long userId, Long parentId,
			String type) {
		UserEO user = userService.getEntity(UserEO.class, userId);
		Long[] roleIds = null;
		List<IndicatorEO> indicators = null;
		if (!AdminUidVO.superAdminUid.equals(user.getUid())) {
			List<RoleEO> roles = roleService.getRoles(userId,
					RoleEO.Type.System.toString());
			if (roles != null && roles.size() > 0) {
				// 获取所有角色的主键构建一个数组
				roleIds = new Long[roles.size()];
				// 是否包含系统管理员标记
				for (int i = 0; i < roles.size(); i++) {
					RoleEO role = roles.get(i);
					roleIds[i] = role.getRoleId();
				}
				// 根据用户的角色和需要获取的桌面图标权限
				indicators = indicatorDao.getIndicatorsByRole(roleIds,
						parentId, type, null, Boolean.FALSE);
			}
		} else {
			// admin特殊处理
			indicators = indicatorDao.getSubIndicatorByType(parentId, type,
					Boolean.TRUE);
		}
		List<IndicatorEO> targets = new ArrayList<IndicatorEO>();
		if (indicators != null && indicators.size() > 0) {
			for (IndicatorEO indicator : indicators) {
				long indicatorId = indicator.getIndicatorId().longValue();
				// targets中是否已包含主键相同的对象
				boolean isContains = false;
				int size = targets.size();
				for (int i = 0; i < size; i++) {
					IndicatorEO target = targets.get(i);
					long targetId = target.getIndicatorId().longValue();
					if (indicatorId == targetId) {
						isContains = true;
						break;
					}
				}
				if (!isContains) {
					targets.add(indicator);
				}
			}
		}
		return targets;
	}

	@Override
	public List<IndicatorEO> getIndicator4DeveloperAndSuperAdmin(Long parentId,
			String type, boolean isOwnedByDeveloper) {
		return indicatorDao.getIndicator4DeveloperAndSuperAdmin(parentId, type,
				isOwnedByDeveloper);
	}

	@Override
	public List<IndicatorEO> getAppIndicators(Long userId, Long organId,
			Long parentId, String type) {
		if (userId == null) {
			throw new NullPointerException();
		}
		List<IndicatorEO> indicators = null;
		// 获取用户本单位和公共的角色
		List<RoleEO> roles = roleService.getRoles(userId, organId,
				new String[] { RoleEO.Type.Private.toString(),
						RoleEO.Type.Public.toString(),RoleEO.Type.System.toString() });
		if (roles != null && roles.size() > 0) {
			// 获取所有角色的主键构建一个数组
			Long[] roleIds = new Long[roles.size()];
			for (int i = 0; i < roles.size(); i++) {
				roleIds[i] = roles.get(i).getRoleId();
			}
			// 验证是否是外部单位,只用应用才需要判断
			Boolean isExternal = null;
			if (IndicatorEO.Type.Shortcut.toString().equals(type)
					|| parentId == null) {
				OrganEO organ = organServie.getEntity(OrganEO.class, organId);
				isExternal = organ.getIsExternal() != null
						&& organ.getIsExternal();
			}
			// 根据用户的角色和需要获取的桌面图标权限
			indicators = indicatorDao.getIndicatorsByRole(roleIds, parentId,type, true, isExternal);
		}
		List<IndicatorEO> targets = new ArrayList<IndicatorEO>();
		if (indicators != null && indicators.size() > 0) {
			for (IndicatorEO i : indicators) {
				// 去除重复内容,已经复写了IndicatorEO的equals方法
				if (!targets.contains(i)) {
					targets.add(i);
				}
			}
		}
		return targets;
	}

	@Override
	public List<IndicatorEO> getIndicatorsByRole(Long roleId, Long parentId,
			String[] types) {
		return indicatorDao.getIndicatorsByRole(roleId, parentId, types);
	}

	@Override
	public List<IndicatorEO> getDesktopIndicators(Long userId, Long organId,
			String[] codes) {
		// 入参验证
		if (userId == null || codes == null || codes.length <= 0) {
			throw new NullPointerException();
		}
		List<IndicatorEO> indicators = null;
		// 获取用户本单位和公共的角色
		List<RoleEO> roles = roleService.getRoles(userId, organId,
				new String[] { RoleEO.Type.Private.toString(),
						RoleEO.Type.Public.toString(),RoleEO.Type.System.toString() });
		if (roles != null && roles.size() > 0) {
			// 获取所有角色的主键构建一个数组
			Long[] roleIds = new Long[roles.size()];
			for (int i = 0; i < roles.size(); i++) {
				roleIds[i] = roles.get(i).getRoleId();
			}
			// 根据用户的角色和需要获取的桌面图标权限
			indicators = indicatorDao.getDesktopIndicators(roleIds, codes);
		}
		return indicators;
	}

	@Override
	public List<IndicatorEO> getSystemIndicators(Long parentId, String[] types,
			Boolean isEnable) {
		return indicatorDao.getSystemIndicators(parentId, types, isEnable);
	}

	@Override
	public List<IndicatorEO> getAppIndicators(Long parentId, String[] types,
			Boolean isEnable, Boolean isVisibleBySystemManager) {
		return indicatorDao.getAppIndicators(parentId, types, isEnable);
	}

	/**
	 * 更新Indicator的isParent属性，
	 * 
	 * @param indicatorId
	 * @param isParent
	 */
	private void updateIsParent(Long indicatorId, Integer isParent) {
		List<String> parentIds = new ArrayList<String>();
		// 更新父亲的isParent
		IndicatorEO parent = getEntity(IndicatorEO.class, indicatorId);
		if (parent.getIsParent() != null
				&& parent.getIsParent().intValue() != isParent) {
			parent.setIsParent(isParent);
			updateEntity(parent);
		}
	}

	/**
	 * 更新Indicator的isParent属性，
	 *
	 * @param indicatorId
	 */
	private void getParentIdAndName(Long indicatorId, List<String> parentIds, List<String> parentNames, List<String> parentNamesPinyin) {
		// 更新父亲的isParent
		IndicatorEO parent = getEntity(IndicatorEO.class, indicatorId);
		if(parent == null){
			return;
		}
		//站点名称不算在内
		if(parent.getType().equals(IndicatorEO.Type.CMS_Site.toString())){
			return;
		}
		parentIds.add(parent.getIndicatorId().toString());
		parentNames.add(parent.getName());
		parentNamesPinyin.add(parent.getNamePinyin());
		//菜单为1为根节点，不列入菜单
		if(parent.getParentId() == null || parent.getParentId() == 0l || parent.getParentId() == 1l){
			return;
		}
		getParentIdAndName(parent.getParentId(), parentIds, parentNames, parentNamesPinyin);
	}

	/**
	 * 初始化父节点完整路径
	 */
	@Override
	public void initParentPath(){
		Map<String, Object> param= new HashMap<String, Object>();
		param.put("recordStatus", RecordStatus.Normal.toString());
		List<IndicatorEO> list = indicatorDao.getEntities(IndicatorEO.class, param);


		//先保存当前节点的拼音
		for(IndicatorEO indicator : list){
			setPinyin(indicator);
		}
		indicatorDao.save(list);
		//再保存父节点路径
		for(IndicatorEO indicator : list){
			List<String> parentIds = new ArrayList<String>();
			List<String> parentNames = new ArrayList<String>();
			List<String> parentNamesPinyin = new ArrayList<String>();
			if(indicator.getParentId() == null || indicator.getParentId() == 0l){
				continue;
			}
			getParentIdAndName(indicator.getParentId(), parentIds, parentNames, parentNamesPinyin);
			StringBuilder ids = new StringBuilder();
			StringBuilder names = new StringBuilder();
			StringBuilder namesPinyin = new StringBuilder();
			for (int i = parentIds.size() -1; i >= 0; i--) {
				ids.append(parentIds.get(i)).append(",");
				names.append(parentNames.get(i)).append(">");
				namesPinyin.append(parentNamesPinyin.get(i)).append("/");
			}
			if(ids.length() <= 1){
				indicator.setParentIds("");
				indicator.setParentNames("");
				indicator.setParentNamesPinyin("");
			}else{
				indicator.setParentIds(ids.substring(0, ids.length() - 1));
				indicator.setParentNames(names.substring(0, names.length() - 1));
				indicator.setParentNamesPinyin(namesPinyin.substring(0, namesPinyin.length() - 1));
			}
		}
		indicatorDao.save(list);
	}

	/**
	 * 更新isParnet
	 */
	private void updateIsParent(Long parentId) {
		if (parentId == null || parentId <= 0) {
			return;
		}
		long count = indicatorDao.getCountByParentId(parentId, true);
		IndicatorEO i = getEntity(IndicatorEO.class, parentId);
		if (count > 0) {
			if (i.getIsParent() == null || i.getIsParent().intValue() == 0) {
				i.setIsParent(1);
				updateEntity(i);
			}
		} else {
			if (i.getIsParent() != null && i.getIsParent().intValue() == 1) {
				i.setIsParent(0);
				updateEntity(i);
			}
		}
	}

	/**
	 * 用于存放IndicatorEO的host
	 */
	private static Map<String, String> hosts = new HashMap<String, String>(30);

	@Override
	public String getHost(String systemCode) {
		String host = hosts.get(systemCode);
		if (host == null) {
			IndicatorEO i = indicatorDao.getIndicator(
					IndicatorEO.Type.Shortcut.toString(), systemCode);
			if (i != null) {
				host = i.getHost();
				hosts.put(systemCode, host);
			}
		}
		return host;
	}

	@Override
	public List<IndicatorEO> getAllShortcuts() {
		return indicatorDao.getAllShortcuts();
	}

	@Override
	public List<IndicatorEO> getIndexIndicators(Long parentId) {
		if (parentId == null) {
			throw new NullPointerException();
		}
		return indicatorDao.getIndexIndicators(parentId);
	}

	@Override
	public List<Long> getIndicatorIds(Long roleId) {
		return indicatorPermissionService.getIndicatorIds(roleId);
	}

	@Override
	public IndicatorEO getIndicatorBySystemCode(String systemCode) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("systemCode", systemCode);
		map.put("type", IndicatorEO.Type.Shortcut.toString());
		map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
		return getEntity(IndicatorEO.class, map);
	}

	@Override
	public List<IndicatorEO> getIndicator4Developer(Long parentId, String type) {
		return indicatorDao.getIndicator4Developer(parentId, type);
	}

	@Override
	public List<IndicatorEO> getIndicator4Admin(Long parentId, String type) {
		return indicatorDao.getIndicator4Admin(parentId, type);
	}

}
