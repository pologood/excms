package cn.lonsun.system.sitechart.vo;

public class LocationPageVO {

	private String city;
	
	private String province;
	
	private String country;
	
	private Long pv;
	
	private Long sv;
	
	private String pvCent;
	
	private String svCent;

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Long getPv() {
		return pv;
	}

	public void setPv(Long pv) {
		this.pv = pv;
	}

	public Long getSv() {
		return sv;
	}

	public void setSv(Long sv) {
		this.sv = sv;
	}

	public String getPvCent() {
		return pvCent;
	}

	public void setPvCent(String pvCent) {
		this.pvCent = pvCent;
	}

	public String getSvCent() {
		return svCent;
	}

	public void setSvCent(String svCent) {
		this.svCent = svCent;
	}
	
}
