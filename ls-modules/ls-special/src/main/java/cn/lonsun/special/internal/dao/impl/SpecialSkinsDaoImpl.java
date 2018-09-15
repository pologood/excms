package cn.lonsun.special.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.special.internal.dao.ISpecialSkinsDao;
import cn.lonsun.special.internal.entity.SpecialSkinsEO;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by doocal on 2016-10-15.
 */
@Repository
public class SpecialSkinsDaoImpl extends MockDao<SpecialSkinsEO> implements ISpecialSkinsDao {

    @Override
    public List<SpecialSkinsEO> getThemeSkinList(SpecialSkinsEO eo) {
        StringBuilder hql = new StringBuilder("from SpecialSkinsEO t where 1=1 ");
        List<Object> values = new ArrayList<Object>();
        if (null != eo.getSiteId()) {
            hql.append(" and t.siteId = ? ");
            values.add(eo.getSiteId());
        }
        if (null != eo.getThemeId()) {
            hql.append(" and t.themeId = ? ");
            values.add(eo.getThemeId());
        }
        if (null != eo.getDefaults()) {
            hql.append(" and t.defaults = ? ");
            values.add(eo.getDefaults());
        }
        if (null != eo.getName()) {
            hql.append(" and t.name like ? ");
            values.add("%" + eo.getName() + "%");
        }
        hql.append(" and t.recordStatus = ?");
        hql.append(" order by t.createDate desc ");
        values.add(AMockEntity.RecordStatus.Normal.toString());
        return getEntitiesByHql(hql.toString(), values.toArray());
    }

    @Override
    public void deleteByThemeId(Long id) {
        executeUpdateByHql("delete from SpecialSkinsEO WHERE themeId = ?", new Object[]{id});
    }
}
