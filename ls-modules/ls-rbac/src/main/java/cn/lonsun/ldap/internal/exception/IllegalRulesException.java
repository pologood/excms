package cn.lonsun.ldap.internal.exception;

import cn.lonsun.core.exception.BusinessException;

public class IllegalRulesException extends BusinessException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 3784416070673256778L;

	public IllegalRulesException(){
	}
	
	public IllegalRulesException(String message){
		super(message);
	}

}
