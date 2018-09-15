package cn.lonsun.content.onlinereview.internal.dao;

import cn.lonsun.content.onlinereview.internal.entity.ReviewInfoEO;
import cn.lonsun.content.onlinereview.vo.ReviewQueryVO;
import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;

public interface IReviewInfoDao extends IBaseDao<ReviewInfoEO>{

	Pagination getPage(ReviewQueryVO query);

	Long getMaxSortNum(Long siteId, Long columnId);

	ReviewInfoEO getSortReview(Long sortNum, String type);

}
