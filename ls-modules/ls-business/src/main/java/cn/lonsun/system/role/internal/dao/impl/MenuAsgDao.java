package cn.lonsun.system.role.internal.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.rbac.internal.entity.IndicatorPermissionEO;
import cn.lonsun.system.role.internal.dao.IMenuAsgDao;

/**
 * @author gu.fei
 * @version 2015-10-30 10:47
 */
@Repository
public class MenuAsgDao extends MockDao<IndicatorPermissionEO> implements IMenuAsgDao {

    @Override
    public void deleteByRoleAndIndicator(Long roleId) {
        if(null == roleId) {
            return;
        }
        this.delete(getByRoleId(roleId));
    }

    @Override
    public List<IndicatorPermissionEO> getByRoleId(Long roleId) {
        String hql = "from IndicatorPermissionEO t where  t.roleId=? and t.recordStatus=?";
        return this.getEntitiesByHql(hql, new Object[]{roleId, AMockEntity.RecordStatus.Normal.toString()});
    }

    @Override
    public IndicatorPermissionEO getByRoleAndIndicator(Long roleId, Long right) {
        String hql = "from IndicatorPermissionEO t where  t.roleId=? and t.indicatorId=? and t.recordStatus=?";
        return this.getEntityByHql(hql, new Object[]{roleId,right,AMockEntity.RecordStatus.Normal.toString()});
    }

    @Override
    public void deletePermissions(Long roleId,List<Long> indicatorIds){
        if(roleId==null||roleId<=0){
            return;
        }
        StringBuilder hql = new StringBuilder("update IndicatorPermissionEO ip set ip.recordStatus=? where ip.recordStatus=? ");
        List<Object> values = new ArrayList<Object>();
        values.add(AMockEntity.RecordStatus.Removed.toString());
        values.add(AMockEntity.RecordStatus.Normal.toString());
        if(indicatorIds!=null&&indicatorIds.size()>0){
            hql.append(" and (1=0");
            for(Long indicatorId:indicatorIds){
                hql.append(" or ip.indicatorId=?");
                values.add(indicatorId);
            }
            hql.append(")");
        }
        hql.append(" and ip.roleId=?");
        values.add(roleId);
        executeUpdateByHql(hql.toString(), values.toArray());
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Long> getIndicatorIds(Long roleId) {
        String hql = "select p.indicatorId from IndicatorPermissionEO p where p.roleId=? and p.recordStatus=?";
        List<?> object = getObjects(hql, new Object[]{roleId,AMockEntity.RecordStatus.Normal.toString()});
        return object==null?null:(List<Long>)object;
    }

}
