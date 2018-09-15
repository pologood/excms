package cn.lonsun.ldap.internal.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import cn.lonsun.common.vo.TreeNodeVO;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.ldap.internal.exception.CommunicationRuntimeException;
import cn.lonsun.ldap.internal.service.ILdapOrganService;
import cn.lonsun.ldap.internal.util.EntityBuilder;
import cn.lonsun.ldap.internal.util.LDAPUtil;
import cn.lonsun.rbac.internal.entity.OrganEO;

@Service("ldapOrganService")
public class LdapOrganServiceImpl implements ILdapOrganService {
	
	@Override
	public List<OrganEO> getOrgans(List<String> rootDns, List<String> types,
			int scope, String blurryName) {
		// 入参验证
		List<OrganEO> organs = new ArrayList<OrganEO>();

		DirContext dc = null;
		try {
			// 获取LDAP容器
			dc = LDAPUtil.getDirContext();
			String filter = getFilter(types, blurryName);
			// 查询条件
			SearchControls sc = new SearchControls();
			sc.setSearchScope(scope);
			for (String rootDn : rootDns) {
				// LDAP查询
				NamingEnumeration<SearchResult> ne = dc.search(rootDn, filter,
						sc);
				// 针对查询结果构建组织/单位集合
				if (ne != null) {
					while (ne.hasMore()) {
						organs.add(EntityBuilder.getOrgan(ne.next()));
					}
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
		return organs;
	}

	/**
	 * 构建过滤器
	 * 
	 * @param types
	 * @param blurryName
	 * @return
	 */
	private String getFilter(List<String> types, String blurryName) {
		StringBuffer filter = new StringBuffer();
		if (types != null && types.size() > 0) {
			if (types.size() > 1) {
				filter.append("(|");
			}
			if (types.contains(TreeNodeVO.Type.Organ.toString())) {
				filter.append("(businessCategory=Organ)");
			}
			if (types.contains(TreeNodeVO.Type.OrganUnit.toString())) {
				filter.append("(businessCategory=OrganUnit)");
			}
			if (types.contains(TreeNodeVO.Type.Virtual.toString())) {
				filter.append("(businessCategory=Virtual)");
			}
			if (types.size() > 1) {
				filter.append(")");
			}
		}
		return filter.toString();
	}

	@Override
	public boolean isNameExisted(String parentDn, String type, String name) {
		// 查询过滤器
		String nameFilter = "(&(registeredAddress=".concat(name).concat(")");
		if (StringUtils.isEmpty(type)) {
			nameFilter = nameFilter
					.concat("(|(objectClass=organization)(objectClass=organizationalUnit)))");
		} else {
			if (TreeNodeVO.Type.Organ.toString().equals(type)) {
				nameFilter = nameFilter.concat("(objectClass=organization))");
			} else {
				nameFilter = nameFilter
						.concat("(objectClass=organizationalUnit))");
			}
		}
		return LDAPUtil.isNodesExisted(parentDn, nameFilter,
				SearchControls.ONELEVEL_SCOPE);
	}

	@Override
	public boolean isSortNumExisted(String parentDn, String type, String sortNum) {
		// 设置过滤器
		String nameFilter = "(&(street=".concat(sortNum).concat(")");
		if (TreeNodeVO.Type.Organ.toString().equals(type)) {
			nameFilter = nameFilter.concat("(objectClass=organization))");
		} else {
			nameFilter = nameFilter.concat("(objectClass=organizationalUnit))");
		}
		return LDAPUtil.isNodesExisted(parentDn, nameFilter,
				SearchControls.ONELEVEL_SCOPE);
	}

	@Override
	public boolean hasSuns(String parentDn) {
		boolean hasSons = false;
		DirContext dc = null;
		// 获取LDAP上下文
		try {
			dc = LDAPUtil.getDirContext();
			SearchControls sc = new SearchControls();
			sc.setSearchScope(SearchControls.ONELEVEL_SCOPE);
			NamingEnumeration<SearchResult> ne = dc.search(parentDn,
					"objectClass=*", sc);
			// 判断组织或单位下是否存在子节点
			if (ne != null && ne.hasMore()) {
				hasSons = true;
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
		return hasSons;
	}

	@Override
	public boolean hasOrganSuns(String parentDn) {
		boolean hasSons = false;
		DirContext dc = null;
		// 获取LDAP上下文
		try {
			dc = LDAPUtil.getDirContext();
			SearchControls sc = new SearchControls();
			sc.setSearchScope(SearchControls.ONELEVEL_SCOPE);
			NamingEnumeration<SearchResult> ne = dc
					.search(parentDn,
							"(1(objectClass=organization)(objectClass=organizationalUnit))",
							sc);
			// 判断组织或单位下是否存在子节点
			if (ne != null && ne.hasMore()) {
				hasSons = true;
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
		return hasSons;
	}

	@Override
	public void save(String simpleDn, OrganEO organ) {

		DirContext dc = null;
		try {
			// 获取LDAP上下文
			dc = LDAPUtil.getDirContext();
			BasicAttributes attrs = new BasicAttributes();
			BasicAttribute objclassSet = new BasicAttribute("objectClass");
			objclassSet.add("top");
			// 生成新的DN
			String dn = organ.getDn();
			// 记录类型
			String type = TreeNodeVO.Type.Organ.toString();
			if (TreeNodeVO.Type.Organ.toString().equals(organ.getType())) {
				// 组织
				objclassSet.add("organization");
				attrs.put("o", simpleDn);
				Integer isFictitious = organ.getIsFictitious();
				if (isFictitious != null && isFictitious == 1) {
					type = TreeNodeVO.Type.VirtualNode.toString();
				} else {
					type = TreeNodeVO.Type.Organ.toString();
				}
			} else {
				// 单位
				objclassSet.add("organizationalUnit");
				attrs.put("ou", simpleDn);
				Integer isFictitious = organ.getIsFictitious();
				if (isFictitious != null && isFictitious == 1) {
					type = TreeNodeVO.Type.Virtual.toString();
				} else {
					type = TreeNodeVO.Type.OrganUnit.toString();
				}
			}
			attrs.put("businessCategory", type);
			// 添加dn属性值
			organ.setDn(dn);
			attrs.put(objclassSet);
			// 名称,一定不为空
			attrs.put("registeredAddress", organ.getName());
			// 平台编码-physicalDeliveryOfficeName
			if (!StringUtils.isEmpty(organ.getPlatformCode())) {
				attrs.put("physicalDeliveryOfficeName", organ.getPlatformCode());
			}
			// 简称
			if (!StringUtils.isEmpty(organ.getSimpleName())) {
				attrs.put("postalAddress", organ.getSimpleName());
			}
			// 描述
			if (!StringUtils.isEmpty(organ.getDescription())) {
				attrs.put("description", organ.getDescription());
			}
			// 排序号
			if (organ.getSortNum() != null && organ.getSortNum() >= 0) {
				attrs.put("street", organ.getSortNum().toString());
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
		}
	}

	@Override
	public void delete(String dn) {
		DirContext dc = null;
		try {
			// 获取LDAP上下文
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
		}
	}

	@Override
	public void update(OrganEO organ) {
		// 1.获取源组织/单位
		OrganEO srcOrgan = getOrgan(organ.getDn());
		// 待更新的属性
		Attributes updateAttrs = new BasicAttributes();
		// 待删除的属性
		Attributes removeAttrs = new BasicAttributes();
		// 待添加的属性
		Attributes addAttrs = new BasicAttributes();
		// 名称,一定不为空
		if (!organ.getName().equals(srcOrgan.getName())) {
			updateAttrs.put("registeredAddress", organ.getName());
			// 平台编码-physicalDeliveryOfficeName
			updateAttrs.put("physicalDeliveryOfficeName", organ.getPlatformCode());
		}
		// 简称
		if (!StringUtils.isEmpty(organ.getSimpleName())) {
			if (StringUtils.isEmpty(srcOrgan.getSimpleName())) {
				addAttrs.put("postalAddress", organ.getSimpleName());
			} else {
				if (!organ.getSimpleName().equals(srcOrgan.getSimpleName())) {
					updateAttrs.put("postalAddress", organ.getSimpleName());
				}
			}
		} else {
			if (!StringUtils.isEmpty(srcOrgan.getSimpleName())) {
				removeAttrs.put("postalAddress", srcOrgan.getSimpleName());
			}
		}
		// 排序号
		Long sortNum = organ.getSortNum();
		Long srcSortNum = srcOrgan.getSortNum();
		if (sortNum != null) {
			if (srcSortNum == null) {
				addAttrs.put("street", sortNum.toString());
			} else {
				if (sortNum.longValue() != srcSortNum.longValue()) {
					updateAttrs.put("street", sortNum.toString());
				}
			}
		} else {
			if (srcOrgan.getSortNum() != null) {
				removeAttrs.put("street", srcSortNum.toString());
			}
		}
		// 描述
		if (!StringUtils.isEmpty(organ.getDescription())) {
			if (StringUtils.isEmpty(srcOrgan.getDescription())) {
				addAttrs.put("description", organ.getDescription());
			} else {
				if (!organ.getDescription().equals(srcOrgan.getDescription())) {
					updateAttrs.put("description", organ.getDescription());
				}
			}
		} else {
			if (!StringUtils.isEmpty(srcOrgan.getDescription())) {
				removeAttrs.put("description", srcOrgan.getDescription());
			}
		}
		DirContext dc = null;
		try {
			dc = LDAPUtil.getDirContext();
			// 更新属性
			dc.modifyAttributes(organ.getDn(), DirContext.REMOVE_ATTRIBUTE,
					removeAttrs);
			dc.modifyAttributes(organ.getDn(), DirContext.REPLACE_ATTRIBUTE,
					updateAttrs);
			dc.modifyAttributes(organ.getDn(), DirContext.ADD_ATTRIBUTE,
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
	public void updateSortNum(String dn, Long sortNum) {
		Attributes attrs = new BasicAttributes();
		// 排序号
		attrs.put("street", sortNum.toString());
		// 描述

		DirContext dc = null;
		try {
			dc = LDAPUtil.getDirContext();
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

	@Override
	public OrganEO getOrgan(String dn) {
		// 获取LDAP容器

		DirContext dc = null;
		try {

			dc = LDAPUtil.getDirContext();
			// 查询条件
			SearchControls sc = new SearchControls();
			sc.setSearchScope(SearchControls.OBJECT_SCOPE);
			// LDAP查询
			NamingEnumeration<SearchResult> ne = dc.search(dn, "objectClass=*",
					sc);
			// 针对查询结果构建组织/单位
			OrganEO organ = null;
			while (ne.hasMore()) {
				organ = EntityBuilder.getOrgan(ne.next());
			}
			return organ;
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
	public List<OrganEO> getOrgansByPlatformCode(String parentDn,String[] types,String platformCode){
		// 入参验证
		List<OrganEO> organs = null;
		organs = new ArrayList<OrganEO>();
		DirContext dc = null;
		try {
			// 获取LDAP容器
			dc = LDAPUtil.getDirContext();
			//dn过滤
			String filter = "(|(objectClass=organization)(objectClass=organizationalUnit))";
			//节点类型businessCategory,节点类型过滤
			String typeFilter = null;
			if(types!=null&&types.length>0){
				typeFilter ="(|";
				for(String type:types){
					typeFilter = typeFilter+"(businessCategory="+type+")";
				}
				typeFilter = typeFilter+")";
			}
			//平台编码过滤
			String platformCodeFilter = null;
			if(!StringUtils.isEmpty(platformCode)){
				platformCodeFilter = "(physicalDeliveryOfficeName="+platformCode+")";
			}
			filter = "(&"+filter;
			if(!StringUtils.isEmpty(typeFilter)){
				filter = filter+typeFilter;
			}
			if(!StringUtils.isEmpty(platformCodeFilter)){
				filter = filter+platformCodeFilter;
			}
			filter = filter+")";
			// 查询条件
			SearchControls sc = new SearchControls();
			sc.setSearchScope(SearchControls.ONELEVEL_SCOPE);
			// LDAP查询
			NamingEnumeration<SearchResult> ne = dc.search(parentDn, filter, sc);
			// 针对查询结果构建组织/单位集合
			if (ne != null) {
				while (ne.hasMore()) {
					OrganEO organ = EntityBuilder.getOrgan(ne.next());
					organs.add(organ);
				}
			}
			// 将organs按sortNum从小到大排序
			if (organs.size() > 1) {
				Collections.sort(organs, new Comparator<OrganEO>() {
					@Override
					public int compare(OrganEO o1, OrganEO o2) {
						int flag = 0;
						if (o1.getSortNum() == null) {
							flag = -1;
						} else if (o2.getSortNum() == null) {
							flag = 1;
						} else {
							flag = o1.getSortNum().compareTo(o2.getSortNum());
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
		return organs;
	}
	
	@Override
	public List<OrganEO> getOrgans(String parentDn) {
		// 入参验证
		List<OrganEO> organs = null;
		organs = new ArrayList<OrganEO>();
		DirContext dc = null;
		try {
			// 获取LDAP容器

			dc = LDAPUtil.getDirContext();
			String filter = "(|(objectClass=organization)(objectClass=organizationalUnit))";
			// 查询条件
			SearchControls sc = new SearchControls();
			sc.setSearchScope(SearchControls.ONELEVEL_SCOPE);
			// LDAP查询
			NamingEnumeration<SearchResult> ne = dc.search(parentDn, filter, sc);
			// 针对查询结果构建组织/单位集合
			if (ne != null) {
				while (ne.hasMore()) {
					OrganEO organ = EntityBuilder.getOrgan(ne.next());
					organs.add(organ);
				}
			}
			// 将organs按sortNum从小到大排序
			if (organs.size() > 1) {
				Collections.sort(organs, new Comparator<OrganEO>() {
					@Override
					public int compare(OrganEO o1, OrganEO o2) {
						int flag = 0;
						if (o1.getSortNum() == null) {
							flag = -1;
						} else if (o2.getSortNum() == null) {
							flag = 1;
						} else {
							flag = o1.getSortNum().compareTo(o2.getSortNum());
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
		}finally{
			try {
				dc.close();
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
		return organs;
	}

	@Override
	public List<OrganEO> getUnits(String parentDn,String platformCode) {
		// 入参验证
		List<OrganEO> organs = null;
		organs = new ArrayList<OrganEO>();

		DirContext dc = null;
		try {
			// 获取LDAP容器

			dc = LDAPUtil.getDirContext();
			String filter = "(objectClass=organization)";
			if(!StringUtils.isEmpty(platformCode)){
				filter = "(&"+filter+"(physicalDeliveryOfficeName="+platformCode+"))";
			}
			// 查询条件
			SearchControls sc = new SearchControls();
			sc.setSearchScope(SearchControls.ONELEVEL_SCOPE);
			// LDAP查询
			NamingEnumeration<SearchResult> ne = dc
					.search(parentDn, filter, sc);
			// 针对查询结果构建组织/单位集合
			if (ne != null) {
				while (ne.hasMore()) {
					OrganEO organ = EntityBuilder.getOrgan(ne.next());
					organs.add(organ);
				}
			}
			// 将organs按sortNum从小到大排序
			if (organs.size() > 1) {
				Collections.sort(organs, new Comparator<OrganEO>() {
					@Override
					public int compare(OrganEO o1, OrganEO o2) {
						int flag = 0;
						if (o1.getSortNum() == null) {
							flag = -1;
						} else if (o2.getSortNum() == null) {
							flag = 1;
						} else {
							flag = o1.getSortNum().compareTo(o2.getSortNum());
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
		}finally{
			try {
				dc.close();
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
		return organs;
	}

	@Override
	public List<OrganEO> getOrgans(String parentDn, String type) {
		// 入参验证
		List<OrganEO> organs = null;
		organs = new ArrayList<OrganEO>();

		DirContext dc = null;
		try {
			// 获取LDAP容器

			dc = LDAPUtil.getDirContext();
			String filter = null;
			if (TreeNodeVO.Type.Organ.toString().equals(type)) {
				filter = "(objectClass=organization)";
			} else {
				filter = "(objectClass=organizationalUnit)";
			}
			// 查询条件
			SearchControls sc = new SearchControls();
			sc.setSearchScope(SearchControls.ONELEVEL_SCOPE);
			// LDAP查询
			NamingEnumeration<SearchResult> ne = dc
					.search(parentDn, filter, sc);
			// 针对查询结果构建组织/单位集合
			if (ne != null) {
				while (ne.hasMore()) {
					OrganEO organ = EntityBuilder.getOrgan(ne.next());
					organs.add(organ);
				}
			}
			// 将organs按sortNum从小到大排序
			if (organs.size() > 1) {
				Collections.sort(organs, new Comparator<OrganEO>() {
					@Override
					public int compare(OrganEO o1, OrganEO o2) {
						return o1.getSortNum().compareTo(o2.getSortNum());
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
		}finally{
			try {
				dc.close();
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
		return organs;
	}

	@Override
	public List<OrganEO> getOrgans(String parentDn, List<String> types,
			boolean isContainsFictitious) {
		// 入参验证
		List<OrganEO> organs = null;
		organs = new ArrayList<OrganEO>();
		DirContext dc = null;
		try {
			// 获取LDAP容器

			dc = LDAPUtil.getDirContext();
			String filter = "(|";
			for (String type : types) {
				if (TreeNodeVO.Type.Organ.toString().equals(type)) {// 组织
					filter = filter.concat("(objectClass=organization)");
				} else {// 单位
					if (isContainsFictitious) {// 含虚拟单位
						filter = filter.concat("(objectClass=organizationalUnit)");
					} else {// 不包含
						String virtualType = TreeNodeVO.Type.Virtual.toString();
						filter = filter
								.concat("(&(objectClass=organizationalUnit)(!(businessCategory=")
								.concat(virtualType).concat(")))");
					}
				}
			}
			filter = filter.concat(")");
			// 查询条件
			SearchControls sc = new SearchControls();
			sc.setSearchScope(SearchControls.ONELEVEL_SCOPE);
			// LDAP查询
			NamingEnumeration<SearchResult> ne = dc
					.search(parentDn, filter, sc);
			// 针对查询结果构建组织/单位集合
			if (ne != null) {
				while (ne.hasMore()) {
					OrganEO organ = EntityBuilder.getOrgan(ne.next());
					organs.add(organ);
				}
			}
			// 将organs按sortNum从小到大排序
			if (organs.size() > 1) {
				Collections.sort(organs, new Comparator<OrganEO>() {
					@Override
					public int compare(OrganEO o1, OrganEO o2) {
						Long o1sortNum = o1.getSortNum();
						Long o2sortNum = o2.getSortNum();
						int flag = 0;
						if (o1sortNum == null) {
							flag = 1;
						} else if (o2sortNum == null) {
							flag = -1;
						} else {
							flag = o1.getSortNum().compareTo(o2.getSortNum());
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
		}finally{
			try {
				dc.close();
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
		return organs;
	}

}
