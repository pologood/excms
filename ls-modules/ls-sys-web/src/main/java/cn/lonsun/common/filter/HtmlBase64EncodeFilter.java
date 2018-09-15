package cn.lonsun.common.filter;

import cn.lonsun.security.web.filters.XSSWrapperRequest;
import cn.lonsun.shiro.filter.DelegatingFilterProxy;
import cn.lonsun.shiro.util.LegalAccessUrlUtil;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.util.WebUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @auth zhongjun
 * @createDate 2018-08-02 18:25
 */
public class HtmlBase64EncodeFilter extends DelegatingFilterProxy {

    private static final Logger logger = LoggerFactory.getLogger(HtmlBase64EncodeFilter.class);
    public static final String REQUEST_ATTRIBUTE_ISMULTIPART = DelegatingFilterProxy.class.getName() + ".isMultipart";
    private MultipartResolver multipartResolver = null;
    private boolean filterXss = false;

    protected Filter initDelegate(WebApplicationContext wac) throws ServletException {
        this.filterXss = BooleanUtils.toBoolean(this.getFilterConfig().getInitParameter("filterXss"));
        this.multipartResolver = (MultipartResolver)wac.getBean("multipartResolver", MultipartResolver.class);
        return super.initDelegate(wac);
    }

    protected void invokeDelegate(Filter delegate, ServletRequest request, ServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest httpRequest = (HttpServletRequest)request;

        if(httpRequest.getParameter("base64") != null && !httpRequest.getParameter("base64").equals("")){
            logger.info("处理base64编码：{}", httpRequest.getParameter("base64"));
            httpRequest = new Base64WrapperRequest((HttpServletRequest)httpRequest);
        }

        if (this.filterXss && !LegalAccessUrlUtil.containsValue(((HttpServletRequest)httpRequest).getRequestURI())) {
            httpRequest = new XSSWrapperRequest((HttpServletRequest)httpRequest);
        }

        if (null != this.multipartResolver && this.multipartResolver.isMultipart((HttpServletRequest)httpRequest)) {
            request.setAttribute(REQUEST_ATTRIBUTE_ISMULTIPART, true);
            if (WebUtils.getNativeRequest((ServletRequest)httpRequest, MultipartHttpServletRequest.class) != null) {
                logger.debug("Request is already a MultipartHttpServletRequest - if not in a forward, this typically results from an additional MultipartFilter in web.xml");
            } else if (((HttpServletRequest)httpRequest).getAttribute("javax.servlet.error.exception") instanceof MultipartException) {
                logger.debug("Multipart resolution failed for current request before - skipping re-resolution for undisturbed error rendering");
            } else {
                httpRequest = this.multipartResolver.resolveMultipart((HttpServletRequest)httpRequest);
            }
        }

        super.invokeDelegate(delegate, (ServletRequest)httpRequest, response, filterChain);
    }


    private void resolveBase64Field(ServletRequest request){



    }
}
