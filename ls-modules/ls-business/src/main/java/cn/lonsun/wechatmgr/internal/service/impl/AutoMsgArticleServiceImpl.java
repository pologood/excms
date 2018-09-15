package cn.lonsun.wechatmgr.internal.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.wechatmgr.internal.dao.IAutoMsgArticleDao;
import cn.lonsun.wechatmgr.internal.entity.AutoMsgArticleEO;
import cn.lonsun.wechatmgr.internal.service.IAutoMsgArticleService;

@Service("autoMsgArticleService")
public class AutoMsgArticleServiceImpl extends MockService<AutoMsgArticleEO> implements
		IAutoMsgArticleService {

	@Autowired
	private IAutoMsgArticleDao autoMsgArticleDao;
	@Override
	public List<AutoMsgArticleEO> getListByKey(Long keyId) {
		return autoMsgArticleDao.getListByKey(keyId);
	}
	
}
