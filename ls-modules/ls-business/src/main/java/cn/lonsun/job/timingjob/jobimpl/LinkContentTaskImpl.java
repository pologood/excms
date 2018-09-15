package cn.lonsun.job.timingjob.jobimpl;

import cn.lonsun.activemq.MessageSender;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.job.service.ISchedulerService;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.message.internal.entity.MessageStaticEO;

import java.util.*;

/**
 * 定时生成连接管理，用来控制广告位的显示时间范围，不在范围内容定时关闭
 */
public class LinkContentTaskImpl extends ISchedulerService {

    private static IBaseContentService baseContentService = SpringContextHolder.getBean("baseContentService");

    @Override
    public void execute(String json) {
        // 查询出开启了定时关闭的连接管理文章列表
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("isJob", 1);// 开启定时关闭
        paramMap.put("isPublish", 1); // 已发布的文章
        paramMap.put("typeCode", BaseContentEO.TypeCode.linksMgr.toString());
        paramMap.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());

        Date now = new Date();
        List<BaseContentEO> contentList = baseContentService.getEntities(BaseContentEO.class, paramMap);
        if (null != contentList && !contentList.isEmpty()) {// 循环
            List<String> existList = new ArrayList<String>();
            for (BaseContentEO baseContentEO : contentList) {
                String existString = baseContentEO.getSiteId() + "_" + baseContentEO.getColumnId();
                if (!existList.contains(existString)) {
                    existList.add(existString);// 放入已存在列表中
                    MessageStaticEO staticEO = new MessageStaticEO();
                    staticEO.setSiteId(baseContentEO.getSiteId());
                    staticEO.setColumnId(baseContentEO.getColumnId());
                    staticEO.setTodb(true).setSource(MessageEnum.CONTENTINFO.value());
                    staticEO.setScope(MessageEnum.COLUMN.value()).setType(MessageEnum.PUBLISH.value()).setUserId(1L);
                    MessageSender.sendMessage(staticEO); // 当做任务入库
                }
            }
        }
    }
}