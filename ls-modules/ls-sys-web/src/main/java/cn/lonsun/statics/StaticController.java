/*
 * StaticController.java         2016年1月21日 <br/>
 *
 * Copyright (c) 1994-1999 AnHui LonSun, Inc. <br/>
 * All rights reserved.	<br/>
 *
 * This software is the confidential and proprietary information of AnHui	<br/>
 * LonSun, Inc. ("Confidential Information").  You shall not	<br/>
 * disclose such Confidential Information and shall use it only in	<br/>
 * accordance with the terms of the license agreement you entered into	<br/>
 * with Sun. <br/>
 */

package cn.lonsun.statics;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.indicator.internal.service.IIndicatorService;
import cn.lonsun.publicInfo.internal.entity.OrganConfigEO;
import cn.lonsun.publicInfo.internal.service.IOrganConfigService;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import cn.lonsun.util.LoginPersonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.activemq.MessageSender;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.message.internal.entity.MessageStaticEO;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 生成静态控制类 <br/>
 *
 * @date 2016年1月21日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Controller
@RequestMapping("static")
public class StaticController extends BaseController {

    @Autowired
    private IColumnConfigService columnConfigService;

    @Autowired
    private IOrganConfigService organConfigService;

    @RequestMapping("content/index")
    public String contentIndex() {
        return "/static/content_index";
    }

    @RequestMapping("content/edit")
    public String contentEdit(Long indicatorId, ModelMap map) {
        IndicatorEO eo = CacheHandler.getEntity(IndicatorEO.class, indicatorId);
        map.put("eo", eo);
        return "/static/content_edit";
    }

    @ResponseBody
    @RequestMapping("publish")
    public Object publish(MessageStaticEO messageStaticEO) {
        messageStaticEO.setTodb(true);// 统一生成静态需要入库
        messageStaticEO.setUserId(LoginPersonUtil.getUserId());
        //生成文章页时分子任务执行
        if(messageStaticEO.getScope().longValue() == 3l){
            return sendSubMessage(messageStaticEO);
        }
        MessageSender.sendMessage(messageStaticEO);
        return getObject();
    }

    private Object sendSubMessage(MessageStaticEO messageStaticEO){
        if(messageStaticEO.getSource() == null){
            return ajaxErr("未知类型");
        }
        //如果是信息公开
        if(messageStaticEO.getSource().longValue() == 2){
            //生成信息具体单位
            if(messageStaticEO.getColumnId() != null && messageStaticEO.getColumnId() != 0){
                MessageSender.sendMessage(messageStaticEO);
                return getObject();
            }
            //生成所有单位时查出所有单位，然后分别新建任务
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("isEnable", true);
            List<OrganConfigEO> list = organConfigService.getEntities(OrganConfigEO.class, map);
            if(list == null){
                MessageSender.sendMessage(messageStaticEO);
                return getObject();
            }
            for(OrganConfigEO item: list){
                if(item.getIsEnable()){
                    messageStaticEO.setColumnId(item.getOrganId());
                    MessageSender.sendMessage(messageStaticEO);
                }
            }
        }else{
            List<ColumnMgrEO> list = null;
            //内容协同 生成全站
            if(messageStaticEO.getColumnId() == null || messageStaticEO.getColumnId() == 0){
//                list = columnConfigService.getLevelColumnTree(_columnId, new int[]{2,3}, null, null);
//                list = CacheHandler.getList(ColumnMgrEO.class, CacheGroup.CMS_SITE_ID, messageStaticEO.getSiteId());
            }else{
                list = CacheHandler.getList(ColumnMgrEO.class, CacheGroup.CMS_PARENTID, messageStaticEO.getColumnId());
            }

            //没有子栏目
            if(list == null){
                MessageSender.sendMessage(messageStaticEO);
                return getObject();
            }
            //分别生成子栏目
            for(ColumnMgrEO item: list){
                messageStaticEO.setColumnId(item.getIndicatorId());
                MessageSender.sendMessage(messageStaticEO);
            }
        }
        return getObject();
    }



    @RequestMapping("public/index")
    public String publicIndex() {
        return "/static/public_index";
    }
}