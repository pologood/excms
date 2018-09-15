package cn.lonsun.core.exception.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * jackson 创建ObjectMapper对象
 * 
 * <pre>
 * 创建为饿汉式单例模式 ，Jackson用于转换的核心类ObjectMapper无需每次都new一个object，
 * 官网上的一句话：can reuse, share globally(可以重复使用，全局共享)
 * </pre>
 * 
 * @author zxh
 * @date 2012-7-25 下午11:36:33
 */
public class JacksonMapper {
	/** 创建 ObjectMapper */
	private static final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * 默认空的构造方法
	 */
	private JacksonMapper() {
	}

	/**
	 * 获取实例
	 * 
	 * @return
	 */
	public static ObjectMapper getInstance() {
		// SerializationFeature改变JSON怎么写
		// 使标准的缩进（“漂亮印刷”）：
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		// 写的java.util.Date，日历AS号（时间戳）：ies to serialize)
		// (不使用此设置，则抛出异常在这种情况下)
		objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		// 写的java.util.Date，日历AS号（时间戳）：
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		// DeserializationFeature改变JSON是如何读作的POJO：
		// 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		// 允许JSON空字符串（“”）的胁迫为null的对象的值：
		objectMapper
				.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

		// JsonParser.Feature配置解析设置：

		// 允许以JSON的C / C风格的注释（非标，默认情况下禁用）
		//objectMapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
		// 允许（非标）在JSON中带引号的字段名称：
		//objectMapper.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
		// 允许使用撇号（单引号），无标准
		//objectMapper.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);

		// JsonGenerator.Feature配置低级别的JSON生成：

		// 给力逃脱非ASCII字符：
		//objectMapper.enable(JsonGenerator.Feature.ESCAPE_NON_ASCII);

		return objectMapper;
	}

}