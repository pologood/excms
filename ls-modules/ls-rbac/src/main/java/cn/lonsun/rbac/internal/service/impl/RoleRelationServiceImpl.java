package cn.lonsun.rbac.internal.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.entity.AMockEntity.RecordStatus;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.rbac.internal.dao.IRoleRelationDao;
import cn.lonsun.rbac.internal.entity.RoleEO;
import cn.lonsun.rbac.internal.entity.RoleRelationEO;
import cn.lonsun.rbac.internal.service.IRoleRelationService;
import cn.lonsun.rbac.internal.service.IRoleService;

/**
 * 角色关系接口实现
 *
 * @author xujh
 * @version 1.0
 * 2015年5月27日
 *
 */
@Service
public class RoleRelationServiceImpl extends MockService<RoleRelationEO> implements
		IRoleRelationService {
	@Autowired
	private IRoleRelationDao roleRelationDao;
	@Autowired
	private IRoleService roleService;

	@Override
	public Pagination getPage(Long roleId, PageQueryVO query) {
		return roleRelationDao.getPage(roleId, query);
	}

	@Override
	public void deleteByRoleIdAndTargetRoleId(Long roleId) {
		roleRelationDao.deleteByRoleIdAndTargetRoleId(roleId);
	}

	@Override
	public void saveRelations(Long roleId, List<Long> targetRoleIds, String type) {
		//1.已存在的不能再保存
		//2.继承只能有二级关系，不能多级继承
		//3.不能跟自己绑定
		if(targetRoleIds.contains(roleId)){
			targetRoleIds.remove(roleId);
		}
		if(targetRoleIds.size()<=0){
			return;
		}
		//已存在的继承关系过滤
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("roleId", roleId);
		map.put("targetRoleId", targetRoleIds);
		map.put("recordStatus", RecordStatus.Normal.toString());
		List<RoleRelationEO> rrs = getEntities(RoleRelationEO.class, map);
		if(rrs!=null&&rrs.size()>0){
			for(RoleRelationEO rr:rrs){
				Long targetRoleId = rr.getTargetRoleId();
				targetRoleIds.remove(targetRoleId);
			}
		}
		//不能形成循环继承
		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("roleId", targetRoleIds);
		map2.put("targetRoleId", roleId);
		map2.put("recordStatus", RecordStatus.Normal.toString());
		List<RoleRelationEO> rrs2 = getEntities(RoleRelationEO.class, map2);
		if(rrs2!=null&&rrs2.size()>0){
			List<Long> roleIds = new ArrayList<Long>(rrs2.size());
			for(RoleRelationEO rr:rrs2){
				roleIds.add(rr.getRoleId());
			}
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("roleId", roleIds);
			params.put("recordStatus", RecordStatus.Normal.toString());
			List<RoleEO> roles = roleService.getEntities(RoleEO.class, params);
			String roleNames = "";
			if(roles!=null&&roles.size()>0){
				for(RoleEO role:roles){
					if(StringUtils.isEmpty(roleNames)){
						roleNames = role.getName();
					}else{
						roleNames = roleNames+","+role.getName();
					}
				}
				throw new BaseRunTimeException(TipsMode.Message.toString(), "角色不支持循环管理("+roleNames+")");
			}
		}
		//新增角色关系
		List<RoleRelationEO> relations = new ArrayList<RoleRelationEO>();
		for(Long targetRoleId:targetRoleIds){
			if(targetRoleId==null){
				continue;
			}
			RoleRelationEO relation = new RoleRelationEO();
			relation.setRoleId(roleId);
			relation.setTargetRoleId(targetRoleId);
			relations.add(relation);
		}
		saveEntities(relations);
	}

	@Override
	public List<Long> getRoleIdsByRelationType(Long roleId,String type) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("roleId", roleId);
		params.put("type", type);
		params.put("recordStatus", RecordStatus.Normal.toString());
		List<RoleRelationEO> rrs = getEntities(RoleRelationEO.class, params);
		List<Long> roleIds = null;
		//遍历出角色关联的角色的主键集合
		if(rrs!=null&&rrs.size()>0){
			roleIds = new ArrayList<Long>(rrs.size());
			for(RoleRelationEO rr:rrs){
				if(rr!=null){
					roleIds.add(rr.getTargetRoleId());
				}
			}
		}
		return roleIds;
	}

}
