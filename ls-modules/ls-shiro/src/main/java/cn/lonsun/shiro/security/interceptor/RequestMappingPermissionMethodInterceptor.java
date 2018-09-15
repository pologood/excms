package cn.lonsun.shiro.security.interceptor;

import cn.lonsun.shiro.security.handler.RequestMappingPermissionAnnotationHandler;
import org.apache.shiro.aop.AnnotationResolver;
import org.apache.shiro.aop.MethodInvocation;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.aop.AuthorizingAnnotationHandler;
import org.apache.shiro.authz.aop.AuthorizingAnnotationMethodInterceptor;

/**
 * @auth zhongjun
 * @createDate 2018-08-07 11:23
 */
public class RequestMappingPermissionMethodInterceptor  extends AuthorizingAnnotationMethodInterceptor {

    public RequestMappingPermissionMethodInterceptor() {
        super( new RequestMappingPermissionAnnotationHandler() );
    }

    /**
     * @param resolver
     * @since 1.1
     */
    public RequestMappingPermissionMethodInterceptor(AnnotationResolver resolver) {
        super( new RequestMappingPermissionAnnotationHandler(), resolver);
    }

    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        assertAuthorized(methodInvocation);
        return methodInvocation.proceed();
    }

    public void assertAuthorized(MethodInvocation mi) throws AuthorizationException {
        try {
            RequiresPermissions annotation = mi.getMethod().getAnnotation(RequiresPermissions.class);
            if(annotation == null){ //如果设置了注解，则配置文件不生效
                ((AuthorizingAnnotationHandler)getHandler()).assertAuthorized(getAnnotation(mi));
            }
        }
        catch(AuthorizationException ae) {
            if (ae.getCause() == null) ae.initCause(new AuthorizationException("Not authorized to invoke method: " + mi.getMethod()));
            throw ae;
        }
    }

}
