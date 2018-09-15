/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.lonsun.desktop.internal.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.util.ContextHolderUtils;
import cn.lonsun.desktop.internal.dao.ISystemBackgroundDao;
import cn.lonsun.desktop.internal.entity.SystemBackgroundEO;
import cn.lonsun.desktop.vo.SystemBackgroundQueryVO;

/**
 * Dao实现类
 *
 * @version 1.0
 * @author Dzl
 */
@Repository
public class SystemBackgroundDaoImpl extends MockDao<SystemBackgroundEO> implements ISystemBackgroundDao {
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @Override
    public void delPyEO(SystemBackgroundEO eo) {
       this.getCurrentSession().delete(eo);      
    }

    @Override
    public void delPyEO(Long id) {
        SystemBackgroundEO eo = this.getEntity(SystemBackgroundEO.class, id);
        this.delPyEO(eo);
    }

    @Override
    public List<SystemBackgroundEO> getList(SystemBackgroundQueryVO queryVo) {
        Map<String, Object> map = new HashMap<String, Object>();
        StringBuilder sb = new StringBuilder("from SystemBackgroundEO a where recordStatus='Normal' ");
        Long userId = ContextHolderUtils.getUserId();
        Long organId = ContextHolderUtils.getOrganId();

        if(!AppUtil.isEmpty(queryVo.getIsSysBg()) && queryVo.getIsSysBg()){
            sb.append(" and a.isSysBg=:isSysBg ");
            map.put("isSysBg", queryVo.getIsSysBg());
        }else{
            sb.append(" and a.isSysBg=:isSysBg ");
            map.put("isSysBg", queryVo.getIsSysBg());
            sb.append(" and a.createUserId=:userId ");
            sb.append(" and a.createOrganId=:organId ");
            map.put("userId", userId);
            map.put("organId", organId);
        }
        if(!AppUtil.isEmpty(queryVo.getSortField()) && !AppUtil.isEmpty(queryVo.getSortOrder())){
            sb.append(" order by a.");
            sb.append(queryVo.getSortField());
            sb.append(" ");
            sb.append(queryVo.getSortOrder());
        }else{
            sb.append(" order by a.id desc");
        }

        List<SystemBackgroundEO> list = this.getListByJqpl(sb.toString(), map);
        return list;       
    }
    
}
