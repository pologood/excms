package cn.lonsun.content.contentcorrection.internal.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.contentcorrection.internal.dao.IContentCorrectionDao;
import cn.lonsun.content.contentcorrection.internal.entity.ContentCorrectionEO;
import cn.lonsun.content.contentcorrection.vo.CorrectionPageVO;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.util.Pagination;

@Repository("contentCorrectionDao")
public class ContentCorrectionDaoImpl extends MockDao<ContentCorrectionEO> implements
		IContentCorrectionDao {

	@Override
	public Pagination getPage(CorrectionPageVO pageVO) {
		StringBuffer hql=new StringBuffer("from ContentCorrectionEO where recordStatus='Normal'");
		Map<String,Object> map=new HashMap<String, Object>();
		if(!AppUtil.isEmpty(pageVO.getSiteId())){
			hql.append(" and siteId=:siteId");
			map.put("siteId", pageVO.getSiteId());
		}else{
			return null;
		}
		if(!AppUtil.isEmpty(pageVO.getLink())){
			hql.append(" and link like :link escape'\\'");
			map.put("link", "%".concat(pageVO.getLink()).concat("%"));
		}
		if(!AppUtil.isEmpty(pageVO.getDesc())){
			hql.append(" and descs like :descs escape'\\'");
			map.put("descs", "%".concat(pageVO.getDesc()).concat("%"));
		}
		if(!AppUtil.isEmpty(pageVO.getIsPublish())){
			hql.append(" and isPublish=:isPublish");
			map.put("isPublish", pageVO.getIsPublish());
		}
		if(!AppUtil.isEmpty(pageVO.getReplyStatus())){
			hql.append(" and replyStatus =:replyStatus");
			map.put("replyStatus", pageVO.getReplyStatus());
		}
		if(!AppUtil.isEmpty(pageVO.getType())){
			hql.append(" and type=:type");
			map.put("type", pageVO.getType());
		}
		hql.append(" order by createDate desc");
		return getPagination(pageVO.getPageIndex(), pageVO.getPageSize(), hql.toString(), map);
	}
	
	@Override
	public void updatePublish(Long[] ids, Integer status) {
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("isPublish", status);
		map.put("publishDate", new Date());
		map.put("ids", ids);
		String hql="update ContentCorrectionEO set isPublish=:isPublish , publishDate=:publishDate where id in (:ids)";
		executeUpdateByJpql(hql, map);
	}

	@Override
	public void changePublish(Long id, Integer status) {
		String hql="update ContentCorrectionEO set isPublish=?,publishDate=? where id=?";
		executeUpdateByHql(hql, new Object[]{status,new Date(),id});
	}
	
	
}
