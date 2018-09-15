package cn.lonsun.wechatmgr.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.wechatmgr.internal.entity.WeChatUserEO;
import cn.lonsun.wechatmgr.vo.WeChatUserVO;

public interface IWeChatUserDao extends IMockDao<WeChatUserEO> {
	/**
	 * 
	 * @Title: deleteUserByOpenId
	 * @Description: 根据openID删除
	 * @param openid   Parameter
	 * @return  void   return type
	 * @throws
	 */
	void deleteUserByOpenId(String openid);
	/**
	 * 
	 * @Title: getUserByOpenId
	 * @Description:根据openID获取
	 * @param openid
	 * @return   Parameter
	 * @return  WeChatUserEO   return type
	 * @throws
	 */
	WeChatUserEO getUserByOpenId(String openid);
	/**
	 * 
	 * @Title: getUserPage
	 * @Description: 用户分页
	 * @param uservo
	 * @return   Parameter
	 * @return  Pagination   return type
	 * @throws
	 */
	
	Pagination getUserPage(WeChatUserVO uservo);
	/**
	 * 
	 * @Title: deleteBySite
	 * @Description: 根据站点删除
	 * @param siteId   Parameter
	 * @return  void   return type
	 * @throws
	 */
	void deleteBySite(Long siteId);
	
	/**
	 * 
	 * @Title: updateGroupByOpenid
	 * @Description: 修改分组
	 * @param openid
	 * @param groupid   Parameter
	 * @return  void   return type
	 * @throws
	 */
	void updateGroupByOpenid(String[] openid,Long groupid);

	WeChatUserEO getUserByName(String originUserName);
}
