package cn.lonsun.content.onlinereview.internal.service;

import java.util.List;

import cn.lonsun.content.onlinereview.internal.entity.ReviewTypeEO;
import cn.lonsun.content.onlinereview.vo.ReviewQueryVO;
import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;

public interface IReviewTypeService extends IBaseService<ReviewTypeEO>{

	Pagination getPage(ReviewQueryVO query);

	List<ReviewTypeEO> getReviewTypes(Long columnId, Long siteId);

}
