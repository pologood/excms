/*
 * CacheInit.java         2015年10月8日 <br/>
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

package cn.lonsun.common.cache;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.cache.service.CacheConfig;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.govbbs.internal.entity.BbsMemberRoleEO;
import cn.lonsun.govbbs.internal.entity.BbsPlateEO;
import cn.lonsun.govbbs.internal.entity.BbsSettingEO;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.msg.submit.entity.CmsMsgSubmitClassifyEO;
import cn.lonsun.msg.submit.entity.CmsMsgSubmitEO;
import cn.lonsun.net.service.entity.CmsGuideResRelatedEO;
import cn.lonsun.net.service.entity.CmsNetServiceClassifyEO;
import cn.lonsun.net.service.entity.CmsRelatedRuleEO;
import cn.lonsun.net.service.entity.CmsTableResourcesEO;
import cn.lonsun.publicInfo.internal.entity.OrganConfigEO;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogEO;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogOrganRelEO;
import cn.lonsun.publicInfo.internal.entity.PublicClassEO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.entity.PersonEO;
import cn.lonsun.rbac.internal.entity.UserEO;
import cn.lonsun.site.contentModel.internal.entity.ContentModelEO;
import cn.lonsun.site.contentModel.internal.entity.ModelTemplateEO;
import cn.lonsun.site.label.internal.entity.LabelEO;
import cn.lonsun.site.site.internal.entity.*;
import cn.lonsun.site.template.internal.entity.TemplateConfEO;
import cn.lonsun.system.datadictionary.internal.entity.DataDictEO;
import cn.lonsun.system.datadictionary.internal.entity.DataDictItemEO;
import cn.lonsun.system.role.internal.entity.RbacInfoOpenRightsEO;
import cn.lonsun.system.role.internal.entity.RbacMenuRightsEO;
import cn.lonsun.system.role.internal.entity.RbacSiteRightsEO;
import cn.lonsun.system.role.internal.site.entity.CmsRoleSiteOptEO;
import cn.lonsun.webservice.internal.entity.WebServiceEO;

/**
 * 缓存初始化 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年10月8日 <br/>
 */
public class CacheInit {

    @PostConstruct
    public void init() {
        // 设置缓存过期时间
        int seconds = 1 * 60 * 60;
        // 缓存菜单、站点、栏目表
        CacheHandler.addToCache(CacheConfig.createBuilder(IndicatorEO.class).setEntity("indicatorId").setEntity(CacheGroup.CMS_PARENTID, true, "parentId")
                .setEntity(CacheGroup.CMS_TYPE, true, "type").setOrder("sortNum asc,createDate asc").setSeconds(seconds).build());
        // 站点配置信息
        CacheHandler.addToCache(CacheConfig.createBuilder(SiteConfigEO.class).setEntity("siteConfigId").setEntity(CacheGroup.CMS_INDICATORID, "indicatorId")
                .setSeconds(seconds).build());
        // 栏目配置信息
        CacheHandler.addToCache(CacheConfig.createBuilder(ColumnConfigEO.class).setEntity("columnConfigId")
                .setEntity(CacheGroup.CMS_INDICATORID, "indicatorId").setSeconds(seconds).build());
        CacheHandler.addToCache(CacheConfig.createBuilder(ColumnTypeEO.class).setEntity("id").setSeconds(seconds).build());
        // 模板配置信息
        CacheHandler.addToCache(CacheConfig.createBuilder(TemplateConfEO.class).setEntity("id").setEntity(CacheGroup.CMS_PARENTID, true, "pid")
                .setEntity(CacheGroup.CMS_JOIN_ID, true, "siteId", "tempType").setSeconds(seconds).setMock(false).build());
        // 缓存内容表，文章不失效
        CacheHandler.addToCache(CacheConfig.createBuilder(BaseContentEO.class).setEntity("id").build());
        // 缓存内容模型表
        CacheHandler.addToCache(CacheConfig.createBuilder(ContentModelEO.class).setEntity("id").setEntity(CacheGroup.CMS_CODE, "code")
                .setEntity(CacheGroup.CMS_SITE_ID, true, "siteId").setOrder("createDate asc").setSeconds(seconds).build());
        // 缓存内容模型、模板、栏目类别中间表
        CacheHandler.addToCache(CacheConfig.createBuilder(ModelTemplateEO.class).setEntity("tplId").setEntity(CacheGroup.CMS_MODEL_ID, true, "modelId")
                .setSeconds(seconds).build());
        // 缓存数据字典
        CacheHandler.addToCache(CacheConfig.createBuilder(DataDictEO.class).setEntity("dictId").setEntity(CacheGroup.CMS_CODE, "code").setSeconds(seconds)
                .setMock(false).build());
        // 缓存数据字典项
        CacheHandler.addToCache(CacheConfig.createBuilder(DataDictItemEO.class).setEntity("itemId").setEntity(CacheGroup.CMS_PARENTID, true, "dataDicId")
                .setOrder("sortNum asc,createDate asc").setSeconds(seconds).setMock(false).build());
        // 缓存操作权限
        CacheHandler.addToCache(CacheConfig.createBuilder(CmsRoleSiteOptEO.class).setEntity("id").setEntity(CacheGroup.CMS_PARENTID, true, "roleId")
                .setSeconds(seconds).setMock(false).build());
        // 缓存组织
        CacheHandler.addToCache(CacheConfig.createBuilder(OrganEO.class).setEntity("organId").setEntity(CacheGroup.CMS_PARENTID, true, "parentId")
                .setSeconds(seconds).build());
        // 缓存标签
        CacheHandler.addToCache(CacheConfig.createBuilder(LabelEO.class).setEntity("id").setEntity(CacheGroup.CMS_NAME, "labelName").setSeconds(seconds)
                .build());
        // 资源分类
        CacheHandler.addToCache(CacheConfig.createBuilder(CmsNetServiceClassifyEO.class).setEntity("id").setEntity(CacheGroup.CMS_PARENTID, true, "pid")
                .setSeconds(seconds).setMock(false).build());
        // 表格资源库
        CacheHandler.addToCache(CacheConfig.createBuilder(CmsTableResourcesEO.class).setEntity("id").setSeconds(seconds).setMock(false).build());
        // 相关法规资源库
        CacheHandler.addToCache(CacheConfig.createBuilder(CmsRelatedRuleEO.class).setEntity("id").setSeconds(seconds).setMock(false).build());
        // 相关法规资源库
        CacheHandler.addToCache(CacheConfig.createBuilder(CmsGuideResRelatedEO.class).setEntity("id").setEntity(CacheGroup.CMS_PARENTID, true, "guideId")
                .setSeconds(seconds).setMock(false).build());
        // 信息报送分类
        CacheHandler.addToCache(CacheConfig.createBuilder(CmsMsgSubmitClassifyEO.class).setEntity("id").setSeconds(seconds).setMock(false).build());
        // 菜单权限信息
        CacheHandler.addToCache(CacheConfig.createBuilder(RbacMenuRightsEO.class).setEntity("id").setEntity(CacheGroup.CMS_ROLE_ID, true, "roleId")
                .setSeconds(seconds).setMock(false).build());
        // 菜单权限信息
        CacheHandler.addToCache(CacheConfig.createBuilder(RbacSiteRightsEO.class).setEntity("id").setEntity(CacheGroup.CMS_ROLE_ID, true, "roleId")
                .setSeconds(seconds).setMock(false).build());
        // 缓存目录树
        CacheHandler.addToCache(CacheConfig.createBuilder(PublicCatalogEO.class).setEntity("id").setEntity(CacheGroup.CMS_CODE, "code")
                .setEntity(CacheGroup.CMS_PARENTID, true, "parentId").setEntity(CacheGroup.CMS_TYPE, true, "type").setOrder("sortNum asc").setSeconds(seconds)
                .build());
        // 缓存目录部门关系
        CacheHandler.addToCache(CacheConfig.createBuilder(PublicCatalogOrganRelEO.class).setEntity("id")
                .setEntity(CacheGroup.CMS_JOIN_ID, false, "organId", "catId").setEntity(CacheGroup.CMS_PARENTID, true, "organId")
                .setSeconds(seconds).build());
        // 缓存部门配置信息
        CacheHandler.addToCache(CacheConfig.createBuilder(OrganConfigEO.class).setEntity("id").setEntity(CacheGroup.CMS_ORGAN_ID, "organId").setMock(false)
                .setSeconds(seconds).build());
        // 缓存信息公开分类树
        CacheHandler.addToCache(CacheConfig.createBuilder(PublicClassEO.class).setEntity("id").setEntity(CacheGroup.CMS_ALL, true, "recordStatus")
                .setEntity(CacheGroup.CMS_PARENTID, true, "parentId").setOrder("sortNum asc").setSeconds(seconds).build());
        // 缓存栏目管理树
        CacheHandler.addToCache(CacheConfig.createBuilder(ColumnMgrEO.class).setEntity("indicatorId").setEntity(CacheGroup.CMS_PARENTID, true, "parentId")
                .setEntity(CacheGroup.CMS_TYPE, true, "type").setEntity(CacheGroup.CMS_SITE_ID, true, "siteId")
                .setEntity(CacheGroup.CMS_COLUMN_TYPE_CODE, true, "columnTypeCode").setOrder("sortNum asc,createDate asc").setSeconds(seconds).build());
        // 缓存站点管理
        CacheHandler.addToCache(CacheConfig.createBuilder(SiteMgrEO.class).setEntity("indicatorId").setEntity(CacheGroup.CMS_PARENTID, true, "parentId")
                .setEntity(CacheGroup.CMS_TYPE, true, "type").setOrder("sortNum asc,createDate asc").setSeconds(seconds).build());
        // 缓存采集信息
        CacheHandler.addToCache(CacheConfig.createBuilder(CmsMsgSubmitEO.class).setEntity("id").setSeconds(seconds).setMock(false).build());
        // 缓存人员信息
        CacheHandler.addToCache(CacheConfig.createBuilder(PersonEO.class).setEntity("personId").setSeconds(seconds).build());
        // 缓存用户信息
        CacheHandler.addToCache(CacheConfig.createBuilder(UserEO.class).setEntity("userId").setSeconds(seconds).build());
        // 缓存站点管理
        CacheHandler.addToCache(CacheConfig.createBuilder(RbacInfoOpenRightsEO.class).setEntity("id").setEntity(CacheGroup.CMS_ROLE_ID, true, "roleId")
                .setSeconds(seconds).build());
        // 缓存数据字典
        CacheHandler.addToCache(CacheConfig.createBuilder(WebServiceEO.class).setEntity("webServiceId").setEntity(CacheGroup.CMS_CODE, "code")
                .setSeconds(seconds).setMock(false).build());
        CacheHandler.addToCache(CacheConfig.createBuilder(ColumnConfigRelEO.class).setEntity("indicatorId")
                .setEntity(CacheGroup.CMS_PARENTID, true, "parentId").setOrder("sortNum asc,createDate asc").setSeconds(seconds).build());
        //缓存论坛配置
        CacheHandler.addToCache(CacheConfig.createBuilder(BbsSettingEO.class).setEntity("siteId").setSeconds(seconds).setMock(false).build());
        //缓存论坛版块
        CacheHandler.addToCache(CacheConfig.createBuilder(BbsPlateEO.class).setEntity("plateId").setSeconds(seconds).build());
        //缓存论坛会员分组
        CacheHandler.addToCache(CacheConfig.createBuilder(BbsMemberRoleEO.class).setEntity("id").setSeconds(seconds).build());

        // 重新加载所有缓存，懒加载模式
        List<String> excludeList = new ArrayList<String>();
        excludeList.add(BaseContentEO.class.getName());// 排除文章
        CacheHandler.reloadAll(excludeList);
    }
}