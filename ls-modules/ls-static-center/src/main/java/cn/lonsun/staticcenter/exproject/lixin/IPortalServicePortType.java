
package cn.lonsun.staticcenter.exproject.lixin;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebService(name = "IPortalServicePortType", targetNamespace = "http://services.ws.iflytek.com")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface IPortalServicePortType {


    /**
     * 
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(name = "out", targetNamespace = "http://services.ws.iflytek.com")
    @RequestWrapper(localName = "getLxxSearchTemplate", targetNamespace = "http://services.ws.iflytek.com", className = "com.iflytek.ws.services.GetLxxSearchTemplate")
    @ResponseWrapper(localName = "getLxxSearchTemplateResponse", targetNamespace = "http://services.ws.iflytek.com", className = "com.iflytek.ws.services.GetLxxSearchTemplateResponse")
    public String getLxxSearchTemplate();

}
