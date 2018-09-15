package cn.lonsun.content.onlinereview.internal.dao;

import java.util.List;

import cn.lonsun.content.onlinereview.internal.entity.ReviewObjectEO;
import cn.lonsun.content.onlinereview.vo.ReviewQueryVO;
import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;

public interface IReviewObjectDao extends IBaseDao<ReviewObjectEO>{

	Pagination getPage(ReviewQueryVO query);

	List<ReviewObjectEO> getReviewObjects(Long columnId, Long siteId);

}
