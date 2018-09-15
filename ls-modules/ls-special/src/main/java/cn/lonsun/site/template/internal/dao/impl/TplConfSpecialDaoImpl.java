package cn.lonsun.site.template.internal.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.site.template.internal.dao.ITplConfSpecialDao;
import cn.lonsun.site.template.internal.entity.TemplateConfEO;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhushouyong on 2017-9-26.
 */
@Repository
public class TplConfSpecialDaoImpl extends BaseDao<TemplateConfEO> implements ITplConfSpecialDao {
    private final static String DEL_SQL = "DELETE FROM CMS_TEMPLATE_CONF WHERE ID = ?";

    public List<TemplateConfEO> getSpecialById(Long siteId, Long specialId, String tempType){

        List<Object> values = new ArrayList<Object>();
        StringBuilder hql = new StringBuilder("from TemplateConfEO where 1=1");

        if (!AppUtil.isEmpty(siteId)) {
            hql.append(" and siteId=?");
            values.add(siteId);
        }

        if (!AppUtil.isEmpty(specialId)) {
            hql.append(" and specialId=?");
            values.add(specialId);
        }

        if (!AppUtil.isEmpty(tempType)) {
            hql.append(" and tempType=?");
            values.add(tempType);
        }

        return this.getEntitiesByHql(hql.toString(), values.toArray());
    }

    @Override
    public Object delEO(Long id) {
        return this.executeUpdateBySql(DEL_SQL, new Object[]{id});
    }


}
