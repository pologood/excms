package cn.lonsun.system.sitechart.vo;

public class SearchKeyTotalVO {

	private Long count;
	
	private Long baidu;
	
	private Long so;
	
	private Long soso;
	
	private Long google;
	
	private Long other;

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		if(count==null) count=0L;
		this.count = count;
	}

	public Long getBaidu() {
		return baidu;
	}

	public void setBaidu(Long baidu) {
		if(baidu==null) baidu=0L;
		this.baidu = baidu;
	}

	public Long getSo() {
		return so;
	}

	public void setSo(Long so) {
		if(so==null) so=0L;
		this.so = so;
	}

	public Long getSoso() {
		return soso;
	}

	public void setSoso(Long soso) {
		if(soso==null) soso=0L;
		this.soso = soso;
	}

	public Long getGoogle() {
		return google;
	}

	public void setGoogle(Long google) {
		if(google==null) google=0L;
		this.google = google;
	}

	public Long getOther() {
		return other;
	}

	public void setOther(Long other) {
		if(other==null) other=0L;
		this.other = other;
	}
	
	
}
