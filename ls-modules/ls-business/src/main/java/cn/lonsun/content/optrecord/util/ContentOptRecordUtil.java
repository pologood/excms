package cn.lonsun.content.optrecord.util;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.optrecord.entity.ContentOptRecordEO;
import cn.lonsun.content.optrecord.service.IContentOptRecordService;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.util.ColumnUtil;
import cn.lonsun.util.ConcurrentUtil;
import cn.lonsun.util.LoginPersonUtil;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;

import java.util.Date;

/**
 * @author gu.fei
 * @version 2018-01-18 13:54
 */
public class ContentOptRecordUtil {

    private static Logger logger = LoggerFactory.getLogger(ContentOptRecordUtil.class);

    private static IContentOptRecordService contentOptRecordService = SpringContextHolder.getBean(IContentOptRecordService.class);

    private static TaskExecutor taskExecutor = SpringContextHolder.getBean(TaskExecutor.class);

    /**
     * 保存
     * @param ids
     * @param status
     * @param type
     */
    public static void saveOptRecord(final Long[] ids, final Integer status, final ContentOptRecordEO.Type type) {
        if(null != ids && ids.length > 0) {
            final Long userId = LoginPersonUtil.getUserId();
            final String userName = LoginPersonUtil.getUserName();
            final Long organId = LoginPersonUtil.getOrganId();
            final String organName = LoginPersonUtil.getOrganName();
            taskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    // 绑定session至当前线程中
                    SessionFactory sessionFactory = SpringContextHolder.getBean(SessionFactory.class);
                    boolean participate = ConcurrentUtil.bindHibernateSessionToThread(sessionFactory);
                    for(Long id : ids) {
                        ContentOptRecordEO recordEO = new ContentOptRecordEO();
                        recordEO.setContentId(id);
                        recordEO.setStatus(status);
                        recordEO.setOptType(type.toString());
                        recordEO.setOptUserId(userId);
                        recordEO.setOptUserName(userName);
                        recordEO.setOptOrganId(organId);
                        recordEO.setOptOrganName(organName);
                        recordEO.setCreateDate(new Date());
                        try {
                            BaseContentEO content = CacheHandler.getEntity(BaseContentEO.class,id);
                            recordEO.setColumnId(content.getColumnId());
                            recordEO.setTitle(content.getTitle());
                            Long siteId = content.getSiteId();
                            if(null == siteId) {
                                IndicatorEO indicator = CacheHandler.getEntity(IndicatorEO.class,content.getColumnId());
                                siteId = indicator.getSiteId();
                            }
                            String columnName = ColumnUtil.getColumnName(content.getColumnId(),siteId);
                            recordEO.setColumnName(columnName);
                            contentOptRecordService.saveEntity(recordEO);
                        } catch (Exception e) {
                            e.printStackTrace();
                            logger.error("保存[{}:{}]操作记录失败",recordEO.getContentId(),recordEO.getTitle());
                        }
                    }
                    ConcurrentUtil.closeHibernateSessionFromThread(participate, sessionFactory);
                }
            });
        }
    }
}
