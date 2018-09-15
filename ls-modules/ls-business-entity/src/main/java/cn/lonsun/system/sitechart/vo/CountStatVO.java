package cn.lonsun.system.sitechart.vo;

public class CountStatVO {

	private Long num;

	private Long siteId;public Long getNum() {
		return num;
	}

	public void setNum(Long num) {
		if(num==null) num=0L;
		this.num = num;
	}

	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

}
