
package cn.lonsun.staticcenter.exproject.tgq;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebServiceClient(name = "WebInter2016", targetNamespace = "http://tempuri.org/", wsdlLocation = "http://zwgk.tl.gov.cn/web/WebInter2016.asmx?wsdl")
public class WebInter2016
    extends Service
{

    private final static URL WEBINTER2016_WSDL_LOCATION;
    private final static Logger logger = Logger.getLogger(WebInter2016 .class.getName());

    static {
        URL url = null;
        try {
            URL baseUrl;
            baseUrl = WebInter2016 .class.getResource(".");
            url = new URL(baseUrl, "http://zwgk.tl.gov.cn/web/WebInter2016.asmx?wsdl");
        } catch (MalformedURLException e) {
            logger.warning("Failed to create URL for the wsdl Location: 'http://zwgk.tl.gov.cn/web/WebInter2016.asmx?wsdl', retrying as a local file");
            logger.warning(e.getMessage());
        }
        WEBINTER2016_WSDL_LOCATION = url;
    }

    public WebInter2016(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public WebInter2016() {
        super(WEBINTER2016_WSDL_LOCATION, new QName("http://tempuri.org/", "WebInter2016"));
    }

    /**
     * 
     * @return
     *     returns WebInter2016Soap
     */
    @WebEndpoint(name = "WebInter2016Soap")
    public WebInter2016Soap getWebInter2016Soap() {
        return super.getPort(new QName("http://tempuri.org/", "WebInter2016Soap"), WebInter2016Soap.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns WebInter2016Soap
     */
    @WebEndpoint(name = "WebInter2016Soap")
    public WebInter2016Soap getWebInter2016Soap(WebServiceFeature... features) {
        return super.getPort(new QName("http://tempuri.org/", "WebInter2016Soap"), WebInter2016Soap.class, features);
    }

}
