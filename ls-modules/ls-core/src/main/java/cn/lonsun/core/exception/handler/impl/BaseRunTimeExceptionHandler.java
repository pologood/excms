package cn.lonsun.core.exception.handler.impl;

import org.springframework.stereotype.Component;

import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.exception.handler.IExceptionHandler;
import cn.lonsun.core.util.TipsMode;

/**
 * 异常默认处理器
 * @author 徐建华
 *
 */
@Component("baseRunTimeExceptionHandler")
public class BaseRunTimeExceptionHandler implements IExceptionHandler {
	
	@Override
	public BaseRunTimeException process(Throwable e) {
		if(e instanceof BaseRunTimeException){
			BaseRunTimeException bException = (BaseRunTimeException) e;
			return bException;
		}
		return new  BaseRunTimeException(TipsMode.Key.toString(),"SystemException");
	}
}
