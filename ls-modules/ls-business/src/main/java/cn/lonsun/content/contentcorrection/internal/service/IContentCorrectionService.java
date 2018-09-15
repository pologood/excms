package cn.lonsun.content.contentcorrection.internal.service;

import cn.lonsun.content.contentcorrection.internal.entity.ContentCorrectionEO;
import cn.lonsun.content.contentcorrection.vo.CorrectionPageVO;
import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;

public interface IContentCorrectionService extends IMockService<ContentCorrectionEO> {
	
	public Pagination getPage(CorrectionPageVO pageVO);
	public void updatePublish(Long[] ids, Integer status);
	public void changePublish(Long id);
}
