package cn.lonsun.core.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.lonsun.core.util.TipsMode;

/**
 * 运行时异常，用户无法处理或不需要处理的异常（包括无法处理或不需要处理的业务异常）
 * 系统RunTime基异常，系统中需要抛出的RunTime类型异常有以下两种做法： 
 * 1.自定义继承自BaseRunTimeException的异常
 * 2.直接抛出BaseRunTimeException，并赋与不同的key（在定义key之前需要查看异常约定文档以确保key不重复）
 * 
 * @author 
 * 
 */
public class BaseRunTimeException extends RuntimeException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -6320797208357263746L;

	protected Logger logger = LoggerFactory.getLogger(BaseRunTimeException.class);

	//提示方式
	private String tipsMode = TipsMode.Key.toString();
	// 异常Key
	private String key;
	//关键字数组，用来替换提示信息中的占位符，按数组的顺序进行替换
	private Object[] keyWords;
	//异常提示信息
	private String tipsMessage;
        
    private Integer errCode;
	
	public BaseRunTimeException() {
		super();
	}
	
	public BaseRunTimeException(String message) {
		super(message);
	}
	
	public BaseRunTimeException(String key, Object[] keyWords) {
		super();
		this.key = key;
		this.keyWords = keyWords;
	}

	public BaseRunTimeException(String tipsMode, String value) {
	    this(value);
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

        public Integer getErrCode() {
            return errCode;
        }

        public void setErrCode(Integer errCode) {
            this.errCode = errCode;
        }
        
}
