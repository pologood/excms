package cn.lonsun.core.util;

import java.util.List;

/**
 * 分页查询返回对象
 * @author xujh
 * @param <T>
 */
public class Pagination{
	//支持分页获取的最多记录数量
	public static int MAX_SIZE = 100;
	
	private List<?> data;

	private Long total;

	private Integer pageSize = 15;

	private Long pageIndex = 0L;

	private Long pageCount = 0L;//总页数
	
	private String linkPrefix;

	public Pagination() {

	}

	public Pagination(List<?> data, Long total, Integer pageSize,
			Long pageIndex) {
		this.data = data;
		this.total = total;
		this.pageSize = Pagination.getPageSize(pageSize);
		this.pageIndex = pageIndex;
	}

	public List<?> getData() {
		return data;
	}

	public void setData(List<?> data) {
		this.data = data;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Long getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(Long pageIndex) {
		this.pageIndex = pageIndex;
	}

	public static Long getStartNumber(Long pageIndex, Integer pageSize) {
//		return (pageIndex - 1) * pageSize;
		//Modify by yangt：分页下标从0开始，获取分页代码列表下标从0计算
		return (pageIndex) * pageSize;
	}

	public static Integer getPageSize(Integer pageSize) {
		if (pageSize.compareTo(0) > 0) {
			return pageSize;
		} else {
			return 100;
		}
	}

	public Long getStartNumber() {
		return pageIndex * pageSize;
	}

	public Integer getPageSize() {
		if (pageSize.compareTo(0) > 0) {
			return pageSize;
		} else {
			return 100;
		}
	}

	public Long getPageCount() {
		if(null != total && null != pageSize){
			pageCount = total / pageSize;
			if(total % pageSize != 0 ){
				pageCount ++;
			}
		}
		return pageCount;
	}

	public void setPageCount(Long pageCount) {
		this.pageCount = pageCount;
	}

	public String getLinkPrefix() {
		return linkPrefix;
	}

	public void setLinkPrefix(String linkPrefix) {
		this.linkPrefix = linkPrefix;
	}
	
}
