package cn.lonsun.govbbs.util;


import cn.lonsun.govbbs.internal.entity.BbsMemberRoleEO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BbsMemberRoleUtil {

	public static Map<Long,List<BbsMemberRoleEO>> roleEOMap = new HashMap<Long,List<BbsMemberRoleEO>>();

	public static void  putMemberRole(Long siteId,List<BbsMemberRoleEO> memberRoleEO){
		roleEOMap.put(siteId, memberRoleEO);
	}

	public static List<BbsMemberRoleEO> getSiteBbsMemberRole(Long siteId){
		List<BbsMemberRoleEO> memberRoleEOs = null;
		if(roleEOMap !=null){
			memberRoleEOs = roleEOMap.get(siteId);
		}
		return memberRoleEOs;
	}


	
}
