package cn.lonsun.system.sitechart.vo;

public class MainCountVO {

	
	//private Long siteId;
	
	private Long pv;
	
	private Long uv;
	
	private Long ip;
	
	private Long nuv;
	
	private Long sv;
	
	private String mold;

//	public Long getSiteId() {
//		return siteId;
//	}
//
//	public void setSiteId(Long siteId) {
//		this.siteId = siteId;
//	}

	public Long getPv() {
		return pv;
	}

	public void setPv(Long pv) {
		if(pv==null) pv=0L;
		this.pv = pv;
	}

	public Long getUv() {
		return uv;
	}

	public void setUv(Long uv) {
		if(uv==null) uv=0L;
		this.uv = uv;
	}

	public Long getIp() {
		return ip;
	}

	public void setIp(Long ip) {
		if(ip==null) ip=0L;
		this.ip = ip;
	}

	public Long getNuv() {
		return nuv;
	}

	public void setNuv(Long nuv) {
		if(nuv==null) nuv=0L;
		this.nuv = nuv;
	}

	public Long getSv() {
		return sv;
	}

	public void setSv(Long sv) {
		if(sv==null) sv=0L;
		this.sv = sv;
	}

	public String getMold() {
		return mold;
	}

	public void setMold(String mold) {
		this.mold = mold;
	}
	
	
}
