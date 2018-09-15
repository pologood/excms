package cn.lonsun.content.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;

@Entity
@Table(name = "GUESTBOOK_TABLE_FORWARD")
public class GuestBookForwardRecordEO extends AMockEntity{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1940691777181747744L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	private Long id;
	@Column(name = "GUESTBOOK_ID")
	private Long guestBookId;
	@Column(name = "RECEIVE_ORGAN_ID")
	private Long receiveOrganId;
	@Column(name = "IP")
	private String ip;
	@Column(name = "USER_NAME")
	private String userName;
	@Column(name = "REMARKS")
	private String remarks;
	@Column(name = "RECEIVE_NAME")
	private String receiveName;

	@Column(name = "RECEIVE_User_code")
	private String receiveUserCode;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getGuestBookId() {
		return guestBookId;
	}
	public void setGuestBookId(Long guestBookId) {
		this.guestBookId = guestBookId;
	}
	public Long getReceiveOrganId() {
		return receiveOrganId;
	}
	public void setReceiveOrganId(Long receiveOrganId) {
		this.receiveOrganId = receiveOrganId;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getReceiveName() {
		return receiveName;
	}
	public void setReceiveName(String receiveName) {
		this.receiveName = receiveName;
	}

	public String getReceiveUserCode() {
		return receiveUserCode;
	}

	public void setReceiveUserCode(String receiveUserCode) {
		this.receiveUserCode = receiveUserCode;
	}
}