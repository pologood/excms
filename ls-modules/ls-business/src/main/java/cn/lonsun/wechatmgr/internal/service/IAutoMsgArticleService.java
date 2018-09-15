package cn.lonsun.wechatmgr.internal.service;

import java.util.List;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.wechatmgr.internal.entity.AutoMsgArticleEO;

public interface IAutoMsgArticleService extends IMockService<AutoMsgArticleEO> {

	/**
	 * 
	 * @Title: getListByKey
	 * @Description: 根据关键词查询
	 * @param keyId
	 * @return   Parameter
	 * @return  List<AutoMsgArticleEO>   return type
	 * @throws
	 */
	List<AutoMsgArticleEO> getListByKey(Long keyId);
}
