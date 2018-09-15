package cn.lonsun.shiro.security.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.apache.shiro.aop.AnnotationResolver;
import org.apache.shiro.aop.MethodInvocation;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.aop.*;
import org.apache.shiro.spring.aop.SpringAnnotationResolver;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 重写 org.apache.shiro.spring.security.interceptor.AopAllianceAnnotationsAuthorizingMethodInterceptor
 * 用于加载配置文件中的权限配置
 * @auth zhongjun
 * @createDate 2018-08-07 8:32
 */
public class LsAopAllianceAnnotationsAuthorizingMethodInterceptor extends AuthorizingMethodInterceptor implements MethodInterceptor {

    /**
     * The method interceptors to execute for the annotated method.
     */
    protected Collection<AuthorizingAnnotationMethodInterceptor> methodInterceptors;

    public LsAopAllianceAnnotationsAuthorizingMethodInterceptor(List<AuthorizingAnnotationMethodInterceptor> interceptors) {
        List<AuthorizingAnnotationMethodInterceptor> interceptors2 = new ArrayList();
        AnnotationResolver resolver = new SpringAnnotationResolver();
        interceptors.add(new RoleAnnotationMethodInterceptor(resolver));
        interceptors.add(new PermissionAnnotationMethodInterceptor(resolver));
        interceptors.add(new AuthenticatedAnnotationMethodInterceptor(resolver));
        interceptors.add(new UserAnnotationMethodInterceptor(resolver));
        interceptors.add(new GuestAnnotationMethodInterceptor(resolver));
        if(interceptors != null && interceptors.size() != 0){
            interceptors2.addAll(interceptors);
        }
        this.setMethodInterceptors(interceptors2);
    }

    protected MethodInvocation createMethodInvocation(Object implSpecificMethodInvocation) {
        final org.aopalliance.intercept.MethodInvocation mi = (org.aopalliance.intercept.MethodInvocation)implSpecificMethodInvocation;
        return new MethodInvocation() {
            public Method getMethod() {
                return mi.getMethod();
            }

            public Object[] getArguments() {
                return mi.getArguments();
            }

            public String toString() {
                return "Method invocation [" + mi.getMethod() + "]";
            }

            public Object proceed() throws Throwable {
                return mi.proceed();
            }

            public Object getThis() {
                return mi.getThis();
            }
        };
    }

    protected Object continueInvocation(Object aopAllianceMethodInvocation) throws Throwable {
        org.aopalliance.intercept.MethodInvocation mi = (org.aopalliance.intercept.MethodInvocation)aopAllianceMethodInvocation;
        return mi.proceed();
    }

    public Object invoke(org.aopalliance.intercept.MethodInvocation methodInvocation) throws Throwable {
        MethodInvocation mi = this.createMethodInvocation(methodInvocation);
        return super.invoke(mi);
    }

    /**
     * Returns the method interceptors to execute for the annotated method.
     * <p/>
     * Unless overridden by the {@link #setMethodInterceptors(java.util.Collection)} method, the default collection
     * contains a
     * {@link RoleAnnotationMethodInterceptor RoleAnnotationMethodInterceptor} and a
     * {@link PermissionAnnotationMethodInterceptor PermissionAnnotationMethodInterceptor} to
     * support role and permission annotations automatically.
     * @return the method interceptors to execute for the annotated method.
     */
    public Collection<AuthorizingAnnotationMethodInterceptor> getMethodInterceptors() {
        return methodInterceptors;
    }

    /**
     * Sets the method interceptors to execute for the annotated method.
     * @param methodInterceptors the method interceptors to execute for the annotated method.
     * @see #getMethodInterceptors()
     */
    public void setMethodInterceptors(Collection<AuthorizingAnnotationMethodInterceptor> methodInterceptors) {
        this.methodInterceptors = methodInterceptors;
    }

    /**
     * Iterates over the internal {@link #getMethodInterceptors() methodInterceptors} collection, and for each one,
     * ensures that if the interceptor
     * {@link AuthorizingAnnotationMethodInterceptor#supports(org.apache.shiro.aop.MethodInvocation) supports}
     * the invocation, that the interceptor
     * {@link AuthorizingAnnotationMethodInterceptor#assertAuthorized(org.apache.shiro.aop.MethodInvocation) asserts}
     * that the invocation is authorized to proceed.
     */
    protected void assertAuthorized(MethodInvocation methodInvocation) throws AuthorizationException {
        //default implementation just ensures no deny votes are cast:
        Collection<AuthorizingAnnotationMethodInterceptor> aamis = getMethodInterceptors();
        if (aamis != null && !aamis.isEmpty()) {
            for (AuthorizingAnnotationMethodInterceptor aami : aamis) {
                if (aami.supports(methodInvocation)) {
                    aami.assertAuthorized(methodInvocation);
                }
            }
        }
    }
}
