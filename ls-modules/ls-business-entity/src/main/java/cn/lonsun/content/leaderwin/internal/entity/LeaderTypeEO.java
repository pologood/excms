package cn.lonsun.content.leaderwin.internal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.lonsun.core.base.entity.ABaseEntity;

@Entity
@Table(name="CMS_LEADER_TYPE")
public class LeaderTypeEO extends ABaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="LEADER_TYPE_ID")
	private Long leaderTypeId;

	// 栏目Id
	@Column(name = "COLUMN_ID")
	private Long columnId;
	// 站点Id
	@Column(name = "SITE_ID")
	private Long siteId;

	@Column(name="TTILE")
	private String title;

	// 排序编码-用于查询排序
	@Column(name = "SORT_NUM")
	private Long sortNum = Long.valueOf(0);

	public Long getLeaderTypeId() {
		return leaderTypeId;
	}

	public void setLeaderTypeId(Long leaderTypeId) {
		this.leaderTypeId = leaderTypeId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getColumnId() {
		return columnId;
	}

	public void setColumnId(Long columnId) {
		this.columnId = columnId;
	}

	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}


	public Long getSortNum() {
		return sortNum;
	}

	public void setSortNum(Long sortNum) {
		this.sortNum = sortNum;
	}
}
