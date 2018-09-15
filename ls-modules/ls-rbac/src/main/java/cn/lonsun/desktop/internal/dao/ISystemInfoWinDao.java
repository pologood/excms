/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.lonsun.desktop.internal.dao;

import java.util.List;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.desktop.internal.entity.SystemInfoWinEO;
import cn.lonsun.desktop.vo.SystemInfoWinQueryVO;

/**
 * 桌面应用接口
 *
 * @author Dzl
 */
public interface ISystemInfoWinDao extends IMockDao<SystemInfoWinEO> {
    
    public void delPyEO(SystemInfoWinEO eo);
    public void batchDelPyEo(List<String> ids);
    public List<SystemInfoWinEO> getList(SystemInfoWinQueryVO queryVo);
    public List<SystemInfoWinEO> getListByIds(List<Long> ids);
}
