package cn.lonsun.ldap.internal.exception;

import cn.lonsun.core.exception.BusinessException;

/**
 * 组织/单位名称重复异常
 * @author xujh
 *
 */
public class NameRepeatedException extends BusinessException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -115397100030009276L;

	public NameRepeatedException(){
	}
	
	public NameRepeatedException(String message){
		super(message);
	}
}
