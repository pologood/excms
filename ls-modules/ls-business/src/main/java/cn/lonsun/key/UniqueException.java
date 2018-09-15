package cn.lonsun.key;

import org.springframework.core.NestedRuntimeException;

/**
 * @author gu.fei
 * @version 2016-07-21 11:24
 */
public class UniqueException  extends NestedRuntimeException {

	private static final long serialVersionUID = 1L;

	public UniqueException(String msg) {
		super(msg);
	}

	public UniqueException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
