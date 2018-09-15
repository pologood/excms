package cn.lonsun.core.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * 监听器注册机
 * @author zhongjun
 */
public class ListenerRegister {

    private Logger log = LoggerFactory.getLogger(getClass());

    private SessionFactory sessionFactory;

    private Map<String, Object> listeners;

    public void registerListeners() {
        Assert.notNull(sessionFactory);
        EventListenerRegistry eventListenerRegistry = ((SessionFactoryImpl) sessionFactory)
                .getServiceRegistry().getService(EventListenerRegistry.class);
        try {
            for (String key : listeners.keySet()) {
                EventType eventType = EventType.resolveEventTypeByName(key);
                eventListenerRegistry.getEventListenerGroup(eventType).prependListener(listeners.get(key));
            }
            log.error("hibernate监听添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("添加hibernate监听失败");
        }
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void setListeners(Map<String, Object> listeners) {
        this.listeners = listeners;
    }
}
