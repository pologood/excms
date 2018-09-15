package cn.lonsun.ldap.internal.exception;

/**
 * 所有的LDAP服务器都无法连接异常
 * @author xujh
 *
 */
public class LDAPDisconnectionException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3636506098026313982L;
	
	public LDAPDisconnectionException(){
	}
	
	public LDAPDisconnectionException(String message){
		super(message);
	}

}
