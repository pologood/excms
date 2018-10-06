package cn.lonsun.common.cache;

import javax.servlet.http.HttpSession;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.lang.StringUtils;

import cn.lonsun.core.cache.ACache;
import cn.lonsun.core.util.SpringContextHolder;

/**
 * 用户session缓存
 */
public class HttpSessionCache extends ACache<HttpSession> {
	
	private static HttpSessionCache ins = null;

	/**
	 * 获取实例对象
	 * 
	 * @return
	 */
	public static HttpSessionCache getInstance() {
		if(ins==null){
			ins = SpringContextHolder.getBean("httpSessionCache");
			Cache cache = new Cache(ins.getName(), ins.getMaxElementsInMemory(),
					ins.getIsEternal(), ins.getIsOverflowToDisk(),
					ins.getTimeToLiveSeconds(), ins.getTimeToIdleSeconds());
			CacheManager.getInstance().addCache(cache);
			ins.setCache(cache);
		}
		return ins;
	}

	/**
	 * 移除
	 * 
	 * @param key
	 */
	public void remove(String key) {
		if (StringUtils.isEmpty(key)) {
			throw new NullPointerException();
		}
		getCache().remove(key);
	}

	/**
	 * 是否过期
	 * 
	 * @param key
	 * @return
	 */
	public boolean isExpired(String key) {
		boolean isExpired = true;
		Element element = getCache().get(key);
		if (element != null) {
			isExpired = element.isExpired();
		}
		return isExpired;
	}

	/**
	 * 是否存在key
	 * 
	 * @param key
	 * @return
	 */
	public boolean containsKey(String key) {
		return getCache().isKeyInCache(key);
	}

	@Override
	public void put(String key, Object value) {
		if (StringUtils.isEmpty(key)) {
			throw new NullPointerException();
		}
		Element e = new Element(key, value);
		getCache().put(e);
	}

	@Override
	public HttpSession getValue(String key) {
		if (StringUtils.isEmpty(key)) {
			throw new NullPointerException();
		}
		Element element = getCache().get(key);
		Object object = null;
		if (element != null) {
			object = element.getObjectValue();
		}
		return object == null ? null : (HttpSession) object;
	}

}
