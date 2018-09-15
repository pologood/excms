package cn.lonsun.rbac.internal.vo;

public class DeleteDnVO {

	private String dn;
	
	private String type;
	
	

	public DeleteDnVO() {
		super();
	}

	public DeleteDnVO(String type, String dn) {
		super();
		this.type = type;
		this.dn = dn;
	}


	public String getDn() {
		return dn;
	}

	public void setDn(String dn) {
		this.dn = dn;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
	
}
