package cn.lonsun.wechatmgr.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.wechatmgr.internal.entity.WeChatUserEO;
import cn.lonsun.wechatmgr.vo.WeChatUserVO;

public interface IWeChatUserService extends IMockService<WeChatUserEO> {
	/**
	 * 
	 * @Title: deleteUserByOpenId
	 * @Description: delete by openid
	 * @param openid   Parameter
	 * @return  void   return type
	 * @throws
	 */
	void deleteUserByOpenId(String openid);
	/**
	 * 
	 * @Title: getUserByOpenId
	 * @Description: TODO
	 * @param openid
	 * @return   Parameter
	 * @return  WeChatUserEO   return type
	 * @throws
	 */
	WeChatUserEO getUserByOpenId(String openid);
	/**
	 * 
	 * @Title: recordUser
	 * @Description: 记录用户
	 * @param user   Parameter
	 * @return  void   return type
	 * @throws
	 */
	void recordUser(WeChatUserEO user);
	/**
	 * 
	 * @Title: getUserPage
	 * @Description: TODO
	 * @param uservo
	 * @return   Parameter
	 * @return  Pagination   return type
	 * @throws
	 */
	Pagination getUserPage(WeChatUserVO uservo);
	/**
	 * 
	 * @Title: removeBySite
	 * @Description: 移除
	 * @param siteId   Parameter
	 * @return  void   return type
	 * @throws
	 */
	void removeBySite(Long siteId);
	/**
	 * 
	 * @Title: updateGroupByOpenid
	 * @Description: TODO
	 * @param openid
	 * @param groupid   Parameter
	 * @return  void   return type
	 * @throws
	 */
	void updateGroupByOpenid(String[] openid,Long groupid);
	/**
	 * 
	 * @Title: synUser
	 * @Description: 同步用户
	 * @param user   Parameter
	 * @return  void   return type
	 * @throws
	 */
	void synUser(WeChatUserEO user);

	/**
	 * 根据来源名查用户
	 * @param originUserName
	 * @return
	 */
	WeChatUserEO getUserByName(String  originUserName);

}
