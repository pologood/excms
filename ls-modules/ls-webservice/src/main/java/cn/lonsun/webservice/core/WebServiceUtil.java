package cn.lonsun.webservice.core;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.client.async.AxisCallback;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.rpc.client.RPCServiceClient;
import org.apache.axis2.transport.http.HTTPConstants;

import javax.xml.namespace.QName;
import java.util.Iterator;
import java.util.Map;

/**
 * <一句话功能简述>：WEBSERVICE调用工具类 <br/>
 * <功能详细描述>：WebService调用工具类，提供三套方法分别为：普通的webService调用；
 * 需要签名的webservices调用；需要签名并加密的方法调用
 * @author yangtao
 * @Time 2014年8月7日 下午6:00:13
 */
public class WebServiceUtil {
	

	/**
	 * <一句话功能简述>：配置文件枚举<br/>
	 * <功能详细描述>：用于判断所需取的配置文件类型
	 * @author yangtao
	 * @Time 2014年8月7日 下午5:59:21
	 */
	private enum CONFIG_TYPE {
		/**加密并签名的配置文件类型*/
		encrypt, 
		/**签名不加密的配置文件类型*/
		signature
	}

	/** 加密安全上下文对象 */
	private static ConfigurationContext encryptConfigContext;
	/** 签名安全上下文对象 */
	private static ConfigurationContext signatureConfigContext;
	

	/**
	 * 〈一句话功能简述〉:Axis2客户端同步调用方法
	 * 〈功能详细描述〉:Axis2客户端同步调用方法，用于已知晓服务端返回的参数类型如String.class
	 * @param sUrl
	 *            所要调用的WebService的URL
	 * @param nameSpace
	 *            方法的命名空间
	 * @param method
	 *            方法的名称
	 * @param param
	 *            传入的参数
	 * @param cls
	 *            指定返回的参数类型
	 * @return 返回指定参数类型的Object数组
	 * @throws AxisFault
	 */
	public static Object[] callNormalService(String sUrl, String nameSpace,
			String method, Object[] param, Class<?> cls) throws AxisFault {
		RPCServiceClient c = new RPCServiceClient();
		Options ops = c.getOptions();
		//设置响应时间2分钟
		ops.setTimeOutInMilliSeconds(120000);

		EndpointReference to = new EndpointReference(sUrl);
		ops.setTo(to);
		QName qname = new QName(nameSpace, method);
		Class<?>[] clses = null;
		if (cls != null) {
			clses = new Class<?>[] { cls };
		} else {
			clses = new Class<?>[] {};
		}
		Object[] obs = c.invokeBlocking(qname, param, clses);
		c.cleanupTransport();
		return obs;
	}

	/**
	 * 〈一句话功能简述〉:Axis2客户端同步调用方法
	 * 〈功能详细描述〉:Axis2客户端同步调用方法，用于已知晓服务端返回的参数类型如String.class
	 * @param sUrl
	 *            所要调用的WebService的URL
	 * @param nameSpace
	 *            方法的命名空间
	 * @param method
	 *            方法的名称
	 * @param param
	 *            传入的参数
	 * @param cls
	 *            指定返回的参数类型
	 * @return 返回指定参数类型的Object数组
	 * @throws AxisFault
	 */
	public static Object[] callNormalService(String sUrl, String nameSpace,
											 String method, Object[] param, Class<?> cls,long timeout) throws AxisFault {
		RPCServiceClient c = new RPCServiceClient();
		Options ops = c.getOptions();
		//设置响应时间2分钟
		ops.setTimeOutInMilliSeconds(timeout);

		EndpointReference to = new EndpointReference(sUrl);
		ops.setTo(to);
		QName qname = new QName(nameSpace, method);
		Class<?>[] clses = null;
		if (cls != null) {
			clses = new Class<?>[] { cls };
		} else {
			clses = new Class<?>[] {};
		}
		Object[] obs = c.invokeBlocking(qname, param, clses);
		c.cleanupTransport();
		return obs;
	}

	/**
	 * 改造
	 * @param sUrl
	 * @param nameSpace
	 * @param method
	 * @param param
	 * @param action
	 * @param cls
	 * @return
	 * @throws AxisFault
	 */
	public static Object[] callNormalService(String sUrl, String nameSpace,
											 String method, Object[] param, String action,Class<?> cls) throws AxisFault {
		RPCServiceClient c = new RPCServiceClient();
		Options ops = c.getOptions();

		//设置响应时间2分钟
		ops.setSoapVersionURI(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI);
		ops.setAction(action);
		ops.setTimeOutInMilliSeconds(120000);
		ops.setProperty(HTTPConstants.CHUNKED, "false");//设置不受限制.
		ops.setTransportInProtocol(Constants.TRANSPORT_HTTP);
		ops.setProperty(Constants.Configuration.HTTP_METHOD,HTTPConstants.HTTP_METHOD_POST);
		EndpointReference to = new EndpointReference(sUrl);
		ops.setTo(to);
		QName qname = new QName(nameSpace, method);
		Class<?>[] clses = null;
		if (cls != null) {
			clses = new Class<?>[] { cls };
		} else {
			clses = new Class<?>[] {};
		}

		Object[] obs = c.invokeBlocking(qname, param, clses);
		c.cleanupTransport();
		return null;
	}

	/**
	 * 〈一句话功能简述〉:Axis2客户端异步调用方法<br/>
	 * 〈功能详细描述〉:Axis2客户端异步调用方法，用于已知晓服务端返回的参数类型如String.class
	 * @param sUrl
	 *            所要调用的WebService的URL
	 * @param nameSpace
	 *            方法的命名空间
	 * @param method
	 *            方法的名称
	 * @param param
	 *            传入的参数
	 * @param callback
	 *            回调接口的实现类
	 * @return 返回指定参数类型的Object数组
	 * @throws AxisFault
	 */
	public static void callAsynNormalService(String sUrl, String nameSpace,
			String method, Object[] param,AxisCallback callback) throws AxisFault {
		RPCServiceClient c = new RPCServiceClient();
		Options ops = c.getOptions();
		EndpointReference to = new EndpointReference(sUrl);
		ops.setTo(to);
		QName qname = new QName(nameSpace, method);
		c.invokeNonBlocking(qname, param,callback);
		c.cleanupTransport();
	}
	
	/**
	 * 〈一句话功能简述〉:不指定返回类型同步调用WebService方法<br/>
	 * 〈功能详细描述〉:采用原始的方法同步调用，返回OMElement对象，返回的数据需要自己手动解析
	 * @Time：2014年8月8日 下午2:49:58
	 * @Author：yangtao
	  * @param sUrl
	 *            所要调用的WebService的URL
	 * @param nameSpace
	 *            方法的命名空间
	 * @param method
	 *            方法的名称
	 * @param param
	 *            传入的参数
	 * @return OMElement对象
	 * @throws AxisFault
	 */
	public static OMElement callNormalServiceOrginal(String sUrl, String nameSpace,
			String method, Object[] param) throws AxisFault{
		RPCServiceClient c = new RPCServiceClient();
		Options ops = c.getOptions();
		EndpointReference to = new EndpointReference(sUrl);
		ops.setTo(to);
		QName qname = new QName(nameSpace, method);
		OMElement element = c.invokeBlocking(qname, param);
		return element;
	}

	/**
	 * 〈一句话功能简述〉:Axis2客户端同步调用工具类
	 * @param sUrl
	 *            所要调用的WebService的URL
	 * @param nameSpace
	 *            方法的命名空间
	 *            方法的名称
	 * @param param
	 *            传入的参数
	 *            指定返回的参数类型
	 * @return 返回指定参数类型的Object数组
	 * @throws AxisFault
	 */
	public static OMElement callDNetService(String sUrl, String nameSpace,
			String methodStr, String soapAction, Object[] param)
			throws AxisFault {
		EndpointReference e = new EndpointReference(sUrl);
		Options options = new Options();
        options.setProperty(HTTPConstants.MC_GZIP_REQUEST,Boolean.TRUE);
        options.setProperty(HTTPConstants.MC_ACCEPT_GZIP, Boolean.TRUE);
		options.setAction(soapAction);
		options.setTo(e);
		RPCServiceClient c = new RPCServiceClient();
		c.setOptions(options);
		QName qname = new QName(methodStr);
		return c.invokeBlocking(qname, new Object[] {});
	}

	public static OMElement callDNetService(String sUrl, String nameSpace,
											String methodStr, String soapAction, Map<String,String> param)
			throws AxisFault {
		EndpointReference e = new EndpointReference(sUrl);
		Options options = new Options();
		options.setSoapVersionURI(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI);
		options.setAction(soapAction);
		options.setTo(e);
		options.setTransportInProtocol(Constants.TRANSPORT_HTTP);
		options.setProperty(HTTPConstants.CHUNKED, "false");//设置不受限制.
		options.setProperty(Constants.Configuration.HTTP_METHOD,HTTPConstants.HTTP_METHOD_POST);
		options.setAction(soapAction);
		ServiceClient c = new ServiceClient();
		c.setOptions(options);
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = fac.createOMNamespace(nameSpace, "");
		OMElement method = fac.createOMElement(methodStr, omNs);// 要调用的接口方法名称
		//设定参数
		Iterator<Map.Entry<String, String>> it = param.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, String> entity =  it.next();
			String key = entity.getKey();
			String value = entity.getValue();
			OMElement para = fac.createOMElement(key, omNs);// 方法的第一个参数名称
			para.addChild(fac.createOMText(para, value));// 设定参数的值
			method.addChild(para);
		}
		OMElement result = c.sendReceive(method);// 调用接口方法
		c.cleanupTransport();//关闭资源
		c.cleanup();
		return result;// 调用接口方法
	}

	/**
	 * 〈一句话功能简述〉:同步调用加密的webservice方法<br/>
	 * 〈功能详细描述〉:用于同步调用签名并加密的webService，整个传输的数据都会被加密，用户传递用户密码等敏感信息的WebService
	 * @param sUrl
	 *            所要调用的WebService的URL
	 * @param nameSpace
	 *            方法的命名空间
	 * @param method
	 *            方法的名称
	 * @param param
	 *            传入的参数
	 * @param cls
	 *            指定返回的参数类型
	 * @return 返回指定参数类型的Object数组
	 * @throws AxisFault
	 */
	public static Object[] callEncryptService(String sUrl, String nameSpace,
			String method, Object[] param, Class<?> cls) throws AxisFault {
		Class<?>[] clses = null;
		if (cls != null) {
			clses = new Class<?>[] { cls };
		} else {
			clses = new Class<?>[] {};
		}

		// 添加获取加密配置文件上下文
		ConfigurationContext configContext = getConfigContext(CONFIG_TYPE.encrypt
				.toString());
		RPCServiceClient client = new RPCServiceClient(configContext, null);
		EndpointReference e = new EndpointReference(sUrl);
		// 调用Rampart方法
		Options options = new Options();
		options.setAction("urn:rampartMethod");
		options.setTo(e);
		client.setOptions(options);
		QName qname = new QName(nameSpace, method);
		return client.invokeBlocking(qname, param, clses);
	}
	
	/**
	 * 〈一句话功能简述〉:异步调用加密的webservice方法<br/>
	 * 〈功能详细描述〉:用于异步调用签名并加密的webService，整个传输的数据都会被加密，用户传递用户密码等敏感信息的WebService
	 * @param sUrl
	 *            所要调用的WebService的URL
	 * @param nameSpace
	 *            方法的命名空间
	 * @param method
	 *            方法的名称
	 * @param param
	 *            传入的参数 
	 * @param callback
	 *            回调接口的实现类
	 * @return 返回指定参数类型的Object数组
	 * @throws AxisFault
	 */
	public static void callAsynEncryptService(String sUrl, String nameSpace,
			String method, Object[] param, AxisCallback callback) throws AxisFault {
		// 添加获取加密配置文件上下文
		ConfigurationContext configContext = getConfigContext(CONFIG_TYPE.encrypt
				.toString());
		RPCServiceClient client = new RPCServiceClient(configContext, null);
		EndpointReference e = new EndpointReference(sUrl);
		// 调用Rampart方法
		Options options = new Options();
		options.setAction("urn:rampartMethod");
		options.setTo(e);
		client.setOptions(options);
		QName qname = new QName(nameSpace, method);
		client.invokeNonBlocking(qname, param, callback);
	}

	/**
	 * 〈一句话功能简述〉:同步调用签名WebService方法 <br/>
	 * 〈功能详细描述〉:此方法主要用同步调用需要签名调用的WebService，不做加密操作，用于增删改的业务的WebService，非敏感查询的WebService不适用该方法
	 * @Time：2014年8月8日 上午8:55:47
	 * @Author：yangtao
	 * @param sUrl
	 *            所要调用的WebService的URL
	 * @param nameSpace
	 *            方法的命名空间
	 * @param method
	 *            方法的名称
	 * @param param
	 *            传入的参数
	 * @param cls
	 *            指定返回的参数类型
	 * @return 返回指定参数类型的Object数组
	 * @throws AxisFault
	 */
	public static Object[] callSignatrueService(String sUrl, String nameSpace,
			String method, Object[] param, Class<?> cls) throws AxisFault {
		Class<?>[] clses = null;
		if (cls != null) {
			clses = new Class<?>[] { cls };
		} else {
			clses = new Class<?>[] {};
		}

		// 添加获取签名配置文件上下文
		ConfigurationContext configContext = getConfigContext(CONFIG_TYPE.signature
				.toString());
		RPCServiceClient client = new RPCServiceClient(configContext, null);
		EndpointReference e = new EndpointReference(sUrl);
		// 调用Rampart方法
		Options options = new Options();
		options.setAction("urn:rampartMethod");
		options.setTo(e);
		client.setOptions(options);
		QName qname = new QName(nameSpace, method);
		return client.invokeBlocking(qname, param, clses);
	}
	
	/**
	 * 〈一句话功能简述〉:异步调用签名WebService方法 <br/>
	 * 〈功能详细描述〉:此方法主要用异步调用需要签名调用的WebService，不做加密操作，用于增删改的业务的WebService，非敏感查询的WebService不适用该方法
	 * @Time：2014年8月8日 上午8:55:47
	 * @Author：yangtao
	 * @param sUrl
	 *            所要调用的WebService的URL
	 * @param nameSpace
	 *            方法的命名空间
	 * @param method
	 *            方法的名称
	 * @param param
	 *            传入的参数
	 * @param callback
	 *            回调接口的实现类
	 * @return 返回指定参数类型的Object数组
	 * @throws AxisFault
	 */
	public static void callAsynSignatrueService(String sUrl, String nameSpace,
			String method, Object[] param, AxisCallback callback) throws AxisFault {
		// 添加获取签名配置文件上下文
		ConfigurationContext configContext = getConfigContext(CONFIG_TYPE.signature
				.toString());
		RPCServiceClient client = new RPCServiceClient(configContext, null);
		EndpointReference e = new EndpointReference(sUrl);
		// 调用Rampart方法
		Options options = new Options();
		options.setAction("urn:rampartMethod");
		options.setTo(e);
		client.setOptions(options);
		QName qname = new QName(nameSpace, method);
		client.invokeNonBlocking(qname, param, callback);
	}

	/**
	 * 〈一句话功能简述〉:获取Axis2配置文件的路径
	 * @Time：2014年8月8日 上午8:45:19
	 * @Author：yangtao
	 * @return
	 */
	private static String getAxis2ConfPath() {
		StringBuilder confPath = new StringBuilder();
		confPath.append(String.class.getResource("/").getPath());
		confPath.append("repository");
		if (confPath.toString().startsWith("/")) {
			return confPath.substring(1);
		}
		return confPath.toString();
	}

	/**
	 * 〈一句话功能简述〉:获取加密的配置文件路径
	 * @Time：2014年8月7日 下午5:54:37
	 * @Author：yangtao
	 * @return
	 */
	private static String getAxis2ConfFilePath(String type) {
		String confFilePath = "";
		if (null == type) {
			return "";
		}
		if (CONFIG_TYPE.encrypt.toString().equals(type)) {
			confFilePath = getAxis2ConfPath() + "/EncryptConfig.xml";
		} else if (CONFIG_TYPE.signature.toString().equals(type)) {
			confFilePath = getAxis2ConfPath() + "/SignatureConfig.xml";
		}
		if (confFilePath.toString().startsWith("\\")) {
			return confFilePath.substring(1);
		}
		return confFilePath;

	}

	/**
	 * 〈一句话功能简述〉：获取客户加密的上下文对象
	 * @Time:2014年8月4日15:32:19
	 * @return Axis2上下文
	 * @throws AxisFault
	 */
	private static ConfigurationContext getConfigContext(String type)
			throws AxisFault {
		// Axis2配置文件目录及axis2.xml的配置文件路径
		String confFilePath = getAxis2ConfFilePath(type);
		String confPath = getAxis2ConfPath();
		// 获取加密的上下文信息
		if (null != type && type.equals(CONFIG_TYPE.encrypt.toString())) {
			if (null == encryptConfigContext) {
				encryptConfigContext = ConfigurationContextFactory
						.createConfigurationContextFromFileSystem(confPath,
								confFilePath);
			}
			return encryptConfigContext;
		} else if (null != type && type.equals(CONFIG_TYPE.signature.toString())) {
			if (null == signatureConfigContext) {
				signatureConfigContext = ConfigurationContextFactory
						.createConfigurationContextFromFileSystem(confPath,
								confFilePath);
			}
			return signatureConfigContext;
		} else {
			return null;
		}
	}
}
