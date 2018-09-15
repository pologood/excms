package cn.lonsun.content.ideacollect.internal.entity;

import java.util.Date;



import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.lonsun.core.base.entity.ABaseEntity;


@Entity
@Table(name="CMS_COLLECT_IDEA")
public class CollectIdeaEO extends ABaseEntity{

	public enum Status {
		Yes(1), // 是
		No(0);// 否
		private Integer status;
		private Status(Integer status){
			this.status=status;
		}
		public Integer getStatus(){
			return status;
		}
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="COLLECT_IDEA_ID")
	private Long collectIdeaId;
	
	@Column(name="COLLECT_INFO_ID")
	private Long collectInfoId;

	//姓名
	@Column(name="NAME")
	private String name;

	//会员id
	@Column(name="MEMBER_ID")
	private Long memberId;

	//电话
	@Column(name="PHONE_")
	private String phone;

	//内容
	@Column(name="CONTENT")
	private String content;

	//IP地址
	@Column(name="IP_")
	private String ip;
	
	//是否发布
	@Column(name="IS_ISSUED")
	private Integer isIssued = Status.No.getStatus();

	//(发布时间)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	@Column(name = "ISSUED_TIME")
	private Date issuedTime;

	@Transient
	private String createWebTime;

	public Long getCollectIdeaId() {
		return collectIdeaId;
	}

	public void setCollectIdeaId(Long collectIdeaId) {
		this.collectIdeaId = collectIdeaId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Integer getIsIssued() {
		return isIssued;
	}

	public void setIsIssued(Integer isIssued) {
		this.isIssued = isIssued;
	}

	public Date getIssuedTime() {
		return issuedTime;
	}

	public void setIssuedTime(Date issuedTime) {
		this.issuedTime = issuedTime;
	}

	public Long getCollectInfoId() {
		return collectInfoId;
	}

	public void setCollectInfoId(Long collectInfoId) {
		this.collectInfoId = collectInfoId;
	}

	public String getCreateWebTime() {
		return createWebTime;
	}

	public void setCreateWebTime(String createWebTime) {
		this.createWebTime = createWebTime;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}
}
