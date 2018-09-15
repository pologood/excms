package cn.lonsun.staticcenter.generate.tag.impl.member.util;

public enum HtmlMemberEnum {

	/** 用户中心*/
	login("login"),//
	register("register"),//
	setpw("setpw"),//
	center("center");//

	private String value;

	private HtmlMemberEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}


	public static boolean exitMember(String module) {
		return login.getValue().equals(module) || center.getValue().equals(module) || register.getValue().equals(module) || setpw.getValue().equals(module);
	}
}
