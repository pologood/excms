package cn.lonsun.shiro.security.interceptor;

import org.apache.shiro.authz.annotation.*;
import org.apache.shiro.authz.aop.AuthorizingAnnotationMethodInterceptor;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @auth zhongjun
 * @createDate 2018-08-07 8:30
 */
public class LsAuthorizationAttributeSourceAdvisor extends StaticMethodMatcherPointcutAdvisor {

    private static final Logger log = LoggerFactory.getLogger(AuthorizationAttributeSourceAdvisor.class);
    private static final Class<? extends Annotation>[] AUTHZ_ANNOTATION_CLASSES =
            new Class[]{RequiresPermissions.class, RequiresRoles.class, RequiresUser.class, RequiresGuest.class, RequiresAuthentication.class};
    protected SecurityManager securityManager = null;

    protected Set<Class<? extends Annotation>> annotations = new HashSet<Class<? extends Annotation>>();

    public LsAuthorizationAttributeSourceAdvisor(Map<String, String> annotationHandlers) throws Exception {

        List<AuthorizingAnnotationMethodInterceptor> interceptors = new ArrayList(annotationHandlers.size());

        if(annotationHandlers != null && annotationHandlers.size() != 0){
            for(Map.Entry<String, String> item : annotationHandlers.entrySet()){
                Class annotation = Class.forName(item.getKey());
                AuthorizingAnnotationMethodInterceptor handlers = (AuthorizingAnnotationMethodInterceptor)getBean(item.getValue());
                interceptors.add(handlers);
                this.annotations.add(annotation);
            }
        }
        this.setAdvice(new LsAopAllianceAnnotationsAuthorizingMethodInterceptor(interceptors));
    }

    public SecurityManager getSecurityManager() {
        return this.securityManager;
    }

    public void setSecurityManager(SecurityManager securityManager) {
        this.securityManager = securityManager;
    }

    public boolean matches(Method method, Class targetClass) {
        Method m = method;
        if (this.isAuthzAnnotationPresent(method)) {
            return true;
        } else {
            if (targetClass != null) {
                try {
                    m = targetClass.getMethod(m.getName(), m.getParameterTypes());
                    if (this.isAuthzAnnotationPresent(m)) {
                        return true;
                    }
                } catch (NoSuchMethodException var5) {
                    ;
                }
            }

            return false;
        }
    }

    private boolean isAuthzAnnotationPresent(Method method) {
        Class[] arr$ = AUTHZ_ANNOTATION_CLASSES;
        int len$ = arr$.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            Class<? extends Annotation> annClass = arr$[i$];
            Annotation a = AnnotationUtils.findAnnotation(method, annClass);
            if (a != null) {
                return true;
            }
        }
        for(Class<? extends Annotation> annClass : this.annotations){
            Annotation a = AnnotationUtils.findAnnotation(method, annClass);
            if (a != null) {
                return true;
            }
        }
        return false;
    }

    public Object getBean(String className) throws Exception {
        Class cls = Class.forName(className);
        Constructor[] cons = cls.getConstructors();
        if (cons == null || cons.length < 1) {
            throw new Exception("没有默认构造方法！");
        }
        Constructor defCon = cons[0];//得到默认构造器,第0个是默认构造器，无参构造方法
        Object obj = defCon.newInstance();//实例化，得到一个对象 //Spring - bean -id
        return obj;
    }
}
