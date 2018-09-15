package cn.lonsun.wechatmgr.internal.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import cn.lonsun.core.base.entity.AMockEntity;
/**
 * 关键词表
 * @ClassName: KeyWordsMsgEO
 * @Description: TODO
 * @author Hewbing
 * @date 2015年12月22日 下午8:12:10
 *
 */
@Entity
@Table(name="CMS_WECHAT_KEWORDS_MSG")
public class KeyWordsMsgEO extends AMockEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2478665580952986857L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "SITE_ID")
	private Long siteId;
	
	//关键词
	@Column(name="KEY_WORDS")
	private String keyWords;
	//类型
	@Column(name="MSGTYPE")
	private String msgType;
	//内容
	@Column(name="CONTENT")
	private String content;
	//时间戳
	@Column(name = "CREATETIME")
	private Long createTime;
	
	@Transient
	private List<WeChatArticleEO> newsList;

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

	public String getKeyWords() {
		return keyWords;
	}

	public void setKeyWords(String keyWords) {
		this.keyWords = keyWords;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public List<WeChatArticleEO> getNewsList() {
		return newsList;
	}

	public void setNewsList(List<WeChatArticleEO> newsList) {
		this.newsList = newsList;
	}

	
	
}
