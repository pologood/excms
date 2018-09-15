package cn.lonsun.core.exception;

/**
 * 当某条数据已经和其他数据建立了关联关系，如果需要禁止删除或修改，那么抛出此异常
 * @author xujh
 *
 */
public class InUsingException extends BusinessException {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 6708381776943822785L;

	public InUsingException(){
	}
	
	public InUsingException(String message){
		super(message);
	}
}
