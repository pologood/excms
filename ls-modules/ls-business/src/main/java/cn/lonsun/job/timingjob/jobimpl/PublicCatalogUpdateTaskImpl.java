package cn.lonsun.job.timingjob.jobimpl;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.job.service.ISchedulerService;
import cn.lonsun.publicInfo.internal.dao.IPublicCatalogUpdateDao;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogEO;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogOrganRelEO;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogUpdateEO;
import cn.lonsun.publicInfo.internal.entity.PublicContentEO;
import cn.lonsun.publicInfo.internal.service.IPublicCatalogUpdateService;
import cn.lonsun.rbac.internal.dao.IRoleAssignmentDao;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * 信息公开目录更新红黄牌预警提示定时统计
 * <p>
 * Created by fth on 2017/6/7.
 */
public class PublicCatalogUpdateTaskImpl extends ISchedulerService {
    private static final String MESSAGE_BODY = "%s栏目离最迟更新时间还剩%s天";
    /* 查询hql */
    private static final String hql = "SELECT new PublicCatalogUpdateEO(p.organId as organId,p.catId as catId,max(b.publishDate) as lastPublishDate) "
            + " from PublicContentEO p,BaseContentEO b where p.contentId = b.id and "
            + " b.recordStatus = ? and p.recordStatus = ? and p.type = ? and b.isPublish = ? and b.publishDate is not null group by p.organId,p.catId ";
    private static final String userHql = "SELECT distinct r.userId from RoleAssignmentEO r where EXISTS (select 1 from RbacInfoOpenRightsEO p where "
            + " r.roleId = p.roleId and p.organId = ? and p.recordStatus = ?) and r.recordStatus = ?";

    /* 查询条件 */
    private static final Object[] object = {AMockEntity.RecordStatus.Normal.toString(), AMockEntity.RecordStatus.Normal.toString(),
            PublicContentEO.Type.DRIVING_PUBLIC.toString(), 1};
    private static IRoleAssignmentDao roleAssignmentDao = SpringContextHolder.getBean(IRoleAssignmentDao.class);
    private static IPublicCatalogUpdateDao publicCatalogUpdateDao = SpringContextHolder.getBean(IPublicCatalogUpdateDao.class);
    private static IPublicCatalogUpdateService publicCatalogUpdateService = SpringContextHolder.getBean(IPublicCatalogUpdateService.class);

    @Override
    public void execute(String json) {
        // 清空数据表
        publicCatalogUpdateDao.executeUpdateByHql("delete PublicCatalogUpdateEO", null);
        // 按照发布时间统计出信息要过期的部门
        List<PublicCatalogUpdateEO> updateList = (List<PublicCatalogUpdateEO>) publicCatalogUpdateDao.getObjects(hql, object, null);
        // 根据单位查询出每个单位的权限对应的用户列表
        if (null != updateList && !updateList.isEmpty()) {
            // 当前日期
            Date now = new Date();
            for (PublicCatalogUpdateEO vo : updateList) {
                Long organId = vo.getOrganId();
                Long catId = vo.getCatId();
                Date publishDate = vo.getLastPublishDate();
                int updateCycle = 0;//更新周期
                int yellowCardWarning = 0;//黄牌警示天数
                int redCardWarning = 0;//红牌警示天数
                String catalogName = "";//栏目名称

                // 查询出该单位目录的更新周期
                PublicCatalogEO catalogEO = CacheHandler.getEntity(PublicCatalogEO.class, catId);
                if (null == catalogEO) {
                    continue;
                }
                PublicCatalogOrganRelEO relEO = CacheHandler.getEntity(PublicCatalogOrganRelEO.class, CacheGroup.CMS_JOIN_ID, organId, catId);
                if (null == relEO) {
                    catalogName = catalogEO.getName();
                    updateCycle = catalogEO.getUpdateCycle();
                    yellowCardWarning = catalogEO.getYellowCardWarning();
                    redCardWarning = catalogEO.getRedCardWarning();
                } else {
                    catalogName = relEO.getName();
                    updateCycle = relEO.getUpdateCycle();
                    yellowCardWarning = relEO.getYellowCardWarning();
                    redCardWarning = relEO.getRedCardWarning();
                }
                if (updateCycle <= 0 || yellowCardWarning <= 0 || redCardWarning <= 0) {
                    continue;
                }
                // 计算时间差距
                int day = Long.valueOf((now.getTime() - publishDate.getTime()) / (24 * 60 * 60 * 1000)).intValue();
                int diff = updateCycle - day;
                if (diff <= yellowCardWarning) {//黄牌警告
                    List<Long> userIdList = (List<Long>) roleAssignmentDao.getObjects(userHql, new Object[]{organId,
                            AMockEntity.RecordStatus.Normal.toString(), AMockEntity.RecordStatus.Normal.toString()});
                    PublicCatalogUpdateEO updateEO = new PublicCatalogUpdateEO(organId, catId, publishDate);
                    if (diff <= redCardWarning) {//红牌警告
                        updateEO.setWarningType(PublicCatalogUpdateEO.WarningType.RED_CARD_WARNING.getType());
                    } else {
                        updateEO.setWarningType(PublicCatalogUpdateEO.WarningType.YELLOW_CARD_WARNING.getType());
                    }
                    updateEO.setMessage(String.format(MESSAGE_BODY, catalogName, diff));
                    updateEO.setRecUserIds(StringUtils.join(userIdList, ","));
                    publicCatalogUpdateService.saveEntity(updateEO);
                }
            }
        }
    }
}
