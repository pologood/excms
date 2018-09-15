package cn.lonsun.system.member.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.system.member.internal.entity.MemberEO;
import cn.lonsun.system.member.vo.MemberQueryVO;

import java.util.List;

public interface IMemberDao extends IMockDao<MemberEO> {

	Pagination getPage(MemberQueryVO query);

	Long getUidCount(String uid, Long siteId, Long id);

	List<MemberEO> getByNumber(String number, Long siteId);

	Long isExistPhone(String phone, Long siteId, Long id);

	Object getMembers(Integer type, Long siteId);
}
