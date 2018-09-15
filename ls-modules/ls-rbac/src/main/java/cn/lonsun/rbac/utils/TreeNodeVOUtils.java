package cn.lonsun.rbac.utils;

import cn.lonsun.common.vo.TreeNodeVO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.entity.RoleAssignmentEO;
import cn.lonsun.rbac.internal.entity.RoleEO;

public class TreeNodeVOUtils {
	
	/**
	 * 获取单位节点-角色树
	 *
	 * @param organ
	 * @return
	 */
	public static TreeNodeVO getUnitNode4Roles(OrganEO organ){
		TreeNodeVO node = new TreeNodeVO();
		node.setId(organ.getOrganId()+"");
		node.setName(organ.getName());
		node.setType(TreeNodeVO.Type.Organ.toString());
		node.setUnitId(organ.getOrganId());
		node.setUnitName(organ.getName());
		node.setPid(organ.getParentId()==null?null:organ.getParentId()+"");
		node.setIcon(TreeNodeVO.Icon.Organ.getValue());
		boolean hasVirtualNodes = organ.getHasVirtualNodes()!=null&&organ.getHasVirtualNodes()==1;
		if(hasVirtualNodes||organ.getHasOrgans()==1||organ.getHasRoles()==1){
			node.setIsParent(Boolean.TRUE);
		}
		return node;
	}
	
	/**
	 * 获取角色节点-角色树
	 *
	 * @param organ
	 * @return
	 */
	public static TreeNodeVO getRoleNode(RoleAssignmentEO assignment){
		TreeNodeVO node = new TreeNodeVO();
		node.setId(assignment.getRoleAssignmentId()+"");
		node.setName(assignment.getRoleName());
		node.setType(assignment.getRoleType());
		node.setRoleId(assignment.getRoleId());
		node.setRoleName(assignment.getRoleName());
		node.setPid(assignment.getUnitId()+"");
		node.setIcon(TreeNodeVO.Icon.Role.getValue());
		return node;
	}
	
	/**
	 * 获取角色节点-角色树
	 *
	 * @param organ
	 * @return
	 */
	public static TreeNodeVO getRoleNode(Long unitId,RoleEO role){
		TreeNodeVO node = new TreeNodeVO();
		node.setId(unitId.toString()+"_"+role.getRoleId().toString());
		node.setName(role.getName());
		node.setType(role.getType());
		node.setRoleId(role.getRoleId());
		node.setRoleName(role.getName());
		node.setRoleCode(role.getCode());
		node.setPid(unitId.toString());
		node.setIcon(TreeNodeVO.Icon.Role.getValue());
		return node;
	}

}
