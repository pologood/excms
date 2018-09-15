
package cn.lonsun.ldap.internal.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.CommunicationException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.ServiceUnavailableException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.exception.BusinessException;
import cn.lonsun.core.exception.RecordsException;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.ldap.internal.exception.CommunicationRuntimeException;
import cn.lonsun.ldap.internal.exception.NameRepeatedException;
import cn.lonsun.ldap.internal.exception.SortNumRepeatedException;
import cn.lonsun.ldap.internal.service.ILdapPersonService;
import cn.lonsun.ldap.internal.util.AttributeUtil;
import cn.lonsun.ldap.internal.util.EntityBuilder;
import cn.lonsun.ldap.internal.util.LDAPUtil;
import cn.lonsun.ldap.vo.InetOrgPersonVO;
import cn.lonsun.rbac.internal.entity.PersonEO;
import cn.lonsun.rbac.internal.entity.UserEO;

@Service("ldapPersonService")
public class LdapPersonServiceImpl implements ILdapPersonService {
	
	@Override
	public void updateIsPluralistic(String dn,Boolean isPluralistic,String uid,String md5Pwd){
		// 待更新的属性
		Attributes updateAttrs = new BasicAttributes();
		Attributes removeAttrs = new BasicAttributes();
		Attributes addAttrs = new BasicAttributes();
		// 是否是兼职人员
		updateAttrs.put("sn", isPluralistic ? "true" : "false");
		if(isPluralistic){
			//设置为兼职，那么需要移除原有的uid和密码
			removeAttrs.put("uid",uid);
			removeAttrs.put("userPassword",md5Pwd);
		}else{
			//设为宿主，那么要新增uid和userPassword
			addAttrs.put("uid",uid);
			addAttrs.put("userPassword",md5Pwd);
		}
		DirContext dc = null;
		try {
			// 获取LDAP上下文
			dc = LDAPUtil.getDirContext();
			dc.modifyAttributes(dn, DirContext.REPLACE_ATTRIBUTE,updateAttrs);
			if(removeAttrs.size()>0){
				dc.modifyAttributes(dn, DirContext.REMOVE_ATTRIBUTE,removeAttrs);
			}
			if(addAttrs.size()>0){
				dc.modifyAttributes(dn, DirContext.ADD_ATTRIBUTE,addAttrs);
			}
		} catch (NamingException e) {
			e.printStackTrace();
			if (e instanceof CommunicationException|| e instanceof ServiceUnavailableException) {
				throw new CommunicationRuntimeException();
			} else {
				throw new BaseRunTimeException();
			}
		}finally{
			try {
				dc.close();
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void saveWithNoVertiry(String parentDn, PersonEO person) {
		// 保存
		DirContext dc = null;
		try {
			// 获取LDAP上下文
			dc = LDAPUtil.getDirContext();
			BasicAttributes attrs = new BasicAttributes();
			BasicAttribute objclassSet = new BasicAttribute("objectClass");
			objclassSet.add("top");
			objclassSet.add("inetOrgPerson");
			attrs.put(objclassSet);
			// 生成simpleDn
			String simpleDn = LDAPUtil.getSimpleDn();
			String dn = "cn=".concat(simpleDn).concat(",").concat(parentDn);
			person.setDn(dn);
			attrs.put("cn", simpleDn);
			// 姓名
			if (!StringUtils.isEmpty(person.getName())) {
				attrs.put("givenName", person.getName());
			}
			// 职务
			if (!StringUtils.isEmpty(person.getPositions())) {
				attrs.put("initials", person.getPositions());
			}
			// 移动电话
			if (!StringUtils.isEmpty(person.getMobile())) {
				attrs.put("mobile", person.getMobile());
			}
			// 是否是兼职人员
			attrs.put("sn", person.getIsPluralistic() ? "true" : "false");
			// 办公电话
			if (!StringUtils.isEmpty(person.getOfficePhone())) {
				attrs.put("postOfficeBox", person.getOfficePhone());
			}
			// 办公地址
			if (!StringUtils.isEmpty(person.getOfficeAddress())) {
				attrs.put("postalCode", person.getOfficeAddress());
			}
			// 邮箱
			if (!StringUtils.isEmpty(person.getMail())) {
				attrs.put("mail", person.getMail());
			}
			// 姓名全拼
			if (!StringUtils.isEmpty(person.getFullPy())) {
				attrs.put("physicalDeliveryOfficeName", person.getFullPy());
			}
			// 姓名简拼
			if (!StringUtils.isEmpty(person.getSimplePy())) {
				attrs.put("preferredLanguage", person.getSimplePy());
			}
			// 是否创建政务邮箱
			if (!StringUtils.isEmpty(person.getJpegPhoto())) {
				attrs.put("jpegPhoto", person.getJpegPhoto());
			}
			// 排序号
			if (!StringUtils.isEmpty(person.getSortNum())) {
				attrs.put("street", person.getSortNum().toString());
			}
			dc.createSubcontext(dn, attrs);
		} catch (NamingException e) {
			e.printStackTrace();
			if (e instanceof CommunicationException
					|| e instanceof ServiceUnavailableException) {
				throw new CommunicationRuntimeException();
			} else {
				throw new BaseRunTimeException();
			}
		}finally{
			try {
				dc.close();
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void save(String parentDn, PersonEO person) throws BusinessException {
		if (StringUtils.isEmpty(parentDn) || person == null) {
			throw new NullPointerException();
		}
		// 验证1.组织下不能直接保存人员
		if (!parentDn.startsWith("ou")) {
			throw new BusinessException(TipsMode.Key.toString(),
					"PersonEO.PersonStorageError");
		}
		saveWithNoVertiry(parentDn, person);
	}

	@Override
	public boolean isNameExisted(String parentDn, String name) {
		String nameFilter = "(&(objectClass=inetOrgPerson)(givenName=".concat(
				name).concat("))");
		return LDAPUtil.isNodesExisted(parentDn, nameFilter,
				SearchControls.ONELEVEL_SCOPE);
	}

	@Override
	public void delete(String dn) {
		// 入参验证
		if (StringUtils.isEmpty(dn) || !dn.startsWith("cn")) {
			throw new IllegalArgumentException();
		}
		// 获取LDAP上下文
		DirContext dc = null;
		try {
			dc = LDAPUtil.getDirContext();
			dc.destroySubcontext(dn);
		} catch (NamingException e) {
			e.printStackTrace();
			if (e instanceof CommunicationException
					|| e instanceof ServiceUnavailableException) {
				throw new CommunicationRuntimeException();
			} else {
				throw new BaseRunTimeException();
			}
		}finally{
			try {
				dc.close();
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void update(PersonEO person) {
		// 1.获取LDAP上的原始数据
		PersonEO srcPerson = getPerson(person.getDn());
		// 待更新的属性
		Attributes updateAttrs = new BasicAttributes();
		// 待删除的属性
		Attributes removeAttrs = new BasicAttributes();
		// 待添加的属性
		Attributes addAttrs = new BasicAttributes();
		// 排序号
		if (person.getSortNum() != null) {
			if (srcPerson.getSortNum() != person.getSortNum().intValue()) {
				if (srcPerson.getSortNum() == null) {
					// 新增属性
					addAttrs.put("street", person.getSortNum().toString());
				} else {
					// 修改属性
					if (person.getSortNum().intValue() != srcPerson.getSortNum()) {
						updateAttrs.put("street", person.getSortNum().toString());
					}
				}
			}
		} else {
			if (srcPerson.getSortNum() != null) {
				removeAttrs.put("street", srcPerson.getSortNum().toString());
			}
		}

		DirContext dc = null;
		try {
			// 获取LDAP上下文
			dc = LDAPUtil.getDirContext();
			// 姓名
			if (!StringUtils.isEmpty(person.getName())) {
				// 2.如果姓名被修改，那么验证姓名是否重复
				if (StringUtils.isEmpty(srcPerson.getName())) {
					addAttrs.put("givenName", person.getName());
				} else {
					if (!person.getName().equals(srcPerson.getName())) {
						updateAttrs.put("givenName", person.getName());
					}
				}
			} else {
				if (!StringUtils.isEmpty(srcPerson.getName())) {
					removeAttrs.put("givenName", srcPerson.getName());
				}
			}
			// 职务
			if (!StringUtils.isEmpty(person.getPositions())) {
				if (StringUtils.isEmpty(srcPerson.getPositions())) {
					addAttrs.put("initials", person.getPositions());
				} else {
					if (!person.getPositions().equals(srcPerson.getPositions())) {
						updateAttrs.put("initials", person.getPositions());
					}
				}
			} else {
				if (!StringUtils.isEmpty(srcPerson.getPositions())) {
					removeAttrs.put("initials", srcPerson.getPositions());
				}
			}
			// 是否兼职人员
			if (person.getIsPluralistic() != srcPerson.getIsPluralistic()) {
				updateAttrs.put("sn", person.getIsPluralistic().toString());
			}
			// 移动电话
			if (!StringUtils.isEmpty(person.getMobile())) {
				if (StringUtils.isEmpty(srcPerson.getMobile())) {
					addAttrs.put("mobile", person.getMobile());
				} else {
					if (!person.getMobile().equals(srcPerson.getMobile())) {
						updateAttrs.put("mobile", person.getMobile());
					}

				}
			} else {
				if (!StringUtils.isEmpty(srcPerson.getMobile())) {
					removeAttrs.put("mobile", srcPerson.getMobile());
				}
			}
			// 办公地址
			if (!StringUtils.isEmpty(person.getOfficeAddress())) {
				if (StringUtils.isEmpty(srcPerson.getOfficeAddress())) {
					addAttrs.put("postalCode", person.getOfficeAddress());
				} else {
					if (!person.getOfficeAddress().equals(
							srcPerson.getOfficeAddress())) {
						updateAttrs.put("postalCode",
								person.getOfficeAddress());
					}
				}
			} else {
				if (!StringUtils.isEmpty(srcPerson.getOfficeAddress())) {
					removeAttrs.put("postalCode",
							srcPerson.getOfficeAddress());
				}
			}
			
			// 办公电话
			if (!StringUtils.isEmpty(person.getOfficePhone())) {
				if (StringUtils.isEmpty(srcPerson.getOfficePhone())) {
					addAttrs.put("postOfficeBox", person.getOfficePhone());
				} else {
					if (!person.getOfficePhone().equals(
							srcPerson.getOfficePhone())) {
						updateAttrs.put("postOfficeBox",person.getOfficePhone());
					}
				}
			} else {
				if (!StringUtils.isEmpty(srcPerson.getOfficePhone())) {
					removeAttrs.put("postOfficeBox",srcPerson.getOfficePhone());
				}
			}
			// 邮箱
			if (!StringUtils.isEmpty(person.getMail())) {
				if (StringUtils.isEmpty(srcPerson.getMail())) {
					addAttrs.put("mail", person.getMail());
				} else {
					if (!person.getMail().equals(srcPerson.getMail())) {
						updateAttrs.put("mail", person.getMail());
					}

				}
			} else {
				if (!StringUtils.isEmpty(srcPerson.getMail())) {
					removeAttrs.put("mail", srcPerson.getMail());
				}
			}
			// 姓名全拼
			if (!StringUtils.isEmpty(person.getFullPy())) {
				if (StringUtils.isEmpty(srcPerson.getFullPy())) {
					addAttrs.put("physicalDeliveryOfficeName",
							person.getFullPy());
				} else {
					if (!person.getFullPy().equals(srcPerson.getFullPy())) {
						updateAttrs.put("physicalDeliveryOfficeName",
								person.getFullPy());
					}
				}
			} else {
				if (!StringUtils.isEmpty(srcPerson.getFullPy())) {
					removeAttrs.put("physicalDeliveryOfficeName",
							srcPerson.getFullPy());
				}
			}
			// 姓名简拼
			if (!StringUtils.isEmpty(person.getSimplePy())) {
				if (StringUtils.isEmpty(srcPerson.getSimplePy())) {
					addAttrs.put("preferredLanguage", person.getSimplePy());
				} else {
					if (!person.getSimplePy().equals(srcPerson.getSimplePy())) {
						updateAttrs.put("preferredLanguage",
								person.getSimplePy());
					}
				}
			} else {
				if (!StringUtils.isEmpty(srcPerson.getSimplePy())) {
					removeAttrs.put("preferredLanguage",
							srcPerson.getSimplePy());
				}
			}
			// 更新属性
			dc.modifyAttributes(person.getDn(), DirContext.REMOVE_ATTRIBUTE,
					removeAttrs);
			dc.modifyAttributes(person.getDn(), DirContext.REPLACE_ATTRIBUTE,
					updateAttrs);
			dc.modifyAttributes(person.getDn(), DirContext.ADD_ATTRIBUTE,
					addAttrs);
		} catch (NamingException e) {
			e.printStackTrace();
			if (e instanceof CommunicationException
					|| e instanceof ServiceUnavailableException) {
				throw new CommunicationRuntimeException();
			} else {
				throw new BaseRunTimeException();
			}
		}
	}
	
	@Override
	public void save(PersonEO person, UserEO user) {
		// 1.获取LDAP上的原始数据
		DirContext dc = null;
		try {
			// 获取LDAP上下文
			dc = LDAPUtil.getDirContext();
			BasicAttributes attrs = new BasicAttributes();
			BasicAttribute objclassSet = new BasicAttribute("objectClass");
			objclassSet.add("top");
			objclassSet.add("inetOrgPerson");
			attrs.put(objclassSet);
			String pdn = person.getDn();
			// 获取simpleDn
			int index = pdn.indexOf(",");
			String simpleDn = pdn.substring(0, index).replace("cn=", "");
			attrs.put("cn", simpleDn);
			// 姓名
			if (!StringUtils.isEmpty(person.getName())) {
				attrs.put("givenName", person.getName());
			}
			//平台编码
			if (!StringUtils.isEmpty(person.getPlatformCode())) {
				attrs.put("displayName", person.getPlatformCode());
			}
			// 排序号
			if (person.getSortNum() != null) {
				// 新增属性
				attrs.put("street", person.getSortNum().toString());
			}
			// 职务
			if (!StringUtils.isEmpty(person.getPositions())) {
				attrs.put("initials", person.getPositions());
			}
			// 是否是兼职人员
			attrs.put("sn", person.getIsPluralistic() ? "true" : "false");
			// 办公电话
			if (!StringUtils.isEmpty(person.getOfficePhone())) {
				attrs.put("postOfficeBox", person.getOfficePhone());
			}
			// 移动电话
			if (!StringUtils.isEmpty(person.getMobile())) {
				attrs.put("mobile", person.getMobile());
			}
			// 办公地址
			if (!StringUtils.isEmpty(person.getOfficeAddress())) {
				attrs.put("postalCode", person.getOfficeAddress());
			}
			// 邮箱
			if (!StringUtils.isEmpty(person.getMail())) {
				attrs.put("mail", person.getMail());
			}
			// 姓名全拼
			if (!StringUtils.isEmpty(person.getFullPy())) {
				attrs.put("physicalDeliveryOfficeName", person.getFullPy());
			}
			// 姓名简拼
			if (!StringUtils.isEmpty(person.getSimplePy())) {
				attrs.put("preferredLanguage", person.getSimplePy());
			}
			//用户状态
			attrs.put("title",user.getStatus());
			attrs.put("uid",user.getUid());
			//经过MD5加密的密码
			String md5Pwd = user.getPassword();
			attrs.put("userPassword",md5Pwd);
			dc.createSubcontext(pdn, attrs);
		} catch (NamingException e) {
			e.printStackTrace();
			if (e instanceof CommunicationException
					|| e instanceof ServiceUnavailableException) {
				throw new CommunicationRuntimeException();
			} else {
				throw new BaseRunTimeException();
			}
		}
	}

	public void update(PersonEO person, UserEO user) {
		// 1.获取LDAP上的原始数据
		PersonEO srcPerson = getPerson(person.getDn());
		// 待更新的属性
		Attributes updateAttrs = new BasicAttributes();
		// 待删除的属性
		Attributes removeAttrs = new BasicAttributes();
		// 待添加的属性
		Attributes addAttrs = new BasicAttributes();
		// 排序号
		if (person.getSortNum() != null) {
			if (srcPerson.getSortNum()!=null&&srcPerson.getSortNum() != person.getSortNum().intValue()) {
				if (srcPerson.getSortNum() == null) {
					// 新增属性
					addAttrs.put("street", person.getSortNum().toString());
				} else {
					// 修改属性
					if (person.getSortNum().intValue() != srcPerson
							.getSortNum()) {
						updateAttrs.put("street", person.getSortNum()
								.toString());
					}
				}
			}
		} else {
			if (srcPerson.getSortNum() != null) {
				removeAttrs.put("street", srcPerson.getSortNum().toString());
			}
		}

		DirContext dc = null;
		try {
			// 获取LDAP上下文

			dc = LDAPUtil.getDirContext();
			// 姓名
			if (!StringUtils.isEmpty(person.getName())) {
				// 2.如果姓名被修改，那么验证姓名是否重复
				if (StringUtils.isEmpty(srcPerson.getName())) {
					addAttrs.put("givenName", person.getName());
				} else {
					if (!person.getName().equals(srcPerson.getName())) {
						updateAttrs.put("givenName", person.getName());
					}
				}
			} else {
				if (!StringUtils.isEmpty(srcPerson.getName())) {
					removeAttrs.put("givenName", srcPerson.getName());
				}
			}
			// 职务
			if (!StringUtils.isEmpty(person.getPositions())) {
				if (StringUtils.isEmpty(srcPerson.getPositions())) {
					addAttrs.put("initials", person.getPositions());
				} else {
					if (!person.getPositions().equals(srcPerson.getPositions())) {
						updateAttrs.put("initials", person.getPositions());
					}
				}
			} else {
				if (!StringUtils.isEmpty(srcPerson.getPositions())) {
					removeAttrs.put("initials", srcPerson.getPositions());
				}
			}
			// 是否兼职人员
			if (person.getIsPluralistic() != srcPerson.getIsPluralistic()) {
				updateAttrs.put("sn", person.getIsPluralistic().toString());
			}
			// 办公地址
			if (!StringUtils.isEmpty(person.getOfficeAddress())) {
				if (StringUtils.isEmpty(srcPerson.getOfficeAddress())) {
					addAttrs.put("postalCode", person.getOfficeAddress());
				} else {
					if (!person.getOfficeAddress().equals(
							srcPerson.getOfficeAddress())) {
						updateAttrs.put("postalCode",
								person.getOfficeAddress());
					}
				}
			} else {
				if (!StringUtils.isEmpty(srcPerson.getOfficeAddress())) {
					removeAttrs.put("postalCode",
							srcPerson.getOfficeAddress());
				}
			}
			// 办公电话
			if (!StringUtils.isEmpty(person.getOfficePhone())) {
				if (StringUtils.isEmpty(srcPerson.getOfficePhone())) {
					addAttrs.put("postOfficeBox", person.getOfficePhone());
				} else {
					if (!person.getOfficePhone().equals(
							srcPerson.getOfficePhone())) {
						updateAttrs.put("postOfficeBox",person.getOfficePhone());
					}
				}
			} else {
				if (!StringUtils.isEmpty(srcPerson.getOfficePhone())) {
					removeAttrs.put("postOfficeBox",srcPerson.getOfficePhone());
				}
			}
			// 邮箱
			if (!StringUtils.isEmpty(person.getMail())) {
				if (StringUtils.isEmpty(srcPerson.getMail())) {
					addAttrs.put("mail", person.getMail());
				} else {
					if (!person.getMail().equals(srcPerson.getMail())) {
						updateAttrs.put("mail", person.getMail());
					}

				}
			} else {
				if (!StringUtils.isEmpty(srcPerson.getMail())) {
					removeAttrs.put("mail", srcPerson.getMail());
				}
			}
			// 移动电话
			if (!StringUtils.isEmpty(person.getMobile())) {
				if (StringUtils.isEmpty(srcPerson.getMobile())) {
					addAttrs.put("mobile", person.getMobile());
				} else {
					if (!person.getMobile().equals(srcPerson.getMobile())) {
						updateAttrs.put("mobile", person.getMobile());
					}

				}
			} else {
				if (!StringUtils.isEmpty(srcPerson.getMobile())) {
					removeAttrs.put("mobile", srcPerson.getMobile());
				}
			}
			// 姓名全拼
			if (!StringUtils.isEmpty(person.getFullPy())) {
				if (StringUtils.isEmpty(srcPerson.getFullPy())) {
					addAttrs.put("physicalDeliveryOfficeName",
							person.getFullPy());
				} else {
					if (!person.getFullPy().equals(srcPerson.getFullPy())) {
						updateAttrs.put("physicalDeliveryOfficeName",
								person.getFullPy());
					}
				}
			} else {
				if (!StringUtils.isEmpty(srcPerson.getFullPy())) {
					removeAttrs.put("physicalDeliveryOfficeName",
							srcPerson.getFullPy());
				}
			}
			// 姓名简拼
			if (!StringUtils.isEmpty(person.getSimplePy())) {
				if (StringUtils.isEmpty(srcPerson.getSimplePy())) {
					addAttrs.put("preferredLanguage", person.getSimplePy());
				} else {
					if (!person.getSimplePy().equals(srcPerson.getSimplePy())) {
						updateAttrs.put("preferredLanguage",
								person.getSimplePy());
					}
				}
			} else {
				if (!StringUtils.isEmpty(srcPerson.getSimplePy())) {
					removeAttrs.put("preferredLanguage",
							srcPerson.getSimplePy());
				}
			}
			// 更新属性
			// 用户状态
			updateAttrs.put("title", user.getStatus());
			// 经过MD5加密的密码
			Boolean isPluralistic = person.getIsPluralistic();
			//非兼职才需要更新密码字段
			if(isPluralistic!=null&&!isPluralistic){
				updateAttrs.put("userPassword", user.getPassword() == null ? null: user.getPassword());
			}
			dc.modifyAttributes(person.getDn(), DirContext.REMOVE_ATTRIBUTE,removeAttrs);
			dc.modifyAttributes(person.getDn(), DirContext.REPLACE_ATTRIBUTE,updateAttrs);
			dc.modifyAttributes(person.getDn(), DirContext.ADD_ATTRIBUTE,addAttrs);
		} catch (NamingException e) {
			e.printStackTrace();
			if (e instanceof CommunicationException
					|| e instanceof ServiceUnavailableException) {
				throw new CommunicationRuntimeException();
			} else {
				throw new BaseRunTimeException();
			}
		}finally{
			try {
				dc.close();
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 更新个人排序号
	 * 
	 * @param person
	 * @throws NameRepeatedException
	 */
	public void updateSortNum(String dn, Long sortNum)
			throws SortNumRepeatedException {
		// 1.获取LDAP上的原始数据

		DirContext dc = null;
		try {
			// 获取LDAP上下文

			dc = LDAPUtil.getDirContext();
			Attributes attrs = new BasicAttributes();
			// 排序号
			attrs.put("street", sortNum.toString());
			// 更新属性
			dc.modifyAttributes(dn, DirContext.REPLACE_ATTRIBUTE, attrs);
		} catch (NamingException e) {
			e.printStackTrace();
			if (e instanceof CommunicationException
					|| e instanceof ServiceUnavailableException) {
				throw new CommunicationRuntimeException();
			} else {
				throw new BaseRunTimeException();
			}
		}
	}

	/**
	 * 更新个人信息</br> 当同一级单位下存现姓名相同的人员时，抛出NameRepeatedException</br>
	 * 
	 * @param person
	 * @throws NameRepeatedException
	 * @throws SortNumRepeatedException
	 */
	public void update(List<PersonEO> persons) {
		if (persons != null && persons.size() > 0) {
			for (PersonEO person : persons) {
				update(person);
			}
		}
	}

	@Override
	public PersonEO getPerson(String dn) {
		if (StringUtils.isEmpty(dn) || !dn.startsWith("cn")) {
			throw new IllegalArgumentException();
		}
		// 获取LDAP容器

		DirContext dc = null;
		try {

			dc = LDAPUtil.getDirContext();
			// 查询条件
			SearchControls sc = new SearchControls();
			sc.setSearchScope(SearchControls.OBJECT_SCOPE);
			// LDAP查询
			NamingEnumeration<SearchResult> ne = dc.search(dn,
					"objectClass=inetOrgPerson", sc);
			// 针对查询结果构建组织/单位
			PersonEO person = null;
			while (ne.hasMore()) {
				if (person == null) {
					SearchResult result = ne.next();
					person = EntityBuilder.getPerson(result);
				} else {
					throw new RecordsException();
				}
			}
			return person;
		} catch (NamingException e) {
			if (e instanceof CommunicationException
					|| e instanceof ServiceUnavailableException) {
				throw new CommunicationRuntimeException();
			} else {
				throw new BaseRunTimeException();
			}
		}
	}

	@Override
	public List<PersonEO> getPersons(String organDN, boolean ignorePluralistics) {
		if (StringUtils.isEmpty(organDN) || !organDN.startsWith("ou")) {
			throw new IllegalArgumentException();
		}
		List<PersonEO> persons = null;
		// 缓存中获取
		// 获取LDAP容器

		DirContext dc = null;
		try {
			dc = LDAPUtil.getDirContext();
			// 查询条件
			SearchControls sc = new SearchControls();
			sc.setSearchScope(SearchControls.ONELEVEL_SCOPE);
			// LDAP查询
			String filter = "objectClass=inetOrgPerson";
			if (ignorePluralistics) {
				filter = "(&(objectClass=inetOrgPerson)(postOfficeBox=0))";
			}
			NamingEnumeration<SearchResult> ne = dc.search(organDN, filter, sc);
			persons = new ArrayList<PersonEO>();
			// 针对查询结果构建人员信息
			while (ne.hasMore()) {
				PersonEO person = EntityBuilder.getPerson(ne.next());
				persons.add(person);
			}
			// 将persons按sortNum从小到大排序
			if (persons.size() > 1) {
				Collections.sort(persons, new Comparator<PersonEO>() {
					@Override
					public int compare(PersonEO p1, PersonEO p2) {
						int flag = 0;
						if (p1.getSortNum() == null) {
							flag = -1;
						} else if (p2.getSortNum() == null) {
							flag = 1;
						} else {
							flag = p1.getSortNum().compareTo(p2.getSortNum());
						}
						return flag;
					}
				});
			}
		} catch (NamingException e) {
			e.printStackTrace();
			if (e instanceof CommunicationException
					|| e instanceof ServiceUnavailableException) {
				throw new CommunicationRuntimeException();
			} else {
				throw new BaseRunTimeException();
			}
		}
		return persons;
	}
	
	/**
	 * 根据组织Dn获取下级人员
	 *
	 * @param organDn
	 * @param searchScope  与javax.naming.directory.SearchControls保持一致
	 * @return
	 */
	public List<InetOrgPersonVO> getInetOrgPersons(String organDn,int searchScope){
		List<InetOrgPersonVO> persons = null;
		DirContext dc = null;
		try {
			dc = LDAPUtil.getDirContext();
			// 查询条件
			SearchControls sc = new SearchControls();
			sc.setSearchScope(searchScope);
			// LDAP查询
			String filter = "objectClass=inetOrgPerson";
			NamingEnumeration<SearchResult> ne = dc.search(organDn, filter, sc);
			persons = new ArrayList<InetOrgPersonVO>();
			// 针对查询结果构建人员信息
			while (ne.hasMore()) {
				InetOrgPersonVO person = EntityBuilder.getInetOrgPerson(ne.next());
				persons.add(person);
			}
			// 将persons按sortNum从小到大排序
			if (persons.size() > 1) {
				Collections.sort(persons, new Comparator<InetOrgPersonVO>() {
					@Override
					public int compare(InetOrgPersonVO p1, InetOrgPersonVO p2) {
						int flag = 0;
						if (p1.getSortNum() == null) {
							flag = -1;
						} else if (p2.getSortNum() == null) {
							flag = 1;
						} else {
							flag = p1.getSortNum().compareTo(p2.getSortNum());
						}
						return flag;
					}
				});
			}
		} catch (NamingException e) {
			e.printStackTrace();
			if (e instanceof CommunicationException
					|| e instanceof ServiceUnavailableException) {
				throw new CommunicationRuntimeException();
			} else {
				throw new BaseRunTimeException();
			}
		}
		return persons;
	}
	
	public List<PersonEO> getSubPersons(String organDN,String status){
		if (StringUtils.isEmpty(organDN) || !organDN.startsWith("ou")) {
			throw new IllegalArgumentException();
		}
		List<PersonEO> persons = null;
		// 缓存中获取
		// 获取LDAP容器

		DirContext dc = null;
		try {
			dc = LDAPUtil.getDirContext();
			// 查询条件
			SearchControls sc = new SearchControls();
			sc.setSearchScope(SearchControls.ONELEVEL_SCOPE);
			// LDAP查询
			String filter = "objectClass=inetOrgPerson";
			if (!StringUtils.isEmpty(status)) {
				filter = "(&(objectClass=inetOrgPerson)(title="+status+"))";
			}
			NamingEnumeration<SearchResult> ne = dc.search(organDN, filter, sc);
			persons = new ArrayList<PersonEO>();
			// 针对查询结果构建人员信息
			while (ne.hasMore()) {
				PersonEO person = EntityBuilder.getPerson(ne.next());
				persons.add(person);
			}
			// 将persons按sortNum从小到大排序
			if (persons.size() > 1) {
				Collections.sort(persons, new Comparator<PersonEO>() {
					@Override
					public int compare(PersonEO p1, PersonEO p2) {
						int flag = 0;
						if (p1.getSortNum() == null) {
							flag = -1;
						} else if (p2.getSortNum() == null) {
							flag = 1;
						} else {
							flag = p1.getSortNum().compareTo(p2.getSortNum());
						}
						return flag;
					}
				});
			}
		} catch (NamingException e) {
			e.printStackTrace();
			if (e instanceof CommunicationException
					|| e instanceof ServiceUnavailableException) {
				throw new CommunicationRuntimeException();
			} else {
				throw new BaseRunTimeException();
			}
		}
		return persons;
	}

	public Map<String, List<?>> getPersons(String[] organDns, String name) {
		// 人员容器，默认容量为80
		Map<String, List<?>> map = new HashMap<String, List<?>>(2);
		// 获取LDAP容器
		DirContext dc = null;
		try {
			dc = LDAPUtil.getDirContext();
			// 查询条件
			SearchControls sc = new SearchControls();
			sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
			// LDAP查询
			String filter = "(&(objectClass=inetOrgPerson)(givenName=" + name + "))";
			// 组织、单位dn容器
			List<String> oDns = new ArrayList<String>();
			List<PersonEO> persons = new ArrayList<PersonEO>();
			for (String organDn : organDns) {
				NamingEnumeration<SearchResult> ne = dc.search(organDn, filter,
						sc);
				// 针对查询结果构建人员信息
				while (ne.hasMore()) {
					PersonEO person = EntityBuilder.getPerson(ne.next());
					String parentDn = LDAPUtil.getParentDn(person.getDn());
					// 获取所有非重复的组织/单位dn集合，每个单位的人员DN只执行一次逻辑
					String dn = parentDn;
					while (!oDns.contains(dn) && !organDn.equals(dn)) {
						oDns.add(dn);
						dn = LDAPUtil.getParentDn(dn);
					}
					persons.add(person);
				}
			}
			map.put("organDns", oDns);
			map.put("persons", persons);
		} catch (NamingException e) {
			e.printStackTrace();
			if (e instanceof CommunicationException
					|| e instanceof ServiceUnavailableException) {
				throw new CommunicationRuntimeException();
			} else {
				throw new BaseRunTimeException();
			}
		}
		return map;
	}

	/**
	 * 排序号是否已存在
	 * 
	 * @param parentDn
	 * @param sortNum
	 * @return
	 */
	@Override
	public boolean isSortNumExisted(String parentDn, Long sortNum) {
		String nameFilter = "(&(objectClass=inetOrgPerson)(street=".concat(
				sortNum.toString()).concat("))");
		return LDAPUtil.isNodesExisted(parentDn, nameFilter,
				SearchControls.ONELEVEL_SCOPE);
	}

	@Override
	public List<String> getDns(String[] organDns, String name) {
		// 获取LDAP容器

		DirContext dc = null;
		List<String> dns = null;
		try {

			dc = LDAPUtil.getDirContext();
			// 查询条件
			SearchControls sc = new SearchControls();
			sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
			// LDAP查询
			String filter = "(&(objectClass=inetOrgPerson)(|(givenName=*"
					+ name + "*)(physicalDeliveryOfficeName=*" + name
					+ "*)(preferredLanguage=*" + name + "*)))";
			// 组织、单位dn容器
			dns = new ArrayList<String>();
			for (String organDn : organDns) {
				NamingEnumeration<SearchResult> ne = dc.search(organDn, filter,
						sc);
				// DN是否处理
				// 针对查询结果构建人员信息
				while (ne.hasMore()) {
					String dn = ne.next().getNameInNamespace();
					dns.add(dn);
				}
			}
		} catch (NamingException e) {
			e.printStackTrace();
			if (e instanceof CommunicationException
					|| e instanceof ServiceUnavailableException) {
				throw new CommunicationRuntimeException();
			} else {
				throw new BaseRunTimeException();
			}
		}
		return dns;
	}

	@Override
	public List<String> getNames(String[] organDns, String name, int count) {
		// 获取LDAP容器
		DirContext dc = null;
		List<String> names = null;
		try {
			dc = LDAPUtil.getDirContext();
			// 查询条件
			SearchControls sc = new SearchControls();
			sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
			sc.setReturningAttributes(new String[] { "givenName" });
			// LDAP查询
			Pattern pattern = Pattern.compile("[a-zA-Z_0-9]+");
			Matcher matcher = pattern.matcher(name);
			String filter = null;
			if (matcher.matches()) {
				// 全拼
				filter = "(&(objectClass=inetOrgPerson)(physicalDeliveryOfficeName=*"
						+ name + "*))";
			} else {
				// 中文
				filter = "(&(objectClass=inetOrgPerson)(givenName=*" + name
						+ "*))";
			}
			// 人员姓名容器
			names = new ArrayList<String>();
			// 如果条数已经满足，那么跳出
			for (String organDn : organDns) {
				NamingEnumeration<SearchResult> ne = dc.search(organDn, filter,
						sc);
				// DN是否处理
				// 针对查询结果构建人员信息
				int size = 0;
				while (ne.hasMore()) {
					String fullName = AttributeUtil.getValue("givenName", ne
							.next().getAttributes().get("givenName"));
					// 重名过滤
					if (!names.contains(fullName)) {
						names.add(fullName);
					}
					size++;
					System.out.println("size:" + size);
				}
			}
		} catch (NamingException e) {
			e.printStackTrace();
			if (e instanceof CommunicationException
					|| e instanceof ServiceUnavailableException) {
				throw new CommunicationRuntimeException();
			} else {
				throw new BaseRunTimeException();
			}
		}
		return names;
	}

}
