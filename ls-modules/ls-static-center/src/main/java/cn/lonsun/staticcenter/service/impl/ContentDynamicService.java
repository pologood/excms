/*
 * ContentDynamicService.java         2016年1月13日 <br/>
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

package cn.lonsun.staticcenter.service.impl;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.entity.ContentReferRelationEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IContentReferRelationService;
import cn.lonsun.content.statistics.internal.service.IClickCountStatService;
import cn.lonsun.content.vo.BaseContentVO;
import cn.lonsun.core.util.ContextHolderUtils;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.site.contentModel.internal.entity.ContentModelEO;
import cn.lonsun.site.contentModel.internal.entity.ModelTemplateEO;
import cn.lonsun.site.contentModel.internal.service.IContentModelService;
import cn.lonsun.site.contentModel.vo.ColumnTypeConfigVO;
import cn.lonsun.site.site.internal.entity.ColumnConfigEO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.template.internal.entity.TemplateConfEO;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.search.util.HtmlUtil;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.staticcenter.service.DynamicService;
import cn.lonsun.staticcenter.service.HtmlEnum;
import cn.lonsun.staticcenter.util.GenerateUtil;
import cn.lonsun.system.member.vo.MemberSessionVO;
import cn.lonsun.util.ModelConfigUtil;
import cn.lonsun.util.PropertiesHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 内容协同 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2016年1月13日 <br/>
 */
@Service
public class ContentDynamicService implements DynamicService {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Resource
    private IBaseContentService baseContentService;

    @Resource
    private IClickCountStatService clickCountStatService;

    @Resource
    private IContentModelService contentModelService;

    @Resource
    private IContentReferRelationService contentReferRelationService;

    @Resource
    private PropertiesHelper properties;

    @Override
    public String queryHtml(String action, Long id, Context context) throws GenerateException {
        if (HtmlEnum.PAGE.getValue().equals(action)) {// 取上一篇下一篇
            // 判断文章是否存在
            BaseContentEO baseContentEO = CacheHandler.getEntity(BaseContentEO.class, id);
            if (null == baseContentEO) {
                return "文章id:" + id + ",不存在.";
            }
            // 设置站点id
            context.setSiteId(baseContentEO.getSiteId());
            StringBuffer sb = new StringBuffer();
            sb.append("document.write(\"");
            BaseContentVO vo = baseContentService.getLastNextVO(baseContentEO.getColumnId(), baseContentEO.getSiteId(), baseContentEO.getTypeCode(), id, false);
            if (null != vo) {
                if (vo.getLastId().equals(0L)) {// 上一篇
                    sb.append("<div><span>上一篇：</span>没有更多新闻了</div>");
                } else {
                    BaseContentEO lastEO = CacheHandler.getEntity(BaseContentEO.class, vo.getLastId());
                    if (null == lastEO) {
                        sb.append("<div><span>上一篇：</span>没有更多新闻了</div>");
                    } else {
                        String redirectLink = lastEO.getRedirectLink();
                        String path = StringUtils.isNotBlank(redirectLink) ? redirectLink : PathUtil.getLinkPath(baseContentEO.getColumnId(), vo.getLastId());// 拿到栏目页和文章页id
                        String title = HtmlUtil.getTextFromHtml(vo.getLastTitle());
                        sb.append("<div><span>上一篇：</span><a href='" + path + "' title='" + title + "'>" + title + "</a></div>");
                    }
                }
                if (vo.getNextId().equals(0L)) {// 上一篇
                    sb.append("<div><span>下一篇：</span>没有更多新闻了</div>");
                } else {
                    BaseContentEO nextEO = CacheHandler.getEntity(BaseContentEO.class, vo.getNextId());
                    if (null == nextEO) {
                        sb.append("<div><span>下一篇：</span>没有更多新闻了</div>");
                    } else {
                        String redirectLink = nextEO.getRedirectLink();
                        String path = StringUtils.isNotBlank(redirectLink) ? redirectLink : PathUtil.getLinkPath(baseContentEO.getColumnId(), vo.getNextId());// 拿到栏目页和文章页id
                        String title = HtmlUtil.getTextFromHtml(vo.getNextTitle());
                        sb.append("<div><span>下一篇：</span><a href='" + path + "' title='" + title + "'>" + title + "</a></div>");
                    }
                }
            }
            return sb.append("\")").toString();
        } else if (HtmlEnum.HIT.getValue().equals(action)) {// 点击数
            // 判断文章是否存在
            BaseContentEO baseContentEO = baseContentService.getEntity(BaseContentEO.class, id);
            if (null == baseContentEO) {
                return "文章id:" + id + ",不存在.";
            }

            //查询是否是引用新闻
            List<ContentReferRelationEO> referList = contentReferRelationService.getByReferId(id, null, null);
            Long causeById;
            BaseContentEO causeByEO;
            if(referList!=null&&referList.size()>0){//引用新闻，查询源新闻
                causeById = referList.get(0).getCauseById();
                causeByEO = baseContentService.getEntity(BaseContentEO.class, causeById);

                //主表记录下自身的点击数
                baseContentEO.setHit((baseContentEO.getHit() == null ? 0 : baseContentEO.getHit()) + 1);
                baseContentService.updateEntity(baseContentEO);

            }else{//非引用新闻
                causeById = id;
                causeByEO = baseContentEO;
            }

            causeByEO.setHit((causeByEO.getHit() == null ? 0 : causeByEO.getHit()) + 1);
            baseContentService.updateEntity(causeByEO);
            if (BaseContentEO.TypeCode.articleNews.toString().equals(causeByEO.getTypeCode())) {// 文章新闻纪录点击量
                clickCountStatService.saveClickCountStat(causeByEO.getSiteId(), causeByEO.getColumnId(), causeById);
            }
            CacheHandler.saveOrUpdate(BaseContentEO.class, causeByEO);// 更新缓存
            //引用新闻显示源新闻的点击数
            return "document.write(\"" + causeByEO.getHit() + "\")";
        } else if (HtmlEnum.COLUMN.getValue().equals(action)) {// 栏目页

            ColumnMgrEO columnConfigEO = CacheHandler.getEntity(ColumnMgrEO.class, id);
            if (null == columnConfigEO) {// 没有栏目配置信息
                return "栏目id:" + id + ",沒有栏目配置信息.";
            }

            // 不能直接根据模型CODE获取到配置，因为是站群可能存在多个相同的CODE
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("code", columnConfigEO.getContentModelCode());
            map.put("siteId", columnConfigEO.getSiteId());
            List<ContentModelEO> contentModel = contentModelService.getEntities(ContentModelEO.class, map);
            // CacheHandler.getEntity(ContentModelEO.class, CacheGroup.CMS_CODE,
            // columnConfigEO.getContentModelCode());
            if (null == contentModel || contentModel.size() <= 0) {
                return "栏目id:" + id + ",沒有模板配置信息.";
            }

            List<ModelTemplateEO> modeList = CacheHandler.getList(ModelTemplateEO.class, CacheGroup.CMS_MODEL_ID, contentModel.get(0).getId());
            if (null == modeList || modeList.isEmpty()) {// 没有栏目配置信息
                return "栏目id:" + id + ",沒有模板配置信息.";
            }

            // 模板数据
            // Wap 站读取wap首页模板
            Long tplId = null;
            if (context.getFrom().equals(Context.From.PC.toString())) {
                tplId = modeList.get(0).getColumnTempId();
            } else {
                tplId = modeList.get(0).getWapColumnTempId();
            }

            TemplateConfEO templateConfEO = CacheHandler.getEntity(TemplateConfEO.class, tplId);
            if (null == templateConfEO) {
                return "栏目id:" + id + ",沒有配置栏目页模板.";
            }

            // 设置站点id
            IndicatorEO indicatorEO = CacheHandler.getEntity(IndicatorEO.class, id);
            context.setSiteId(indicatorEO.getSiteId()).setColumnId(id);
            context.setTemplateConfEO(templateConfEO);
            context.setScope(MessageEnum.COLUMN.value());
            return GenerateUtil.generate(context);
        } else if (HtmlEnum.ACRTILE.getValue().equals(action)) {// 文章页
            BaseContentEO baseContentEO = CacheHandler.getEntity(BaseContentEO.class, id);

            // 判断文章是否存在
            if (null == baseContentEO) {
                return "文章id:" + id + ",不存在.";
            }

            if (BaseContentEO.TypeCode.messageBoard.toString().equals(baseContentEO.getTypeCode())) {
                ColumnTypeConfigVO configVO = ModelConfigUtil.getCongfigVO(baseContentEO.getColumnId(), baseContentEO.getSiteId());
                if (configVO.getIsLoginGuest() != null && configVO.getIsLoginGuest() == 1) {
                    if (baseContentEO.getIsPublish() == null || baseContentEO.getIsPublish().intValue() == 0) {
                        MemberSessionVO memberVO = (MemberSessionVO) ContextHolderUtils.getSession().getAttribute("member");
                        if (memberVO == null || memberVO.getId() == null) {
                            return "请重新登录！";
                        }
                    }
                }
            } else {
                if (!accessCheck(baseContentEO)) {
                    return "文章id:" + id + ",未发布.";
                }
            }

            ColumnConfigEO columnConfigEO = CacheHandler.getEntity(ColumnConfigEO.class, CacheGroup.CMS_INDICATORID, baseContentEO.getColumnId());
            if (null == columnConfigEO) {// 没有栏目配置信息
                return "栏目id:" + id + ",沒有栏目配置信息.";
            }

            // 获取到内容模型
            ContentModelEO contentModel = CacheHandler.getEntity(ContentModelEO.class, CacheGroup.CMS_CODE, columnConfigEO.getContentModelCode());
            if (null == contentModel) {
                return "栏目id:" + id + ",沒有模板配置信息.";
            }
            List<ModelTemplateEO> modeList = CacheHandler.getList(ModelTemplateEO.class, CacheGroup.CMS_MODEL_ID, contentModel.getId());
            if (null == modeList || modeList.isEmpty()) {// 没有栏目配置信息
                return "栏目id:" + id + ",沒有模板配置信息.";
            }

            // 模板id
            Long tplId = null;
            // Wap 站读取wap首页模板
            if (context.getFrom().equals(Context.From.PC.toString())) {
                tplId = modeList.get(0).getArticalTempId();
            } else {
                tplId = modeList.get(0).getWapArticalTempId();
            }

            // 文章类型
            String typeCode = baseContentEO.getTypeCode();
            // 文章模板根据类型来判断
            /*
             * for (ModelTemplateEO tplEO : modeList) { if
             * (typeCode.equals(tplEO.getModelTypeCode())) { tplId =
             * tplEO.getArticalTempId(); break; } }
             */

            // 判断文章页模板
            if (null == tplId) {
                return "文章id:" + id + ",沒有配置文章页模板.";
            }

            // 模板数据
            TemplateConfEO templateConfEO = CacheHandler.getEntity(TemplateConfEO.class, tplId);
            if (null == templateConfEO) {
                return "栏目id:" + id + ",沒有配置栏目页模板.";
            }

            // 设置站点id
            IndicatorEO indicatorEO = CacheHandler.getEntity(IndicatorEO.class, baseContentEO.getColumnId());
            context.setSiteId(indicatorEO.getSiteId()).setColumnId(baseContentEO.getColumnId()).setContentId(id);
            context.setTemplateConfEO(templateConfEO).setTypeCode(typeCode);
            context.setScope(MessageEnum.CONTENT.value());
            return GenerateUtil.generate(context);
        }
        return null;
    }

    /**
     * 判断是否让访问动态地址
     *
     * @param content
     * @return
     */
    private boolean accessCheck(BaseContentEO content) {
        if (content.getIsPublish() == 1) {
            return true;
        }
        HttpServletRequest request = ContextHolderUtils.getRequest();
        String url = request.getHeader("Referer");
        logger.info("当前请求来源:{}", url);
        if (null == url) {
            return false;
        }
        String sysDomain = properties.getSysDomain();
        logger.info("允许的域名地址:{}", sysDomain);
        if (null == sysDomain) {
            logger.error("请在配置文件中配置sysDomain属性");
            return true;
        }
        if (!url.contains(sysDomain)) {
            logger.error("当前信息[{}]未发布，前台[{}]禁止访问动态地址", content.getTitle(), url);
            return false;
        }
        return true;
    }
}