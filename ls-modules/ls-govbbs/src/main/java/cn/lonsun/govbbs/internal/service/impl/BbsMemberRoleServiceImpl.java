package cn.lonsun.govbbs.internal.service.impl;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.govbbs.internal.dao.IBbsMemberRoleDao;
import cn.lonsun.govbbs.internal.entity.BbsMemberRoleEO;
import cn.lonsun.govbbs.internal.service.IBbsMemberRoleService;
import cn.lonsun.govbbs.util.BbsMemberRoleUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("bbsMemberRoleService")
public class BbsMemberRoleServiceImpl extends MockService<BbsMemberRoleEO> implements IBbsMemberRoleService {

	@Autowired
	private IBbsMemberRoleDao memberRoleDao;

	@Override
	public List<BbsMemberRoleEO> getBbsMemberRoleMap(Long siteId) {
		List<BbsMemberRoleEO> memberRoleEOs = BbsMemberRoleUtil.getSiteBbsMemberRole(siteId);
		if(memberRoleEOs == null){
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("siteId", siteId);
			params.put("recordStatus", BbsMemberRoleEO.RecordStatus.Normal.toString());
			memberRoleEOs = getEntities(BbsMemberRoleEO.class, params);
			if(memberRoleEOs != null){
				BbsMemberRoleUtil.putMemberRole(siteId, memberRoleEOs);
			}
		}
		return memberRoleEOs;
	}

	@Override
	public BbsMemberRoleEO getMemberRoleByPoints(Integer memberPoints, Long siteId) {
		return memberRoleDao.getMemberRoleByPoints(memberPoints,siteId);
	}

	@Override
	public List<BbsMemberRoleEO> BbsMemberRoleList(Long siteId) {
		return memberRoleDao.BbsMemberRoleList(siteId);
	}
}
