package cn.lonsun.key;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author gu.fei
 * @version 2016-07-21 11:24
 */
public class SingleSequence {
	private final ReentrantLock lock = new ReentrantLock(false);
	
	protected long currVal = 0L;

	protected long maxVal = 0L;

	private final int cacheNum;

	private final UniqueTableApp app;

	public SingleSequence(int cacheNum, UniqueTableApp app) {
		this.cacheNum = cacheNum;
		this.app = app;
	}

	public long getNextVal(String name,String idName) {
		try {
			lock.lock();
			if (this.currVal < this.maxVal) {
				return (this.currVal++);
			}
			CacheValue cache = getNewValFromDB(name,idName);
			this.currVal = cache.getMinVal();
			this.maxVal = cache.getMaxVal();
			return (this.currVal++);
		} finally {
			lock.unlock();
		}
	}

	private CacheValue getNewValFromDB(final String name,final String idName) {
		return app.getCacheValue(cacheNum, name, idName);
	}
}