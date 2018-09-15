package cn.lonsun.content.leaderwin.vo;

import java.util.List;


public class LeaderWebVO implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long leaderTypeId;
	// 栏目Id
	private Long columnId;
	// 站点Id
	private Long siteId;

	private String title;
	
	private Integer isOpen = 0;
	 
	private List<LeaderInfoVO> leaderInfos;

	public Long getLeaderTypeId() {
		return leaderTypeId;
	}

	public void setLeaderTypeId(Long leaderTypeId) {
		this.leaderTypeId = leaderTypeId;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<LeaderInfoVO> getLeaderInfos() {
		return leaderInfos;
	}

	public void setLeaderInfos(List<LeaderInfoVO> leaderInfos) {
		this.leaderInfos = leaderInfos;
	}

	public Integer getIsOpen() {
		return isOpen;
	}

	public void setIsOpen(Integer isOpen) {
		this.isOpen = isOpen;
	}

}
