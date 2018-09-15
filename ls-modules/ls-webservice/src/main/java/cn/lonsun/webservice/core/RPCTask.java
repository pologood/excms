package cn.lonsun.webservice.core;

import org.apache.axis2.AxisFault;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.webservice.to.WebServiceTO;

/**
 * WebService调用任务
 *
 * @author xujh
 * @version 1.0
 * 2014年12月15日
 *
 */
@Component("rpcTask")  
@Scope("prototype")
public class RPCTask implements Runnable{
	//任务名称
	private String name;
	//地址
	private String url;
	//命名空间
	private String nameSpace;
	//方法
	private String method;
	//参数
	private Object[] params;
	//返回类型
	private Class<?> clazz = WebServiceTO.class;
	
	private WebServiceTO to;
	
	public RPCTask(String name, String url, String nameSpace, String method,
			Object[] params, Class<?> clazz) {
		super();
		this.name = name;
		this.url = url;
		this.nameSpace = nameSpace;
		this.method = method;
		this.params = params;
		this.clazz = clazz;
	}

	@Override
	public void run() {
		try {
			Object[] objs = WebServiceUtil.callNormalService(url, nameSpace, method, params, clazz);
			if (objs != null && objs.length > 0) {
				to = (WebServiceTO) objs[0];
			}
		} catch (AxisFault e) {
			e.printStackTrace();
			throw new BaseRunTimeException(TipsMode.Message.toString(),
					"调用外部服务异常，请联系管理员.");
		}
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getNameSpace() {
		return nameSpace;
	}

	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public Object[] getParams() {
		return params;
	}

	public void setParams(Object[] params) {
		this.params = params;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public WebServiceTO getTo() {
		return to;
	}

	public void setTo(WebServiceTO to) {
		this.to = to;
	}
}
