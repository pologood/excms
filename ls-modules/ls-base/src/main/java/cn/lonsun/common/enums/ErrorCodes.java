package cn.lonsun.common.enums;

/**
 * 系统错误编码
 */
public enum ErrorCodes {
	Unlogin("-100"),//未登录
	LogoutSuccess("101"),//退出成功
	LogoutFailure("102");//退出失败
	
	
	private ErrorCodes(String value) {
		this.value = value;
	}
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
