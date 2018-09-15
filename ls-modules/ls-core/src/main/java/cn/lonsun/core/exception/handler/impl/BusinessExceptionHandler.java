package cn.lonsun.core.exception.handler.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.exception.BusinessException;
import cn.lonsun.core.exception.handler.IExceptionHandler;
import cn.lonsun.core.util.TipsMode;

/**
 * 业务异常处理器
 * @author 徐建华
 *
 */
@Component("businessExceptionHandler")
public class BusinessExceptionHandler implements IExceptionHandler {
	
	@Autowired
	private IExceptionHandler baseRunTimeExceptionHandler;

	@Override
	public BaseRunTimeException process(Throwable e) {
		if(e instanceof BusinessException){
			BusinessException be = (BusinessException)e;
			String tipsMode = be.getTipsMode();
			if(TipsMode.Key.toString().equals(tipsMode)){
				return  new BaseRunTimeException(be.getKey(),be.getKeyWords());
			}else{
				return new BaseRunTimeException(TipsMode.Message.toString(),be.getTipsMessage()); 
			}
		}else{
			return  getBaseExceptionHandler().process(e);
		}
	}

	public void setBaseExceptionHandler(IExceptionHandler baseRunTimeExceptionHandler) {
		this.baseRunTimeExceptionHandler = baseRunTimeExceptionHandler;
	}

	public IExceptionHandler getBaseExceptionHandler() {
		return baseRunTimeExceptionHandler;
	}
}
