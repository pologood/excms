package cn.lonsun.system.member.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.system.member.internal.entity.MemberEO;
import cn.lonsun.system.member.vo.MemberQueryVO;

import java.util.List;

public interface IMemberService extends IMockService<MemberEO> {

	Pagination getPage(MemberQueryVO query);

	Object updateStatus(Long[] ids, Integer status);

	Boolean isExistUid(String uid, Long siteId, Long id);
	
	Boolean isExistPhone(String phone, Long siteId);

	MemberEO getMemberByUid(String uid, Long siteId);

	List<MemberEO> getByNumber(String number, Long siteId);

	void insertSql(String sql, Object[] objects);

	Boolean isExistPhone(String phone, Long siteId, Long id);

	Object getMembers(Integer type, Long siteId);
}
