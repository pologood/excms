package cn.lonsun.special.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.special.internal.dao.ISpecialDao;
import cn.lonsun.special.internal.entity.SpecialEO;
import cn.lonsun.special.internal.vo.SpecialQueryVO;
import cn.lonsun.special.internal.vo.SpecialVO;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by doocal on 2016-10-15.
 */
@Repository
public class SpecialDaoImpl extends MockDao<SpecialEO> implements ISpecialDao {

    @Override
    public Pagination getPagination(SpecialQueryVO queryVO) {
        StringBuilder hql = new StringBuilder("select t1.id as id,t1.name as name,t1.index_id as tplId, ");
        hql.append("t2.id as themeId, t2.name as themeName,t2.path as themePath,t2.img_path as themeImgPath,t1.column_root_id as columnId, ");
        hql.append(" t1.SPECIAL_STATUS as specialStatus, t1.SPECIAL_TYPE as specialType , t1.create_date as createDate");
        hql.append(" from CMS_SPECIAL t1 left join CMS_SPECIAL_THEME t2 on t1.theme_id = t2.id where (SPECIAL_TYPE=0 or SPECIAL_TYPE is null)");
        List<Object> values = new ArrayList<Object>();
        if (null != queryVO.getSiteId()) {
            hql.append(" and t1.site_id = ? ");
            values.add(queryVO.getSiteId());
        }
        if (null != queryVO.getName()) {
            hql.append(" and t1.name like ? ");
            values.add("%" + queryVO.getName() + "%");
        }
        hql.append(" and t1.record_status = ?");
        values.add(AMockEntity.RecordStatus.Normal.toString());
        hql.append(" order by t1.create_date desc ");
        String[] queryFields = new String[]{"id", "name", "tplId", "themeId", "themeName", "themePath", "columnId", "themeImgPath", "specialStatus", "specialType", "createDate"};
        return getPaginationBySql(queryVO.getPageIndex(), queryVO.getPageSize(), hql.toString(), values.toArray(), SpecialVO.class, queryFields);
    }

    @Override
    public Object changeSpecial(Long id, Long specialStatus) {
        return this.executeUpdateBySql("UPDATE CMS_SPECIAL SET SPECIAL_STATUS=? WHERE ID = ?", new Object[]{specialStatus, id});
    }

    @Override
    public SpecialEO getThemeById(Long id) {
        StringBuilder hql = new StringBuilder("from SpecialEO");
        List<Object> list = new ArrayList<Object>();
        hql.append(" where recordStatus=? ");
        list.add(AMockEntity.RecordStatus.Normal.toString());
        hql.append(" AND themeId=?");
        list.add(id);
        return this.getEntityByHql(hql.toString(), list.toArray());
    }

    @Override
    public SpecialEO getById(Long id) {
        StringBuilder hql = new StringBuilder("from SpecialEO");
        List<Object> list = new ArrayList<Object>();
        hql.append(" where recordStatus=? ");
        list.add(AMockEntity.RecordStatus.Normal.toString());
        hql.append(" AND id=?");
        list.add(id);
        return this.getEntityByHql(hql.toString(), list.toArray());
    }
}
