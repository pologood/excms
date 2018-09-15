package cn.lonsun.system.sitechart.internal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.lonsun.core.base.entity.ABaseEntity;

/**
 * 
 * @ClassName: SiteChartTrendEO
 * @Description: 定时统计
 * @author Hewbing
 * @date 2016年4月7日 下午4:58:16
 *
 */
@Entity
@Table(name="CMS_SITE_CHART_TREND")
public class SiteChartTrendEO extends ABaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8762356067597463931L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private Long id;
	
	@Column(name="SITE_ID")
	private Long siteId;
	//浏览量
	@Column(name="PV")
	private Long pv;
	//独立访客
	@Column(name="UV")
	private Long uv;
	//ip地址
	@Column(name="IP")
	private Long ip;
	//新独立访客
	@Column(name="NUV")
	private Long nuv;
	//来访量
	@Column(name="SV")
	private Long sv;
	//日期
	@Column(name="DAY")
	private String day;
	//时间段
	@Column(name="HOUR")
	private String hour;
	//时间戳
	@Column(name="TIME")
	private Long time;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	public Long getPv() {
		return pv;
	}

	public void setPv(Long pv) {
		this.pv = pv;
	}

	public Long getUv() {
		return uv;
	}

	public void setUv(Long uv) {
		this.uv = uv;
	}

	public Long getIp() {
		return ip;
	}

	public void setIp(Long ip) {
		this.ip = ip;
	}

	public Long getNuv() {
		return nuv;
	}

	public void setNuv(Long nuv) {
		this.nuv = nuv;
	}

	public Long getSv() {
		return sv;
	}

	public void setSv(Long sv) {
		this.sv = sv;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getHour() {
		return hour;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}
	
}
