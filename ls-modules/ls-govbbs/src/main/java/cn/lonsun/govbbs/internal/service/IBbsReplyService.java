package cn.lonsun.govbbs.internal.service;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.govbbs.internal.entity.BbsPostEO;
import cn.lonsun.govbbs.internal.entity.BbsReplyEO;
import cn.lonsun.govbbs.internal.vo.BbsMemberVO;
import cn.lonsun.govbbs.internal.vo.ExportReplyVO;
import cn.lonsun.govbbs.internal.vo.PostQueryVO;

import java.util.List;
import java.util.Map;

public interface IBbsReplyService extends IBaseService<BbsReplyEO> {

	/**
	 * EO 分页
	 * @param query
	 * @return
     */
	Pagination getPage(PostQueryVO query);

	/**
	 * VO分页
	 * @param query
	 * @return
	 */
	Pagination getVoPage(PostQueryVO query);

	/**
	 * 删除
	 * @param replyIds
     */
	void delete(Long[] replyIds);

	/**
	 * 删除
	 * @param postIds
     */
	void delByPostIds(Long[] postIds);

	/**
	 * 更新状态
	 * @param postIds
	 */
	void updateStatus(Integer type, Integer status, Long[] replyIds);

	/**
	 * 更新主题下 所有回复板块信息
	 * @param bbsPost
     */
	void updateReplys(BbsPostEO bbsPost);

	/**
	 * 逻辑删除
	 * @param postIds
     */
	void updateRecordStatus(Long[] postIds);

	/**
	 * 保存回帖
	 * @param reply
	 * @param post
     */
	void saveWebEntity(BbsReplyEO reply, BbsPostEO post, BbsMemberVO member);


	List<BbsReplyEO> getBbsReplyEOs(Long postId);

	List<ExportReplyVO> getBbsReplys();

	Map<Long,Long> getReplysByMemberIds(List<Long> memberIds);

	Long getReplyCount(String type);
}
