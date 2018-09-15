package cn.lonsun.rbac.vo;

import cn.lonsun.common.vo.PageQueryVO;

/**
 * 角色分页查询条件VO
 *
 * @author xujh
 * @version 1.0
 * 2015年1月31日
 *
 */
public class RolePaginationQueryVO extends PageQueryVO{
	//是否系统预定义角色
	private Integer isPredefine;
	
	private Integer isLocked;
	//角色名称
	private String name;
	//角色编码
	private String code;
	//用于模糊搜索
	private String searchStr;
	public Integer getIsPredefine() {
		return isPredefine;
	}
	public void setIsPredefine(Integer isPredefine) {
		this.isPredefine = isPredefine;
	}
	public Integer getIsLocked() {
		return isLocked;
	}
	public void setIsLocked(Integer isLocked) {
		this.isLocked = isLocked;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getSearchStr() {
		return searchStr;
	}
	public void setSearchStr(String searchStr) {
		this.searchStr = searchStr;
	}
}
