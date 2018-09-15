package cn.lonsun.content.onlinereview.internal.service;

import cn.lonsun.content.onlinereview.internal.entity.ReviewInfoEO;
import cn.lonsun.content.onlinereview.vo.ReviewQueryVO;
import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;

public interface IReviewInfoService extends IBaseService<ReviewInfoEO>{

	Pagination getPage(ReviewQueryVO query);

	Long getMaxSortNum(Long siteId, Long columnId);

	void updateSort(Long id, Long sortNum, String type);

	void delete(Long[] ids);

}
