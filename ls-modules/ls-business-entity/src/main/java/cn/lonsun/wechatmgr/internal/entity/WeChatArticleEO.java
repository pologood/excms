package cn.lonsun.wechatmgr.internal.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.lonsun.core.base.entity.AMockEntity;

/**
 * 
 * @ClassName: WeChatArticleEO
 * @Description: 图文素材表
 * @author Hewbing
 * @date 2015年12月22日 下午8:18:44
 *
 */
@Entity
@Table(name="CMS_WECHAT_ARTICLE")
public class WeChatArticleEO extends AMockEntity {

	private static final long serialVersionUID = 1212121L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "SITE_ID")
	private Long siteId;
	
	@Column(name="TITLE")
	private String title;
	//作者
	@Column(name="AUTHOR")
	private String author;
	//缩略图
	@Column(name="THUMB_IMG")
	private String thumbImg;
	//描述
	@Column(name="DESCRIPTION")
	private String description;
	//类型
	@Column(name="TYPE")
	private Integer type;
	//内容
	@Column(name="CONTENT")
	private String content;
	
	@Column(name="PUBLISH_DATE")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private Date publishDate;
	//跳转URL
	@Column(name="URL")
	private String url;
	//媒体ID
	@Column(name="MEDIA_ID")
	private String mediaId;
	//缩略图媒体ID
	@Column(name="THUMB_MEDTA_ID")
	private String thumbMedtaId;

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

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getThumbImg() {
		return thumbImg;
	}

	public void setThumbImg(String thumbImg) {
		this.thumbImg = thumbImg;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}

	public String getThumbMedtaId() {
		return thumbMedtaId;
	}

	public void setThumbMedtaId(String thumbMedtaId) {
		this.thumbMedtaId = thumbMedtaId;
	}
	
}
