package cn.lonsun.content.onlinereview.internal.service;

import java.util.List;

import cn.lonsun.content.onlinereview.internal.entity.ReviewObjectEO;
import cn.lonsun.content.onlinereview.vo.ReviewQueryVO;
import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;

public interface IReviewObjectService extends IBaseService<ReviewObjectEO>{

	Pagination getPage(ReviewQueryVO query);

	List<ReviewObjectEO> getReviewObjects(Long columnId, Long siteId);

}
