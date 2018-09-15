package cn.lonsun.rbac.internal.util;

import org.springframework.beans.BeanUtils;

import cn.lonsun.common.vo.TreeNodeVO;
import cn.lonsun.common.vo.TreeNodeVO.Icon;
import cn.lonsun.ldap.vo.OrganNodeVO;
import cn.lonsun.rbac.internal.entity.OrganEO;

/**
 * 组织架构树节点构建工具
 *
 * @author xujh
 * @version 1.0
 * 2015年2月5日
 *
 */
public class OrganNodeUtils {

	public static OrganNodeVO getOrganNode(OrganEO organ,int targetType){
		OrganNodeVO node = new OrganNodeVO();
		BeanUtils.copyProperties(organ, node);
		node.setHasOrgans(organ.getHasOrgans() == 1 ? true : false);
		node.setId(organ.getOrganId());
		node.setPid(organ.getParentId());
		if (Integer.valueOf(organ.getHasOrgans()) == 1) {
			node.setIsParent(true);
		}
		// 节点类型处理
		String type = organ.getType();
		if (type.equals(TreeNodeVO.Type.Organ.toString())) {
			if (Integer.valueOf(organ.getIsFictitious()) == 1) {
				node.setNodeType(TreeNodeVO.Type.VirtualNode.toString());
				node.setIcon(Icon.VirtualNode.getValue());
			} else {
				node.setNodeType(TreeNodeVO.Type.Organ.toString());
				node.setIcon(Icon.Organ.getValue());
			}
		} else {
			if (Integer.valueOf(organ.getIsFictitious()) == 1) {
				node.setNodeType(TreeNodeVO.Type.Virtual.toString());
				node.setIcon(Icon.Virtual.getValue());
			} else {
				node.setNodeType(TreeNodeVO.Type.OrganUnit.toString());
				node.setIcon(Icon.OrganUnit.getValue());
			}
		}
		node.setIsParent(isParent(organ, targetType));
		return node;
	}
	
	/**
	 * 判断节点在树中是否是满足查询条件的父节点
	 * 
	 * @param organ
	 * @param types
	 * @param targetType
	 * @return
	 */
	public static boolean isParent(OrganEO organ, int targetType) {
		//是否有虚节点
		boolean hasVirtualNodes = organ.getHasVirtualNodes()!=null&& organ.getHasVirtualNodes()==1;
		boolean hasOrgans = organ.getHasOrgans() == 1;
		boolean hasOrganUnits = organ.getHasOrganUnits() == 1
				&& organ.getIsFictitious() == 0;
		boolean hasVirtuals = organ.getHasOrganUnits() == 1
				&& organ.getIsFictitious() == 1;
		boolean hasPersons = organ.getHasPersons() == 1;
		boolean hasRoles = organ.getHasRoles() == 1;
		boolean isParent = false;
		switch (targetType) {
		case 0:
			if (hasOrgans) {
				isParent = true;
			}
			break;
		case 1:
			if (hasOrgans || hasOrganUnits) {
				isParent = true;
			}
			break;
		case 2:
			if (hasOrgans || hasOrganUnits || hasVirtuals) {
				isParent = true;
			}
			break;
		case 3:
			if (hasOrgans || hasOrganUnits || hasVirtuals || hasPersons) {
				isParent = true;
			}
			break;
		case 4:
			if (hasVirtualNodes||hasOrgans || hasRoles) {
				isParent = true;
			}
			break;
		}
		return isParent;
	}
}
