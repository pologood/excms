package cn.lonsun.job.timingjob.jobimpl;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.content.internal.dao.IBaseContentUpdateDao;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.entity.BaseContentUpdateEO;
import cn.lonsun.content.internal.service.IBaseContentUpdateService;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.job.service.ISchedulerService;
import cn.lonsun.rbac.internal.dao.IRoleAssignmentDao;
import cn.lonsun.site.site.internal.entity.ColumnConfigEO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * 新闻栏目更新红黄牌预警提示定时统计
 * <p>
 * Created by liuk on 2017/6/30.
 */
public class BaseContentUpdateTaskImpl extends ISchedulerService {
    private static final String MESSAGE_BODY = "%s栏目离最迟更新时间还剩%s天";
    /* 查询hql */
    private static final String hql = "SELECT new BaseContentUpdateEO(b.columnId as columnId,b.siteId as siteId,max(b.publishDate) as lastPublishDate) "
            + " from BaseContentEO b where b.recordStatus = ? and b.typeCode != ? and b.isPublish = ? and b.publishDate is not null group by b.columnId,b.siteId ";
    private static final String userHql = "SELECT distinct r.userId from RoleAssignmentEO r where r.recordStatus = ? and r.roleId in " +
            " ( SELECT distinct u.roleId from RbacSiteRightsEO u where u.indicatorId = ?  ) ";

    /* 查询条件 */
    private static final Object[] object = {AMockEntity.RecordStatus.Normal.toString(), BaseContentEO.TypeCode.public_content.toString(), 1};
    private static IRoleAssignmentDao roleAssignmentDao = SpringContextHolder.getBean(IRoleAssignmentDao.class);
    private static IBaseContentUpdateDao baseContentUpdateDao = SpringContextHolder.getBean(IBaseContentUpdateDao.class);
    private static IBaseContentUpdateService baseContentUpdateService = SpringContextHolder.getBean(IBaseContentUpdateService.class);

    @Override
    public void execute(String json) {
        // 清空数据表
        baseContentUpdateDao.executeUpdateByHql("delete BaseContentUpdateEO", null);
        // 按照发布时间统计出信息要过期的部门
        List<BaseContentUpdateEO> updateList = (List<BaseContentUpdateEO>) baseContentUpdateDao.getObjects(hql, object, null);
        // 根据单位查询出每个单位的权限对应的用户列表
        if (null != updateList && !updateList.isEmpty()) {
            // 当前日期
            Date now = new Date();
            for (BaseContentUpdateEO vo : updateList) {
                Long columnId = vo.getColumnId();
                Long siteId = vo.getSiteId();
                Date publishDate = vo.getLastPublishDate();
                int updateCycle = 0;//更新周期
                int yellowCardWarning = 0;//黄牌警示天数
                int redCardWarning = 0;//红牌警示天数
                String columnName = "";//栏目名称

                // 查询出该栏目的更新周期
                ColumnMgrEO columnMgrEO = CacheHandler.getEntity(ColumnMgrEO.class, columnId);
                ColumnConfigEO columnConfigEO = CacheHandler.getEntity(ColumnConfigEO.class, CacheGroup.CMS_INDICATORID, columnId);
                if (null == columnConfigEO) {
                    continue;
                }else{
                    columnName = columnMgrEO.getName();
                    updateCycle = columnConfigEO.getUpdateCycle();
                    yellowCardWarning = columnConfigEO.getYellowCardWarning();
                    redCardWarning = columnConfigEO.getRedCardWarning();
                }

                if (updateCycle <= 0 || yellowCardWarning <= 0 || redCardWarning <= 0) {
                    continue;
                }
                // 计算时间差距
                int day = Long.valueOf((now.getTime() - publishDate.getTime()) / (24 * 60 * 60 * 1000)).intValue();
                int diff = updateCycle - day;
                if (diff <= yellowCardWarning) {//黄牌警告
                    List<Long> userIdList = (List<Long>) roleAssignmentDao.getObjects(userHql, new Object[]{AMockEntity.RecordStatus.Normal.toString(),columnId});
                    BaseContentUpdateEO updateEO = new BaseContentUpdateEO(columnId, siteId, publishDate);
                    if (diff <= redCardWarning) {//红牌警告
                        updateEO.setWarningType(BaseContentUpdateEO.WarningType.RED_CARD_WARNING.getType());
                    } else {
                        updateEO.setWarningType(BaseContentUpdateEO.WarningType.YELLOW_CARD_WARNING.getType());
                    }
                    updateEO.setMessage(String.format(MESSAGE_BODY, columnName, diff));
                    updateEO.setRecUserIds(StringUtils.join(userIdList, ","));
                    baseContentUpdateService.saveEntity(updateEO);
                }
            }
        }
    }
}
