package cn.lonsun.webservice.core;

import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.exception.util.Jacksons;
import cn.lonsun.core.util.JSONHelper;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.webservice.to.WebServiceTO;
import cn.lonsun.webservice.vo.axis2.WebServiceVO;
import org.apache.axis2.AxisFault;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * WebService调用器
 *
 * @author xujh
 * @version V1.0
 * @date 2014年11月5日 下午2:37:02
 */
public class WebServiceCaller {


    private static Logger logger = LoggerFactory.getLogger(WebServiceCaller.class);

    /**
     * WebService调用方法</br> 该方法的返回值支持以下类型：基本数据类型、自定义POJO、数组、List（存放基础类型的数据）</br>
     * 当返回值不为List，且存放了自定义的pojo时请勿使用该方法
     *
     * @param code   服务编码
     * @param params 参数
     * @param clazz  返回值类型
     * @return
     */
    public static Object getSimpleObject(String code, Object[] params,
                                         Class<?> clazz) {
        // 获取返回内容
        WebServiceTO to = WebServiceCaller.getWebServiceTO(code, params);
        if (to.getJsonData() == null) {
            return null;
        }
        // 通过Jacksons工具进行反转
        Object object = to.getJsonData();
        if (clazz != null) {
            object = Jacksons.json().fromJsonToObject(to.getJsonData(), clazz);
        }
        return object;
    }

    /**
     * WebService调用方法</br> 该方法的返回值支持以下类型：基本数据类型、自定义POJO、数组、List（存放基础类型的数据）</br>
     * 当返回值不为List，且存放了自定义的pojo时请勿使用该方法
     *
     * @param code   服务编码
     * @param params 参数
     * @param clazz  返回值类型
     * @return
     */
    public static Object getSimpleObject(String code, Object[] params,
                                         Class<?> clazz,long timeout) {
        // 获取返回内容
        WebServiceTO to = WebServiceCaller.getWebServiceTO(code, params,timeout);
        if (to.getJsonData() == null) {
            return null;
        }
        // 通过Jacksons工具进行反转
        Object object = to.getJsonData();
        if (clazz != null) {
            object = Jacksons.json().fromJsonToObject(to.getJsonData(), clazz);
        }
        return object;
    }

    /**
     * WebService调用方法</br> 该方法的返回值支持以下类型：基本数据类型、自定义POJO、数组、List（存放基础类型的数据）</br>
     * 当返回值不为List，且存放了自定义的pojo时请勿使用该方法
     *
     * @param platformCode 平台编码
     * @param code         服务编码
     * @param params       参数
     * @param clazz        返回值类型
     * @return
     */
    public static Object getSimpleObject(String platformCode, String code, Object[] params,
                                         Class<?> clazz) {
        // 获取返回内容
        WebServiceTO to = getWebServiceTO(platformCode, code, params);
        if (to.getJsonData() == null) {
            return null;
        }
        // 通过Jacksons工具进行反转
        Object object = to.getJsonData();
        if (clazz != null) {
            object = Jacksons.json().fromJsonToObject(to.getJsonData(), clazz);
        }
        return object;
    }

    /**
     * WebService调用方法，返回值为List，List中的对象类型为clazz
     *
     * @param code   服务编码
     * @param params 参数
     * @param clazz  数组中存储的返回值类型
     * @return
     */
    public static List<?> getList(String code, Object[] params, Class<?> clazz) {
        // 获取返回内容
        WebServiceTO to = getWebServiceTO(code, params);
        if (to.getJsonData() == null) {
            return null;
        }
        // 通过Jacksons工具进行反转
        return Jacksons.json().fromJsonToList(to.getJsonData(), clazz);
    }

    /**
     * WebService调用方法，返回值为List，List中的对象类型为clazz
     *
     * @param code   服务编码
     * @param params 参数
     * @param clazz  数组中存储的返回值类型
     * @param timeout 访问超时时间
     * @return
     */
    public static List<?> getListAutoTime(String code, Object[] params, Class<?> clazz,long timeout) {
        // 获取返回内容
        WebServiceTO to = getWebServiceTO(code, params,timeout);
        if (to.getJsonData() == null) {
            return null;
        }
        // 通过Jacksons工具进行反转
        return Jacksons.json().fromJsonToList(to.getJsonData(), clazz);
    }

    /**
     * WebService调用方法，返回值为List，List中的对象类型为clazz
     *
     * @param platformCode 平台编码
     * @param code         服务编码
     * @param params       参数
     * @param clazz        数组中存储的返回值类型
     * @return
     */
    public static List<?> getList(String platformCode, String code, Object[] params, Class<?> clazz) {
        // 获取返回内容
        WebServiceTO to = getWebServiceTO(platformCode, code, params);
        if (to.getJsonData() == null) {
            return null;
        }
        // 通过Jacksons工具进行反转
        return Jacksons.json().fromJsonToList(to.getJsonData(), clazz);
    }

    /**
     * WebService调用方法，返回值为Map，其中key的类型为keyClazz，value的类型为valueClazz
     *
     * @param code
     * @param params
     * @param keyClazz
     * @param valueClazz
     * @return
     */
    public static Map<?, ?> getMap(String code, Object[] params,
                                   Class<?> keyClazz, Class<?> valueClazz) {
        // 获取返回内容
        WebServiceTO to = getWebServiceTO(code, params);
        if (to.getJsonData() == null) {
            return null;
        }
        // 通过Jacksons工具进行反转
        return Jacksons.json().fromJsonToMap(to.getJsonData(), keyClazz, valueClazz);
    }

    /**
     * WebService调用方法，返回值为Map，其中key的类型为keyClazz，value的类型为valueClazz
     *
     * @param platformCode
     * @param code
     * @param params
     * @param keyClazz
     * @param valueClazz
     * @return
     */
    public static Map<?, ?> getMap(String platformCode, String code, Object[] params,
                                   Class<?> keyClazz, Class<?> valueClazz) {
        // 获取返回内容
        WebServiceTO to = getWebServiceTO(platformCode, code, params);
        if (to.getJsonData() == null) {
            return null;
        }
        // 通过Jacksons工具进行反转
        return Jacksons.json().fromJsonToMap(to.getJsonData(), keyClazz, valueClazz);
    }

    /**
     * 通过webservice获取WebServiceTO类型的返回对象
     *
     * @param code
     * @param params
     * @param clazz
     * @return
     */
    public static WebServiceTO getWebServiceTO(String code, Object[] params) {
        // 在容器中获取服务信息
        WebServiceVO service = WebServiceContainer.get(code);
        if (service == null) {
            //获取配置文件中的配置信息
            WebServiceLoader.initWebServices();
            service = WebServiceContainer.get(code);
            //如果服务仍然不存在，那么可能是配置出了问题，此时抛出异常
            if (service == null) {
                logger.error("Error Info>>>>>Can not found the service whit code:" + code);
                throw new BaseRunTimeException(TipsMode.Message.toString(), "服务繁忙(404)");
            }
        }

        String url = service.getUri();
        String nameSpace = service.getNameSpace();
        String method = service.getMethod();
//        url = url.replace("192.168.1.207:8083","localhost:8888");
        logger.info("webservice-url>>>>>>>" + url);
        logger.info("webservice-nameSpace>>>>>>>" + nameSpace);
        logger.info("webservice-method>>>>>>>" + method);
        try {
            logger.info("webservice-params>>>>>>>" + JSONHelper.toJSON(params));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 返回值
        Object[] res = null;
        try {
            res = WebServiceUtil.callNormalService(url, nameSpace, method, params, WebServiceTO.class);
        } catch (AxisFault ex) {
            ex.printStackTrace();
            String str = url.replace("http://", "");
            String host = str.split(":")[0];
            throw new BaseRunTimeException(TipsMode.Message.toString(), "服务繁忙(" + host + ")");
        }
        //获取返回内容
        WebServiceTO to = null;
        if (res != null && res.length > 0) {
            to = (WebServiceTO) res[0];
            if (to.getErrorCode() != null) {
                String description = to.getDescription();
                if (StringUtils.isEmpty(description)) {
                    description = "服务繁忙(500)";
                }
                throw new BaseRunTimeException(TipsMode.Message.toString(), description);
            }
        }
        return to;
    }

    /**
     * 通过webservice获取WebServiceTO类型的返回对象
     *
     * @param code
     * @param params
     * @param clazz
     * @return
     */
    public static WebServiceTO getWebServiceTO(String code, Object[] params,long timeout) {
        // 在容器中获取服务信息
        WebServiceVO service = WebServiceContainer.get(code);
        if (service == null) {
            //获取配置文件中的配置信息
            WebServiceLoader.initWebServices();
            service = WebServiceContainer.get(code);
            //如果服务仍然不存在，那么可能是配置出了问题，此时抛出异常
            if (service == null) {
                logger.error("Error Info>>>>>Can not found the service whit code:" + code);
                throw new BaseRunTimeException(TipsMode.Message.toString(), "服务繁忙(404)");
            }
        }

        String url = service.getUri();
        String nameSpace = service.getNameSpace();
        String method = service.getMethod();
        //url = url.replace("goagzlyq.hf.cn:8003","localhost:8080");
        logger.info("webservice-url>>>>>>>" + url);
        logger.info("webservice-nameSpace>>>>>>>" + nameSpace);
        logger.info("webservice-method>>>>>>>" + method);
        try {
            logger.info("webservice-params>>>>>>>" + JSONHelper.toJSON(params));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 返回值
        Object[] res = null;
        try {
            res = WebServiceUtil.callNormalService(url, nameSpace, method, params, WebServiceTO.class);
        } catch (AxisFault ex) {
            ex.printStackTrace();
            String str = url.replace("http://", "");
            String host = str.split(":")[0];
            throw new BaseRunTimeException(TipsMode.Message.toString(), "服务繁忙(" + host + ")");
        }
        //获取返回内容
        WebServiceTO to = null;
        if (res != null && res.length > 0) {
            to = (WebServiceTO) res[0];
            if (to.getErrorCode() != null) {
                String description = to.getDescription();
                if (StringUtils.isEmpty(description)) {
                    description = "服务繁忙(500)";
                }
                throw new BaseRunTimeException(TipsMode.Message.toString(), description);
            }
        }
        return to;
    }

    /**
     * 通过webservice获取WebServiceTO类型的返回对象
     *
     * @param platformCode
     * @param code
     * @param params
     * @return
     */
    public static WebServiceTO getWebServiceTO(String platformCode, String code, Object[] params) {
        // 在容器中获取服务信息
        WebServiceVO service = WebServiceContainer.get(platformCode, code);
        if (service == null) {
            //获取配置文件中的配置信息
            WebServiceLoader.initExternalPlatformWebServices();
            service = WebServiceContainer.get(platformCode, code);
            //如果服务仍然不存在，那么可能是配置出了问题，此时抛出异常
            if (service == null) {
                logger.error("Error Info>>>>>Can not found the service whit code:" + code);
                throw new BaseRunTimeException(TipsMode.Message.toString(), "服务繁忙(404)");
            }
        }
        String url = service.getUri();
        String nameSpace = service.getNameSpace();
        String method = service.getMethod();
        logger.info("webservice-url>>>>>>>" + url);
        logger.info("webservice-nameSpace>>>>>>>" + nameSpace);
        logger.info("webservice-method>>>>>>>" + method);
        logger.info("webservice-params>>>>>>>" + JSONHelper.toJSON(params));
        // 返回值
        Object[] res = null;
        try {
            res = WebServiceUtil.callNormalService(url, nameSpace, method, params, WebServiceTO.class);
        } catch (AxisFault ex) {
            ex.printStackTrace();
            String str = url.replace("http://", "");
            String host = str.split(":")[0];
            throw new BaseRunTimeException(TipsMode.Message.toString(), "服务繁忙(" + host + ")");
        }
        //获取返回内容
        WebServiceTO to = null;
        if (res != null && res.length > 0) {
            to = (WebServiceTO) res[0];
            if (to.getErrorCode() != null) {
                String description = to.getDescription();
                if (StringUtils.isEmpty(description)) {
                    description = "服务繁忙(500)";
                }
                throw new BaseRunTimeException(TipsMode.Message.toString(), description);
            }
        }
        return to;
    }
}
