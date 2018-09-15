package cn.lonsun.content.vo;

import java.util.List;

import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.indicator.internal.entity.FunctionEO;

/**
 * 
 * @ClassName: BaseContentByOptVO
 * @Description: 带操作权限的BaseContent
 * @author Hewbing
 * @date 2016年4月15日 下午5:03:07
 *
 */
public class BaseContentByOptVO extends BaseContentEO {
	
	private static final long serialVersionUID = 12121212l;
	
	private List<FunctionEO> opt;

	public List<FunctionEO> getOpt() {
		return opt;
	}

	public void setOpt(List<FunctionEO> opt) {
		this.opt = opt;
	}
	
}
