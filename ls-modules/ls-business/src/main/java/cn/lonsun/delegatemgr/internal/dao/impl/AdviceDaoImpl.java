package cn.lonsun.delegatemgr.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.delegatemgr.entity.AdviceEO;
import cn.lonsun.delegatemgr.internal.dao.IAdviceDao;
import cn.lonsun.delegatemgr.vo.DelegateQueryVO;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2017-6-6<br/>
 */
@Repository("adviceDao")
public class AdviceDaoImpl extends MockDao<AdviceEO> implements IAdviceDao{
    @Override
    public Pagination getPage(DelegateQueryVO queryVO) {
        StringBuffer sb=new StringBuffer(" from AdviceEO a where a.recordStatus=:recordStatus and a.siteId=:siteId");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        map.put("siteId",queryVO.getSiteId());
        if(!StringUtils.isEmpty(queryVO.getSession())){
            sb.append(" and a.session=:session");
            map.put("session",queryVO.getSession());
        }
        if(!StringUtils.isEmpty(queryVO.getLeader())){
            sb.append(" and a.leader like :leader escape'\\'");
            map.put("leader", "%".concat(queryVO.getLeader()).concat("%"));
        }
        if(!StringUtils.isEmpty(queryVO.getTitle())){
            sb.append(" and a.title like :title escape'\\'");
            map.put("title", "%".concat(queryVO.getTitle()).concat("%"));
        }
         sb.append(" order by a.session desc,a.num asc ");

        return getPagination(queryVO.getPageIndex(), queryVO.getPageSize(), sb.toString(), map);
    }

    @Override
    public List<AdviceEO> getBySession(String session, Long siteId) {
        StringBuffer sb=new StringBuffer(" from AdviceEO a where a.recordStatus=:recordStatus and a.siteId=:siteId");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        map.put("siteId",siteId);
        if(!StringUtils.isEmpty(session)){
            sb.append(" and a.session=:session");
            map.put("session",session);
        }
        return this.getEntitiesByHql(sb.toString(),map);
    }
}
