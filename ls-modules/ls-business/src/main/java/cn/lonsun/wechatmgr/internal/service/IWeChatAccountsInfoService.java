package cn.lonsun.wechatmgr.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.wechatmgr.internal.entity.WeChatAccountsInfoEO;

public interface IWeChatAccountsInfoService extends IMockService<WeChatAccountsInfoEO> {
	/**
	 * 
	 * @Title: getInfoByPrimitiveId
	 * @Description: 根据原始ID查询
	 * @param primitiveId
	 * @return   Parameter
	 * @return  WeChatAccountsInfoEO   return type
	 * @throws
	 */
	public WeChatAccountsInfoEO getInfoByPrimitiveId(String primitiveId);
	/**
	 * 
	 * @Title: getInfoBySite
	 * @Description: 根据站点查询
	 * @param siteId
	 * @return   Parameter
	 * @return  WeChatAccountsInfoEO   return type
	 * @throws
	 */
	public WeChatAccountsInfoEO getInfoBySite(Long siteId);
	/**
	 * 
	 * @Title: saveConfig
	 * @Description:保存
	 * @param config   Parameter
	 * @return  void   return type
	 * @throws
	 */
	public void saveConfig(WeChatAccountsInfoEO config);
	/**
	 * 
	 * @Title: getInfoByUid
	 * @Description: 获取帐号信息
	 * @param primitiveId
	 * @return   Parameter
	 * @return  WeChatAccountsInfoEO   return type
	 * @throws
	 */
	public WeChatAccountsInfoEO getInfoByUid(String primitiveId);

	/**
	 * 获取原始id公众号的站点id
	 * @param toUserName
	 * @return
     */
	Long getSiteId(String toUserName);
}
