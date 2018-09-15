package cn.lonsun.content.internal.dao;

import cn.lonsun.content.internal.entity.ContentReferRelationEO;
import cn.lonsun.content.vo.ContentReferRelationPageVO;
import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;

import java.util.List;

public interface IContentReferRelationDao extends IMockDao<ContentReferRelationEO> {


	Pagination getPagination(ContentReferRelationPageVO pageVO);

	List<ContentReferRelationEO> getByCauseId(Long causeId, String modelCode, String type);
	
	List<ContentReferRelationEO> getByReferId(Long referId,String modelCode,String type);
	
	void delteByReferId(Long referId);

	List<ContentReferRelationEO> getCauseById(Long causeById);

	List<ContentReferRelationEO>  getByParentReferColumn(Long columnId,Long catId,Long pReferColumnId);

	List<ContentReferRelationEO>  getByParentReferOrganCat(Long columnId,Long catId,Long pReferColumnId,Long pReferCatId);

	/**
	 * 恢复复制引用关系
	 * @param ids
	 */
	void recoveryByReferIds(Long[] ids);

	/**
	 * 查询被引用的新闻id列表
	 * @param causeId
	 * @param modelCode
	 * @return
	 */
	List<Long> getReferedIds(Long[] causeId, String modelCode);

	/**
	 * 查询被引用的新闻id列表
	 * @param referId
	 * @param modelCode
	 * @return
	 */
	List<Long> getReferIds(Long[] referId, String modelCode);
}
