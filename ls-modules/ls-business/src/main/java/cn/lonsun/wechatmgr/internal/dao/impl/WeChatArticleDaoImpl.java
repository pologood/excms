package cn.lonsun.wechatmgr.internal.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.wechatmgr.internal.dao.IWeChatArticleDao;
import cn.lonsun.wechatmgr.internal.entity.WeChatArticleEO;
import cn.lonsun.wechatmgr.vo.WeChatArticleVO;

@Repository("weChatArticleDao")
public class WeChatArticleDaoImpl extends MockDao<WeChatArticleEO> implements
		IWeChatArticleDao {

	@Override
	public Pagination getPage(WeChatArticleVO vo) {
		StringBuffer hql=new StringBuffer("from WeChatArticleEO where recordStatus='Normal'");
		Map<String,Object> map=new HashMap<String, Object>();
		if(!AppUtil.isEmpty(vo.getTitle())){
			hql.append(" and title like :title escape'\\'");
			map.put("title","%".concat(vo.getTitle()).concat("%"));
		}
		if(!AppUtil.isEmpty(vo.getType())){
			hql.append(" and type=:type");
			map.put("type", vo.getType());
		}
		if(!AppUtil.isEmpty(vo.getSiteId())){
			hql.append(" and siteId=:siteId");
			map.put("siteId", vo.getSiteId());
		}
		hql.append(" order by publishDate desc");
		return getPagination(vo.getPageIndex(), vo.getPageSize(), hql.toString(), map);
	}

	@Override
	public List<WeChatArticleEO> getByKeyWordsMsgId(Long id) {
		String hql = "select a.title as title ,a.description as description,a.thumbImg as thumbImg,a.url as url from WeChatArticleEO a,AutoMsgArticleEO b where a.id = b.articleId and" +
				" a.recordStatus='Normal' and b.recordStatus='Normal' and b.keyWordsId = ? order by a.id desc";
		  List<WeChatArticleEO> list =(List<WeChatArticleEO>)getBeansByHql(hql, new Object[]{id}, WeChatArticleEO.class);

		return list;
	}
}
