package cn.lonsun.ldap.vo;

import java.io.Serializable;
import java.util.Date;

import org.springframework.beans.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;

import cn.lonsun.common.vo.TreeNodeVO;
import cn.lonsun.common.vo.TreeNodeVO.Icon;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.rbac.internal.entity.OrganEO;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Column;

/**
 * 组织架构树管理前后端交互VO
 *
 * @Description:
 * @author xujh
 * @date 2014年9月23日 下午10:09:14
 * @version V1.0
 */
public class OrganNodeVO implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 2810773358959626316L;
	//父节点类型
	private String parentNodeType;
	//父节点是否是虚节点
	private Integer isParentFictitious = Integer.valueOf(0);
	// 节点类型
	private String nodeType;
	private Long id;
	private Long organId;
	// 组织/单位名称
	private String name;
	// 组织/单位简称
	private String simpleName;
	// 
	private String type;
	// 是否是虚拟单位
	private Integer fictitious = Integer.valueOf(0);// 0表示非虚拟节点/部门，1表示虚拟节点/部门
	// 描述
	private String description;
	// 父组织或单位的主键
	private Long pid;
	// 用户在LDAP上的DN
	private String dn;
	// 排序码-用于查询排序
	private Long sortNum;
	private Long parentId;
	private String recordStatus = AMockEntity.RecordStatus.Normal.toString(); // 记录状态，正常：Normal,已删除:Removed
	private Long createUserId;// 创建人ID
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private Date createDate = new Date();// 创建时间
	private Long updateUserId;// 更新人ID
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private Date updateDate = new Date();// 更新时间
	private boolean isParent = false;
	private boolean hasPersons = false;
	private boolean hasOrgans = false;
	//是否外部用户
	private Boolean isExternal = Boolean.FALSE;

	private String icon;

	private Long siteId;

	private String officePhone;
	// 办公地址
	private String officeAddress;

	private String servePhone;

	// 服務地址
	private String serveAddress;

	// 單位網址
	private String organUrl;

	// 單位编码
	private String code;

	private Integer isPublic;

	// 单位负责人
	private String headPerson;

	// 职务
	private String positions;

	private Integer count;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSimpleName() {
		return simpleName;
	}

	public void setSimpleName(String simpleName) {
		this.simpleName = simpleName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getFictitious() {
		return fictitious;
	}

	public void setFictitious(Integer fictitious) {
		this.fictitious = fictitious;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDn() {
		return dn;
	}

	public void setDn(String dn) {
		this.dn = dn;
	}

	public Long getSortNum() {
		return sortNum;
	}

	public void setSortNum(Long sortNum) {
		this.sortNum = sortNum;
	}

	public String getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

	public Long getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(Long createUserId) {
		this.createUserId = createUserId;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Long getUpdateUserId() {
		return updateUserId;
	}

	public void setUpdateUserId(Long updateUserId) {
		this.updateUserId = updateUserId;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}

	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public Boolean getIsParent() {
		return isParent;
	}

	public void setIsParent(Boolean isParent) {
		this.isParent = isParent;
	}

	public Boolean getHasPersons() {
		return hasPersons;
	}

	public void setHasPersons(Boolean hasPersons) {
		this.hasPersons = hasPersons;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Long getOrganId() {
		return organId;
	}

	public void setOrganId(Long organId) {
		this.organId = organId;
	}

	public boolean getHasOrgans() {
		return hasOrgans;
	}

	public void setHasOrgans(boolean hasOrgans) {
		this.hasOrgans = hasOrgans;
	}

	public Boolean getIsExternal() {
		return isExternal;
	}

	public void setIsExternal(Boolean isExternal) {
		this.isExternal = isExternal;
	}

	public String getParentNodeType() {
		return parentNodeType;
	}

	public void setParentNodeType(String parentNodeType) {
		this.parentNodeType = parentNodeType;
	}


	public Integer getIsParentFictitious() {
		return isParentFictitious;
	}

	public void setIsParentFictitious(Integer isParentFictitious) {
		this.isParentFictitious = isParentFictitious;
	}

	public void addProperties(OrganEO organ,int flag){
		BeanUtils.copyProperties(organ, this);
		this.setHasOrgans(organ.getHasOrgans() == 1 ? true : false);
		this.setId(organ.getOrganId());
		this.setPid(organ.getParentId());
		this.setIsExternal(organ.getIsExternal());
		if (Integer.valueOf(organ.getHasOrgans()) == 1) {
			this.setIsParent(true);
		}
		if (organ.getType().equals(TreeNodeVO.Type.Organ.toString())) {
			if (Integer.valueOf(organ.getIsFictitious()) == 1) {
				this.setNodeType(TreeNodeVO.Type.VirtualNode.toString());
				this.setIcon(Icon.VirtualNode.getValue());
			} else {
				this.setNodeType(TreeNodeVO.Type.Organ.toString());
				this.setIcon(Icon.Organ.getValue());
			}
		} else {
			if (Integer.valueOf(organ.getIsFictitious()) == 1) {
				this.setNodeType(TreeNodeVO.Type.Virtual.toString());
				this.setIcon(Icon.Virtual.getValue());
			} else {
				this.setNodeType(TreeNodeVO.Type.OrganUnit.toString());
				this.setIcon(Icon.OrganUnit.getValue());
			}
		}
		if (organ.getHasOrgans() == 1) {
			this.setHasOrgans(true);
		}
		if (organ.getHasPersons() == 1) {
			this.setHasPersons(true);
		}
		Integer hasOrgans = organ.getHasOrgans();
		Integer hasOrganUnits = organ.getHasOrganUnits();
		Integer hasFictitiousUnits = organ.getHasFictitiousUnits();
		switch (flag) {
			case 0:
				if (hasOrgans == 1 || hasOrganUnits == 1 || hasFictitiousUnits == 1) {
					this.setIsParent(true);
				}
				break;
			case 1:
				if (hasOrgans == 1 || hasOrganUnits == 1 || hasFictitiousUnits == 1 || organ.getHasPersons() == 1) {
					this.setIsParent(true);
				}
				break;
			case 2:
				// 当组织或单位下不存在人员且不存在子组织和单位时，设置当前节点的isParent为false
				if (hasOrgans == 1 || organ.getHasRoles() == 1) {
					this.setIsParent(true);
				}
				break;
			default:
				break;
		}
	}

	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	public String getOfficePhone() {
		return officePhone;
	}

	public String getOfficeAddress() {
		return officeAddress;
	}

	public void setOfficePhone(String officePhone) {
		this.officePhone = officePhone;
	}

	public void setOfficeAddress(String officeAddress) {
		this.officeAddress = officeAddress;
	}

	public String getOrganUrl() {
		return organUrl;
	}

	public void setOrganUrl(String organUrl) {
		this.organUrl = organUrl;
	}

	public String getServeAddress() {
		return serveAddress;
	}

	public void setServeAddress(String serveAddress) {
		this.serveAddress = serveAddress;
	}

	public String getServePhone() {
		return servePhone;
	}

	public void setServePhone(String servePhone) {
		this.servePhone = servePhone;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getIsPublic() {
		return isPublic;
	}

	public void setIsPublic(Integer isPublic) {
		this.isPublic = isPublic;
	}

	public String getPositions() {
		return positions;
	}

	public void setPositions(String positions) {
		this.positions = positions;
	}

	public String getHeadPerson() {
		return headPerson;
	}

	public void setHeadPerson(String headPerson) {
		this.headPerson = headPerson;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}
}
