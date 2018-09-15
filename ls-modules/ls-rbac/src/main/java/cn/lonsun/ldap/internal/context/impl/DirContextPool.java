package cn.lonsun.ldap.internal.context.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;

import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.ldap.internal.context.ADirContextContainer;
import cn.lonsun.ldap.internal.entity.ConfigEO;
import cn.lonsun.ldap.internal.exception.LDAPDisconnectionException;
import cn.lonsun.ldap.internal.service.IConfigService;
import cn.lonsun.ldap.internal.util.LDAPUtil;

/**
 * DirContext池
 * 
 * @Description:
 * @author xujh
 * @date 2014年9月23日 下午10:08:50
 * @version V1.0
 */
public class DirContextPool extends ADirContextContainer {
	
	/**
	 * 用于标识LDAP上下文池是否可用
	 */
	public static boolean isPoolNormal = false;
	
	public static void updateIsPoolNormal(boolean isPoolNormal){
		DirContextPool.isPoolNormal = isPoolNormal;
	}

	/**
	 * DirContext池的大小，默认为50
	 */
	private int size = 50;

	/**
	 * 存放LDAP所有的链接
	 */
	private static final List<DirContext> pool = new ArrayList<DirContext>(200);
	/**
	 * 存放正在使用的链接
	 */
	private static final List<DirContext> usingPool = new ArrayList<DirContext>(100);
	/**
	 * 存放未使用的链接
	 */
	private static final List<DirContext> unusedPool = new ArrayList<DirContext>(100);
	
	/**
	 * key：未释放的Context，value：获取链接的时间，如果占用Context超过60s，那么自动释放该链接——需要借助定时器处理，
	 * 此处暂时在每次获取链接的时候验证一下
	 */
	private static final Map<DirContext, Long> unreleasedContext = new HashMap<DirContext, Long>(200);

	@Override
	public void init() throws LDAPDisconnectionException {
		// 防止上下文池出现线程问题
		synchronized (pool) {
			// 当池的状态正常时，表示初始化已完成，此时直接返回
			if (DirContextPool.isPoolNormal) {
				return;
			}
			//清空所有连接
			clear();
			IConfigService configService = SpringContextHolder.getBean("configService");
			ConfigEO config = configService.getEffectiveConfig();
			for (int i = 0; i < this.size; i++) {
				DirContext context = null;
				try {
					context = LDAPUtil.getDirContext(config);
				} catch (NamingException e) {
					e.printStackTrace();
				}
				pool.add(context);
				unusedPool.add(context);
			}
			// 初始化完成后，设置池的状态为可用
			DirContextPool.isPoolNormal = true;
		}
	}

	private static long releaseCount = 0;

	@Override
	public void release(DirContext context) {
		releaseCount++;
		System.out.println("==============================第" + releaseCount + "次释放LDAP链接上下文");
		usingPool.remove(context);
		unreleasedContext.remove(context);
		unusedPool.add(context);
		System.out.println("调用释放方法release，unusedPool添加一个元素,size+1="+unusedPool.size());
		if (unreleasedContext.size() > 0) {
			Set<DirContext> keys = unreleasedContext.keySet();
			for (DirContext c : keys) {
				Long times = unreleasedContext.get(c);
				// 链接占用时间超过60S，一般是未释放导致的，此时需要释放这些链接
				if ((new Date().getTime()) - times > 60000) {
					usingPool.remove(c);
					unusedPool.add(c);
					System.out.println("链接占用时间超过60S，unusedPool添加一个元素,size+1="+unusedPool.size());
					unreleasedContext.remove(c);
				}
			}
		}
	}


	@Override
	public DirContext getContext() throws NamingException {
		if (!isPoolNormal) {
			throw new RuntimeException("The statu of pool is abnormal.");
		}
		DirContext context = null;
		synchronized (unusedPool) {
			if (unusedPool.size() > 0) {
				context = unusedPool.get(0);
				unusedPool.remove(0);
				usingPool.add(context);
				unreleasedContext.put(context, new Date().getTime());
			} else {
				//超过了获取一个额外的连接
				DirContextContainer container = new DirContextContainer();
				container.init();
				context = container.getContext();
			}
		}
		return context;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		if (size <= 0) {
			throw new Error("The size of pool can not be less than zero.");
		}
		this.size = size;
	}

	@Override
	public void clear() {
		pool.clear();
		usingPool.clear();
		unusedPool.clear();
		unreleasedContext.clear();
	}
}
