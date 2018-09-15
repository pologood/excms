package cn.lonsun.shiro;

import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.ResultVO;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.shiro.util.AjaxRequestUtil;
import cn.lonsun.shiro.util.ShiroExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

/**
 *
 * SQL注入拦截器
 * Created by zsy on 2016-9-26.
 */
public class SqlInterceptor {

    // 日志
    private static final Logger logger = LoggerFactory.getLogger(ShiroExceptionUtil.class);

    /**
     * 拦截非法字符
     * @param request
     * @param response
     */
    public static boolean intercept(HttpServletRequest request, HttpServletResponse response) {

        Enumeration enumeration = request.getParameterNames();
        while (enumeration.hasMoreElements()) {
            String paramName = (String) enumeration.nextElement();
            String paramValue = request.getParameter(paramName);
            if ((checkString(paramValue) == 0)) {
                System.out.println("被过滤的字段--paramName:" + paramName + "   paramValue:" + paramValue);
                if (!AjaxRequestUtil.isAjax(request)) {
                    try {
                        response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    } catch (IOException e) {
                        e.printStackTrace();
                        logger.error("服务器异常！",e);
                    }
                } else {
                    ResultVO vo = new ResultVO();
                    vo.setStatus(ResultVO.Status.Failure.getValue());
                    vo.setDesc("请求参数存在安全风险,被系统拦截");
                    AjaxRequestUtil.printAjax(response, vo);
                }
                return true;
            }
        }

        return false;
    }

    public static int checkString(String in) {
        try {
            if ((in == null) || (in.trim().length() == 0))
                return 1;

            if ((in.indexOf("|") >= 0) || (in.indexOf("&") >= 0)
                    || (in.indexOf(";") >= 0) || (in.indexOf("$") >= 0)
                    || (in.indexOf("%") >= 0) || (in.indexOf("@") >= 0)
                    || (in.indexOf(">") >= 0) || (in.indexOf("<") >= 0)
                    || (in.indexOf("(") >= 0) || (in.indexOf(")") >= 0))
                return 0;
        } catch (Exception e) {
            return 0;
        }
        return 1;
    }


}
