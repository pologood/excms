package cn.lonsun.core.exception.util;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.exception.BaseRunTimeException;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

/**
 * jackson一些转换方法
 * 
 * <pre>
 * Jacksons.json().setDateFormate();
 * </pre>
 * 
 * @author zxh
 * @date 2012-7-25 下午11:36:33
 */
public class Jacksons {

	private ObjectMapper objectMapper;
	/** 格式化时间的string */
	private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	public static Jacksons json() {
		return new Jacksons();
	}

	/**
	 * 构造方法
	 */
	private Jacksons() {
		objectMapper = JacksonMapper.getInstance();
		this.setDateFormate();
	}

	/**
	 * 过滤属性
	 * 
	 * <pre>
	 * 使用方法:先给ObjectMapper添加一个filter，然后还要在需要过滤的类上加@JsonFilter("filterName")注解。
	 * 比如说要过滤User 上的name属性，先
	 * Jacksons.json().filter("myFilter", "name").readAsString(user)，具体看Jacksons代码。并在User类上加@JsonFilter("myFilter")。
	 * 有点不爽的是如果用另外一个没有添加该filter的ObjectMapper解析的话会报错。	 	
	 * 如果这个User类已经添加了@JsonFilter("myFilter")注解，但在另外一个地方又要解析它并不想过滤name 属性，那只能是
	 * 	Jacksons.json().filter("myFilter", "")，然后在读出来。
	 * </pre>
	 * 
	 * @param filterName
	 *            过滤器的名称
	 * @param properties
	 *            要过滤的字段名称
	 * @return
	 */
	public Jacksons filter(String filterName, String... properties) {
		FilterProvider filterProvider = new SimpleFilterProvider().addFilter(
				filterName,
				SimpleBeanPropertyFilter.serializeAllExcept(properties));
		objectMapper.setFilters(filterProvider);
		return this;
	}

	/**
	 * 设置日期格式
	 * 
	 * @param dateFormat
	 *            自定义的日期/时间格式。该属性的值遵循java标准的date/time格式规范。如：yyyy-MM-dd
	 * @return
	 */
	public Jacksons setDateFormate(String dateFormat) {
		objectMapper.setDateFormat(new SimpleDateFormat(dateFormat));
		return this;
	}

	/**
	 * 设置默认的日期
	 * 
	 * @return
	 */
	public Jacksons setDateFormate() {
		objectMapper.setDateFormat(new SimpleDateFormat(DATE_TIME_FORMAT));
		return this;
	}

	/**
	 * java对象转换为json字符串
	 * 
	 * @param object
	 * @return
	 */
	public String fromObjectToJson(Object object) {
		if (object == null) {
			return null;
		}
		try {
			return objectMapper.writeValueAsString(object);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("解析对象错误");
		}
	}

	/**
	 * json转换为类型为clazz的对象
	 * 
	 * @param json
	 * @param clazz
	 * @return
	 */
	public <T> T fromJsonToObject(String json, Class<T> clazz) {
		if(StringUtils.isEmpty(json)){
			return null;
		}
		// 入参验证
		if (clazz == null) {
			throw new NullPointerException();
		}
		try {
			return objectMapper.readValue(json, clazz);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("解析json错误");
		}
	}

	/**
	 * json串反解为List，返回的List中对象类型为class
	 * 
	 * @param json
	 * @param clazz
	 * @return
	 */
	public List<?> fromJsonToList(String json, Class<?> clazz) {
		if(StringUtils.isEmpty(json) ){
			return null;
		}
		// 入参验证
		if (clazz == null) {
			throw new NullPointerException();
		}
		// List中对象类型验证
		if (clazz.isArray()) {
			throw new IllegalArgumentException("不支持数组类型");
		} else if (Map.class.isAssignableFrom(clazz)) {
			throw new IllegalArgumentException("不支持Map类型");
		} else if (Collection.class.isAssignableFrom(clazz)) {
			throw new IllegalArgumentException("不支持集合类型");
		}

		// 构造返回类型
		JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, clazz);
		List<?> list = null;
		try {
			list = objectMapper.readValue(json, javaType);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseRunTimeException("解析json错误");
		}
		return list;
	}

	/**
	 * json串反解为List，返回的List中对象类型为class
	 * 
	 * @param json
	 * @param clazz
	 * @return
	 */
	public Map<?, ?> fromJsonToArray(String json, Class<?> keyClazz,
			Class<?> valueClazz) {
		if(StringUtils.isEmpty(json) ){
			return null;
		}
		// 入参验证
		if (keyClazz == null || valueClazz == null) {
			throw new NullPointerException();
		}
		// Map中对象类型验证
		if (keyClazz.isArray()||valueClazz.isArray()) {
			throw new IllegalArgumentException("不支持数组类型");
		} else if (Map.class.isAssignableFrom(keyClazz)||Map.class.isAssignableFrom(valueClazz)) {
			throw new IllegalArgumentException("不支持Map类型");
		} else if (Collection.class.isAssignableFrom(keyClazz)||Collection.class.isAssignableFrom(valueClazz)) {
			throw new IllegalArgumentException("不支持集合类型");
		}
		// 构造返回类型
		JavaType javaType = objectMapper.getTypeFactory()
				.constructParametricType(Map.class, keyClazz, valueClazz);
		Map<?, ?> map = null;
		try {
			map = objectMapper.readValue(json, javaType);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseRunTimeException("解析json错误");
		}
		return map;
	}

	/**
	 * json串反解为List，返回的List中对象类型为class
	 * 
	 * @param json
	 * @param clazz
	 * @return
	 */
	public Map<?, ?> fromJsonToMap(String json, Class<?> keyClazz,
			Class<?> valueClazz) {
		if(StringUtils.isEmpty(json) ){
			return null;
		}
		// 入参验证
		if (keyClazz == null || valueClazz == null) {
			throw new NullPointerException();
		}
		// 构造返回类型
		JavaType javaType = objectMapper.getTypeFactory()
				.constructParametricType(Map.class, keyClazz, valueClazz);
		Map<?, ?> map = null;
		try {
			map = objectMapper.readValue(json, javaType);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseRunTimeException("解析json错误");
		}
		return map;
	}

	/**
	 * Json转List
	 * 
	 * @param json
	 * @return list
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	public List<Map<String, Object>> fromJsonToList(String json) {
		if(StringUtils.isEmpty(json) ){
			return null;
		}
		try {
			return objectMapper.readValue(json, List.class);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseRunTimeException("解析json错误");
		}
	}
}
