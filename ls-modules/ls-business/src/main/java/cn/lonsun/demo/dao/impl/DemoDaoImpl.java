package cn.lonsun.demo.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.demo.dao.IDemoDao;
import cn.lonsun.demo.entity.DemoEO;
import cn.lonsun.demo.vo.DemoQueryVO;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * demo dao<br/>
 *
 * @author wangshibao <br/>
 * @version v1.0 <br/>
 * @date 2018-8-2<br/>
 */
@Repository("demoDao")
public class DemoDaoImpl extends MockDao<DemoEO> implements IDemoDao {
    @Override
    public Pagination getDemoPage(DemoQueryVO queryVO) {
        StringBuffer sb = new StringBuffer(" from DemoEO a where 1 = 1");
        Map<String, Object> params = new HashMap<String, Object>();
        if (!StringUtils.isEmpty(queryVO.getCode())) {
            sb.append(" and a.code=:code");
            params.put("code", queryVO.getCode());
        }
        if (!StringUtils.isEmpty(queryVO.getName())) {
            sb.append(" and a.name like :name escape'\\'");
            params.put("name", "%".concat(queryVO.getName()).concat("%"));
        }
        sb.append(" order by a.name desc,a.id asc ");
        return getPagination(queryVO.getPageIndex(), queryVO.getPageSize(), sb.toString(), params);
    }

    @Override
    public List<DemoEO> getDemoListByCodeAndName(String code, String name) {
        StringBuffer sb = new StringBuffer(" from DemoEO a where 1 = 1");
        Map<String, Object> params = new HashMap<String, Object>();
        if (!StringUtils.isEmpty(code)) {
            sb.append(" and a.code=:code");
            params.put("code", code);
        }
        if (!StringUtils.isEmpty(name)) {
            sb.append(" and a.name like :name escape'\\'");
            params.put("name", "%".concat(name).concat("%"));
        }
        return this.getEntitiesByHql(sb.toString(), params);
    }

    @Override
    public DemoEO getDemoInfoById(Long id) {
        return this.getEntityByHql("from DemoEO where id = ?", new Object[]{id});
    }

    @Override
    public Object saveDemoInfo(DemoEO eo) {
        return this.save(eo);
    }

    @Override
    public void updateDemoInfo(DemoEO eo) {
        Map<String, Object> map = new HashMap<String, Object>();
        String hql = "update DemoEO set code=:code,name=:name where id=:id";
        map.put("id", eo.getId());
        map.put("code", eo.getCode());
        map.put("name", eo.getName());
        executeUpdateByJpql(hql, map);
    }

    @Override
    public void deleteDemoInfo(Long id) {
        this.delete(DemoEO.class, new Long[]{id});
    }

    @Override
    public void update(DemoEO eo) {
        this.update(eo);
    }
}
