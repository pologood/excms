package cn.lonsun.monitor.internal.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 *
 * @ClassName: MonitorSiteRegisterVO
 * @Description: 日常监测站点注册
 * @author liuk
 * @date 2017年12月27日
 *
 */

public class MonitorSiteRegisterVO {

	/**
	 *
	 */
	private static final long serialVersionUID = -121L;


	//主键
	private Long id;

	//站点id
	private Long siteId;

	//站点名称
	private String siteName;

	//日常监测是否已经注册  1-已注册 其他-未注册
	private Integer isRegistered;

	//日常监测注册码
	private String registeredCode;

	//日常监测开通时间
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private Date registeredTime;

	public MonitorSiteRegisterVO(){

	}

	public MonitorSiteRegisterVO(Long siteId, String siteName, Integer isRegistered, String registeredCode, Date registeredTime) {
		this.siteId = siteId;
		this.siteName = siteName;
		this.isRegistered = isRegistered;
		this.registeredCode = registeredCode;
		this.registeredTime = registeredTime;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getIsRegistered() {
		return isRegistered;
	}

	public void setIsRegistered(Integer isRegistered) {
		this.isRegistered = isRegistered;
	}

	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	public String getRegisteredCode() {
		return registeredCode;
	}

	public void setRegisteredCode(String registeredCode) {
		this.registeredCode = registeredCode;
	}

	public Date getRegisteredTime() {
		return registeredTime;
	}

	public void setRegisteredTime(Date registeredTime) {
		this.registeredTime = registeredTime;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
}
