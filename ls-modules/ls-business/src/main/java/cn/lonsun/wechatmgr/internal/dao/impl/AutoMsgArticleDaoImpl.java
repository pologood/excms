package cn.lonsun.wechatmgr.internal.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.wechatmgr.internal.dao.IAutoMsgArticleDao;
import cn.lonsun.wechatmgr.internal.entity.AutoMsgArticleEO;

@Repository("autoMsgArticleDao")
public class AutoMsgArticleDaoImpl extends MockDao<AutoMsgArticleEO> implements
		IAutoMsgArticleDao {

	@Override
	public List<AutoMsgArticleEO> getListByKey(Long keyId) {
		String hql="from AutoMsgArticleEO where keyWordsId=? and recordStatus='Normal'";
		return  getEntitiesByHql(hql, new Object[]{keyId});
	}

	@Override
	public void deleteByKey(Long keyId) {
		String hql="delete AutoMsgArticleEO where keyWordsId=?";
		executeUpdateByHql(hql, new Object[]{keyId});
	}
}
