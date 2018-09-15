package cn.lonsun.ldap.internal.cache;

import java.util.ArrayList;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.springframework.core.task.TaskExecutor;
import org.springframework.util.StringUtils;

import cn.lonsun.common.vo.TreeNodeVO;
import cn.lonsun.common.vo.TreeNodeVO.Type;
import cn.lonsun.core.cache.ACache;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.ldap.internal.util.Constants;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.entity.PersonEO;
import cn.lonsun.rbac.internal.entity.UserEO;
import cn.lonsun.rbac.internal.facade.ILdapFacadeService;
import cn.lonsun.rbac.internal.service.IOrganService;

/**
 * PersonEO缓存
 * 
 * @Description:
 * @author xujh
 * @date 2014年9月23日 下午10:07:58
 * @version V1.0
 */
public class InternalLdapCache extends ACache<PersonEO> {
	//是否启用cache
	public static boolean isCacheOn = false;

	private final static InternalLdapCache instance = new InternalLdapCache();
	// LDAP缓存名称
	private static String PERSON_CACHE_NAME = "ldap_cache";
	// Cahce对象
	private static final Cache cache = new Cache(PERSON_CACHE_NAME, 20000,false, false, 600, 60);

	static {
		CacheManager.getInstance().addCache(cache);
	}

	/**
	 * 获取实例对象
	 * 
	 * @return
	 */
	public static InternalLdapCache getInstance() {
		return instance;
	}
	
	/**
	 * 异步加载
	 *
	 * @param parentId
	 * @param nodeTypes
	 */
	public void updateSyn(final Long parentId,final String[] nodeTypes){
		IOrganService organService = SpringContextHolder.getBean("organService");
		ILdapFacadeService ldapFacadeService =  SpringContextHolder.getBean("ldapFacadeService");
		String parentDn = Constants.ROOT_DN;
		List<TreeNodeVO> nodes = null;
		if(parentId!=null){
			OrganEO parent = organService.getEntity(OrganEO.class, parentId);
			parentDn = parent.getDn();
			//外平台的不加载
			String[] statuses = new String[]{UserEO.STATUS.Enabled.toString()};
			nodes = ldapFacadeService.getSubNodes(false,nodeTypes,parentId,statuses);
		}else{
			nodes = ldapFacadeService.getNodes(nodeTypes, null, false);
		}
		// 将重新组织和单位保存到缓存中
		if(nodes!=null&&nodes.size()>0){
			getInstance().put(getKey(parentDn,nodeTypes), nodes);
		}
		
	}
	
	/**
	 * 异步更新缓存
	 *
	 * @param parentId
	 */
	public void update(final Long parentId){
		TaskExecutor taskExecutor = SpringContextHolder.getBean("taskExecutor");
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				IOrganService organService = SpringContextHolder.getBean("organService");
				ILdapFacadeService ldapFacadeService =  SpringContextHolder.getBean("ldapFacadeService");
				String parentDn = Constants.ROOT_DN;
				String virtualNode = Type.VirtualNode.toString();
				String organ = Type.Organ.toString();
				String organUnit = Type.OrganUnit.toString();
				String virtual = Type.Virtual.toString();
				String person = Type.Person.toString();
				String[] noteTypes2 = new String[] {virtualNode,organ};
				String[] noteTypes3 = new String[] {virtualNode,organ,organUnit};
				String[] noteTypes4 = new String[] {virtualNode,organ,organUnit,virtual};
				String[] noteTypes5 = new String[] {virtualNode,organ,organUnit,virtual,person};
				List<TreeNodeVO> nodes2 = null;
				List<TreeNodeVO> nodes3 = null;
				List<TreeNodeVO> nodes4 = null;
				List<TreeNodeVO> nodes5 = null;
				if(parentId!=null){
					OrganEO parent = organService.getEntity(OrganEO.class, parentId);
					parentDn = parent.getDn();
					//外平台的不加载
					String[] statuses = new String[]{UserEO.STATUS.Enabled.toString()};
					nodes2 = ldapFacadeService.getSubNodes(false,noteTypes2,parentId,statuses);
					nodes3 = ldapFacadeService.getSubNodes(false,noteTypes3,parentId,statuses);
					nodes4 = ldapFacadeService.getSubNodes(false,noteTypes4,parentId,statuses);
					nodes5 = ldapFacadeService.getSubNodes(false,noteTypes5,parentId,statuses);
					// 将重新组织和单位保存到缓存中
					if(nodes3!=null&&nodes3.size()>0){
						getInstance().put(getKey(parentDn,noteTypes3), nodes3);
					}
					if(nodes4!=null&&nodes4.size()>0){
						getInstance().put(getKey(parentDn,noteTypes4), nodes4);			
					}
					if(nodes5!=null&&nodes5.size()>0){
						getInstance().put(getKey(parentDn,noteTypes5), nodes5);
					}
				}else{
					nodes2 = ldapFacadeService.getNodes(noteTypes2, null, false);
				}
				if(nodes2!=null&&nodes2.size()>0){
					getInstance().put(getKey(parentDn,noteTypes2), nodes2);
				}
			}
		});
	}
	
	/**
	 * 根据Dn以及noteTypes获取key，机制是根据noteTypes的长度获取，因为根据获取数据范围的不同有一下几种情况，对应的noteTypes长度不同</br>
	 *  <br>String[] noteTypes1 = new String[] {virtualNode,organ,organUnit,virtual,person};
	 *	<br>String[] noteTypes2 = new String[] {virtualNode,organ,organUnit,virtual};
	 *	<br>String[] noteTypes3 = new String[] {virtualNode,organ,organUnit};
	 *	<br>String[] noteTypes4 = new String[] {virtualNode,organ};
	 *
	 * @param parentDn
	 * @param noteTypes
	 * @return
	 */
	public static String getKey(String parentDn,String[] nodeTypes){
		if(StringUtils.isEmpty(parentDn)||nodeTypes==null||nodeTypes.length<=1){
			throw new IllegalArgumentException();
		}
		return parentDn+nodeTypes.length;
	}

	@Override
	public void put(String key, Object value) {
		if (StringUtils.isEmpty(key)) {
			throw new NullPointerException();
		}
		Element e = new Element(key, value);
		cache.put(e);
	}

	@Override
	public Object getValue(String key) {
		if (StringUtils.isEmpty(key)) {
			throw new NullPointerException();
		}
		Element element = cache.get(key);
		Object object = null;
		if (element != null) {
			object = element.getObjectValue();
		}
		return object;
	}
	
	/**
	 * 根据key获取类型在nodeTypes中的节点
	 *
	 * @param key
	 * @param nodeTypes
	 * @return
	 */
	public List<TreeNodeVO> getValue(String key,String[] nodeTypes) {
		if (StringUtils.isEmpty(key)||nodeTypes==null||nodeTypes.length<=0) {
			throw new NullPointerException();
		}
		List<TreeNodeVO> treeNodes = null;
		Element element = cache.get(key);
		Object object = null;
		if (element != null) {
			object = element.getObjectValue();
		}
		if(object!=null){
			@SuppressWarnings("unchecked")
			List<TreeNodeVO> nodes = (List<TreeNodeVO>)object;
			treeNodes = new ArrayList<TreeNodeVO>();
			List<String> list = new ArrayList<String>(nodeTypes.length);
			for(String type:nodeTypes){
				list.add(type);
			}
			if(nodes.size()>0){
				for(TreeNodeVO node:nodes){
					String type = node.getType();
					if(list.contains(type)){
						//过滤需要的节点
						treeNodes.add(node);
					}
				}
			}
		}
		return treeNodes;
	}

	/**
	 * 移除
	 * 
	 * @param key
	 */
	public void remove(String key) {
		if (StringUtils.isEmpty(key)) {
			throw new NullPointerException();
		}
		cache.remove(key);
	}

	/**
	 * 是否过期
	 * 
	 * @param key
	 * @return
	 */
	public boolean isExpired(String key) {
		boolean isExpired = true;
		Element element = cache.get(key);
		if (element != null) {
			isExpired = element.isExpired();
		}
		return isExpired;
	}

	/**
	 * 是否存在key
	 * 
	 * @param key
	 * @return
	 */
	public boolean containsKey(String key) {
		return cache.isKeyInCache(key);
	}
	
}
