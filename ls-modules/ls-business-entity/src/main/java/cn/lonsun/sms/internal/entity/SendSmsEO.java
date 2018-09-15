package cn.lonsun.sms.internal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.lonsun.core.base.entity.ABaseEntity;

@Entity
@Table(name="CMS_SEND_SMS")
public class SendSmsEO extends ABaseEntity{


	public enum SmsStatus {
		E_SUCCESS(1), // 成功
		E_ERROR(0),// 错误
		E_NOTLOGIN(-1),//用户未登录，请重新登录
		S_ERROR(-2);//异常
		private Integer sta;
		private SmsStatus(Integer sta){
			this.sta=sta;
		}
		public Integer getSmsStatus(){
			return sta;
		}
	}

	public enum Status{
		Used,
		Unused,
		Timeout;
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="SMS_ID")
	private Long smsId;

	@Column(name="PHONE_")
	private String phone;

	@Column(name="CODE_")
	private String code;

	@Column(name="STATUS")
	private String status = Status.Unused.toString();

	@Column(name="SMS_STATUS")
	private Integer smsStatus;

	//短信发送的用户登录sessionId
	@Column(name = "SESSION_ID")
	private String sessionId;

	/**
	 * 返回结果描述
	 */
	@Column(name = "DESC_")
	private String desc;


	public Long getSmsId() {
		return smsId;
	}

	public void setSmsId(Long smsId) {
		this.smsId = smsId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Integer getSmsStatus() {
		return smsStatus;
	}

	public void setSmsStatus(Integer smsStatus) {
		this.smsStatus = smsStatus;
	}
}
