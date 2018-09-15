package cn.lonsun.content.officePublicity.internal.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.officePublicity.internal.dao.IOfficePublicityDao;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.net.service.entity.CmsOfficePublicityEO;
import cn.lonsun.net.service.entity.vo.OfficePublicityQueryVO;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by huangxx on 2017/2/24.
 */
@Repository("officePublicityDao")
public class OfficePublicityDaoImpl extends MockDao<CmsOfficePublicityEO> implements IOfficePublicityDao{

    @Override
    public Pagination getPage(OfficePublicityQueryVO queryVO) {

        StringBuffer hql = new StringBuffer();
        Map<String,Object> map = new HashMap<String,Object>();
        hql.append(" from CmsOfficePublicityEO where recordStatus =:recordStatus");
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());

        if(!AppUtil.isEmpty(queryVO.getAcceptanceItem())) {
            hql.append(" and acceptanceItem like:acceptanceItem");
            map.put("acceptanceItem","%" + queryVO.getAcceptanceItem() + "%");
        }
        if(!AppUtil.isEmpty(queryVO.getOfficeStatus())) {
            hql.append(" and officeStatus=:officeStatus");
            map.put("officeStatus",queryVO.getOfficeStatus());
        }
        hql.append(" order by inputDate DESC");
        return getPagination(queryVO.getPageIndex(),queryVO.getPageSize(),hql.toString(),map);
    }
}
