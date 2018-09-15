package cn.lonsun.rbac.internal.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import cn.lonsun.core.base.entity.ABaseEntity;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 访问策略
 * @author xujh
 *
 */
@Entity
@Table(name="rbac_access_policy")
public class AccessPolicyEO extends ABaseEntity{
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -73073729273702394L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO) 
	@Column(name="ACCESS_POLICY_ID")
	private Long accessPolicyId;
	//ip段限制-开始ip
	@Column(name="START_IP")
	private String startIp;
	//ip段限制-结束ip
	@Column(name="END_IP")
	private String endIp;
	//时间段限制-开始时间
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	@Column(name="START_DATE")
	private Date startDate;
	//时间段限制-结束时间
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	@Column(name="END_DATE")
	private Date endDate;
	//通过isForbidden对访问进行双向控制，禁止访问或允许访问
	@Column(name="IS_ENABLE")
	private boolean isEnable = true;
	//创建人姓名
	@Column(name="CREATE_PERSON_NAME")
	private String createPersonName;
	
	public Long getAccessPolicyId() {
		return accessPolicyId;
	}
	public void setAccessPolicyId(Long accessPolicyId) {
		this.accessPolicyId = accessPolicyId;
	}
	public String getStartIp() {
		return startIp;
	}
	public void setStartIp(String startIp) {
		this.startIp = startIp;
	}
	public String getEndIp() {
		return endIp;
	}
	public void setEndIp(String endIp) {
		this.endIp = endIp;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public boolean getIsEnable() {
		return isEnable;
	}
	public void setIsEnable(boolean isEnable) {
		this.isEnable = isEnable;
	}
	public String getCreatePersonName() {
		return createPersonName;
	}
	public void setCreatePersonName(String createPersonName) {
		this.createPersonName = createPersonName;
	}
}
