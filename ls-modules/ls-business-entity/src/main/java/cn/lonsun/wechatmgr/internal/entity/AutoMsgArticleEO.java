package cn.lonsun.wechatmgr.internal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import cn.lonsun.core.base.entity.AMockEntity;

/**
 * 关键词与新闻关系表
 */
@Entity
@Table(name="CMS_WECHAT_AUTO_ARTICLE")
public class AutoMsgArticleEO extends AMockEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 20169546315203372L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "SITE_ID")
	private Long siteId;
	
	//关键词ID
	@Column(name = "KEY_WORDS_ID")
	private Long keyWordsId;
	//新闻ID
	@Column(name = "ARTICLE_ID")
	private Long articleId;
	
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

	public Long getKeyWordsId() {
		return keyWordsId;
	}

	public void setKeyWordsId(Long keyWordsId) {
		this.keyWordsId = keyWordsId;
	}

	public Long getArticleId() {
		return articleId;
	}

	public void setArticleId(Long articleId) {
		this.articleId = articleId;
	}
	
}
