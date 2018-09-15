package cn.lonsun.ldap.internal.exception;

import cn.lonsun.core.exception.BaseRunTimeException;

/**
 * LDAP连接失败异常
 *  
 * @author xujh 
 * @date 2014年9月25日 上午9:44:47
 * @version V1.0
 */
public class CommunicationRuntimeException extends BaseRunTimeException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 8704127610053601005L;

	public CommunicationRuntimeException(){
	}
	
	public CommunicationRuntimeException(String message){
		super(message);
	}
}
