package cn.lonsun.monitor.internal.entity;

import cn.lonsun.core.base.entity.ABaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 *
 * @ClassName: MonitorSiteRegisterEO
 * @Description: 日常监测站点注册
 * @author liuk
 * @date 2017年12月27日
 *
 */
@Entity
@Table(name="MONITOR_SITE_REGISTER")
public class MonitorSiteRegisterEO extends ABaseEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = -121L;


	//主键
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="ID")
	private Long id;

	//站点id
	@Column(name = "SITE_ID")
	private Long siteId;

	//日常监测是否已经注册  1-已注册 其他-未注册
	@Column(name = "IS_REGISTERED")
	private Integer isRegistered;

	//日常监测注册码
	@Column(name = "REGISTERED_CODE")
	private String registeredCode;

	//日常监测开通时间
	@Column(name = "REGISTERED_TIME")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private Date registeredTime;


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
}
