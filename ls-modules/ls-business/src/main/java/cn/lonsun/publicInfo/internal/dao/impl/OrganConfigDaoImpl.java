package cn.lonsun.publicInfo.internal.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.publicInfo.internal.dao.IOrganConfigDao;
import cn.lonsun.publicInfo.internal.dao.IPublicClassDao;
import cn.lonsun.publicInfo.internal.entity.OrganConfigEO;
import cn.lonsun.publicInfo.internal.entity.PublicClassEO;
import cn.lonsun.publicInfo.internal.entity.PublicContentEO;
import cn.lonsun.publicInfo.vo.PublicClassMobileVO;
import cn.lonsun.publicInfo.vo.PublicContentRetrievalVO;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zx on 2016-6-27.
 */
@Repository
public class OrganConfigDaoImpl extends BaseDao<OrganConfigEO> implements IOrganConfigDao {


    @Override
    public List<Long> getOrganIdsByCatId(Long catId) {
        StringBuffer hql = new StringBuffer("select o.organId from OrganConfigEO o where 1=1 ");
        List<Object> paramList = new ArrayList<Object>();

        if(!AppUtil.isEmpty(catId)){
            hql.append(" and o.catId = ? ");
            paramList.add(catId);
        }

        return (List<Long>)super.getObjects(hql.toString(),paramList.toArray());
    }
}