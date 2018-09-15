package cn.lonsun.wechatmgr.internal.dao;

import java.util.List;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.wechatmgr.internal.entity.WeChatMenuEO;

public interface IWeChatMenuDao extends IMockDao<WeChatMenuEO> {

	/**
	 * 
	 * @Title: get1Leve
	 * @Description: 获取一级菜单
	 * @param siteId
	 * @return   Parameter
	 * @return  List<WeChatMenuEO>   return type
	 * @throws
	 */
	List<WeChatMenuEO> get1Leve(Long siteId);
	/**
	 * 
	 * @Title: get2Leve
	 * @Description: 获取二级菜单
	 * @param pId
	 * @return   Parameter
	 * @return  List<WeChatMenuEO>   return type
	 * @throws
	 */
	List<WeChatMenuEO> get2Leve(Long pId);
	/**
	 * 
	 * @Title: getMenuBySite
	 * @Description: 获取菜单集合
	 * @param siteId
	 * @return   Parameter
	 * @return  List<WeChatMenuEO>   return type
	 * @throws
	 */
	List<WeChatMenuEO> getMenuBySite(Long siteId);
	
	/**
	 * 
	 * @Title: deleteMenu
	 * @Description: 删除ID及父ID记录
	 * @param id   Parameter
	 * @return  void   return type
	 * @throws
	 */
	void deleteMenu(Long id);
}
