//package cn.lonsun.rbac.cache;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import net.sf.ehcache.Cache;
//import net.sf.ehcache.CacheManager;
//import net.sf.ehcache.Element;
//import net.sf.ehcache.config.CacheConfiguration;
//import net.sf.ehcache.config.SearchAttribute;
//import net.sf.ehcache.config.Searchable;
//import net.sf.ehcache.search.Attribute;
//import net.sf.ehcache.search.Query;
//import net.sf.ehcache.search.Result;
//import net.sf.ehcache.search.Results;
//import net.sf.ehcache.search.expression.Criteria;
//
//import org.apache.commons.lang.StringUtils;
//
//import cn.lonsun.core.cache.ACache;
//import cn.lonsun.webservice.vo.rbac.SimplePersonVO;
//
///**
// * 选人界面信息缓存
// *
// * @author xujh
// * @version 1.0
// * 2015年4月4日
// *
// */
//public class SimplePersonCache extends ACache<SimplePersonVO> {
//	// PersonEO缓存
//	private static String PERSON_CACHE_NAME = "PersonInfoCache";
//
//	// Cahce对象
//	private static Cache cache = null;
//	static {
//		CacheManager cacheManager = CacheManager.create();
//		CacheConfiguration cacheConfig = new CacheConfiguration();
//		cacheConfig.name(PERSON_CACHE_NAME).maxElementsInMemory(0);
//		cacheConfig.setEternal(true);
//		// 新建一个Searchable对象
//		Searchable searchable = new Searchable();
//		// 给Cache配置Searchable对象，表明该Cache是一个可查询的Cache
//		cacheConfig.searchable(searchable);
//		// 新建一个查询属性
//		SearchAttribute nameSearchAttribute = new SearchAttribute();
//		// 指定查询属性的名称和属性提取器的类名
//		nameSearchAttribute.name("name");
//		nameSearchAttribute.className("cn.lonsun.rbac.cache.SimplePersonNameExtractor");
//		// 新建一个查询属性
//		SearchAttribute fullPySearchAttribute = new SearchAttribute();
//		// 指定查询属性的名称和属性提取器的类名
//		fullPySearchAttribute.name("fullPy");
//		fullPySearchAttribute.className("cn.lonsun.rbac.cache.SimplePersonFullPyExtractor");
//		// 新建一个查询属性
//		SearchAttribute simplePySearchAttribute = new SearchAttribute();
//		// 指定查询属性的名称和属性提取器的类名
//		simplePySearchAttribute.name("simplePy");
//		simplePySearchAttribute.className("cn.lonsun.rbac.cache.SimplePersonSimplePyExtractor");
//		// 新建一个查询属性
//		SearchAttribute dnSearchAttribute = new SearchAttribute();
//		dnSearchAttribute.name("dn");
//		dnSearchAttribute.className("cn.lonsun.rbac.cache.SimplePersonDnExtractor");
//		// Searchalbe对象添加查询属性
//		searchable.addSearchAttribute(nameSearchAttribute);
//		searchable.addSearchAttribute(fullPySearchAttribute);
//		searchable.addSearchAttribute(simplePySearchAttribute);
//		searchable.addSearchAttribute(dnSearchAttribute);
//		SimplePersonCache.cache = new Cache(cacheConfig);
//	    //把Cache对象纳入CacheManager的管理中
//	    cacheManager.addCache(cache);
//	}
//
//	private final static SimplePersonCache instance = new SimplePersonCache();
//	
//	
//	/**
//	 * 从chache中通过blurryName匹配查询用户，blurryName可能对应姓名、全拼或简拼
//	 *
//	 * @param blurryName
//	 * @param parentDn
//	 * @return
//	 */
//	public static List<SimplePersonVO> search(String blurryName,String parentDn){
//		List<SimplePersonVO> simplePersons = null;
//		//获取名称为name的可查询属性Attribute对象
//	    Attribute<String> name = cache.getSearchAttribute("name");
//	    Attribute<String> fullPy = cache.getSearchAttribute("fullPy");
//	    Attribute<String> simplePy = cache.getSearchAttribute("simplePy");
//	    Attribute<String> dn = cache.getSearchAttribute("dn");
//	    //创建一个用于查询的Query对象
//	    Query query = cache.createQuery();
//	    //给当前query添加一个筛选条件——可查询属性name的值等于“name1”
//	    String filter = "*".concat(blurryName).concat("*");
//	    Criteria criteria = null;
//	    if(!org.apache.commons.lang3.StringUtils.isEmpty(parentDn)){
//	    	String dnFilter = "*".concat(parentDn);
//	    	criteria = dn.ilike(dnFilter).and(name.ilike(filter).or(fullPy.ilike(filter)).or(simplePy.ilike(filter)));
//	    }else{
//	    	criteria = name.ilike(filter).or(fullPy.ilike(filter)).or(simplePy.ilike(filter));
//	    }
//	    query.addCriteria(criteria);
//	    Results results = query.execute();
//	    //获取Results中包含的所有的Result对象
//	    List<Result> resultList = results.all();
//	    if (resultList != null && !resultList.isEmpty()) {
//	    	simplePersons = new ArrayList<SimplePersonVO>(resultList.size());
//	      for (Result result : resultList) {
//	        //结果中包含value时可以获取value
//	        if (results.hasValues()) {
//	          Object obj = result.getValue();
//	          if(obj!=null){
//	        	  simplePersons.add((SimplePersonVO)obj);
//	          }
//	        }
//	      }
//	    }
//	    return simplePersons;
//	}
//
//	/**
//	 * 获取实例对象
//	 * 
//	 * @return
//	 */
//	public static SimplePersonCache getInstance() {
//		return instance;
//	}
//
//	@Override
//	public void put(String key, Object value) {
//		if (StringUtils.isEmpty(key)) {
//			throw new NullPointerException();
//		}
//		Element e = new Element(key, value);
//		cache.put(e);
//	}
//
//	@Override
//	public Object getValue(String key) {
//		if (StringUtils.isEmpty(key)) {
//			throw new NullPointerException();
//		}
//		Element element = cache.get(key);
//		Object object = null;
//		if (element != null) {
//			object = element.getObjectValue();
//		}
//		return object == null;
//	}
//
//	/**
//	 * 移除
//	 * 
//	 * @param key
//	 */
//	public void remove(String key) {
//		if (StringUtils.isEmpty(key)) {
//			throw new NullPointerException();
//		}
//		cache.remove(key);
//	}
//
//	/**
//	 * 是否过期
//	 * 
//	 * @param key
//	 * @return
//	 */
//	public boolean isExpired(String key) {
//		boolean isExpired = true;
//		Element element = cache.get(key);
//		if (element != null) {
//			isExpired = element.isExpired();
//		}
//		return isExpired;
//	}
//
//	/**
//	 * 是否存在key
//	 * 
//	 * @param key
//	 * @return
//	 */
//	public boolean containsKey(String key) {
//		return cache.isKeyInCache(key);
//	}
//
//}
