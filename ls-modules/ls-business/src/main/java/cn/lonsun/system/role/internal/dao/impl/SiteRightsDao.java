package cn.lonsun.system.role.internal.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.system.role.internal.dao.ISiteRightsDao;
import cn.lonsun.system.role.internal.entity.RbacSiteRightsEO;
import cn.lonsun.system.role.internal.entity.vo.IdsVO;
import cn.lonsun.system.role.internal.entity.vo.SiteRightsVO;

/**
 * @author gu.fei
 * @version 2015-10-30 10:47
 */
@Repository
public class SiteRightsDao extends BaseDao<RbacSiteRightsEO> implements ISiteRightsDao {

    @Override
    public List<SiteRightsVO> getEOsByRoleIds(Long[] roleIds) {
        if(null  == roleIds || roleIds.length <= 0) {
            return new ArrayList<SiteRightsVO>();
        }
        StringBuffer sb = new StringBuffer("SELECT INDICATOR_ID as indicatorId,OPT_CODE as optCode FROM RBAC_SITE_RIGHTS WHERE ROLE_ID IN (");
        int count = 0;
        for(Long roleId : roleIds) {
            if(count == 0) {
                sb.append(roleId);
            } else {
                sb.append("," + roleId);
            }
            count++;
        }
        sb.append(")");
        sb.append(" GROUP BY INDICATOR_ID,OPT_CODE");

        return (List<SiteRightsVO>) this.getBeansBySql(sb.toString(),new Object[]{},SiteRightsVO.class);
    }

    @Override
    public List<SiteRightsVO> getEOsByRoleIds(Long[] roleIds, String type) {
        if(null  == roleIds || roleIds.length <= 0) {
            return new ArrayList<SiteRightsVO>();
        }
        StringBuffer sb = new StringBuffer("SELECT INDICATOR_ID as indicatorId,OPT_CODE as optCode FROM RBAC_SITE_RIGHTS WHERE ROLE_ID IN (");
        int count = 0;
        for(Long roleId : roleIds) {
            if(count == 0) {
                sb.append(roleId);
            } else {
                sb.append("," + roleId);
            }
            count++;
        }
        sb.append(") AND TYPE = '" + type + "' ");
        sb.append(" GROUP BY INDICATOR_ID,OPT_CODE");

        return (List<SiteRightsVO>) this.getBeansBySql(sb.toString(),new Object[]{},SiteRightsVO.class);
    }

    @Override
    public List<IdsVO> getSiteIdsByRoleIds(Long[] roleIds, String type) {
        if(null  == roleIds || roleIds.length <= 0) {
            return new ArrayList<IdsVO>();
        }
        StringBuffer sb = new StringBuffer("SELECT INDICATOR_ID as indicatorId FROM RBAC_SITE_RIGHTS WHERE ROLE_ID IN (");
        int count = 0;
        for(Long roleId : roleIds) {
            if(count == 0) {
                sb.append(roleId);
            } else {
                sb.append("," + roleId);
            }
            count++;
        }
        sb.append(") AND TYPE = '" + type + "' ");
        sb.append(" GROUP BY INDICATOR_ID");

        return (List<IdsVO>) this.getBeansBySql(sb.toString(), new Object[]{}, IdsVO.class);
    }

    @Override
    public void delByRoleId(Long roleId) {
        this.executeUpdateBySql("DELETE FROM RBAC_SITE_RIGHTS WHERE ROLE_ID = ?" ,new Object[]{roleId});
    }
}
