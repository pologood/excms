package cn.lonsun.wechatmgr.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.wechatmgr.internal.entity.WeChatAccountsInfoEO;

public interface IWeChatAccountsInfoDao extends IMockDao<WeChatAccountsInfoEO> {
	/**
	 * 
	 * @Title: getInfoByPrimitiveId
	 * @Description: 根据原始ID获取信息
	 * @param primitiveId
	 * @return   Parameter
	 * @return  WeChatAccountsInfoEO   return type
	 * @throws
	 */
	public WeChatAccountsInfoEO getInfoByPrimitiveId(String primitiveId);
	/**
	 * 
	 * @Title: getInfoBySite
	 * @Description: 根据站点获取信息
	 * @param siteId
	 * @return   Parameter
	 * @return  WeChatAccountsInfoEO   return type
	 * @throws
	 */
	public WeChatAccountsInfoEO getInfoBySite(Long siteId);
	
	/**
	 * 
	 * @Title: getInfoByUid
	 * @Description: TODO
	 * @param primitiveId
	 * @return   Parameter
	 * @return  WeChatAccountsInfoEO   return type
	 * @throws
	 */
	public WeChatAccountsInfoEO getInfoByUid(String primitiveId);
}
