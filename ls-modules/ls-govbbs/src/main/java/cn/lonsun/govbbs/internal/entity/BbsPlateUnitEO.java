package cn.lonsun.govbbs.internal.entity;

import cn.lonsun.core.base.entity.ABaseEntity;
import javax.persistence.*;

@Entity
@Table(name="CMS_BBS_PLATE_UNIT")
public class BbsPlateUnitEO extends ABaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="PLATE_UNIT_ID")
	private Long plateUnitId;

	//(站点id)	
	@Column(name="PLATE_ID")
	private Long plateId;
	
	@Column(name="UNIT_ID")
	private Long unitId;
	
	@Column(name="UNIT_NAME")
	private String unitName;

	public Long getPlateUnitId() {
		return plateUnitId;
	}

	public Long getPlateId() {
		return plateId;
	}

	public Long getUnitId() {
		return unitId;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setPlateUnitId(Long plateUnitId) {
		this.plateUnitId = plateUnitId;
	}

	public void setPlateId(Long plateId) {
		this.plateId = plateId;
	}

	public void setUnitId(Long unitId) {
		this.unitId = unitId;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}
	
	
	
}
