package cn.lonsun.base.util;

import cn.lonsun.common.vo.TreeNodeVO.Type;


/**
 * 树展示组织架构工具
 *
 * @author xujh
 * @version 1.0
 * 2015年1月31日
 *
 */
public class TreeNodeUtil {

	/**
	 * 根据scope返回真实需要获取的节点类型数组，从前端转换到后端需要的数据
	 * 
	 * @param scope
	 * @return
	 */
	public static String[] getNodeTypes(int scope) {
		//节点类型
		String virtualOrgan = Type.VirtualNode.toString();
		String organ = Type.Organ.toString();
		String organUnit = Type.OrganUnit.toString();
		String virtual = Type.Virtual.toString();
		String person = Type.Person.toString();
		String[] noteTypes = null;
		switch (scope) {
		case 0:// 0- 全显示
			noteTypes = new String[] {virtualOrgan,organ,organUnit,virtual,person};
			break;
		case 1:// 1- 只显示单位、部门/处室和虚拟处室
			noteTypes = new String[] {virtualOrgan,organ,organUnit,virtual};
			break;
		case 2:// 2- 只显示单位和部门/处室
			noteTypes = new String[] {virtualOrgan,organ,organUnit};
			break;
		case 3:// 3- 只显示单位
			noteTypes = new String[] {virtualOrgan,organ};
			break;
		default:
			noteTypes = new String[] {virtualOrgan,organ,organUnit,virtual,person};
			break;
		}
		return noteTypes;
	}
}
