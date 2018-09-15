package cn.lonsun.system.role.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.system.role.internal.dao.IUserInfoOpenRightsDao;
import cn.lonsun.system.role.internal.entity.RbacInfoOpenRightsEO;
import cn.lonsun.system.role.internal.entity.RbacUserInfoOpenRightsEO;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gu.fei
 * @version 2015-10-30 10:47
 */
@Repository
public class UserInfoOpenRightsDao extends MockDao<RbacUserInfoOpenRightsEO> implements IUserInfoOpenRightsDao {

    @Override
    public void delete(Long userId, String organIds) {
        this.executeUpdateBySql("DELETE FROM RBAC_USER_INFO_OPEN_RIGHTS WHERE USER_ID = ? AND SITE_ID = ? AND ORGAN_ID in (" + organIds + ")" ,new Object[]{userId, LoginPersonUtil.getSiteId()});
    }

    @Override
    public List<RbacInfoOpenRightsEO> getInfoOpenRights(Long userId, Long siteId) {
        List<Object> value = new ArrayList<Object>();
        StringBuffer sb = new StringBuffer("SELECT ORGAN_ID as organId,CODE as code,OPT_CODE as optCode FROM RBAC_USER_INFO_OPEN_RIGHTS WHERE USER_ID = ?");
        value.add(userId);
        if(null != siteId) {
            sb.append(" AND SITE_ID=?");
            value.add(siteId);
        }
        String[] queryFields = {"organId","code","optCode"};
        return (List<RbacInfoOpenRightsEO>) this.getBeansBySql(sb.toString(),value.toArray(),RbacInfoOpenRightsEO.class,queryFields);
    }
}
