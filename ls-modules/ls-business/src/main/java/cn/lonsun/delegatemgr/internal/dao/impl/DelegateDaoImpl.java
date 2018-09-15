package cn.lonsun.delegatemgr.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.delegatemgr.entity.DelegateEO;
import cn.lonsun.delegatemgr.internal.dao.IDelegateDao;
import cn.lonsun.delegatemgr.vo.DelegateQueryVO;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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
@Repository("delegateDao")
public class DelegateDaoImpl extends MockDao<DelegateEO> implements IDelegateDao {
    @Override
    public Pagination getPage(DelegateQueryVO queryVO) {
        StringBuffer sb=new StringBuffer(" from DelegateEO d where d.recordStatus=:recordStatus and d.siteId=:siteId");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("recordStatus",AMockEntity.RecordStatus.Normal.toString());
        map.put("siteId",queryVO.getSiteId());
        if(!StringUtils.isEmpty(queryVO.getSession())){
            sb.append(" and d.session=:session");
            map.put("session",queryVO.getSession());
        }
        if(!StringUtils.isEmpty(queryVO.getDeleNum())){
            sb.append(" and d.deleNum like :deleNum escape'\\'");
            map.put("deleNum", "%".concat(queryVO.getDeleNum()).concat("%"));
        }
        if(!StringUtils.isEmpty(queryVO.getName())){
            sb.append(" and d.name like :name escape'\\'");
            map.put("name", "%".concat(queryVO.getName()).concat("%"));
        }
        if(!StringUtils.isEmpty(queryVO.getDelegation())){
            sb.append(" and d.delegation=:delegation");
            map.put("delegation",queryVO.getDelegation());
        }

        sb.append(" order by d.delegation asc,nlssort(d.name,'NLS_SORT=SCHINESE_PINYIN_M')");
        return getPagination(queryVO.getPageIndex(), queryVO.getPageSize(), sb.toString(), map);
    }

    @Override
    public List<DelegateEO> orderByDelegation(DelegateQueryVO queryVO) {
        StringBuffer sb=new StringBuffer("  from DelegateEO d where d.recordStatus=:recordStatus and d.siteId=:siteId");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("recordStatus",AMockEntity.RecordStatus.Normal.toString());
        map.put("siteId",queryVO.getSiteId());
        if(!StringUtils.isEmpty(queryVO.getSession())){
            sb.append(" and d.session=:session");
            map.put("session",queryVO.getSession());
        }
        if(!StringUtils.isEmpty(queryVO.getDelegation())){
            sb.append(" and d.delegation=:delegation");
            map.put("delegation",queryVO.getDelegation());
        }
        if(!StringUtils.isEmpty(queryVO.getDeleGroup())){
            sb.append(" and d.deleGroup=:deleGroup");
            map.put("deleGroup",queryVO.getDeleGroup());
        }
        if(!StringUtils.isEmpty(queryVO.getName())){
            sb.append(" and d.name like :name escape'\\'");
            map.put("name", "%".concat(queryVO.getName()).concat("%"));
        }
        sb.append(" order by d.delegation asc,nlssort(d.name,'NLS_SORT=SCHINESE_PINYIN_M')");
        List<DelegateEO> list=(List<DelegateEO>) getEntitiesByHql(sb.toString(),map);
        return list;
    }


}
