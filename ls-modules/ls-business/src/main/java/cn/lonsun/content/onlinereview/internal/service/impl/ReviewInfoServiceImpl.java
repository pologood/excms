package cn.lonsun.content.onlinereview.internal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.content.onlinereview.internal.dao.IReviewInfoDao;
import cn.lonsun.content.onlinereview.internal.entity.ReviewInfoEO;
import cn.lonsun.content.onlinereview.internal.service.IReviewInfoService;
import cn.lonsun.content.onlinereview.vo.ReviewQueryVO;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.util.Pagination;

@Service
public class ReviewInfoServiceImpl extends BaseService<ReviewInfoEO> implements IReviewInfoService{

	@Autowired
	private IReviewInfoDao reviewInfoDao;

	@Override
	public Pagination getPage(ReviewQueryVO query) {
		return reviewInfoDao.getPage(query);
	}

	@Override
	public Long getMaxSortNum(Long siteId, Long columnId) {
		return reviewInfoDao.getMaxSortNum(siteId,columnId);
	}

	@Override
	public void updateSort(Long id, Long sortNum, String type) {
		ReviewInfoEO  info= reviewInfoDao.getEntity(ReviewInfoEO.class, id);
		ReviewInfoEO  info1 = reviewInfoDao.getSortReview(sortNum,type);
		if(info != null && info1 !=null){
			Long sortN = info.getSortNum();
			info.setSortNum(info1.getSortNum());
			info1.setSortNum(sortN);
			reviewInfoDao.update(info);
			reviewInfoDao.update(info1);
		}
	}

	@Override
	public void delete(Long[] ids) {
		reviewInfoDao.delete(ReviewInfoEO.class,ids);
	}
}
