package cn.lonsun.wechatmgr.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.wechatmgr.internal.entity.WeChatPushMsgEO;
import cn.lonsun.wechatmgr.vo.PushMsgVO;

public interface IWeChatPushMsgDao extends IMockDao<WeChatPushMsgEO> {

	/**
	 * 
	 * @Title: getPage
	 * @Description: 分页列表
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
	
}
