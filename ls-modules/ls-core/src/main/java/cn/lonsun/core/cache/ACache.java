package cn.lonsun.core.cache;

import net.sf.ehcache.Cache;

/**
 * 自定义缓存基类
* @Description: 
* @author xujh 
* @date 2014年9月23日 下午10:07:37
* @version V1.0
 */
public abstract class ACache<T>{
	//缓存
	private Cache cache;
	//缓存名称
	private String name;
	//缓存最多纪录数量，默认1000条
	private int maxElementsInMemory = 1000;
	//是否支持磁盘存储,默认不支持
	private boolean isOverflowToDisk = false;
	//是否永不过期,默认不支持
	private boolean isEternal = false;
	//从纪录被创建开始的存活时间-秒，默认1小时
	private long timeToLiveSeconds = 3600;
	//从纪录最后一次被访问或更新后可存活的时间-秒，默认0.5小时
	private long timeToIdleSeconds = 1800;
	
	/**
	 * 新增
	 * @param key
	 * @param value
	 */
	public abstract void put(String key,Object value);
	
	/**
	 * 获取
	 * @param key
	 * @return
	 */
	public abstract Object getValue(String key);
	

	/**
	 * 移除
	 * @param key
	 */
	public abstract void remove(String key);
	
	/**
	 * 是否过期
	 * @param key
	 * @return
	 */
	public abstract boolean isExpired(String key);
	
	/**
	 * 是否存在key
	 * @param key
	 * @return
	 */
	public abstract boolean containsKey(String key);

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMaxElementsInMemory() {
		return maxElementsInMemory;
	}

	public void setMaxElementsInMemory(int maxElementsInMemory) {
		this.maxElementsInMemory = maxElementsInMemory;
	}

	public boolean getIsOverflowToDisk() {
		return isOverflowToDisk;
	}

	public void setIsOverflowToDisk(boolean isOverflowToDisk) {
		this.isOverflowToDisk = isOverflowToDisk;
	}

	public boolean getIsEternal() {
		return isEternal;
	}

	public void setIsEternal(boolean isEternal) {
		this.isEternal = isEternal;
	}

	public long getTimeToLiveSeconds() {
		return timeToLiveSeconds;
	}

	public void setTimeToLiveSeconds(long timeToLiveSeconds) {
		this.timeToLiveSeconds = timeToLiveSeconds;
	}

	public long getTimeToIdleSeconds() {
		return timeToIdleSeconds;
	}

	public void setTimeToIdleSeconds(long timeToIdleSeconds) {
		this.timeToIdleSeconds = timeToIdleSeconds;
	}

	public Cache getCache() {
		return cache;
	}

	public void setCache(Cache cache) {
		this.cache = cache;
	}
}
