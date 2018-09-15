package cn.lonsun.mobile.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.mobile.internal.dao.IMobileConfigDao;
import cn.lonsun.mobile.internal.entity.MobileConfigEO;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Doocal
 * @ClassName: mobileColumnDao
 * @Description: 限制IP数据访问层
 */
@Repository("mobileConfigDao")
public class MobileConfigDaoImpl extends MockDao<MobileConfigEO> implements IMobileConfigDao {

    @Override
    public List<MobileConfigEO> getMobileConfigList(Long siteId) {
        List<Object> list = new ArrayList<Object>();
        StringBuffer hql = new StringBuffer("from MobileConfigEO where 1=1");
        hql.append(" and siteId=?");
        list.add(siteId);
        hql.append(" and record_status=?");
        list.add(AMockEntity.RecordStatus.Normal.toString());
        hql.append(" order by id");
        return getEntitiesByHql(hql.toString(), list.toArray());
    }

    @Override
    public Object saveConfig(MobileConfigEO eo) {
        return this.save(eo);
    }

    @Override
    public Object updateConfigChecked(MobileConfigEO eo) {
        Map<String, Object> map = new HashMap<String, Object>();
        String hql = "update MobileConfigEO set isChecked=:isChecked where siteId=:siteId and indicatorId=:indicatorId and type=:type";
        map.put("isChecked", eo.getIsChecked());
        map.put("siteId", eo.getSiteId());
        map.put("indicatorId", eo.getIndicatorId());
        map.put("type", eo.getType());
        return executeUpdateByJpql(hql, map);
    }

    @Override
    public Object deleteConfig(Long id) {
        this.delete(MobileConfigEO.class, new Long[]{id});
        return null;
    }

    @Override
    public Object deleteAllbyType(String type) {
        this.executeUpdateByHql("delete from MobileConfigEO where siteId=? AND type=? ", new Object[]{LoginPersonUtil.getSiteId(), type});
        return null;
    }

    @Override
    public MobileConfigEO getOneConfig(Long id) {
        return this.getEntityByHql("from MobileConfigEO where record_status=? and id = ?", new Object[]{AMockEntity.RecordStatus.Normal.toString(), id});
    }
}