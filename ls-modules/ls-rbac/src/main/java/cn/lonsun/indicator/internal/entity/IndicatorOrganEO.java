package cn.lonsun.indicator.internal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.lonsun.core.base.entity.ABaseEntity;

/**
 * 部分应用只展示固定单位的数据，此实体类对象就是用于标记应用与单位显示关系-厂商管理中配置
 *
 * @author xujh
 * @version 1.0
 * 2015年3月12日
 *
 */
@Entity
@Table(name = "RBAC_INDICATOR_ORGAN")
public class IndicatorOrganEO extends ABaseEntity{
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 8107029517555092526L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column
	private Long indicatorOrganId;
	@Column
	private Long unitId;
	@Column
	private Long shortCutId;
	@Column
	private Long menuId;
	@Column
	private Integer sortNum;
	@Column
	private String description;

	public Long getIndicatorOrganId() {
		return indicatorOrganId;
	}

	public void setIndicatorOrganId(Long indicatorOrganId) {
		this.indicatorOrganId = indicatorOrganId;
	}

	public Long getUnitId() {
		return unitId;
	}

	public void setUnitId(Long unitId) {
		this.unitId = unitId;
	}

	public Long getShortCutId() {
		return shortCutId;
	}

	public void setShortCutId(Long shortCutId) {
		this.shortCutId = shortCutId;
	}

	public Long getMenuId() {
		return menuId;
	}

	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}

	public Integer getSortNum() {
		return sortNum;
	}

	public void setSortNum(Integer sortNum) {
		this.sortNum = sortNum;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
