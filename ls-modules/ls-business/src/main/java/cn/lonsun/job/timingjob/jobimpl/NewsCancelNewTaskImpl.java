package cn.lonsun.job.timingjob.jobimpl;

import cn.lonsun.activemq.MessageSenderUtil;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.job.service.ISchedulerService;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.message.internal.entity.MessageStaticEO;

/**
 * Created by liuk on 2017/01/16.
 */
public class NewsCancelNewTaskImpl extends ISchedulerService {

    private static IBaseContentService baseContentService= SpringContextHolder.getBean("baseContentService");

    @Override
    public void execute(String json) {
        Long id=Long.parseLong(json);
        try {
            BaseContentEO eo = baseContentService.getEntity(BaseContentEO.class, id);
            if (eo != null && eo.getIsNew() == 1) {
                eo.setIsNew(0);
                baseContentService.updateEntity(eo);
                MessageStaticEO messageStaticEO = new MessageStaticEO(eo.getSiteId(), eo.getColumnId(), new Long[]{id});
                messageStaticEO.setType(MessageEnum.PUBLISH.value());

                //设置信息接收人
                if(!AppUtil.isEmpty(eo.getUpdateUserId())){
                    messageStaticEO.setUserId(eo.getUpdateUserId());
                }else if(!AppUtil.isEmpty(eo.getCreateUserId())){
                    messageStaticEO.setUserId(eo.getCreateUserId());
                }

                MessageSenderUtil.publishContent(messageStaticEO, 1);
                CacheHandler.saveOrUpdate(BaseContentEO.class, eo);
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.print("取消标新时间");
        }
    }
}
