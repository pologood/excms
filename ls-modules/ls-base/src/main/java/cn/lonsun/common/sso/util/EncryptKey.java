package cn.lonsun.common.sso.util;

import cn.lonsun.core.util.SpringContextHolder;


/**
 * 对称加密key
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
