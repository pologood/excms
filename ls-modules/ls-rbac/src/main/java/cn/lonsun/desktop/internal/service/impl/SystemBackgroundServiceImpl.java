/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.lonsun.desktop.internal.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.desktop.internal.dao.ISystemBackgroundDao;
import cn.lonsun.desktop.internal.entity.SystemBackgroundEO;
import cn.lonsun.desktop.internal.service.ISystemBackgroundService;
import cn.lonsun.desktop.vo.SystemBackgroundQueryVO;

/**
 * 服务接口实现类
 *
 * @author Dzl
 */
@Service
public class SystemBackgroundServiceImpl extends MockService<SystemBackgroundEO> implements
        ISystemBackgroundService {
        private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired 
    private ISystemBackgroundDao systemBackgroundDao;
    
    private final String moduleName = "[桌面背景]:";
    
    @Override
    public SystemBackgroundEO saveEO(SystemBackgroundEO eo) {
        this.saveEntity(eo);        
        
        // 写入日志
        String logStr = moduleName + "新增桌面背景(编号:"+eo.getId()+",名称:"+eo.getName()+")";
        return eo;
        
    }
    @Override
    public SystemBackgroundEO updateEO(SystemBackgroundEO eo) {
        this.getMockDao().update(eo);        
        
        // 写入日志
        String logStr = moduleName + "修改桌面背景(编号:"+eo.getId()+",名称:"+eo.getName()+")";
        return eo;
        
    }

  
   

    @Override
    public void delPyEO(SystemBackgroundEO eo) {
        systemBackgroundDao.delPyEO(eo);
        // 写入日志
        String logStr = moduleName + "删除桌面背景(编号:"+eo.getId()+")";
        
    }

   
    @Override
    public void batchDelEO(String ids) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void batchDelPyEO(String ids) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SystemBackgroundEO uploadImg() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<SystemBackgroundEO> getList(SystemBackgroundQueryVO queryVo) {
       return systemBackgroundDao.getList(queryVo);
        
    }

   
}
