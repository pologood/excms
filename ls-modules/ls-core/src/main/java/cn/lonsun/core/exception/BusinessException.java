package cn.lonsun.core.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.lonsun.core.util.TipsMode;

/**
 * 业务异常——必须进行捕获处理的业务异常
 * @author 徐建华
 *
 */
public class BusinessException extends Exception {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 4317976563792118690L;
	
	protected Logger logger = LoggerFactory.getLogger(BusinessException.class);
	
	//提示方式
	private String tipsMode = TipsMode.Key.toString();
	// 异常Key
	private String key;
	//关键字数组，用来替换提示信息中的占位符，按数组的顺序进行替换
	private Object[] keyWords;
	//异常提示信息
	private String tipsMessage;
	
	public BusinessException() {
		super();
	}
	
	public BusinessException(String message) {
		super(message);
	}
	
	public BusinessException(String key, Object[] keyWords) {
		super();
		this.key = key;
		this.keyWords = keyWords;
	}

	public BusinessException(String tipsMode, String value) {
		if(!TipsMode.Key.toString().equals(tipsMode)&&!TipsMode.Message.toString().equals(tipsMode)){
			throw new IllegalArgumentException();
		}
		this.tipsMode = tipsMode;
		if(TipsMode.Key.toString().equals(tipsMode)){
			this.key = value;
		}else{
			this.tipsMessage = value;
		}
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Object[] getKeyWords() {
		return keyWords;
	}

	public void setKeyWords(Object[] keyWords) {
		this.keyWords = keyWords;
	}

	public String getTipsMode() {
		return tipsMode;
	}

	public void setTipsMode(String tipsMode) {
		this.tipsMode = tipsMode;
	}

	public String getTipsMessage() {
		return tipsMessage;
	}

	public void setTipsMessage(String tipsMessage) {
		this.tipsMessage = tipsMessage;
	}
	
}
