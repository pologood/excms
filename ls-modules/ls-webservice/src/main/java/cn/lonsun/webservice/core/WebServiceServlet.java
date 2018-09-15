package cn.lonsun.webservice.core;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.lonsun.core.util.PropertiesReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Properties;

/**
 * WebService初始化
 * 
 * @author xujh
 * @date 2014年11月5日 上午8:44:30
 * @version V1.0
 */
public class WebServiceServlet extends HttpServlet {

    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 2145507932782652645L;

    @Override
    public void init(ServletConfig config) throws ServletException {
        Properties properties = new Properties();
        try {
            Resource resource = new ClassPathResource("webservice.properties");
            properties.load(resource.getInputStream());
            int serviceCount = Integer.valueOf(properties.getProperty("serviceCount").trim()).intValue();
            for (int i = 0; i < serviceCount; i++) {
                String url = properties.getProperty("url[" + i + "]");
                String nameSpace = properties.getProperty("nameSpace[" + i + "]");
                String method = properties.getProperty("method[" + i + "]");
                String systemCodes = properties.getProperty("systemCodes[" + i + "]");
                String[] params = systemCodes.split(",");
                WebServiceLoaderVO loader = new WebServiceLoaderVO(url, nameSpace, method, systemCodes, params);
                WebServiceLoader.loaderList.add(loader);
            }
            WebServiceLoader.initWebServices();
        } catch (Throwable e) {
            throw new ServletException("webservice初始化失败！", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
        log.info("重新加载webservice");
        WebServiceLoader.initWebServices();
    }
}
