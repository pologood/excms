package cn.lonsun.core.exception.handler;

import cn.lonsun.core.exception.BaseRunTimeException;

public interface IExceptionHandler {
	
	/**
	 * 处理方法
	 */
	public BaseRunTimeException process(Throwable e);
	
}
