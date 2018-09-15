package cn.lonsun.common.sso.util;

import cn.lonsun.core.util.SpringContextHolder;


/**
 * 对称加密key
 *
 * @author xujh
 * @version 1.0
 * 2015年2月3日
 *
 */
public class EncryptKey {

	public EncryptKey() {
	}

	/**
	 * 获取对象
	 *
	 * @return
	 */
	public static EncryptKey getInstance() {
		return SpringContextHolder.getBean("encryptKey");
	}
			
	private String key;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
}
