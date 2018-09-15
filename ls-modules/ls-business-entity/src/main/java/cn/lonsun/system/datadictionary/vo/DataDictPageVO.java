package cn.lonsun.system.datadictionary.vo;

import cn.lonsun.common.vo.PageQueryVO;

/**
 * 
 * @ClassName: DataDictPageVO
 * @Description: 
 * @author Hewbing
 * @date 2015年8月24日 上午11:19:52
 *
 */
public class DataDictPageVO extends PageQueryVO {
	private String code;
	private String name;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
