package cn.lonsun.core.exception;

/**
 * 非法操作
 * @author 徐建华
 *
 */
public class IllegalActException extends BaseRunTimeException {
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	public IllegalActException(){
		super();
	}
	
	public IllegalActException(String key){
		super(key);
	}
	
	public IllegalActException(String key,String message){
		super(key,message);
	}
}
