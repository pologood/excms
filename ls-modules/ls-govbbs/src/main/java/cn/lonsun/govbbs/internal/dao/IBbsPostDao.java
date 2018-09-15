package cn.lonsun.govbbs.internal.dao;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.govbbs.internal.entity.BbsPostEO;
import cn.lonsun.govbbs.internal.vo.ExportPostVO;
import cn.lonsun.govbbs.internal.vo.MemberCountVO;
import cn.lonsun.govbbs.internal.vo.PostQueryVO;

import java.util.List;

public interface IBbsPostDao extends IBaseDao<BbsPostEO> {

	Pagination getPage(PostQueryVO query);

	Pagination getUnitPlate(PostQueryVO query);

	Pagination getMemberStatic(PostQueryVO query);

	Pagination getUnitList(PostQueryVO query, String dn);

	Pagination getStaticUnitPlate(PostQueryVO query, String dn);

	Pagination getUnitReply(PostQueryVO query);

	List<MemberCountVO> getPostsByMemberIds(List<Long> memberIds);

	List<ExportPostVO> getAllPost(PostQueryVO query);

	List<ExportPostVO> getAllPost();

	List<BbsPostEO> getAllBbsPostEO(PostQueryVO query);
}
