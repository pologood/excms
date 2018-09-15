package cn.lonsun.govbbs.internal.service;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.govbbs.internal.entity.BbsPlateEO;
import cn.lonsun.govbbs.internal.entity.BbsPostEO;
import cn.lonsun.govbbs.internal.vo.ExportPostVO;
import cn.lonsun.govbbs.internal.vo.PostQueryVO;

import java.util.List;
import java.util.Map;

public interface IBbsPostService extends IBaseService<BbsPostEO> {

	/**
	 * 获取分页
	 * @param query
	 * @return
     */
	Pagination getPage(PostQueryVO query);

	/**
	 * 是否有帖子
	 * @param plateId
	 * @return
     */
	Boolean hasPost(Long plateId);

	/**
	 * 总数
	 * @param plateId
	 * @return
	 */
	Long getCount(Long indicatorId);

	/**
	 * 删除 逻辑和 物理
	 * @param postIds
	 * @param isDel
     */
	void delete(Long[] postIds, Integer isDel);

	/**
	 * 更新状态
	 * @param type
	 * @param status
	 * @param postIds
     */
	void updateStatus(Integer type, Integer status, Long[] postIds);

	/**
	 * 还原
	 * @param postIds
     */
	void restore(List<BbsPostEO> posts, Long[] postIds);

	Pagination getUnitPlate(PostQueryVO query);

	Pagination getMemberStatic(PostQueryVO query);

	Pagination getUnitList(PostQueryVO query);

	Pagination getStaticUnitPlate(PostQueryVO query);

	Pagination getUnitReply(PostQueryVO query);

	//更新浏览数
	void updateViewCount(Long caseId);

	//更新回复数
	void updatePostReply(Long postId);

	void updateSupport(Long caseId);

	Map<Long,Long> getPostsByMemberIds(List<Long> memberIds);

	List<ExportPostVO> getAllPost(PostQueryVO query);

	List<ExportPostVO> getAllPost();

	List<BbsPostEO> getAllBbsPostEO(PostQueryVO query);

	void move(Long[] postIds, BbsPlateEO plate);
}
