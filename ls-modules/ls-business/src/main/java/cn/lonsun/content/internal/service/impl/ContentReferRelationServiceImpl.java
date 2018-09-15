package cn.lonsun.content.internal.service.impl;

import cn.lonsun.content.internal.dao.IContentReferRelationDao;
import cn.lonsun.content.internal.entity.ContentReferRelationEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IContentReferRelationService;
import cn.lonsun.content.vo.ContentReferRelationPageVO;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("contentReferRelationService")
public class ContentReferRelationServiceImpl extends MockService<ContentReferRelationEO> implements
		IContentReferRelationService {
	
	@Autowired
	private IContentReferRelationDao contentReferRelationDao;
	
	@Autowired
	private IBaseContentService baseContentService;

	@Override
	public Pagination getPagination(ContentReferRelationPageVO pageVO) {
		Pagination page = contentReferRelationDao.getPagination(pageVO);
		return page;
	}

	@Override
	public List<ContentReferRelationEO> getByCauseId(Long causeId,
			String modelCode,String type) {
		return contentReferRelationDao.getByCauseId(causeId, modelCode,type);
	}

	@Override
	public List<ContentReferRelationEO> getByReferId(Long referId,
			String modelCode, String type) {
		return contentReferRelationDao.getByReferId(referId, modelCode, type);
	}

	@Override
	public void deleteReferInfo(Long contentId) {
		List<ContentReferRelationEO> causeRefer=contentReferRelationDao.getByCauseId(contentId, null, ContentReferRelationEO.TYPE.REFER.toString());
		for(ContentReferRelationEO _cr:causeRefer){
		}
	}

	@Override
	public void delteByReferId(Long[] referIds) {
		if(null!=referIds&&referIds.length>0){
			for(Long id:referIds){
			contentReferRelationDao.delteByReferId(id);
			}
		}
	}

	/**
	 * 判断新闻是否是被引用新闻
	 * @param baseContentId
	 * @return
	 */
	@Override
	public boolean checkIsRefered(Long baseContentId) {
		List<ContentReferRelationEO> list = contentReferRelationDao.getByCauseId(baseContentId, null,
				ContentReferRelationEO.TYPE.REFER.toString());
		if(list!=null&&list.size()>0){
			return true;
		}
		return false;
	}

	/**
	 * 判断新闻是否是引用新闻
	 * @param baseContentId
	 * @return
	 */
	@Override
	public boolean checkIsRefer(Long baseContentId) {
		List<ContentReferRelationEO> list = contentReferRelationDao.getByReferId(baseContentId, null,
				ContentReferRelationEO.TYPE.REFER.toString());
		if(list!=null&&list.size()>0){
			return true;
		}
		return false;
	}

	@Override
	public List<ContentReferRelationEO> getByParentReferColumn(Long columnId, Long catId, Long pReferColumnId) {
		return contentReferRelationDao.getByParentReferColumn(columnId, catId, pReferColumnId);
	}

	@Override
	public List<ContentReferRelationEO> getByParentReferOrganCat(Long columnId, Long catId, Long pReferColumnId, Long pReferCatId) {
		return contentReferRelationDao.getByParentReferOrganCat(columnId, catId, pReferColumnId,pReferCatId);
	}

	@Override
	public void recoveryByReferIds(Long[] ids) {
		contentReferRelationDao.recoveryByReferIds(ids);
	}

	@Override
	public List<Long> getReferedIds(Long[] causeId, String modelCode) {
		return contentReferRelationDao.getReferedIds(causeId,modelCode);
	}

	@Override
	public List<Long> getReferIds(Long[] referId, String modelCode) {
		return contentReferRelationDao.getReferIds(referId,modelCode);
	}

}
