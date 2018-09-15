package cn.lonsun.phrase.internal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.lonsun.core.base.entity.ABaseEntity;

/**
 * 常用语
 *
 * @author xujh
 * @version 1.0
 * 2014年12月3日
 *
 */
@Entity
@Table(name="rbac_phrase")
public class PhraseEO extends ABaseEntity{
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 9146607331198047344L;
	
	public enum Type{
		DeviceIdea,//拟办意见
		LeaderComment,//领导批示
		SignAndIssue,//签发
		Check,//审核
		CheckDraft,//核稿
		MainReceivers,//主送
		CopyReceivers//抄送
	}
	//主键
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO) 
	@Column(name="PHRASE_Id")
	private Long phraseId;
	//常用语内容
	@Column(name="TEXT_")
	private String text;
	//常用语类型
	@Column(name="TYPE_")
	private String type;
	public Long getPhraseId() {
		return phraseId;
	}
	public void setPhraseId(Long phraseId) {
		this.phraseId = phraseId;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
