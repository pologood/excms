package cn.lonsun.site.site.internal.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.site.site.internal.dao.IColumnConfigRelDao;
import cn.lonsun.site.site.internal.dao.IColumnTypeDao;
import cn.lonsun.site.site.internal.entity.ColumnConfigRelEO;
import cn.lonsun.site.site.internal.entity.ColumnTypeEO;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-4-7<br/>
 */
@Repository("columnTypeDao")
public class ColumnTypeDaoImpl extends MockDao<ColumnTypeEO> implements IColumnTypeDao {
    public Pagination getPage(ContentPageVO pageVO) {
        StringBuffer hql = new StringBuffer("from ColumnTypeEO c where c.recordStatus='Normal'");
        Map<String, Object> map = new HashMap<String, Object>();
        if (!AppUtil.isEmpty(pageVO.getSiteId())) {
            hql.append(" and c.siteId=:siteId");
            map.put("siteId", pageVO.getSiteId());
        }
        if (!AppUtil.isEmpty(pageVO.getTitle())) {
            hql.append(" and c.typeName like '%" + pageVO.getTitle() + "%'");
        }
        hql.append(" order by c.id desc");
        return getPagination(pageVO.getPageIndex(), pageVO.getPageSize(), hql.toString(), map);
    }

    public boolean isHave(Long siteId, Long id, String name) {
        boolean buf = false;
        String hql = "from ColumnTypeEO where recordStatus = 'Normal' and typeName = ? and siteId = ? and id != ?";
        Long buff = getCount(hql, new Object[]{name, siteId, id});
        if (buff != null && buff > 0)
            buf = true;
        return buf;
    }

    public List<ColumnTypeEO> getCtsByIds(Long[] ids) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ids", ids);
        String hql = "from ColumnTypeEO where id in (:ids)";
        return getEntitiesByHql(hql, map);
    }
}
