package cn.lonsun.content.publicInfo.internal.service.impl;

import cn.lonsun.content.publicInfo.internal.service.IPublicInfoStatisticsService;
import cn.lonsun.content.publicInfo.vo.PublicApplyStatisticsVO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.publicInfo.vo.PublicTjVO;
import cn.lonsun.util.ReflectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by lonsun on 2016-9-23.
 */
@Service
public class PublicInfoStatisticsServiceImpl extends BaseService implements IPublicInfoStatisticsService {

    @Override
    public PublicApplyStatisticsVO getStatistics(Long orgId, Long siteId) {
        PublicApplyStatisticsVO vo = new PublicApplyStatisticsVO();
        String hql = "select p.replyStatus as replyStatus,count(p.id) as total from PublicApplyEO p " +
                "where p.recordStatus = ? and p.siteId = ? and p.replyStatus is not null GROUP BY p.replyStatus";
        List<PublicTjVO> resultList = getBaseDao().getBeansByHql(hql, new Object[]{AMockEntity.RecordStatus.Normal.toString(), siteId}, PublicTjVO.class);
        if (null != resultList && !resultList.isEmpty()) {
            long total = 0L;
            try {
                for (PublicTjVO tjVO : resultList) {
                    total += tjVO.getTotal();
                    ReflectionUtils.setFieldValue(vo, tjVO.getReplyStatus(), tjVO.getTotal());
                }
                vo.setInfoTotal(total);
            } catch (Throwable e) {
                e.printStackTrace();
                return null;
            }
        }
        return vo;
    }
}
