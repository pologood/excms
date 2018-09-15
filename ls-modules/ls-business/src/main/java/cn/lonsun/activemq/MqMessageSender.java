package cn.lonsun.activemq;

import org.apache.activemq.command.ActiveMQDestination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.*;
import java.io.Serializable;

/**
 * mq消息发送接口
 * 使用： 在配置文件中加入配置，
 * @author zhongjun
 */
public class MqMessageSender {

    private Logger log = LoggerFactory.getLogger(getClass());

    public MqMessageSender() { }

    public MqMessageSender(ActiveMQDestination destination) {
        this.destination = destination;
    }

    public MqMessageSender(JmsTemplate jmsTemplate, ActiveMQDestination destination) {
        this.jmsTemplate = jmsTemplate;
        this.destination = destination;
    }

    private JmsTemplate jmsTemplate;

    private ActiveMQDestination destination;

    /**
     * 发送一条消息到指定的队列（目标）
     * @param message 消息内容
     */
    public void send(final Serializable message){
        jmsTemplate.send(destination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                log.debug("消息队列发送消息，队列名：{}, 消息体：{}", destination.getPhysicalName(), message);
                return session.createObjectMessage(message);
            }
        });
    }

    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void setDestination(ActiveMQDestination destination) {
        this.destination = destination;
    }
}
