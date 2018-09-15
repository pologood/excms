package cn.lonsun.core.util;

import java.util.Collection;
import java.util.Map;

/**
 * 
 * <p>类名 : CollectionUtils</p>
 * <p>功能模块 : 系统核心模块</p>
 * <p>描述 : 提供判断容器类的常用操作</p>
 * <p>公司 : lonsun</p>
 * @author 朱磊
 * @date 2014年8月28日
 */
public class CollectionUtils {

	/**
	 * <p>
	 * 功能描述 : 判断容器对象为Null
	 * </p>
	 * 
	 * @param t
	 * @return boolean
	 */
	public static <T> boolean isNull(T t) {
		return t == null ? true : false;
	}

	/**
	 * <p>
	 * 功能描述 : 判断容器对象不为Null
	 * </p>
	 * 
	 * @param t
	 * @return boolean
	 */
	public static <T> boolean isNotNull(T t) {
		return !(isNull(t));
	}

	/**
	 * <p>
	 * 功能描述 : 判断容器对象为空或者大小为零
	 * </p>
	 * 
	 * @param t
	 * @return boolean
	 */
	public static <T> boolean isLengthless(T t) {
		return t == null ? true
				: t instanceof Map ? (((Map<?, ?>) t).size() == 0 ? true
						: false)
						: (t instanceof Collection ? (((Collection<?>) t)
								.size() == 0 ? true : false) : false);
	}

	/**
	 * <p>
	 * 功能描述 : 判断容器对象不为空且大小不为零
	 * </p>
	 * 
	 * @param t
	 * @return boolean
	 */
	public static <T> boolean isNotLengthless(T t) {
		return !(isLengthless(t));
	}
}
