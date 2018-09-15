package cn.lonsun.govbbs.internal.entity;

import cn.lonsun.core.base.entity.ABaseEntity;

import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name="CMS_BBS_POST_EVALUATE")
public class BbsPostEvaluateEO extends ABaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//(主键)
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="POST_EVALUATE_ID")
	private Long postEvaluateId;	
	
	//(帖子id)	
	@Column(name="POST_ID")
	private Long postId;
	
	//(评价状态)	
	@Column(name="TYPE_")
	private String type;
	
	//(评价内容)		
	@Column(name="CONTENT_")
	private String content;
	
	//(会员id)	
	@Column(name="MEMBER_ID")
	private Long memberId;
	
	//(发帖人)		
	@Column(name="MEMBER_NAME")
	private String membeName;
	
	//(手机号)	
	@Column(name="MEMBER_PHONE")
	private String membePhone;
	
	//(邮箱)	
	@Column(name="MEMBER_EMAIL")
	private String membeEmail;	
	
	//(地址)		
	@Column(name="MEMBER_ADDRESS")
	private String membeAddress;
	
	//(回帖ip)	
	@Column(name="IP_")
	private String ip;
	
	//(是否发布)		
	@Column(name="IS_PUBLISH")
	private Integer isPublish;
	
	//(发布时间)	
	@Column(name="PUBLISH_DATE")
	private Date publishDate;

	public Long getPostEvaluateId() {
		return postEvaluateId;
	}

	public Long getPostId() {
		return postId;
	}

	public String getType() {
		return type;
	}

	public String getContent() {
		return content;
	}

	public Long getMemberId() {
		return memberId;
	}

	public String getMembeName() {
		return membeName;
	}

	public String getMembePhone() {
		return membePhone;
	}

	public String getMembeEmail() {
		return membeEmail;
	}

	public String getMembeAddress() {
		return membeAddress;
	}

	public String getIp() {
		return ip;
	}

	public Integer getIsPublish() {
		return isPublish;
	}

	public Date getPublishDate() {
		return publishDate;
	}

	public void setPostEvaluateId(Long postEvaluateId) {
		this.postEvaluateId = postEvaluateId;
	}

	public void setPostId(Long postId) {
		this.postId = postId;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public void setMembeName(String membeName) {
		this.membeName = membeName;
	}

	public void setMembePhone(String membePhone) {
		this.membePhone = membePhone;
	}

	public void setMembeEmail(String membeEmail) {
		this.membeEmail = membeEmail;
	}

	public void setMembeAddress(String membeAddress) {
		this.membeAddress = membeAddress;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setIsPublish(Integer isPublish) {
		this.isPublish = isPublish;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}
	
	
}
