package cn.lonsun.ldap.internal.exception;

import cn.lonsun.core.exception.BusinessException;

/**
 * 组织/单位/人员排序码重复异常
 * @author xujh
 *
 */
public class SortNumRepeatedException extends BusinessException {


	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 4708392354243849078L;

	public SortNumRepeatedException(){
	}
	
	public SortNumRepeatedException(String message){
		super(message);
	}
}
