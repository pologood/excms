package cn.lonsun.weibo.entity.vo;

import java.util.List;
import java.util.Map;

/**
 * 分页查询返回对象
 * @author xujh
 * @param <T>
 */
public class WeiboPagination {
	//支持分页获取的最多记录数量
	public static int MAX_SIZE = 100;
	
	private List<?> data;

	private Map<String,?> map;

	private Long total;

	private Integer pageSize = 15;

	private Long pageIndex = 0L;

	private Long pageCount = 0L;//总页数
	
	private String linkPrefix;

	public WeiboPagination() {

	}

	public WeiboPagination(List<?> data, Long total, Integer pageSize,
						   Long pageIndex) {
		this.data = data;
		this.total = total;
		this.pageSize = WeiboPagination.getPageSize(pageSize);
		this.pageIndex = pageIndex;
	}

	public List<?> getData() {
		return data;
	}

	public void setData(List<?> data) {
		this.data = data;
	}

	public Map<String, ?> getMap() {
		return map;
	}

	public void setMap(Map<String, ?> map) {
		this.map = map;
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

	public String getLinkPrefix() {
		return linkPrefix;
	}

	public void setLinkPrefix(String linkPrefix) {
		this.linkPrefix = linkPrefix;
	}
	
}
