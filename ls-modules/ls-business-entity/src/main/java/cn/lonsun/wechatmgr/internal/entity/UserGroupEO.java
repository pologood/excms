package cn.lonsun.wechatmgr.internal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.lonsun.core.base.entity.AMockEntity;

/**
 * 
 * @ClassName: UserGroupEO
 * @Description: 关注用户分组表
 * @author Hewbing
 * @date 2015年12月22日 下午8:17:47
 *
 */
@Entity
@Table(name="CMS_WECHAT_USER_GROUP")
public class UserGroupEO extends AMockEntity {
	
	private static final long serialVersionUID = 121211L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	private Long id;
	
	@Column(name="SITE_ID")
	private Long siteId;
	//微信分组id
	@Column(name="GROUP_ID")
	private Long groupid;
	//组名
	@Column(name="name")
	private String name;
	
	@Column(name="COUNT")
	private Long count;
	//备注
	@Column(name="REMARK")
	private String remark;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	public Long getGroupid() {
		return groupid;
	}

	public void setGroupid(Long groupid) {
		this.groupid = groupid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
