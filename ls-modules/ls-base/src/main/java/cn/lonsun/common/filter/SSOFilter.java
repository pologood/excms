package cn.lonsun.common.filter;

import cn.lonsun.common.config.LegalAccessResourcesConfig;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.util.ResultVO;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author zhusy
 * @version V1.0
 * @date 2014年9月26日 上午11:09:18
 */
public class SSOFilter implements Filter {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void init(FilterConfig arg0) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) arg0;
        HttpServletResponse response = (HttpServletResponse) arg1;
        HttpSession session = request.getSession();

        if (request.getParameter("JSESSIONID") != null) {
            //session = LsSessionContext.getSession(request.getParameter("JSESSIONID"));
        }
        // 2.vm页面禁止访问
        if (isIllegalAccess(request, response)) {
            return;
        }
        Object userId = session.getAttribute("userId");
        Object organId = session.getAttribute("organId");
        // 获取当前的上下文
        String webContext = request.getContextPath();
        String uri = request.getRequestURI().replace(webContext, "");
        // 带"."的静态资源都无需进行权限验证
        if (!uri.contains(".")) {
            // 需要跳过权限验证的资源直接忽略
            if (!LegalAccessResourcesConfig.isContainValue(uri)) {
                if (userId == null) {
                    String dataType = AppUtil.lowerCase(request.getParameter("dataType"));
                    if (!((request.getHeader("X-Requested-With") != null && request.getHeader("X-Requested-With").contains("XMLHttpRequest")))) {
                        logger.info("this is not ajax request!");
                        // 普通页面访问时,弹出小的登录窗口
                        reSSOLogin(request, response, false, dataType);
                        String host = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
                        logger.info(webContext);
                        response.sendRedirect(host + webContext + "/login");
                        return;
                    } else {
                        logger.info("this is ajax request!");
                        reSSOLogin(request, response, true, dataType);
                        // 在请求头中存储ajax信息
                        response.setHeader("sessionstatus", "timeout");
                        return;
                    }
                }
            }
        }
        chain.doFilter(arg0, arg1);
    }

    private void reSSOLogin(HttpServletRequest request, HttpServletResponse response, boolean isAjax, String dataType) throws ServletException, IOException {
        logger.info("reSSOLogin >>> request.getRequestURI() = " + request.getRequestURI());
        String appUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        logger.info(request.getRequestURI());
        // 获取当前路径
        String webContext = request.getContextPath();
        // 获取完整路径
        String host = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();

        if (isAjax && (!AppUtil.isEmpty(dataType) && dataType.equals("html"))) {
            request.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            StringBuilder sb = new StringBuilder();
            sb.append("<script>");
            sb.append("top.location.href=\"" + appUrl + "/login\"");
            sb.append("</script>");
            out.print(sb.toString());
            out.flush();
        } else if (isAjax) {
            ResultVO ro = new ResultVO();
            ro.setStatus(0);
            ro.setData(-9);
            ro.setDesc("应用模块SSO失效,尝试重新验证!");
            Object json = null;
            String callback = request.getParameter("callback");
            if (callback != null && !"".equals(callback)) {
                json = JSON.toJSON(new JSONPObject(callback, ro));
            } else {
                json = JSON.toJSON(ro);
            }
            PrintWriter writer = null;
            try {
                response.setHeader("Pragma", "no-cache");
                response.setHeader("Cache-Control", "no-cache");
                response.setDateHeader("Expires", 0);
                response.setContentType("application/json;charset=UTF-8");
                writer = response.getWriter();
                writer.print(json);
            } catch (Exception e2) {
            } finally {
                if (writer != null) {
                    writer.close();
                }
            }
        } else {

            // 如果session中不存在登录者实体，则弹出框提示重新登录
            // 设置request和response的字符集，防止乱码
            // request.setCharacterEncoding("UTF-8");
            // PrintWriter out = response.getWriter();
            // StringBuilder sb = new StringBuilder();
            // sb.append("<!doctype html><html><head><meta charset=\"utf-8\">");
            // sb.append("<script src=\"/assets/core/jquery.min.js\"></script>");
            // sb.append("<script>");
            // sb.append("var __WEB_CONTEXT_PATH = \"" + webContext + "\";");
            // sb.append("var __WEB_HOST = \"" + host + "\";");
            // sb.append("</script>");
            // sb.append("<script src=\"/assets/core/boot.js\" debug=\"0\"></script>");
            // sb.append("<script src=\"/app/common/js/config.js\"></script>");
            // sb.append("<script>");
            // sb.append("jQuery(document).ready(function(){");
            // sb.append("  __ssoReLogin('" + "" + "','" + appUrl + "')");
            // sb.append("");
            // sb.append("});");
            // sb.append("</script>");
            // sb.append("");
            // sb.append("<base target=\"_self\"></head><body><a id=\"reload\" style=\"display:none\">reload...</a></body></html>");
            // out.print(sb.toString());
            // out.flush();

        }
    }

    @Override
    public void destroy() {
    }

    /**
     * 判断请求是否合法，如果是直接访问jsp页面，返回不合法fasle
     *
     * @param request
     * @param response
     * @return
     * @throws java.io.IOException
     */
    private boolean isIllegalAccess(HttpServletRequest request, HttpServletResponse response) throws IOException {
        boolean isIllegal = false;
        String path = request.getServletPath();
        try {
            // jsp返回403
            if (path.endsWith(".vm")) {
                logger.error(path + " error!");
                response.sendError(403);
            }
        } catch (IOException e) {
            throw new RuntimeException();
        }
        return isIllegal;
    }

}
