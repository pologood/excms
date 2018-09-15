package cn.lonsun.indicator.internal.util;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import cn.lonsun.core.util.ContextHolderUtils;
import cn.lonsun.indicator.internal.entity.IndicatorEO;

/**
 * Indicator工具类
 *  
 * @author xujh 
 * @date 2014年10月4日 下午4:58:29
 * @version V1.0
 */
public class IndicatorUtil {
	
	/**
	 * 按钮保存到session中key的开始部分，整个key的组成为PRE_KEY+菜单ID
	 */
	public static final String PRE_KEY = "menu_";

	/**
	 * 判断菜单(indicatorId)下是否存在编码为code的按钮
	 * @param indicatorId 菜单主键
	 * @param code 按钮code
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static boolean isExisted(Long indicatorId, String code) {
		boolean isExisted = false;
		if (indicatorId == null || StringUtils.isEmpty(code)) {
			throw new NullPointerException();
		}
		//从session中获取按钮
		String key = PRE_KEY.concat(indicatorId.toString());
		List<IndicatorEO> buttons = (List<IndicatorEO>) ContextHolderUtils
				.getSession().getAttribute(key);
		//验证code是否存在
		if (buttons != null && buttons.size() > 0) {
			for(IndicatorEO button:buttons){
				if(code.equals(button.getCode())){
					isExisted = true;
					break;
				}
			}
		}
		return isExisted;
	}
	
	
	/**
	 * 判断菜单(indicatorId)下是否存在编码为code的按钮
	 * @param indicatorId 菜单主键
	 * @param code 按钮code
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String getName(Long indicatorId, String code) {
		String lable = null;
		if (indicatorId == null || StringUtils.isEmpty(code)) {
			throw new NullPointerException();
		}
		//从session中获取按钮
		String key = PRE_KEY.concat(indicatorId.toString());
		List<IndicatorEO> buttons = (List<IndicatorEO>) ContextHolderUtils
				.getSession().getAttribute(key);
		//验证code是否存在
		if (buttons != null && buttons.size() > 0) {
			for(IndicatorEO button:buttons){
				if(code.equals(button.getCode())){
					lable = button.getName();
					break;
				}
			}
		}
		return lable;
	}

}
