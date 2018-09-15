/*
 * LinkContentController.java         2015年11月18日 <br/>
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

package cn.lonsun.content.controller;

import cn.lonsun.activemq.MessageSenderUtil;
import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.ILinkContentService;
import cn.lonsun.content.vo.SortVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.message.internal.entity.MessageStaticEO;
import cn.lonsun.site.site.internal.entity.ColumnConfigEO;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.ColumnUtil;
import cn.lonsun.util.SysLog;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * 链接管理控制类 <br/>
 * 
 * @date 2015年11月18日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Controller
@RequestMapping(value = "/linksMgr")
public class LinkContentController extends BaseController {

    @Resource
    private IBaseContentService baseContentService;

    @Resource
    private ILinkContentService linkContentService;

    /**
     * 首页
     * 
     * @author fangtinghua
     * @return
     */
    @RequestMapping("index")
    public String contentList(Long indicatorId, ModelMap map) {
        ColumnConfigEO columnConfigEO = CacheHandler.getEntity(ColumnConfigEO.class, CacheGroup.CMS_INDICATORID, indicatorId);
        if (columnConfigEO.getIsStartUrl() != 1) {// 不是跳转链接
            map.put("isLogo", columnConfigEO.getIsLogo());
        }
        return "/content/link/index";
    }

    /**
     * 获取链接内容
     * 
     * @author fangtinghua
     * @return
     */
    @ResponseBody
    @RequestMapping("getLinkContent")
    public Object getLinkContent(Long columnId, Long contentId) {
        BaseContentEO baseContentEO = null;
        if (null != contentId) {
            baseContentEO = CacheHandler.getEntity(BaseContentEO.class, contentId);
        } else {
            baseContentEO = new BaseContentEO();
            SortVO vo = baseContentService.getMaxNumByColumn(columnId);
            Long sort = 1L;
            if (!AppUtil.isEmpty(vo.getSortNum())) {
                sort = vo.getSortNum() + 10L;
            }
            // 设置序号
            baseContentEO.setNum(sort);// 每次递增加10
        }
        return getObject(baseContentEO);
    }

    /**
     * 跳转编辑
     * 
     * @author fangtinghua
     * @param map
     * @return
     */
    @RequestMapping("toLinkEdit")
    public String linkEdit(Long indicatorId, ModelMap map) {
        ColumnConfigEO columnConfigEO = CacheHandler.getEntity(ColumnConfigEO.class, CacheGroup.CMS_INDICATORID, indicatorId);
        if (columnConfigEO.getIsStartUrl() != 1) {// 不是跳转链接
            map.put("isLogo", columnConfigEO.getIsLogo());
        }
        return "/content/link/edit";
    }

    /**
     * 保存
     * 
     * @author fangtinghua
     * @param contentEO
     * @return
     */
    @ResponseBody
    @RequestMapping("linkEdit")
    public Object linkEdit(BaseContentEO contentEO) {
        String type = "新增";
        if(contentEO!=null&&contentEO.getId()!=null&&contentEO.getId()>0){
            type = "修改";
        }

        linkContentService.saveEntity(contentEO);
        SysLog.log(type+"链接 ：栏目（" + ColumnUtil.getColumnName(contentEO.getColumnId(), contentEO.getSiteId())
                + "），链接名称（"+contentEO.getTitle()+"）", "BaseContentEO", CmsLogEO.Operation.Update.toString());
        // 生成静态
        MessageStaticEO staticEO = new MessageStaticEO(contentEO.getSiteId(), contentEO.getColumnId(), new Long[] { contentEO.getId() });
        MessageSenderUtil.publishContent(staticEO.setType(MessageEnum.PUBLISH.value()), 1);
        return getObject();
    }
}