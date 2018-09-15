package cn.lonsun.indicator.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

/**
 * 指示器与资源绑定中间表
 * @author xujh
 *
 */
public class IndicatorResourceEO extends AMockEntity {
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 4292542924588507425L;
	
	private Long indicatorResourceId;
	
	private Long indicatorId;
	
	private Long resourceId;
	//是否启用
	private boolean isEnable = false;
	
	public Long getIndicatorResourceId() {
		return indicatorResourceId;
	}

	public void setIndicatorResourceId(Long indicatorResourceId) {
		this.indicatorResourceId = indicatorResourceId;
	}

	public Long getIndicatorId() {
		return indicatorId;
	}

	public void setIndicatorId(Long indicatorId) {
		this.indicatorId = indicatorId;
	}

	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}

	public boolean getIsEnable() {
		return isEnable;
	}

	public void setIsEnable(boolean isEnable) {
		this.isEnable = isEnable;
	}
	
	

}
