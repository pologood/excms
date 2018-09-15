/**
 * 
 */
package cn.lonsun.message;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import cn.lonsun.activemq.MessageChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jms.core.JmsTemplate;

import cn.lonsun.activemq.MessageConfig;
import cn.lonsun.message.internal.entity.MessageSystemEO;

/**
 * 
 * EX8系统管理消息推送监听类. <br/>
 *
 * @date: 2015年8月19日 下午2:58:45 <br/>
 * @author fangtinghua
 * @see cn.lonsun.message.SystemMessageListener
 */
@Deprecated
public class MessageListener {

    // 日志
    private static final Logger logger = LoggerFactory.getLogger(MessageListener.class);

    @Resource
    private JmsTemplate jmsTemplate;

    // 线程池执行接口
    @Resource
    private TaskExecutor taskExecutor;

    @Autowired
    private SystemMessageListener SystemMessageListener;

    @PostConstruct
    public void message() {
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                do {
                    Message objectMessage = jmsTemplate.receive(MessageChannel.MESSAGE_QUEUE.toString());
                    // 消费
                    SystemMessageListener.onMessage(objectMessage);
                } while (true);
            }
        });
    }
}