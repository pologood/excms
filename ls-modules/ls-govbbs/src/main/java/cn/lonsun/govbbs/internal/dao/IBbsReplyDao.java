package cn.lonsun.govbbs.internal.dao;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.govbbs.internal.entity.BbsReplyEO;
import cn.lonsun.govbbs.internal.vo.ExportReplyVO;
import cn.lonsun.govbbs.internal.vo.MemberCountVO;
import cn.lonsun.govbbs.internal.vo.PostQueryVO;

import java.util.List;

public interface IBbsReplyDao extends IBaseDao<BbsReplyEO> {

	Pagination getPage(PostQueryVO query);

	void delByPostIds(Long[] postIds);

	Pagination getVoPage(PostQueryVO query);

	void updateRecordStatus(Long[] postIds);

	List<BbsReplyEO> getBbsReplyEOs(Long postId);

	List<MemberCountVO> getReplysByMemberIds(List<Long> memberIds);

	Long getReplyCount(String type);

	List<ExportReplyVO> getBbsReplys();
}
