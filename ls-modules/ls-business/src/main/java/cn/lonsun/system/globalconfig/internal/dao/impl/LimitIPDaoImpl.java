package cn.lonsun.system.globalconfig.internal.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.system.globalconfig.internal.dao.ILimitIPDao;
import cn.lonsun.system.globalconfig.internal.entity.LimitIPEO;
import cn.lonsun.system.globalconfig.vo.LimitIPPageVO;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Doocal
 * @ClassName: LimitIPDaoImpl
 * @Description: 限制IP数据访问层
 */
@Repository("limitIPDao")
public class LimitIPDaoImpl extends MockDao<LimitIPEO> implements ILimitIPDao {

    @Override
    public Pagination getPage(LimitIPPageVO vo) {
        StringBuffer hql = new StringBuffer("from LimitIPEO where 1=1");
        hql.append(" and record_status=:record_status");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("record_status", AMockEntity.RecordStatus.Normal.toString());
        if (!AppUtil.isEmpty(vo.getIp())) {
            hql.append(" and ip like :ip escape'\\'");
            map.put("ip", "%".concat(vo.getIp()).concat("%"));
        }

        hql.append(" order by updateDate desc");
        return getPagination(vo.getPageIndex(), vo.getPageSize(), hql.toString(), map);
    }

    @Override
    public Object saveLimitIP(LimitIPEO eo) {
        return this.save(eo);
    }

    @Override
    public Object updateLimitIP(LimitIPEO eo) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", eo.getId());
        map.put("ip", eo.getIp());
        map.put("rules", eo.getRules());
        map.put("description", eo.getDescription());
        String hql = "update LimitIPEO set ip=:ip,rules=:rules,description=:description where id=:id";
        return executeUpdateByJpql(hql, map);
    }

    @Override
    public Object deleteLimitIP(Long id) {
        this.delete(LimitIPEO.class, new Long[]{id});
        return null;
    }

    @Override
    public List<LimitIPEO> checkIP(LimitIPEO eo) {
        String hql = "from LimitIPEO";
        List<Object> list = new ArrayList<Object>();
        hql += " where record_status=? and ip=?";
        list.add(AMockEntity.RecordStatus.Normal.toString());
        list.add(eo.getIp());

        if (!AppUtil.isEmpty(eo.getId())) {
            hql += " and id<>?";
            list.add(eo.getId());
        }

        return this.getEntitiesByHql(hql, list.toArray());
    }

    @Override
    public LimitIPEO getOneIP(Long id) {
        return this.getEntityByHql("from LimitIPEO where record_status=? and id = ?", new Object[]{AMockEntity.RecordStatus.Normal.toString(), id});
    }
}