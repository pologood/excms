/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.lonsun.desktop.internal.service;

/**
 *
 * @author Dzl
 */

import java.util.List;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.desktop.internal.entity.SystemInfoWinEO;
import cn.lonsun.desktop.vo.SystemInfoWinQueryVO;

/**
 * 服务接口
 *
 * @author Dzl
 */
public interface ISystemInfoWinService extends IMockService<SystemInfoWinEO> {
    
    public SystemInfoWinEO saveEO(SystemInfoWinEO eo);
    public SystemInfoWinEO updateEO(SystemInfoWinEO eo);
    public SystemInfoWinEO mergeEO(SystemInfoWinEO eo);
    public void delPyEO(SystemInfoWinEO eo);
    public void batchDelPyEO(String ids);
    public List<SystemInfoWinEO> getList(SystemInfoWinQueryVO queryVo);
    public List<SystemInfoWinEO> getListByIds(List<Long> ids);
}
