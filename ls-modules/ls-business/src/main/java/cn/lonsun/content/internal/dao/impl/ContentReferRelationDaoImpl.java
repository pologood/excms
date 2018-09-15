package cn.lonsun.content.internal.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.dao.IContentReferRelationDao;
import cn.lonsun.content.internal.entity.ContentReferRelationEO;
import cn.lonsun.content.vo.ContentReferRelationPageVO;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("contentReferRelationDao")
public class ContentReferRelationDaoImpl extends MockDao<ContentReferRelationEO> implements
		IContentReferRelationDao {

	@Override
	public Pagination getPagination(ContentReferRelationPageVO pageVO) {
		Map<String,Object> map=new HashMap<String,Object>();
		StringBuffer hql=new StringBuffer("from ContentReferRelationEO where recordStatus = :recordStatus ");
		map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
		if(pageVO!=null){
			if(null!=pageVO.getCauseById()){
				hql.append(" and causeById=:causeById");
				map.put("causeById", pageVO.getCauseById());
			}

			if(!AppUtil.isEmpty(pageVO.getModelCode())){
				hql.append(" and modelCode=:modelCode");
				map.put("modelCode", pageVO.getModelCode());
			}

			if(!AppUtil.isEmpty(pageVO.getType())){
				hql.append(" and type=:type");
				map.put("type", pageVO.getType());
			}

			if(!AppUtil.isEmpty(pageVO.getReferId())){
				hql.append(" and referId=:referId");
				map.put("referId", pageVO.getReferId());
			}

			if(!AppUtil.isEmpty(pageVO.getColumnId())){
				hql.append(" and columnId=:columnId");
				map.put("columnId", pageVO.getColumnId());
			}

			if(!AppUtil.isEmpty(pageVO.getReferModelCode())){
				hql.append(" and referModelCode=:referModelCode");
				map.put("referModelCode", pageVO.getReferModelCode());
			}

			if(!AppUtil.isEmpty(pageVO.getCatId())){
				hql.append(" and catId=:catId");
				map.put("catId", pageVO.getCatId());
			}
		}
		hql.append(" order by createDate desc ");

		return getPagination(pageVO.getPageIndex(),pageVO.getPageSize(),hql.toString(),map);
	}

	@Override
	public List<ContentReferRelationEO> getByCauseId(Long causeId,
			String modelCode,String type) {
		Map<String,Object> map=new HashMap<String,Object>();
		StringBuffer hql=new StringBuffer("from ContentReferRelationEO where recordStatus = :recordStatus ");
		map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
		if(null!=causeId){
			hql.append(" and causeById=:causeId");
			map.put("causeId", causeId);
		}
		if(!AppUtil.isEmpty(modelCode)){
			hql.append(" and modelCode=:modelCode");
			map.put("modelCode", modelCode);
		}
		if(!AppUtil.isEmpty(type)){
			hql.append(" and type=:type");
			map.put("type", type);
		}
		return getEntitiesByHql(hql.toString(), map);
	}

	@Override
	public List<ContentReferRelationEO> getByReferId(Long referId,
			String modelCode, String type) {
		Map<String,Object> map=new HashMap<String,Object>();
		StringBuffer hql=new StringBuffer("from ContentReferRelationEO where recordStatus = :recordStatus ");
		map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
		if(null!=referId){
			hql.append(" and referId=:referId");
			map.put("referId", referId);
		}
		if(!AppUtil.isEmpty(modelCode)){
			hql.append(" and modelCode=:modelCode");
			map.put("modelCode", modelCode);
		}
		if(!AppUtil.isEmpty(type)){
			hql.append(" and type=:type");
			map.put("type", type);
		}
		return getEntitiesByHql(hql.toString(), map);
	}

	@Override
	public void delteByReferId(Long referId) {
		String hql = "update ContentReferRelationEO set recordStatus = 'Removed' where referId = ? ";
//		String hql="delete ContentReferRelationEO where referId =?";
		executeUpdateByHql(hql, new Object[]{referId});
	}

	@Override
	public List<ContentReferRelationEO>  getCauseById(Long causeById){
		Map<String,Object> map=new HashMap<String,Object>();
		StringBuffer hql=new StringBuffer("from ContentReferRelationEO where recordStatus = :recordStatus ");
		map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
		if(null!=causeById){
			hql.append(" and o.causeById=:causeById");
			map.put("causeById", causeById);
		}
		return getEntitiesByHql(hql.toString(), map);
	}


	@Override
	public List<ContentReferRelationEO>  getByParentReferColumn(Long columnId,Long catId,Long pReferColumnId){
		Map<String,Object> map=new HashMap<String,Object>();
		StringBuffer hql=new StringBuffer("from ContentReferRelationEO t where t.recordStatus = :recordStatus  ");
		hql.append(" and t.columnId = :columnId and t.type = :type ");
		if(!AppUtil.isEmpty(catId)){
			hql.append(" and t.catId = :catId ");
			map.put("catId",catId);
		}
		hql.append(" and exists ( select 1 from ContentReferRelationEO t2 where t2.recordStatus = :recordStatus   ");
		hql.append(" and t2.columnId = :pReferColumnId and t2.type = :type and t2.causeById = t.causeById ) ");
		map.put("recordStatus",AMockEntity.RecordStatus.Normal.toString());
		map.put("columnId",columnId);
		map.put("pReferColumnId",pReferColumnId);
		map.put("type",ContentReferRelationEO.TYPE.REFER.toString());
		return getEntitiesByHql(hql.toString(), map);
	}

	@Override
	public List<ContentReferRelationEO>  getByParentReferOrganCat(Long columnId,Long catId,Long pReferColumnId,Long pReferCatId){
		Map<String,Object> map=new HashMap<String,Object>();
		StringBuffer hql=new StringBuffer("from ContentReferRelationEO t where t.recordStatus = :recordStatus  ");
		hql.append(" and t.columnId = :columnId and t.type = :type ");
		if(!AppUtil.isEmpty(catId)){
			hql.append(" and t.catId = :catId ");
			map.put("catId",catId);
		}
		hql.append(" and exists ( select 1 from ContentReferRelationEO t2 where t2.recordStatus = :recordStatus   ");
		if(!AppUtil.isEmpty(pReferCatId)){
			hql.append(" and t2.catId = :pReferCatId ");
			map.put("pReferCatId",pReferCatId);
		}
		hql.append(" and t2.columnId = :pReferColumnId and t2.type = :type and t2.causeById = t.causeById ) ");
		map.put("recordStatus",AMockEntity.RecordStatus.Normal.toString());
		map.put("columnId",columnId);
		map.put("pReferColumnId",pReferColumnId);
		map.put("type",ContentReferRelationEO.TYPE.REFER.toString());
		return getEntitiesByHql(hql.toString(), map);
	}



	@Override
	public void recoveryByReferIds(Long[] ids) {
		String hql = "update ContentReferRelationEO set recordStatus = 'Normal' where referId in (:ids)";
		getCurrentSession().createQuery(hql).setParameterList("ids", ids).executeUpdate();
	}

	@Override
	public List<Long> getReferedIds(Long[] causeId,String modelCode) {

		List<Long> result = new ArrayList<Long>();
		if(causeId==null||causeId.length==0){
			return result;
		}

		Map<String,Object> map=new HashMap<String,Object>();
		StringBuffer hql=new StringBuffer("select causeById as causeById from ContentReferRelationEO where type=:type and recordStatus = :recordStatus ");
		map.put("type", ContentReferRelationEO.TYPE.REFER.toString());
		map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
		if(null!=causeId){
			hql.append(" and causeById in (:causeId) ");
			map.put("causeId", causeId);
		}
		if(!AppUtil.isEmpty(modelCode)){
			hql.append(" and modelCode=:modelCode");
			map.put("modelCode", modelCode);
		}
		hql.append(" group by causeById ");

		List<ContentReferRelationEO> eos = (List<ContentReferRelationEO>)getBeansByHql(hql.toString(), map,ContentReferRelationEO.class,null);
		if(eos!=null&&eos.size()>0){
			for(ContentReferRelationEO eo:eos){
				result.add(eo.getCauseById());
			}
		}

		return result;
	}

	@Override
	public List<Long> getReferIds(Long[] referId,String modelCode) {
		List<Long> result = new ArrayList<Long>();
		if(referId==null||referId.length==0){
			return result;
		}

		Map<String,Object> map=new HashMap<String,Object>();
		StringBuffer hql=new StringBuffer("select referId as referId from ContentReferRelationEO where type=:type and recordStatus = :recordStatus ");
		map.put("type", ContentReferRelationEO.TYPE.REFER.toString());
		map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
		hql.append(" and referId in (:referId) ");
		map.put("referId", referId);

		if(!AppUtil.isEmpty(modelCode)){
			hql.append(" and modelCode=:modelCode");
			map.put("modelCode", modelCode);
		}

		hql.append(" group by referId ");


		List<ContentReferRelationEO> eos = (List<ContentReferRelationEO>)getBeansByHql(hql.toString(), map,ContentReferRelationEO.class,null);
		if(eos!=null&&eos.size()>0){
			for(ContentReferRelationEO eo:eos){
				result.add(eo.getReferId());
			}
		}

		return result;
	}

}
