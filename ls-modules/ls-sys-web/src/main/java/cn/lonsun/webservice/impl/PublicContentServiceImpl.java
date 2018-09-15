/*
 * PublicContentServiceImpl.java         2016年10月21日 <br/>
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

package cn.lonsun.webservice.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import cn.lonsun.activemq.MessageSenderUtil;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.message.internal.entity.MessageStaticEO;
import cn.lonsun.util.LoginPersonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.publicInfo.internal.entity.PublicClassEO;
import cn.lonsun.publicInfo.internal.entity.PublicContentEO;
import cn.lonsun.publicInfo.internal.service.IPublicContentService;
import cn.lonsun.publicInfo.vo.PublicContentVO;
import cn.lonsun.webservice.PublicContentService;
import cn.lonsun.webservice.to.WebServiceTO;
import cn.lonsun.webservice.to.WebServiceTO.ErrorCode;

/**
 * TODO <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2016年10月21日 <br/>
 */
@Service("PublicContentServiceImpl_webservice")
public class PublicContentServiceImpl implements PublicContentService {

    @Resource
    private IPublicContentService publicContentService;

    @Override
    public WebServiceTO savePublicContent(PublicContentVO contentVO) {
        WebServiceTO to = new WebServiceTO();
        // 检查必填项
        if (null == contentVO) {
            return to.error(ErrorCode.ArgumentsError, "数据不能为空");
        }
        Long siteId = contentVO.getSiteId();
        if (null == siteId) {
            return to.error(ErrorCode.ArgumentsError, "siteId不能为空");
        }
        Long organId = contentVO.getOrganId();
        if (null == organId) {
            return to.error(ErrorCode.ArgumentsError, "organId不能为空");
        }
        Long catId = contentVO.getCatId();
        if (null == catId) {
            return to.error(ErrorCode.ArgumentsError, "catId不能为空");
        }
        /*String indexNum = contentVO.getIndexNum();
        if (StringUtils.isEmpty(indexNum)) {
            return to.error(ErrorCode.ArgumentsError, "indexNum不能为空");
        }*/
        String classIds = contentVO.getClassIds();
        if (StringUtils.isEmpty(classIds)) {
            return to.error(ErrorCode.ArgumentsError, "classIds不能为空");
        }
        String title = contentVO.getTitle();
        if (StringUtils.isEmpty(title)) {
            return to.error(ErrorCode.ArgumentsError, "title不能为空");
        }
        String content = contentVO.getContent();
        if (StringUtils.isEmpty(content)) {
            return to.error(ErrorCode.ArgumentsError, "content不能为空");
        }
        // 设置默认值
        if (StringUtils.isEmpty(contentVO.getFileNum())) {
            contentVO.setFileNum("无");
        }
        if (null == contentVO.getPublishDate()) {// 默认当前日期
            contentVO.setPublishDate(new Date());
        }
        contentVO.setType(PublicContentEO.Type.DRIVING_PUBLIC.toString());
        contentVO.setIndexNum(publicContentService.getIndexNum(organId));//设置索引号
        try {
            // 设置父分类id
            String[] classIdArr = classIds.split(",");
            Set<Long> parentIds = new HashSet<Long>();
            for (String classId : classIdArr) {
                PublicClassEO publicClassEO = CacheHandler.getEntity(PublicClassEO.class, classId);
                if (null != publicClassEO) {
                    parentIds.add(publicClassEO.getParentId());
                }
            }
            contentVO.setParentClassIds(StringUtils.join(parentIds, ","));
            Long id = publicContentService.saveWeServiceEntity(contentVO);
            // 发布
            if (contentVO.getIsPublish() == 1) {
                MessageStaticEO messageStaticEO = new MessageStaticEO();
                messageStaticEO.setSiteId(siteId);
                messageStaticEO.setColumnId(organId);
                messageStaticEO.setContentIds(new Long[]{contentVO.getContentId()});
                messageStaticEO.setSource(MessageEnum.PUBLICINFO.value());
                messageStaticEO.setType(MessageEnum.PUBLISH.value());
                messageStaticEO.setScope(MessageEnum.CONTENT.value());
                messageStaticEO.setUserId(1L);//写死为root发布
                MessageSenderUtil.publishContent(messageStaticEO, 1);
            }
            return to.success(String.valueOf(id), "添加成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return to.error(ErrorCode.SystemError, "系统异常，请稍后重试！");
        }
    }
}