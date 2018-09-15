package cn.lonsun.ldap.internal.service.advice;

import java.lang.reflect.Method;

import org.springframework.aop.ThrowsAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;

import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.ldap.internal.context.DirContextContainerFactory;
import cn.lonsun.ldap.internal.context.impl.DirContextPool;
import cn.lonsun.ldap.internal.entity.ConfigEO;
import cn.lonsun.ldap.internal.exception.CommunicationRuntimeException;
import cn.lonsun.ldap.internal.service.IConfigService;
import cn.lonsun.ldap.internal.util.LDAPUtil;

/**
 * LDAP连接异常处理类
 * 
 * @author xujh
 * @date 2014年9月25日 上午9:40:32
 * @version V1.0
 */
public class ConnectionFailureAdvice implements ThrowsAdvice {

	@Autowired
	private TaskExecutor executor;

	/**
	 * @param method
	 * @param args
	 *            方法参数
	 * @param target
	 *            代理的目标对象
	 * @param throwable
	 */
	public void afterThrowing(Method method, Object[] args, Object target,
			Throwable e) throws Throwable {
		if (e instanceof CommunicationRuntimeException) {
			IConfigService configService = SpringContextHolder.getBean("configService");
			ConfigEO config = configService.getEffectiveConfig();
			if(config!=null){
				LDAPUtil.effectiveConfig = config;
			}
			//method.invoke(target, args);
		}
	}

	/**
	 * 允许连接LDAP有3次失败机会
	 * 
	 * @param method
	 * @param args
	 * @param times
	 * @return
	 * @throws Throwable
	 */
	public Object getResult(Method method, Object[] args, Object target,int times,Throwable throwable) throws Throwable {
		Object result = null;
		// 3次连接机会
		if (times < 3) {
			try {
				result = method.invoke(target, args);
			} catch (Exception e) {
				e.printStackTrace();
				Throwable t = e.getCause();
				// CommunicationException:表示与ldap服务器链接失败
				if (t instanceof CommunicationRuntimeException) {
					// LDAP连接失败次数
					times++;
					result = getResult(method, args, target, times,throwable);
				} else {
					// 如果不是LDAP连接失败，那么直接抛出异常
					throw t;
				}
			}
		} else {
			// 3次连接失败后，认为ldap服务器已宕机，此时连接另外一台服务器
			if (throwable instanceof CommunicationRuntimeException) {
				executor.execute(new Runnable() {
					@Override
					public void run() {
						DirContextPool pool = DirContextContainerFactory.getPool();
						pool.init();
					}
				});
				method.invoke(target, args);
			}
		}
		return result;
	}
}
