package cn.lonsun.rbac.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.lang.StringUtils;

import cn.lonsun.core.cache.ACache;

public class RolesCache extends ACache<Long> {
	// LDAP缓存名称
	private static String ROLEID_CACHE_NAME = "RoleID";

	// Cahce对象
	private static final Cache cache = new Cache(ROLEID_CACHE_NAME, 5000, false,false, 600, 60);
	static {
		CacheManager.getInstance().addCache(cache);
	}

	private final static RolesCache instance = new RolesCache();

	/**
	 * 获取实例对象
	 * 
	 * @return
	 */
	public static RolesCache getInstance() {
		return instance;
	}

	@Override
	public void put(String key, Object value) {
		if (StringUtils.isEmpty(key)) {
			throw new NullPointerException();
		}
		Element e = new Element(key, value);
		cache.put(e);
	}

	@Override
	public Object getValue(String key) {
		if (StringUtils.isEmpty(key)) {
			throw new NullPointerException();
		}
		Element element = cache.get(key);
		Object object = null;
		if (element != null) {
			object = element.getObjectValue();
		}
		return object == null;
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
		cache.remove(key);
	}

	/**
	 * 是否过期
	 * 
	 * @param key
	 * @return
	 */
	public boolean isExpired(String key) {
		boolean isExpired = true;
		Element element = cache.get(key);
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
		return cache.isKeyInCache(key);
	}

}
