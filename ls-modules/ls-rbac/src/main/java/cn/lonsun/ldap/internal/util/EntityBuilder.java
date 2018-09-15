package cn.lonsun.ldap.internal.util;

import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;

import org.apache.commons.lang.StringUtils;

import cn.lonsun.common.vo.TreeNodeVO;
import cn.lonsun.ldap.vo.InetOrgPersonVO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.entity.PersonEO;

/**
 * LDAP获取信息转换成Entity的构造器
 * @author Administrator
 *
 */
public class EntityBuilder {
	
	/**
	 * 通过LDAP的属性构造组织/单位
	 * @param attrs
	 * @return
	 */
	public static OrganEO getOrgan(SearchResult result){
		OrganEO organ = new OrganEO();
		organ.setDn(result.getNameInNamespace());
		Attributes attrs = result.getAttributes();
		String type = AttributeUtil.getValue("businessCategory", attrs.get("businessCategory"));
		if(!StringUtils.isEmpty(type)){
			if(TreeNodeVO.Type.VirtualNode.toString().equals(type)){
				organ.setType(TreeNodeVO.Type.Organ.toString());
				organ.setIsFictitious(Integer.valueOf(1));
			}
			if(TreeNodeVO.Type.Organ.toString().equals(type)){
				organ.setType(TreeNodeVO.Type.Organ.toString());
				organ.setIsFictitious(Integer.valueOf(0));
			}
			if(TreeNodeVO.Type.OrganUnit.toString().equals(type)){
				organ.setType(TreeNodeVO.Type.OrganUnit.toString());
				organ.setIsFictitious(Integer.valueOf(0));
			}
			if(TreeNodeVO.Type.Virtual.toString().equals(type)){
				organ.setType(TreeNodeVO.Type.OrganUnit.toString());
				organ.setIsFictitious(Integer.valueOf(1));
			}
		}
		//名称
		String name = AttributeUtil.getValue("registeredAddress", attrs.get("registeredAddress"));
		organ.setName(name);
		//平台名称
		String platformCode =  AttributeUtil.getValue("physicalDeliveryOfficeName", attrs.get("physicalDeliveryOfficeName"));
		organ.setPlatformCode(platformCode);
		//简称
		String simpleName = AttributeUtil.getValue("postalAddress", attrs.get("postalAddress"));
		organ.setSimpleName(simpleName);
		//描述
		String description = AttributeUtil.getValue("description", attrs.get("description"));
		organ.setDescription(description);
		//排序号
		String street =  AttributeUtil.getValue("street", attrs.get("street"));
		Long sortNum = StringUtils.isEmpty(street)?null:Long.valueOf(street);
		organ.setSortNum(sortNum);
		return organ;
	}
	
	

	/**
	 * 通过LDAP的属性构造组织/单位
	 * @param attrs
	 * @return
	 */
	public static InetOrgPersonVO getInetOrgPerson(SearchResult result){
		Attributes attrs = result.getAttributes();
		InetOrgPersonVO person = new InetOrgPersonVO();
		String dn = result.getNameInNamespace();
		person.setDn(dn);
		//姓名
		String name = AttributeUtil.getValue("givenName", attrs.get("givenName"));
		person.setName(name);
		//职务
		String positions = AttributeUtil.getValue("initials", attrs.get("initials"));
		person.setPositions(positions);
		//移动电话
		String mobile = AttributeUtil.getValue("mobile", attrs.get("mobile"));
		person.setMobile(mobile);
		//办公电话
		String officePhone = AttributeUtil.getValue("postOfficeBox", attrs.get("postOfficeBox"));
		person.setOfficePhone(officePhone);
		//平台编码
		String platfromCode = AttributeUtil.getValue("displayName", attrs.get("displayName"));
		person.setPlatformCode(platfromCode);
		//办公地址
		String officeAddress = AttributeUtil.getValue("postalCode", attrs.get("postalCode"));
		person.setOfficeAddress(officeAddress);
		//邮箱
		String mail = AttributeUtil.getValue("mail", attrs.get("mail"));
		person.setMail(mail);
		//是否是兼职人员
		String pluralisticString = AttributeUtil.getValue("sn", attrs.get("sn"));
		if(!StringUtils.isEmpty(pluralisticString)){
			person.setIsPluralistic("false".equals(pluralisticString)?false:true);
		}
		//姓名全拼
		String fullPY = AttributeUtil.getValue("physicalDeliveryOfficeName", attrs.get("physicalDeliveryOfficeName"));
		person.setFullPy(fullPY);
		//姓名简拼
		String simplePY = AttributeUtil.getValue("preferredLanguage", attrs.get("preferredLanguage"));
		person.setSimplePy(simplePY);
		//排序号
		String street =  AttributeUtil.getValue("street", attrs.get("street"));
		Long sortNum = StringUtils.isEmpty(street)?null:Long.valueOf(street);
		person.setSortNum(sortNum);
		//uid
		String uid = AttributeUtil.getValue("uid", attrs.get("uid"));
		person.setUid(uid);
		//密码
		String desPassword = AttributeUtil.getValue("userPassword", attrs.get("userPassword"));
		person.setDesPassword(desPassword);
		return person;
	}
	
	
	/**
	 * 通过LDAP的属性构造组织/单位
	 * @param attrs
	 * @return
	 */
	public static PersonEO getPerson(SearchResult result){
		Attributes attrs = result.getAttributes();
		PersonEO person = new PersonEO();
		String dn = result.getNameInNamespace();
		person.setDn(dn);
		//姓名
		String name = AttributeUtil.getValue("givenName", attrs.get("givenName"));
		person.setName(name);
		//uid
		String uid = AttributeUtil.getValue("uid", attrs.get("uid"));
		person.setUid(uid);
		//职务
		String positions = AttributeUtil.getValue("initials", attrs.get("initials"));
		person.setPositions(positions);
		//移动电话
		String mobile = AttributeUtil.getValue("mobile", attrs.get("mobile"));
		person.setMobile(mobile);
		//办公电话
		String officePhone = AttributeUtil.getValue("postOfficeBox", attrs.get("postOfficeBox"));
		person.setOfficePhone(officePhone);
		//平台编码
		String platfromCode = AttributeUtil.getValue("displayName", attrs.get("displayName"));
		person.setPlatformCode(platfromCode);
		//办公地址
		String officeAddress = AttributeUtil.getValue("postalCode", attrs.get("postalCode"));
		person.setOfficeAddress(officeAddress);
		//邮箱
		String mail = AttributeUtil.getValue("mail", attrs.get("mail"));
		person.setMail(mail);
		//是否是兼职人员
		String pluralisticString = AttributeUtil.getValue("sn", attrs.get("sn"));
		if(!StringUtils.isEmpty(pluralisticString)){
			person.setIsPluralistic("false".equals(pluralisticString)?false:true);
		}
		//姓名全拼
		String fullPY = AttributeUtil.getValue("physicalDeliveryOfficeName", attrs.get("physicalDeliveryOfficeName"));
		person.setFullPy(fullPY);
		//姓名简拼
		String simplePY = AttributeUtil.getValue("preferredLanguage", attrs.get("preferredLanguage"));
		person.setSimplePy(simplePY);
		//头像
		String jpegPhoto = AttributeUtil.getValue("jpegPhoto", attrs.get("jpegPhoto"));
		person.setJpegPhoto(jpegPhoto);
		//排序号
		String street =  AttributeUtil.getValue("street", attrs.get("street"));
		Long sortNum = StringUtils.isEmpty(street)?null:Long.valueOf(street);
		person.setSortNum(sortNum);
		return person;
	}
}
