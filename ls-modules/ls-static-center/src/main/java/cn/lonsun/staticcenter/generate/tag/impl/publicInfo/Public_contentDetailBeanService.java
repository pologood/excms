/*
 * Public_contentDetailBeanService.java         2016年8月1日 <br/>
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

package cn.lonsun.staticcenter.generate.tag.impl.publicInfo;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.lonsun.publicInfo.internal.service.IPublicContentService;
import cn.lonsun.publicInfo.vo.PublicContentVO;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.AssertUtil;
import cn.lonsun.staticcenter.generate.util.RegexUtil;

import com.alibaba.fastjson.JSONObject;

/**
 * 信息公开文章详细页 <br/>
 * 
 * @date 2016年8月1日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Service
public class Public_contentDetailBeanService extends AbstractBeanService {

//    @Resource(name = "WordsCheckServiceImpl")
//    private IWordsCheckService wordsCheckService;
    @Resource
    private IPublicContentService publicContentService;

    @Override
    public Object getObject(JSONObject paramObj) throws GenerateException {
        Context context = ContextHolder.getContext();
        Long contentId = context.getContentId();// 根据文章id查询文章
        AssertUtil.isEmpty(contentId, "文章id不能为空！");
        return publicContentService.getPublicContentByBaseContentId(contentId);
    }

    @Override
    public String objToStr(String content, Object resultObj, JSONObject paramObj) throws GenerateException {
        PublicContentVO vo = (PublicContentVO) resultObj;
        // 文章内容
//        String article = vo.getContent();
//        if (StringUtils.isNotEmpty(article)) {
//            // 获取热词链接
//            List<WordsHotConfEO> list = null;
//            try {
//                list = wordsCheckService.getWords(article, Type.HOT.toString(), vo.getSiteId(), WordsHotConfEO.class);
//            } catch (Throwable e) {
//                throw new GenerateException("热词webservice接口调用失败！", e);
//            }
//            if (null != list && !list.isEmpty()) {
//                for (WordsHotConfEO hotEO : list) {
//                    String aLink = "<a target='_blank' href='" + hotEO.getHotUrl() + "' title='" + hotEO.getUrlDesc() + "'>" + hotEO.getHotName() + "</a>";
//                    article = article.replaceAll(hotEO.getHotName(), aLink);
//                }
//            }
//            vo.setContent(article);
//        }
        return RegexUtil.parseProperty(content, vo);
    }
}