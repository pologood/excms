package cn.lonsun.staticcenter.activemq;

import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.message.internal.entity.MessageStaticEO;
import cn.lonsun.staticcenter.generate.GenerateCustomer;
import cn.lonsun.staticcenter.generate.util.ContextUtil;
import cn.lonsun.statictask.internal.entity.StaticTaskEO;
import cn.lonsun.statictask.internal.service.IStaticTaskService;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 * 生成静态取消队列
 * @author zhongjun
 * @mqdestination EX8.STATIC.CANCEL.QUEUE
 */
@Component("staticCancelQueueListener")
public class StaticCancelQueueListener implements MessageListener {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private IStaticTaskService staticTaskService = SpringContextHolder.getBean(IStaticTaskService.class);

    @Resource
    private GenerateCustomer customer;

    @Override
    public void onMessage(Message message) {
        try {
            Object task = ((ObjectMessage) message).getObject();
            StaticTaskEO staticTaskEO = (StaticTaskEO) task;
            MessageStaticEO messageEO = JSON.parseObject(staticTaskEO.getJson(), MessageStaticEO.class);
            logger.debug("任务终止开始");
            ContextUtil.overTask(messageEO.getTaskId(), null); // 终止任务
            logger.debug("任务终止完毕，删除数据"+messageEO.getTaskId());
            //删除数据
            staticTaskService.delete(StaticTaskEO.class, messageEO.getTaskId());
        } catch (JMSException e) {
            logger.error("activemq连接失败，请检查其是否启动！", e);
        } catch (Throwable e) {
            logger.error("生成静态线程异常，请检查！");
            throw new RuntimeException(e);
        }
    }

    public GenerateCustomer getCustomer() {
        return customer;
    }

    public void setCustomer(GenerateCustomer customer) {
        this.customer = customer;
    }
}