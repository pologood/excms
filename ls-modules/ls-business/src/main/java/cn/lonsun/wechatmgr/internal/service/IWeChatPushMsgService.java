package cn.lonsun.wechatmgr.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.wechatmgr.internal.entity.WeChatPushMsgEO;
import cn.lonsun.wechatmgr.vo.PushArticleVO;
import cn.lonsun.wechatmgr.vo.PushMsgVO;

public interface IWeChatPushMsgService extends IMockService<WeChatPushMsgEO> {

	/**
	 * 
	 * @Title: getPage
	 * @Description: 分页查询
	 * @param msgVO
	 * @return   Parameter
	 * @return  Pagination   return type
	 * @throws
	 */
	Pagination getPage(PushMsgVO msgVO);
	/**
	 * 
	 * @Title: updatePublish
	 * @Description: 发布状态
	 * @param id
	 * @param status   Parameter
	 * @return  void   return type
	 * @throws
	 */
	void updatePublish(Long id ,Long msgId, Integer status);
	
	/**
	 * 
	 * @Title: getPushMsg
	 * @Description: 获取群发信息
	 * @param id
	 * @return   Parameter
	 * @return  PushArticleVO   return type
	 * @throws
	 */
	PushArticleVO getPushMsg(Long id);
}
