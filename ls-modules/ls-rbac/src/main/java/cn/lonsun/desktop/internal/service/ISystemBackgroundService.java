/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.lonsun.desktop.internal.service;

import java.util.List;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.desktop.internal.entity.SystemBackgroundEO;
import cn.lonsun.desktop.vo.SystemBackgroundQueryVO;

/**
 * 服务接口
 *
 * @author Dzl
 */
public interface ISystemBackgroundService extends IMockService<SystemBackgroundEO> {
    
    /**
     * 新增
     * @param eo
     * @return 
     */
    public SystemBackgroundEO saveEO(SystemBackgroundEO eo);
    /**
     * 修改
     * @param eo
     * @return 
     */
    public SystemBackgroundEO updateEO(SystemBackgroundEO eo);
    
    /**
     * 物理删除对象
     * @param eo 
     */
    public void delPyEO(SystemBackgroundEO eo);
    
    /**
     * 批量逻辑删除对象(recordStatus='Removed')
     * @param ids 
     */
    public void batchDelEO(String ids);
    /**
     * 批量物理删除对象
     * @param ids 
     */
    public void batchDelPyEO(String ids);
    /**
     * 上传背景图片
     * @return 
     */
    public SystemBackgroundEO uploadImg();
   
    /**
     * 返回分页列表
     * @param queryVo
     * @return 
     */
    public List<SystemBackgroundEO> getList(SystemBackgroundQueryVO queryVo);
    
}
