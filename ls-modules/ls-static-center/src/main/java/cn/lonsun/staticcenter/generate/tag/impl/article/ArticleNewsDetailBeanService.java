/*
 * ContentBeanServiceImpl.java         2015年9月8日 <br/>
 *
 * Copyright (c) 1994-1999 AnHui LonSun, Inc. <br/>
 * All rights reserved. <br/>
 *
 * This software is the confidential and proprietary information of AnHui   <br/>
 * LonSun, Inc. ("Confidential Information").  You shall not    <br/>
 * disclose such Confidential Information and shall use it only in  <br/>
 * accordance with the terms of the license agreement you entered into  <br/>
 * with Sun. <br/>
 */

package cn.lonsun.staticcenter.generate.tag.impl.article;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.entity.KnowledgeBaseEO;
import cn.lonsun.content.internal.service.IKnowledgeBaseService;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.MongoUtil;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.staticcenter.generate.util.RegexUtil;
import cn.lonsun.webservice.words.IWordsCheckService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 文章内容页面生成 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年9月8日 <br/>
 */
@Component
public class ArticleNewsDetailBeanService extends AbstractBeanService {

    @Resource(name = "WordsCheckServiceImpl")
    private IWordsCheckService wordsCheckService;

    @Autowired
    private IKnowledgeBaseService knowledgeBaseService;

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        Long contentId = context.getContentId();// 根据文章id查询文章
        return CacheHandler.getEntity(BaseContentEO.class, contentId);
    }

    @Override
    public String objToStr(String content, Object resultObj, JSONObject paramObj) throws GenerateException {
        BaseContentEO eo = (BaseContentEO) resultObj;
        //替换标题中的\n，前台展示用
        String title = null != eo.getTitle() ? eo.getTitle().replace("\n", "<br/>") : eo.getTitle();
        eo.setTitle(title);
        if (!AppUtil.isEmpty(eo.getSubTitle())) {
            String subTitle = eo.getSubTitle().replace("\n", "<br/>");
            eo.setSubTitle(subTitle);
        }
        // 根据id去mongodb读取文件内容
        String article = MongoUtil.queryById(eo.getId());
        eo.setRemarks(null);//备注设置为空
//        if (StringUtils.isNotEmpty(article)) {
//            // 获取热词链接
//            List<WordsHotConfEO> list = null;
//            try {
//                list = wordsCheckService.getWords(article, Type.HOT.toString(), eo.getSiteId(), WordsHotConfEO.class);
//            } catch (Throwable e) {
//                throw new GenerateException("热词webservice接口调用失败！", e);
//            }
//            if (null != list && !list.isEmpty()) {
//                for (WordsHotConfEO hotEO : list) {
//                    String aLink = "<a target='_blank' href='" + hotEO.getHotUrl() + "' title='" + hotEO.getUrlDesc() + "'>" + hotEO.getHotName() + "</a>";
//                    article = article.replaceAll(hotEO.getHotName(), aLink);
//                }
//            }
        if (AppUtil.isEmpty(article)) {//图片新闻内容为空时 默认显示缩略图
            if (BaseContentEO.TypeCode.pictureNews.toString().equals(eo.getTypeCode())) {
                if (!AppUtil.isEmpty(eo.getImageLink())) {
//                    String imgPath = "";
//                    if (eo.getImageLink().indexOf(PathUtil.getPathConfig().getFileServerPath()) == -1 ||
//                            eo.getImageLink().indexOf(PathUtil.getPathConfig().getFileServerNamePath()) == -1) {//手动上传的
//                        if (eo.getImageLink().indexOf(".") == -1) {
//                            imgPath = PathUtil.getPathConfig().getFileServerPath() + eo.getImageLink();
//                        }
//                    } else {//导入数据
//                        imgPath = eo.getImageLink();
//                    }
                    article = "<div style='text-align:center;'>\n\t<img src='" + PathUtil.getUrl(eo.getImageLink()) + "' alt='' style='line-height:1.5;' />\n</div>";
                }
            }else if (BaseContentEO.TypeCode.knowledgeBase.toString().equals(eo.getTypeCode())) {
                //问答知识库内容提取
                Map<String,Object> map = new HashMap<String,Object>();
                map.put("contentId",eo.getId());
                KnowledgeBaseEO knowledgeBaseEO = knowledgeBaseService.getEntity(KnowledgeBaseEO.class,map);
                if (knowledgeBaseEO != null) {
                    article = knowledgeBaseEO.getContent();
                }

                eo.setRemarks(knowledgeBaseEO.getReplyContent());//把答复内容添加到备注里
            }
        }
        eo.setArticle(article);
//        }
        return RegexUtil.parseProperty(content, eo);
    }
}