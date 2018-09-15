package cn.lonsun.rbac.indicator.service.impl;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.rbac.indicator.service.IIndicatorSpecialService;
import cn.lonsun.system.role.internal.cache.MenuUserHideCache;
import cn.lonsun.system.role.internal.entity.RbacMenuUserHideEO;
import cn.lonsun.system.role.internal.service.IMenuUserHideSpecialService;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.SysLog;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/26.
 */
@Service
public class IndicatorSpecialServiceImpl extends MockService<IndicatorEO> implements IIndicatorSpecialService  {

    @Resource
    private cn.lonsun.indicator.internal.service.IIndicatorService indicatorService;

    @Resource
    private IMenuUserHideSpecialService menuUserHideSpecialService;



    @Override
    public void save(IndicatorEO indicator) {
        Long indicatorId = indicator.getIndicatorId();
        if (null == indicatorId) {
            indicatorService.save(indicator);
            /*if (IndicatorEO.Type.CMS_Site.toString().equals(indicator.getType())) {
                SysLog.log("添加站点 >> ID：" + indicatorId + ",名称：" + indicator.getName(), "IndicatorEO", CmsLogEO.Operation.Add.toString());
            } else if (IndicatorEO.Type.CMS_Section.toString().equals(indicator.getType())) {
                SysLog.log("添加栏目 >> ID：" + indicatorId + ",名称：" + indicator.getName(), "IndicatorEO", CmsLogEO.Operation.Add.toString());
            } else */if (IndicatorEO.Type.CMS_Menu.toString().equals(indicator.getType())) {
                SysLog.log("添加菜单项 >> ID：" + indicatorId + ",名称：" + indicator.getName(), "IndicatorEO", CmsLogEO.Operation.Add.toString());
            } else if (IndicatorEO.Type.Shortcut.toString().equals(indicator.getType())) {
                SysLog.log("添加快捷方式 >> ID：" + indicatorId + ",名称：" + indicator.getName(), "IndicatorEO", CmsLogEO.Operation.Add.toString());
            } else if (IndicatorEO.Type.ToolBarButton.toString().equals(indicator.getType())) {
                SysLog.log("添加工具栏按钮 >> ID：" + indicatorId + ",名称：" + indicator.getName(), "IndicatorEO", CmsLogEO.Operation.Add.toString());
            } else if (IndicatorEO.Type.Menu.toString().equals(indicator.getType())) {
                SysLog.log("添加cms菜单 >> ID：" + indicatorId + ",名称：" + indicator.getName(), "IndicatorEO", CmsLogEO.Operation.Add.toString());
            } else if (IndicatorEO.Type.CMS_Button.toString().equals(indicator.getType())) {
                SysLog.log("添加cms按钮 >> ID：" + indicatorId + ",名称：" + indicator.getName(), "IndicatorEO", CmsLogEO.Operation.Add.toString());
            } else if (IndicatorEO.Type.Other.toString().equals(indicator.getType())) {
                SysLog.log("添加权限集合 >> ID：" + indicatorId + ",名称：" + indicator.getName(), "IndicatorEO", CmsLogEO.Operation.Add.toString());
            }
        } else {
            indicatorService.updateEntity(indicator);
            //IndicatorEO cacheEO = CacheHandler.getEntity(IndicatorEO.class, indicatorId);
            IndicatorEO cacheEO = indicatorService.getEntity(IndicatorEO.class, indicatorId);

            if (!cacheEO.getIsShown4Admin().equals(indicator.getIsShown4Admin())
                    || !cacheEO.getIsShown4SystemManager().equals(indicator.getIsShown4SystemManager())
                    || !cacheEO.getIsShown4ExternalUser().equals(indicator.getIsShown4ExternalUser())) {
                // 找到下面所有的子菜单更新超级管理员可见属性、站点管理员可见属性、普通用户可见属性
                List<IndicatorEO> list = new ArrayList<IndicatorEO>();
                this.getAllChildrenList(indicatorId, list, indicator);
                indicatorService.updateEntities(list);
            }
            /*if (IndicatorEO.Type.CMS_Site.toString().equals(indicator.getType())) {
                SysLog.log("修改站点 >> ID：" + indicatorId + ",名称：" + indicator.getName(), "IndicatorEO", CmsLogEO.Operation.Update.toString());
            } else if (IndicatorEO.Type.CMS_Section.toString().equals(indicator.getType())) {
                SysLog.log("修改栏目 >> ID：" + indicatorId + ",名称：" + indicator.getName(), "IndicatorEO", CmsLogEO.Operation.Update.toString());
            } else */if (IndicatorEO.Type.CMS_Menu.toString().equals(indicator.getType())) {
                SysLog.log("修改菜单项 >> ID：" + indicatorId + ",名称：" + indicator.getName(), "IndicatorEO", CmsLogEO.Operation.Update.toString());
            } else if (IndicatorEO.Type.Shortcut.toString().equals(indicator.getType())) {
                SysLog.log("修改快捷方式 >> ID：" + indicatorId + ",名称：" + indicator.getName(), "IndicatorEO", CmsLogEO.Operation.Update.toString());
            } else if (IndicatorEO.Type.ToolBarButton.toString().equals(indicator.getType())) {
                SysLog.log("修改工具栏按钮 >> ID：" + indicatorId + ",名称：" + indicator.getName(), "IndicatorEO", CmsLogEO.Operation.Update.toString());
            } else if (IndicatorEO.Type.Menu.toString().equals(indicator.getType())) {
                SysLog.log("修改cms菜单 >> ID：" + indicatorId + ",名称：" + indicator.getName(), "IndicatorEO", CmsLogEO.Operation.Update.toString());
            } else if (IndicatorEO.Type.CMS_Button.toString().equals(indicator.getType())) {
                SysLog.log("修改cms按钮 >> ID：" + indicatorId + ",名称：" + indicator.getName(), "IndicatorEO", CmsLogEO.Operation.Update.toString());
            } else if (IndicatorEO.Type.Other.toString().equals(indicator.getType())) {
                SysLog.log("修改权限集合 >> ID：" + indicatorId + ",名称：" + indicator.getName(), "IndicatorEO", CmsLogEO.Operation.Update.toString());
            }
        }

        //增加菜单设置针对哪些用户隐藏
        if(IndicatorEO.Type.CMS_Menu.toString().equals(indicator.getType()) && null != indicator.getUsersList()) {
            //先物理删除
            if(!AppUtil.isEmpty(indicatorId)) {
                menuUserHideSpecialService.phyDelete(indicatorId);
            }
            List<RbacMenuUserHideEO> userHnEOs = JSONObject.parseArray(indicator.getUsersList(),RbacMenuUserHideEO.class);
            if(null != userHnEOs) {
                for(RbacMenuUserHideEO hide : userHnEOs) {
                    hide.setMenuId(indicatorId);
                }
            }
            menuUserHideSpecialService.saveEntities(userHnEOs);
            MenuUserHideCache.refresh();
        }
    }

    @Override
    public void delete(Long indicatorId) {
        this.delete(new Long[]{indicatorId});
    }

    @Override
    public void delete(Long[] ids) {
        indicatorService.delete(IndicatorEO.class, ids);
    }

    private void getAllChildrenList(Long indicatorId, List<IndicatorEO> list, IndicatorEO indicator) {
        List<IndicatorEO> childList = CacheHandler.getList(IndicatorEO.class, CacheGroup.CMS_PARENTID, indicatorId);
        if (null != childList && !childList.isEmpty()) {
            for (IndicatorEO indicatorEO : childList) {
                indicatorEO.setIsShown4Admin(indicator.getIsShown4Admin());
                indicatorEO.setIsShown4SystemManager(indicator.getIsShown4SystemManager());
                indicatorEO.setIsShown4ExternalUser(indicator.getIsShown4ExternalUser());
                list.add(indicatorEO);
                this.getAllChildrenList(indicatorEO.getIndicatorId(), list, indicatorEO);
            }
        }
    }
}
