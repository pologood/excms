package cn.lonsun.ldap.internal.util;

import org.springframework.util.StringUtils;

import cn.lonsun.common.vo.TreeNodeVO;


/**
 * 枚举验证类
 * @author xujh
 *
 */
public class EnumUtil {
	
	/**
	 * 验证组织/单位下的枚举值是否合法，如果不在Type范围内，那么返回false
	 * @param type
	 * @return
	 */
	public static boolean isOrganTypeIllegal(String type){
		boolean isIllegal = false;
		if(!StringUtils.isEmpty(type)){
			TreeNodeVO.Type[] types = TreeNodeVO.Type.values();
			for(TreeNodeVO.Type t:types){
				if(type.equals(t.toString())){
					isIllegal = true;
					break;
				}
			}
		}
		return isIllegal;
	}
}
