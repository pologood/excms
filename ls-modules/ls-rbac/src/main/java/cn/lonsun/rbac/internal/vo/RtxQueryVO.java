package cn.lonsun.rbac.internal.vo;

import cn.lonsun.common.vo.PageQueryVO;

/**
 * rtx分页查询
 * @author HEwb
 * @date -15.4.23
 *
 */
public class RtxQueryVO extends PageQueryVO {
	private Long id;
	private String type;
	private String key;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
}
