package cn.lonsun.content.internal.dao.impl;

import cn.lonsun.content.internal.dao.IBaseContentSpecialDao;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2017/9/26.
 */
@Repository
public class BaseContentSpecialDaoImpl extends MockDao<BaseContentEO> implements IBaseContentSpecialDao  {

    @Override
    public Long getCountByColumnId(Long columnId) {
        Long siteId = LoginPersonUtil.getSiteId();
        String hql = "from BaseContentEO where recordStatus='Normal' and columnId=? and siteId=?";
        return getCount(hql, new Object[]{columnId, siteId});
    }
}
