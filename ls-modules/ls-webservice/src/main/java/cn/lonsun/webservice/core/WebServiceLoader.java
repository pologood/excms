package cn.lonsun.webservice.core;

import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.exception.util.Jacksons;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.webservice.processEngine.util.AppUtil;
import cn.lonsun.webservice.rbac.IPlatformService;
import cn.lonsun.webservice.to.WebServiceTO;
import cn.lonsun.webservice.vo.axis2.WebServiceVO;
import cn.lonsun.webservice.vo.rbac.PlatformVO;
import org.apache.axis2.AxisFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * WebService加载器
 *
 * @author xujh
 * @version 1.0
 *          2015年4月28日
 */
public class WebServiceLoader {

    private static Logger logger = LoggerFactory.getLogger(WebServiceLoader.class);
    //项目启动时，由WebServiceServlet进行初始化
    public static List<WebServiceLoaderVO> loaderList = new ArrayList<WebServiceLoaderVO>();

    /**
     * 本平台应用服务初始化
     */
    @SuppressWarnings("unchecked")
    public static void initWebServices() {
        WebServiceContainer.clear();
        // 返回的对象类型
        Class<?> clazz = WebServiceTO.class;
        // 返回值
        WebServiceTO vo = null;
        Object[] res = null;
        for (WebServiceLoaderVO loader : loaderList) {
            try {
                res = WebServiceUtil.callNormalService(loader.getUrl(), loader.getNameSpace(), loader.getMethod(), new Object[]{loader.getParams()}, clazz);
            } catch (AxisFault ex) {
                ex.printStackTrace();
                logger.error("Error>>>>>>调用外部服务异常，请检查系统管理是否正常运行...");
                String str = loader.getUrl().replace("http://", "");
                String host = str.split(":")[0];
                //throw new BaseRunTimeException(TipsMode.Message.toString(), "服务繁忙(" + host + ")");
            }
            if (res != null && res.length > 0) {
                vo = (WebServiceTO) res[0];
                String json = vo.getJsonData();
                logger.info("webservice接口数据：{}", json);
                //通过Jacksons工具进行反转
                List<WebServiceVO> services = (List<WebServiceVO>) Jacksons.json().fromJsonToList(json, WebServiceVO.class);
                if (services != null && services.size() > 0) {
                    String serviceHost = loader.getUrl().substring(0,loader.getUrl().indexOf("/services"));
                    for (WebServiceVO service : services) {
                        if (service != null && !StringUtils.isEmpty(service.getCode())) {
                            //存放到容器中
                            if(!AppUtil.isEmpty(service.getUri()) && !service.getUri().startsWith("http")){
                                service.setUri(serviceHost+service.getUri());
                            }
                            WebServiceContainer.put(service.getCode(), service);
                        }
                    }
                }
            }
        }
    }

    /**
     * 加载外平台的WebService，需要建立在本平台系统管理的WebService正常调用的基础上
     */
    @SuppressWarnings("unchecked")
    public static void initExternalPlatformWebServices() {
        //开始初始化外平台的WebService
        IPlatformService platformService = SpringContextHolder.getBean("indirectPlatfromService");
        List<PlatformVO> platforms = platformService.getExternalPlatforms();
        if (platforms != null && platforms.size() > 0) {
            for (PlatformVO platform : platforms) {
                String code = platform.getCode();
                String url = platform.getWebserviceUrl();
                String nameSpace = platform.getNameSpace();
                String method = platform.getMethod();
                if (StringUtils.isEmpty(code) || StringUtils.isEmpty(url) || StringUtils.isEmpty(nameSpace) || StringUtils.isEmpty(method)) {
                    logger.error("此平台信息不全，webservice未进行初始化....");
                    continue;
                }
                // 返回值
                WebServiceTO to = null;
                Object[] res = null;
                try {
                    res = WebServiceUtil.callNormalService(url, nameSpace, method, new Object[]{}, WebServiceTO.class);
                } catch (AxisFault ex) {
                    ex.printStackTrace();
                    throw new BaseRunTimeException(TipsMode.Message.toString(), "调用外部服务异常，请联系管理员.");
                }
                if (res != null && res.length > 0) {
                    to = (WebServiceTO) res[0];
                    //外平台服务报异常
                    if (to.getErrorCode() != null) {
                        String description = to.getDescription();
                        if (StringUtils.isEmpty(description)) {
                            String str = url.replace("http://", "");
                            String host = str.split(":")[0];
                            description = "系统繁忙(" + host + ")";
                        }
                        logger.error(description);
                    }
                    String json = to.getJsonData();
                    //通过Jacksons工具进行反转
                    List<WebServiceVO> services = (List<WebServiceVO>) Jacksons.json().fromJsonToList(json, WebServiceVO.class);
                    if (services != null && services.size() > 0) {
                        for (WebServiceVO service : services) {
                            if (service != null && !StringUtils.isEmpty(service.getCode())) {
                                //存放到容器中
                                WebServiceContainer.put(platform.getCode(), service.getCode(), service);
                            }
                        }
                    }
                }
            }
        }
    }

}
