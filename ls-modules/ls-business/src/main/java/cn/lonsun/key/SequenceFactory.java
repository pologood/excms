package cn.lonsun.key;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author gu.fei
 * @version 2016-07-21 11:24
 */
public class SequenceFactory implements ApplicationContextAware {
	private static Map<String, SingleSequence> singleSequenceMap = new ConcurrentHashMap<String, SingleSequence>();
	private static SequenceFactory factory;
	
	private UniqueTableApp uniqueTableApp;
	private ApplicationContext context;
	private int cacheKeyNum = 0;
	
	private SequenceFactory() {}
	
	public static SingleSequence getSequence(String name) {
		SingleSequence sequence = singleSequenceMap.get(name);
		if (sequence == null) {
			synchronized(SequenceFactory.class) {
				if (sequence == null) {
					sequence = factory.createSequence(name);
					singleSequenceMap.put(name, sequence);
				}
			}
		}
		return sequence;
	}

	private SingleSequence createSequence(String name) {
		int cacheNum = getCacheKeyNum();
		SingleSequence sequence = new SingleSequence(cacheNum, uniqueTableApp);
		return sequence;
	}

	public void setUniqueTableApp(UniqueTableApp uniqueTableApp) {
		this.uniqueTableApp = uniqueTableApp;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.context = applicationContext;
		factory = this.context.getBean(SequenceFactory.class);
	}

	public int getCacheKeyNum() {
		return cacheKeyNum;
	}

	public void setCacheKeyNum(int cacheKeyNum) {
		this.cacheKeyNum = cacheKeyNum;
	}
}