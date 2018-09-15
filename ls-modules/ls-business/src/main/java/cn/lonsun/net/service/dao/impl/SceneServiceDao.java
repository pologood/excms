package cn.lonsun.net.service.dao.impl;

import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.net.service.dao.ISceneServiceDao;
import cn.lonsun.net.service.entity.CmsSceneServiceEO;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.site.template.util.SqlHelper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author gu.fei
 * @version 2015-11-18 13:46
 */
@Repository
public class SceneServiceDao extends BaseDao<CmsSceneServiceEO> implements ISceneServiceDao {

    @Override
    public List<CmsSceneServiceEO> getEOs() {
        return this.getEntitiesByHql("from CmsSceneServiceEO", new Object[]{});
    }

    @Override
    public Pagination getPageEOs(ParamDto dto) {
        Long pageIndex = dto.getPageIndex();
        Integer pageSize = dto.getPageSize();

        StringBuffer hql = new StringBuffer(" from CmsSceneServiceEO where 1=1");

        if(dto.getColumnId() != null) {
            hql.append(" and columnId = ");
            hql.append(dto.getColumnId());
        }

        return this.getPagination(pageIndex, pageSize, SqlHelper.getSearchAndOrderSql(hql.toString(), dto), new Object[]{});
    }

    @Override
    public Object publish(Long[] ids,Long publish) {

        String idsStr = String.valueOf(-1);
        for(Long id : ids) {
            if("-1".equals(idsStr)) {
                idsStr = String.valueOf(id);
            } else {
                idsStr += String.valueOf(id);
            }
        }

        String hql = "update CmsSceneServiceEO set publish = ? where id in(" + idsStr + ")";
        return this.executeUpdateByHql(hql,new Object[]{publish});
    }

}
