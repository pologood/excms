package cn.lonsun.wechatmgr.internal.service;

import java.util.List;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.wechatmgr.internal.entity.WeChatMenuEO;

public interface IWeChatMenuService extends IMockService<WeChatMenuEO> {


	/**
	 * 
	 * @Title: get1Leve
	 * @Description: 一级菜单
	 * @param siteId
	 * @return   Parameter
	 * @return  List<WeChatMenuEO>   return type
	 * @throws
	 */
	List<WeChatMenuEO> get1Leve(Long siteId);
	/**
	 * 
	 * @Title: get2Leve
	 * @Description: 二级菜单
	 * @param pId
	 * @return   Parameter
	 * @return  List<WeChatMenuEO>   return type
	 * @throws
	 */
	List<WeChatMenuEO> get2Leve(Long pId);
	/**
	 * 
	 * @Title: getMenuBySite
	 * @Description: 根据站点获取
	 * @param siteId
	 * @return   Parameter
	 * @return  List<WeChatMenuEO>   return type
	 * @throws
	 */
	List<WeChatMenuEO> getMenuBySite(Long siteId);
	/**
	 * 
	 * @Title: deleteMenu
	 * @Description: 删除及其子项
	 * @param id   Parameter
	 * @return  void   return type
	 * @throws
	 */
	void deleteMenu(Long id);
}
