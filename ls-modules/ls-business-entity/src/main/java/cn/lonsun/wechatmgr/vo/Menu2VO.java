package cn.lonsun.wechatmgr.vo;

import java.util.List;

public class Menu2VO {

	private String name;
	
	private List<Menu1VO> sub_button;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Menu1VO> getSub_button() {
		return sub_button;
	}

	public void setSub_button(List<Menu1VO> sub_button) {
		this.sub_button = sub_button;
	}
	
}
