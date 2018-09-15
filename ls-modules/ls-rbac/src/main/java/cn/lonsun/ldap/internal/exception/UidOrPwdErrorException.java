package cn.lonsun.ldap.internal.exception;

import cn.lonsun.core.exception.BusinessException;

/**
 * 密码错误异常
 * @author xujh
 *
 */
public class UidOrPwdErrorException extends BusinessException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -7610562675497929484L;

	public UidOrPwdErrorException(){
	}
	
	public UidOrPwdErrorException(String message){
		super(message);
	}
}
