package cn.lonsun.core.util;
/**
 * 用于标识获取异常提示信息的方式
 * @author xujh
 *
 */
public enum TipsMode {
	Key,//通过在exceptionTipsMessage中获取
	Message//抛出异常时直接定义
}
