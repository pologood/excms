package cn.lonsun.system.member.internal.service.impl;

import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.system.member.internal.dao.IMemberDao;
import cn.lonsun.system.member.internal.entity.MemberEO;
import cn.lonsun.system.member.internal.service.IMemberService;
import cn.lonsun.system.member.vo.MemberQueryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("memberService")
public class MemberServiceImpl extends MockService<MemberEO> implements IMemberService {

	@Autowired
	private IMemberDao memberDao;

	@Override
	public Pagination getPage(MemberQueryVO query) {
		return memberDao.getPage(query);
	}

	@Override
	public Object updateStatus(Long[] ids, Integer status) {
		if(ids != null && ids.length > 0 ){
			List<MemberEO> list = new ArrayList<MemberEO>();
			for(Long id:ids){
				MemberEO member = getEntity(MemberEO.class, id);
				if(member != null){
					member.setStatus(status);
					list.add(member);
				}
			}
			if(list != null && list.size()>0)memberDao.update(list);
		}
		return null;
	}

	@Override
	public Boolean isExistUid(String uid,Long siteId, Long id) {
		Boolean hasUid = false;
		Long count = memberDao.getUidCount(uid,siteId,id);
		if (count != null && count > 0) {
			hasUid = true;
		}
		return hasUid;
	}

	@Override
	public Boolean isExistPhone(String phone, Long siteId) {
		Boolean hasPhone = false;
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
		map.put("phone", phone);
		map.put("siteId", siteId);
		List<MemberEO> members = getEntities(MemberEO.class, map);
		if(members != null && members.size() > 0){
			hasPhone = true;
		}
		return hasPhone;
	}

	@Override
	public Boolean isExistPhone(String phone, Long siteId,Long id) {
		Boolean hasPhone = false;
		Map<String,Object> map = new HashMap<String,Object>();
		Long count = memberDao.isExistPhone(phone,siteId,id);
		if (count != null && count > 0) {
			hasPhone = true;
		}
		return hasPhone;
	}

	@Override
	public MemberEO getMemberByUid(String uid, Long siteId) {
		MemberEO member = null;
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
		map.put("uid", uid);
		map.put("siteId", siteId);
		List<MemberEO> members = getEntities(MemberEO.class, map);
		if(members != null && members.size() > 0){
			member = members.get(0);
		}
		return member;
	}

	@Override
	public List<MemberEO> getByNumber(String number, Long siteId) {
		return memberDao.getByNumber(number,siteId);
	}

	@Override
	public void insertSql(String sql, Object[] objects) {
		memberDao.executeUpdateBySql(sql,objects);
	}

	@Override
	public Object getMembers(Integer type,Long siteId) {
		return memberDao.getMembers(type,siteId);
	}
}
