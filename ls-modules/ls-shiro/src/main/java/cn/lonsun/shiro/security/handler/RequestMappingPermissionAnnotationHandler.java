package cn.lonsun.shiro.security.handler;

import cn.lonsun.shiro.util.PermissionCacheLoader;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.aop.AuthorizingAnnotationHandler;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.subject.WebSubject;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @auth zhongjun
 * @createDate 2018-08-07 11:04
 */
public class RequestMappingPermissionAnnotationHandler extends AuthorizingAnnotationHandler {

    // 日志
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final String propertiesKey = "PermissionUrlProp";

    private final String propertiesName = "permission.properties";

    private static Map<String, String> permissionMap;

    /**
     * Default no-argument constructor that ensures this handler looks for
     * {@link org.apache.shiro.authz.annotation.RequiresPermissions RequiresPermissions} annotations.
     */
    public RequestMappingPermissionAnnotationHandler() {
        super(RequestMapping.class);
    }

    /**
     * Returns the annotation {@link RequiresPermissions#value value}, from which the Permission will be constructed.
     *
     * @param a the RequiresPermissions annotation being inspected.
     * @return the annotation's <code>value</code>, from which the Permission will be constructed.
     */
    protected String[] getAnnotationValue(Annotation a) {
        RequestMapping rpAnnotation = (RequestMapping) a;
        return rpAnnotation.value();
    }

    /**
     * Ensures that the calling <code>Subject</code> has the Annotation's specified permissions, and if not, throws an
     * <code>AuthorizingException</code> indicating access is denied.
     *
     * @param a the RequiresPermission annotation being inspected to check for one or more permissions
     * @throws org.apache.shiro.authz.AuthorizationException
     *          if the calling <code>Subject</code> does not have the permission(s) necessary to
     *          continue access or execution.
     */
    public void assertAuthorized(Annotation a) throws AuthorizationException {
        if (!(a instanceof RequestMapping)) {
            return;
        }
        PermissionCacheLoader.loadPermissionCache();
        Subject subject = getSubject();
        String uri = getRequestUrl(subject);
        if (PermissionCacheLoader.needCheckPermission(uri)) {
            String code = PermissionCacheLoader.getPermissionCode(uri);
            if(StringUtils.isEmpty(code)){
                return;
            }
            String[] codes = code.split(";");
            for(String c : codes){
                try {
                    //如果有一个权限通过，则余下的不做校验
                    subject.checkPermission(c);
                    return;
                } catch (UnauthorizedException e) {
                    logger.error("没有权限：{}", e.getMessage());
                }
            }
            throw new UnauthorizedException("没有权限");
        }
    }

    public String getRequestUrl(Subject subject){
        HttpServletRequest request = WebUtils.toHttp(((WebSubject) subject).getServletRequest());
        return request.getRequestURI();
    }


}
