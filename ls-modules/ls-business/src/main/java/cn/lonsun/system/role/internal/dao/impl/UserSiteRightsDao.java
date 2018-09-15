package cn.lonsun.system.role.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.system.role.internal.dao.IUserSiteRightsDao;
import cn.lonsun.system.role.internal.entity.RbacUserSiteRightsEO;
import cn.lonsun.system.role.internal.entity.vo.IdsVO;
import cn.lonsun.system.role.internal.entity.vo.SiteRightsVO;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gu.fei
 * @version 2015-10-30 10:47
 */
@Repository
public class UserSiteRightsDao extends BaseDao<RbacUserSiteRightsEO> implements IUserSiteRightsDao {

    @Override
    public void deleteByUserId(Long userId) {
        this.executeUpdateByHql("delete from RbacUserSiteRightsEO where userId=? and siteId=?",new Object[]{userId, LoginPersonUtil.getSiteId()});
    }

    @Override
    public List<Long> getInicatorIdList(Long userId, Long siteId) {
        List<Object> values = new ArrayList<Object>();
        StringBuffer hql = new StringBuffer("select indicatorId from RbacUserSiteRightsEO where userId=?");
        values.add(userId);
        if(null != siteId) {
            hql.append(" and siteId=?");
            values.add(siteId);
        }
        List list = getObjects(hql.toString(),values.toArray());
        List<Long> ids = new ArrayList<Long>();
        if(null != list && !list.isEmpty()) {
            for(Object o : list) {
                ids.add(Long.valueOf(o.toString()));
            }
        }
        return ids;
    }

    @Override
    public List<SiteRightsVO> getSiteRights(Long userId, String type, Long siteId) {
        List<Object> value = new ArrayList<Object>();
        StringBuffer sb = new StringBuffer("SELECT INDICATOR_ID as indicatorId,OPT_CODE as optCode FROM RBAC_USER_SITE_RIGHTS WHERE USER_ID = ?");
        value.add(userId);
        sb.append(" AND TYPE = ?");
        value.add(type);
        if(null != siteId) {
            sb.append(" AND SITE_ID=?");
            value.add(siteId);
        }
        sb.append(" GROUP BY INDICATOR_ID,OPT_CODE");

        return (List<SiteRightsVO>) this.getBeansBySql(sb.toString(),value.toArray(),SiteRightsVO.class);
    }

    @Override
    public List<IdsVO> getSites(Long userId, String type) {
        List<Object> value = new ArrayList<Object>();
        StringBuffer sb = new StringBuffer("SELECT INDICATOR_ID as indicatorId FROM RBAC_USER_SITE_RIGHTS WHERE USER_ID = ?");
        value.add(userId);
        sb.append(" AND TYPE = ?");
        value.add(type);
        sb.append(" GROUP BY INDICATOR_ID");
        return (List<IdsVO>) this.getBeansBySql(sb.toString(), value.toArray(), IdsVO.class);
    }
}
