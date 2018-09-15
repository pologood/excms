package cn.lonsun.wechatmgr.internal.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.wechatmgr.internal.dao.IKeyWordsMsgDao;
import cn.lonsun.wechatmgr.internal.entity.KeyWordsMsgEO;
import cn.lonsun.wechatmgr.vo.KeyWordsVO;

@Repository("keyWordsMsgDao")
public class KeyWordsMsgDaoImpl extends MockDao<KeyWordsMsgEO> implements IKeyWordsMsgDao {

	@Override
	public List<KeyWordsMsgEO> getListBySite(Long siteId) {
		String hql="from KeyWordsMsgEO where siteId=? and recordStatus='Normal'";
		return getEntitiesByHql(hql, new Object[]{siteId});
	}

	@Override
	public List<KeyWordsMsgEO> getgetKeyWordsMsg(Long siteId, String eventKey) {
		String hql="from KeyWordsMsgEO where siteId=? and recordStatus='Normal' and keyWords = ?";
		return getEntitiesByHql(hql, new Object[]{siteId,eventKey});
	}

	@Override
	public Pagination getPage(KeyWordsVO vo) {
		StringBuffer hql=new StringBuffer("from KeyWordsMsgEO where recordStatus='Normal'");
		Map<String,Object> map=new HashMap<String,Object>();
		hql.append(" and siteId=:siteId");
		map.put("siteId", vo.getSiteId());
		if(!AppUtil.isEmpty(vo.getKeyWords())){
			hql.append(" and keyWords=:keyWords");
			map.put("keyWords", vo.getKeyWords());
		}
		if(!AppUtil.isEmpty(vo.getMsgType())){
			hql.append(" and msgType=:msgType");
			map.put("msgType", vo.getMsgType());
		}
		hql.append(" order by createDate desc");
		return getPagination(vo.getPageIndex(), vo.getPageSize(), hql.toString(), map);
	}

}
