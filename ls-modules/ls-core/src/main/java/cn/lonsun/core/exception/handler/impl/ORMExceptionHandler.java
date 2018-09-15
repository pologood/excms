package cn.lonsun.core.exception.handler.impl;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.exception.handler.IExceptionHandler;
import cn.lonsun.core.util.TipsMode;
/**
 * Hibernate异常处理器
 * @author 徐建华
 *
 */
@Component("ormExceptionHandler")
public class ORMExceptionHandler implements IExceptionHandler{

	@Autowired
	private IExceptionHandler businessExceptionHandler;
	
	@Override
	public BaseRunTimeException process(Throwable e) {
		if(e instanceof HibernateException){
			//获取ORM异常码
			String message = e.getCause()==null?null:e.getCause().getMessage();;
			if(message!=null&&!"".equals(message)){
				String[] messages = message.split(":");
				if(messages.length>0){
					String errorCode = messages[0].trim();
					//异常码ORA-01017表示数据库连接失败，需要特殊处理告知用户
					if("ORA-01017".equals(errorCode)){
						return new BaseRunTimeException(TipsMode.Key.toString(),"DBConnectFailed");
					}
				}
			}
			return new BaseRunTimeException(TipsMode.Key.toString(),"HibernateException");
		}else{
			return getBusinessExceptionHandler().process(e);
		}
	}

	public void setBusinessExceptionHandler(
			IExceptionHandler businessExceptionHandler) {
		this.businessExceptionHandler = businessExceptionHandler;
	}

	public IExceptionHandler getBusinessExceptionHandler() {
		return businessExceptionHandler;
	}
}
