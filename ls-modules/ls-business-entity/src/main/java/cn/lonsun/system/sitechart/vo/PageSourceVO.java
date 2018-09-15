package cn.lonsun.system.sitechart.vo;

public class PageSourceVO {

	private String referer;
	
	private Long pv;
	
	//pv占比
	//private String pvCent;
	
	private Long sv;
	
	//sv占比
	//private String svCent;

	public String getReferer() {
		return referer;
	}

	public void setReferer(String referer) {
		this.referer = referer;
	}

	public Long getPv() {
		return pv;
	}

	public void setPv(Long pv) {
		this.pv = pv;
	}

//	public String getPvCent() {
//		return pvCent;
//	}
//
//	public void setPvCent(String pvCent) {
//		this.pvCent = pvCent;
//	}

	public Long getSv() {
		return sv;
	}

	public void setSv(Long sv) {
		this.sv = sv;
	}

//	public String getSvCent() {
//		return svCent;
//	}
//
//	public void setSvCent(String svCent) {
//		this.svCent = svCent;
//	}
//	
	
}
