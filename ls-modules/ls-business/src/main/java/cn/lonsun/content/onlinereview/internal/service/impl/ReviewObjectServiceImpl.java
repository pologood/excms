package cn.lonsun.content.onlinereview.internal.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.content.onlinereview.internal.dao.IReviewObjectDao;
import cn.lonsun.content.onlinereview.internal.entity.ReviewObjectEO;
import cn.lonsun.content.onlinereview.internal.service.IReviewObjectService;
import cn.lonsun.content.onlinereview.vo.ReviewQueryVO;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.util.Pagination;

@Service
public class ReviewObjectServiceImpl extends BaseService<ReviewObjectEO> implements IReviewObjectService{
	
	@Autowired
	private IReviewObjectDao reviewObjectDao;

	@Override
	public Pagination getPage(ReviewQueryVO query) {
		return reviewObjectDao.getPage(query);
	}

	@Override
	public List<ReviewObjectEO> getReviewObjects(Long columnId, Long siteId) {
		return reviewObjectDao.getReviewObjects(columnId,siteId);
	}

}
