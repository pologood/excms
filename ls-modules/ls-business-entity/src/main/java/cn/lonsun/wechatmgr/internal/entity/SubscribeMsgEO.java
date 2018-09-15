package cn.lonsun.wechatmgr.internal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.lonsun.core.base.entity.AMockEntity;

/**
 * 关注回复表
 * @ClassName: SubscribeMsgEO
 * @Description: TODO
 * @author Hewbing
 * @date 2015年12月22日 下午8:12:43
 *
 */
@Entity
@Table(name="CMS_WECHAT_SUBSCRIBE_MSG")
public class SubscribeMsgEO extends AMockEntity {
	public enum MSGTYPE{
		judge;//评价
		private String msgType;
		public String getMsgType() {
			return msgType;
		}
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 6562520138175622398L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	private Long id;
	
	@Column(name="SITE_ID")
	private Long siteId;
	//回复内容	
	@Column(name="CONTENT")
	private String content;
	//类型
	@Column(name="MSGTYPE")
	private String msgType;

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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
	
}
