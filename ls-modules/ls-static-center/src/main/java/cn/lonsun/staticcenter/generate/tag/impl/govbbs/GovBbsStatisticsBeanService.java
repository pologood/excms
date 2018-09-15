package cn.lonsun.staticcenter.generate.tag.impl.govbbs;

import cn.lonsun.govbbs.internal.dao.IBbsPostDao;
import cn.lonsun.govbbs.internal.vo.BbsStatisticsVO;
import cn.lonsun.govbbs.util.BbsStaticCenterUtil;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.system.member.internal.dao.IMemberDao;
import cn.lonsun.system.member.internal.entity.MemberEO;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by zhangchao on 2016/12/26.
 */

/**
 * 在线统计
 */
@Component
public class GovBbsStatisticsBeanService extends AbstractBeanService {

    @Autowired
    private IBbsPostDao bbsPostDao;

    @Autowired
    private IMemberDao memberDao;

    @Override
    public Object getObject(JSONObject paramObj) throws GenerateException {
        Context context = ContextHolder.getContext();

        Long siteId = paramObj.getLong("siteId");
        if (!(siteId != null && siteId != 0)) {
            siteId = context.getSiteId();
        }
        BbsStatisticsVO vo =  getStatistics(siteId);
        if(vo == null){
            vo = new BbsStatisticsVO();
        }
        vo.setTodaySum(vo.getTodaySum() == null?0:vo.getTodaySum());
        vo.setYesSum(vo.getYesSum() == null?0:vo.getYesSum());
        Object object = getMembers(siteId);
        if(object != null){
            Object[] objs = (Object[])object;
            Long msum = objs[0] == null ? 0L : Long.valueOf(objs[0].toString());
            Long mid = objs[1] == null ? null : Long.valueOf(objs[1].toString());
            vo.setMemberSum(msum);
            if(mid != null){
                //最新会员
                MemberEO m = memberDao.getEntity(MemberEO.class,mid);
                if(null != m){
                    vo.setNewMemberName(m.getName());
                    vo.setNewMemberId(m.getId());
                }
            }
        }
        return vo;
    }

    private Object getMembers(Long siteId) {
        String hql = "select count(*),max(m.id) from MemberEO m where m.recordStatus = 'Normal' and m.siteId = ? and m.status = ? order by m.createDate desc";
        return (Object)memberDao.getObject(hql,new Object[]{siteId, MemberEO.Status.Enabled.getStatus()});
    }

    private BbsStatisticsVO getStatistics(Long siteId) {
        String hql = "select " +
                "sum(CASE WHEN to_char(b.createDate,'yyyy-mm-dd') = ?  THEN 1 else 0 END) as todaySum," +
                "sum(CASE WHEN to_char(b.createDate,'yyyy-mm-dd') = ? THEN 1 else 0 END) as yesSum," +
                "count(*) as allSum" +
                " from BbsPostEO b where b.recordStatus = 'Normal' and b.isPublish = 1 and b.siteId = ?";

        return (BbsStatisticsVO)bbsPostDao.getBean(hql,new Object[]{BbsStaticCenterUtil.getTimeStr(0), BbsStaticCenterUtil.getTimeStr(1),siteId},BbsStatisticsVO.class);
    }
}
