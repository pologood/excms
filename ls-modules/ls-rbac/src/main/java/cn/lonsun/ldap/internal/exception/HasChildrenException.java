package cn.lonsun.ldap.internal.exception;

import cn.lonsun.core.exception.BusinessException;

/**
 * 存在子节点异常
 * @author xujh
 *
 */
public class HasChildrenException extends BusinessException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 8704127610053601005L;

	public HasChildrenException(){
	}
	
	public HasChildrenException(String message){
		super(message);
	}
}
