package cn.lonsun.rbac.vo;

/**
 * 分页查找上下级关系的条件VO
 *  
 * @author xujh 
 * @date 2014年10月30日 下午5:11:40
 * @version V1.0
 */
public class RelationshipQueryVO {
	
	private Long leaderPersonId;
	//模糊的姓名
	private String blurryName;
	//模糊的组织名称
	private String blurryOrganName;
	//页码
	private Long pageIndex = Long.valueOf(0);
	//每页返回的最大数据条数
	private Integer pageSize = Integer.valueOf(15);
	//排序字段
	private String sortField;
	//是否逆序排序
	private String sortOrder;
	public Long getLeaderPersonId() {
		return leaderPersonId;
	}
	public void setLeaderPersonId(Long leaderPersonId) {
		this.leaderPersonId = leaderPersonId;
	}
	public String getBlurryName() {
		return blurryName;
	}
	public void setBlurryName(String blurryName) {
		this.blurryName = blurryName;
	}
	public String getBlurryOrganName() {
		return blurryOrganName;
	}
	public void setBlurryOrganName(String blurryOrganName) {
		this.blurryOrganName = blurryOrganName;
	}
	public Long getPageIndex() {
		return pageIndex;
	}
	public void setPageIndex(Long pageIndex) {
		this.pageIndex = pageIndex;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public String getSortField() {
		return sortField;
	}
	public void setSortField(String sortField) {
		this.sortField = sortField;
	}
	public String getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}
}
