package cn.lonsun.webservice.indicator;

import cn.lonsun.webservice.vo.indicator.IndicatorVO;


public interface IIndicatorService {

	/**
	 * 获取应用图标、菜单和按钮
	 * @param userId
	 * @param organId
	 * @param parentId
	 * @return
	 */
	public IndicatorVO[] getAppIndicators(Long userId, Long organId, Long parentId);
	
	/**
	 * 获取应用图标、菜单和按钮
	 * @param userId
	 * @param organId
	 * @param parentId
	 * @param type
	 * @return
	 */
	public IndicatorVO[] getAppIndicatorsByType(Long userId, Long organId, Long parentId, String type);
	
	/**
	 * 获取应用图标、菜单和按钮
	 * @param userId
	 * @param organId
	 * @param parentId
	 * @return
	 */
	public IndicatorVO[] getDesktopIndicators(Long userId, Long organId, String codes);
	
	
	/**
	 * 获取所有的应用信息
	 *
	 * @return
	 */
	public IndicatorVO[] getAllApps();
}
