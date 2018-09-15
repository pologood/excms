package cn.lonsun.wechatmgr.internal.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.wechatmgr.internal.dao.IWeChatPushMsgDao;
import cn.lonsun.wechatmgr.internal.entity.WeChatPushMsgEO;
import cn.lonsun.wechatmgr.vo.PushMsgVO;

@Repository("weChatPushMsgDao")
public class WeChatPushMsgDaoImpl extends MockDao<WeChatPushMsgEO> implements
		IWeChatPushMsgDao {

	@Override
	public Pagination getPage(PushMsgVO msgVO) {
		StringBuffer hql=new StringBuffer("from WeChatPushMsgEO where recordStatus='Normal'");
		Map<String,Object> map=new HashMap<String,Object>();
		if(!AppUtil.isEmpty(msgVO.getSiteId())){
			hql.append(" and siteId=:siteId");
			map.put("siteId", msgVO.getSiteId());
		}
		if(!AppUtil.isEmpty(msgVO.getTitle())){
			hql.append(" and title like:title escape'\\'");
			map.put("title", "%".concat(msgVO.getTitle()).concat("%"));
		}
		hql.append(" order by createDate desc");
		return getPagination(msgVO.getPageIndex(), msgVO.getPageSize(), hql.toString(), map);
	}

	@Override
	public void updatePublish(Long id,Long msgId, Integer status) {
		String hql="update WeChatPushMsgEO set publishDate=?, isPublish=?,msgId=? where id=?";
		executeUpdateByHql(hql, new Object[]{new Date(),status,msgId,id});
	}
	
	
}
