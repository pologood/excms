/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.lonsun.desktop.internal.service.impl;

/**
 *
 * @author Dzl
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.StrUtil;
import cn.lonsun.desktop.internal.dao.ISystemInfoWinDao;
import cn.lonsun.desktop.internal.entity.SystemInfoWinEO;
import cn.lonsun.desktop.internal.service.ISystemInfoWinService;
import cn.lonsun.desktop.vo.SystemInfoWinQueryVO;

/**
 * 服务接口实现类
 *
 * @author Dzl
 */
@Service
public class SystemInfoWinServiceImpl extends MockService<SystemInfoWinEO> implements
        ISystemInfoWinService {
        private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired 
    private ISystemInfoWinDao systemInfoWinDao;

    @Override
    public SystemInfoWinEO saveEO(SystemInfoWinEO eo) {
        this.getMockDao().save(eo);
        // 写入日志
        //String logStr = "[桌面信息窗口](新增编号:" + eo.getId() + ")";
        //LogUtil.saveLog(logStr, "DesktopWinInfoEO", LogVO.Operation.Add.toString());
        return eo;
    }

    @Override
    public SystemInfoWinEO updateEO(SystemInfoWinEO eo) {
        this.getMockDao().update(eo);
        // 写入日志
        //String logStr = "[桌面信息窗口](修改编号:" + eo.getId() + ")";
        //LogUtil.saveLog(logStr, "DesktopWinInfoEO", LogVO.Operation.Update.toString());
        return eo;
    }

    @Override
    public SystemInfoWinEO mergeEO(SystemInfoWinEO eo) {
        this.getMockDao().merge(eo);
        // 写入日志
       // String logStr = "[桌面信息窗口](修改编号:" + eo.getId() + ")";
       // LogUtil.saveLog(logStr, "DesktopWinInfoEO", LogVO.Operation.Update.toString());
        return eo;
    }

    @Override
    public void delPyEO(SystemInfoWinEO eo) {
        systemInfoWinDao.delPyEO(eo);
        // 写入日志
      //  String logStr = "[桌面信息窗口](删除编号:" + eo.getId() + ")";
      //  LogUtil.saveLog(logStr, "DesktopWinInfoEO", LogVO.Operation.Delete.toString());
    }
    
    @Override
    public void batchDelPyEO(String ids){
        List<String> idsArr = (List<String>) StrUtil.StringToList(ids);
        systemInfoWinDao.batchDelPyEo(idsArr);
    }

    @Override
    public List<SystemInfoWinEO> getList(SystemInfoWinQueryVO queryVo) {
        return systemInfoWinDao.getList(queryVo);
    }

    @Override
    public List<SystemInfoWinEO> getListByIds(List<Long> ids) {
        Map<String, SystemInfoWinEO> map = getInfoWin2Map(ids);
        List<SystemInfoWinEO> list = new ArrayList<SystemInfoWinEO>();
        for(Long id:ids){
            if(map.containsKey(String.valueOf(id))){
                list.add(map.get(String.valueOf(id)));
            }
        }
        return list;
    }
    
    
    private Map<String, SystemInfoWinEO> getInfoWin2Map(List<Long> ids){
        List<SystemInfoWinEO> list = systemInfoWinDao.getListByIds(ids);
        return getInfoWin2Map1(list);
    }
    private Map<String, SystemInfoWinEO> getInfoWin2Map1(List<SystemInfoWinEO> list){
        Map<String, SystemInfoWinEO> map=new HashMap<String, SystemInfoWinEO>();
        for(SystemInfoWinEO eo:list){
            map.put(String.valueOf(eo.getId()), eo);
        }
        return map;
    }
    
}
