package cn.lonsun.content.contentcorrection.internal.dao;

import cn.lonsun.content.contentcorrection.internal.entity.ContentCorrectionEO;
import cn.lonsun.content.contentcorrection.vo.CorrectionPageVO;
import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;

public interface IContentCorrectionDao extends IMockDao<ContentCorrectionEO> {
	
	public Pagination getPage(CorrectionPageVO pageVO);
	public void updatePublish(Long[] ids, Integer status);
	public void changePublish(Long id,Integer status);
}
