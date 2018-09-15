package cn.lonsun.wechatmgr.internal.dao;

import java.util.List;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.wechatmgr.internal.entity.AutoMsgArticleEO;

public interface IAutoMsgArticleDao extends IMockDao<AutoMsgArticleEO> {

	/**
	 * 
	 * @Title: getListByKey
	 * @Description: 根据关键词获取关键词与新闻关系表
	 * @param keyId
	 * @return   Parameter
	 * @return  List<AutoMsgArticleEO>   return type
	 * @throws
	 */
	List<AutoMsgArticleEO> getListByKey(Long keyId);
	
	/**
	 * 
	 * @Title: deleteByKey
	 * @Description: 根据关键词删除关系记录
	 * @param keyId   Parameter
	 * @return  void   return type
	 * @throws
	 */
	void deleteByKey(Long keyId);
}
