package cn.lonsun.monitor.internal.entity;

import cn.lonsun.core.base.entity.ABaseEntity;

import javax.persistence.*;

/**
 *
 * @ClassName: MonitorCodeRegisterEO
 * @Description: 日常监测注册
 * @author liuk
 * @date 2017年12月01日
 *
 */
@Entity
@Table(name="MONITOR_CODE_REGISTER")
public class MonitorCodeRegisterEO extends ABaseEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = -121L;


	//主键
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="ID")
	private Long id;

	//注册码
	@Column(name="CODE")
	private String code;

	//注册状态 1-已注册 0/其他-未注册
	@Column(name="IS_REGISTERED")
	private Integer isRegistered;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getIsRegistered() {
		return isRegistered;
	}

	public void setIsRegistered(Integer isRegistered) {
		this.isRegistered = isRegistered;
	}
}
