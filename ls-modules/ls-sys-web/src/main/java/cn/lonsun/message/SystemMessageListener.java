package cn.lonsun.message;

import cn.lonsun.message.internal.entity.MessageSystemEO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 * 系统消息队列监听处理器
 * 用来接收mq的消息，然后通过dwr转发到前台
 * @author zhongjun
 */
@Component("systemMessageListener")
public class SystemMessageListener implements MessageListener {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private MessageCustomer messageCustomer;

    @Override
    public void onMessage(Message message) {
       try{
            ObjectMessage objectMessage = (ObjectMessage)message;
            // 消费
            messageCustomer.add((MessageSystemEO) objectMessage.getObject());
        } catch (JMSException e) {
            logger.error("activemq连接失败，请检查其是否启动！", e);
        }
    }
}
