package cn.lonsun.staticcenter.activemq;

import cn.lonsun.staticcenter.generate.GenerateCustomer;
import cn.lonsun.staticcenter.generate.thread.ThreadCount;
import cn.lonsun.statictask.internal.entity.StaticTaskEO;
import cn.lonsun.statictask.internal.service.IStaticTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 * 生成静态队列监听器
 * @author zhongjun
 * @mqdestination EX8.STATIC.GENERATE.QUEUE
 */
@Component("staticGenerateQueueListener")
public class StaticGenerateQueueListener implements MessageListener {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private IStaticTaskService staticTaskService;

    @Resource
    private GenerateCustomer customer;

    /**
     * 这个类对接收消息没有任何限制，容易造成服务器过载
     * @param message
     */
    @Override
    public void onMessage(Message message) {
        try {
            Object task = ((ObjectMessage) message).getObject();
            customer.execute((StaticTaskEO)task);// 消费
        } catch (JMSException e) {
            logger.error("activemq连接失败，请检查其是否启动！", e);
        } catch (Throwable e) {
            logger.error("生成静态线程异常，请检查！");
            throw new RuntimeException(e);
        }
    }

    public IStaticTaskService getStaticTaskService() {
        return staticTaskService;
    }

    public void setStaticTaskService(IStaticTaskService staticTaskService) {
        this.staticTaskService = staticTaskService;
    }

    public GenerateCustomer getCustomer() {
        return customer;
    }

    public void setCustomer(GenerateCustomer customer) {
        this.customer = customer;
    }
}
