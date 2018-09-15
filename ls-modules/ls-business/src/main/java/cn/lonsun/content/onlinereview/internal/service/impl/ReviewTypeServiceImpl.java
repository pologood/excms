package cn.lonsun.content.onlinereview.internal.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.content.onlinereview.internal.dao.IReviewTypeDao;
import cn.lonsun.content.onlinereview.internal.entity.ReviewTypeEO;
import cn.lonsun.content.onlinereview.internal.service.IReviewTypeService;
import cn.lonsun.content.onlinereview.vo.ReviewQueryVO;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.util.Pagination;

@Service
public class ReviewTypeServiceImpl extends BaseService<ReviewTypeEO> implements IReviewTypeService{
	
	@Autowired
	private IReviewTypeDao reviewTypeDao;

	@Override
	public Pagination getPage(ReviewQueryVO query) {
		return reviewTypeDao.getPage(query);
	}

	@Override
	public List<ReviewTypeEO> getReviewTypes(Long columnId, Long siteId) {
		return reviewTypeDao.getReviewTypes(columnId,siteId);
	}

}
