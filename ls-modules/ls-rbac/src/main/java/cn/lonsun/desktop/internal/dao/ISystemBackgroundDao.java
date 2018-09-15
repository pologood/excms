/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.lonsun.desktop.internal.dao;

import java.util.List;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.desktop.internal.entity.SystemBackgroundEO;
import cn.lonsun.desktop.vo.SystemBackgroundQueryVO;

/**
 * 桌面应用接口
 *
 * @author Dzl
 */
public interface ISystemBackgroundDao extends IMockDao<SystemBackgroundEO> {
    
    public void delPyEO(SystemBackgroundEO eo);
    public void delPyEO(Long id);
    public List<SystemBackgroundEO> getList(SystemBackgroundQueryVO queryVo);
    
}
