package cn.lonsun.wechatmgr.internal.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.lonsun.core.base.entity.AMockEntity;

/**
 * 
 * @ClassName: WeChatPushMsgEO
 * @Description: 消息推送表
 * @author Hewbing
 * @date 2015年12月22日 下午8:19:31
 *
 */
@Entity
@Table(name="CMS_WECHAT_PUSH_MSG")
public class WeChatPushMsgEO extends AMockEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 12121L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "SITE_ID")
	private Long siteId;
	//标题
	@Column(name="TITLE")
	private String title;
	//类型1：文本，2：图文
	@Column(name="TYPE")
	private Integer type;
	//内容
	@Column(name="MSG_CONTENT")
	private String msgContent;
	//文章
	@Column(name="ARTICLES")
	private String articles;
	//发送分组
	@Column(name="PUSH_GROUP")
	private Long pushGroup=-1L;
	//媒体ID
	@Column(name="MATE_ID")
	private String mateId;
	//发布标识
	@Column(name="IS_PUBLISH")
	private Integer isPublish=0;
	
	@Column(name="PUBLISH_DATE")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private Date publishDate;

	//群发消息微信返回的消息ID
	@Column(name="MSG_ID")
	private Long msgId;
	
	//群发消息微信返回的图文消息的内容ID
	@Column(name="MSG_DATA_ID")
	private Long msgDataId;
	
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getMsgContent() {
		return msgContent;
	}

	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}

	public String getArticles() {
		return articles;
	}

	public void setArticles(String articles) {
		this.articles = articles;
	}

	public String getMateId() {
		return mateId;
	}

	public void setMateId(String mateId) {
		this.mateId = mateId;
	}

	public Long getPushGroup() {
		return pushGroup;
	}

	public void setPushGroup(Long pushGroup) {
		this.pushGroup = pushGroup;
	}

	public Integer getIsPublish() {
		return isPublish;
	}

	public void setIsPublish(Integer isPublish) {
		this.isPublish = isPublish;
	}

	public Date getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	public Long getMsgId() {
		return msgId;
	}

	public void setMsgId(Long msgId) {
		this.msgId = msgId;
	}

	public Long getMsgDataId() {
		return msgDataId;
	}

	public void setMsgDataId(Long msgDataId) {
		this.msgDataId = msgDataId;
	}

}
