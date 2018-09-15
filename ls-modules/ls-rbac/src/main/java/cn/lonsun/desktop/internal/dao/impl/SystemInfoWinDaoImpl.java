/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.lonsun.desktop.internal.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.desktop.internal.dao.ISystemInfoWinDao;
import cn.lonsun.desktop.internal.entity.SystemInfoWinEO;
import cn.lonsun.desktop.vo.SystemInfoWinQueryVO;

/**
 * Dao实现类
 *
 * @version 1.0
 * @author Dzl
 */
@Repository
public class SystemInfoWinDaoImpl extends MockDao<SystemInfoWinEO> implements ISystemInfoWinDao {

    @Override
    public void delPyEO(SystemInfoWinEO eo) {
        this.getCurrentSession().delete(eo);
    }
    @Override
    public void batchDelPyEo(List<String> ids){
        String hql = "delete SystemInfoWinEO a where id in :ids";
        Query query = this.getCurrentSession().createQuery(hql);
        query.setParameter("ids", ids);
        query.executeUpdate();
        
    }

    @Override
    public List<SystemInfoWinEO> getList(SystemInfoWinQueryVO queryVo) {
        
        String hql = "from SystemInfoWinEO a order by a.id desc";
        List<SystemInfoWinEO> list= this.getEntitiesByHql(hql);
        return list;
    }
    @Override
    public List<SystemInfoWinEO> getListByIds(List<Long> ids){
        Map<String, Object> map = new HashMap<String, Object>();
         String hql = "from SystemInfoWinEO a where id in :ids order by a.id desc";
         map.put("ids", ids);
        List<SystemInfoWinEO> list= this.getListByJqpl(hql, map);
        return list;
    }
    
}
