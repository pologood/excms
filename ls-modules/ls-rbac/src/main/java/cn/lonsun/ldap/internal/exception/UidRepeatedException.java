package cn.lonsun.ldap.internal.exception;

import cn.lonsun.core.exception.BusinessException;

/**
 * 用户名重复异常
 * @author xujh
 *
 */
public class UidRepeatedException extends BusinessException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -2355136451861252531L;

	public UidRepeatedException(){
	}
	
	public UidRepeatedException(String message){
		super(message);
	}
}
