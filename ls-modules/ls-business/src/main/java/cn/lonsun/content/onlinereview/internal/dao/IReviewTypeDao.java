package cn.lonsun.content.onlinereview.internal.dao;

import java.util.List;

import cn.lonsun.content.onlinereview.internal.entity.ReviewTypeEO;
import cn.lonsun.content.onlinereview.vo.ReviewQueryVO;
import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;

public interface IReviewTypeDao extends IBaseDao<ReviewTypeEO>{

	Pagination getPage(ReviewQueryVO query);

	List<ReviewTypeEO> getReviewTypes(Long columnId, Long siteId);

}
