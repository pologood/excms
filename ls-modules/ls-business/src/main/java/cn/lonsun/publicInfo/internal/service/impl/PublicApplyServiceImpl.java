/*
 * PublicApplyServiceImpl.java         2015年12月25日 <br/>
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

package cn.lonsun.publicInfo.internal.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import cn.lonsun.content.vo.ContentPageVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.exception.BusinessException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.publicInfo.internal.dao.IPublicApplyDao;
import cn.lonsun.publicInfo.internal.entity.CompanyEO;
import cn.lonsun.publicInfo.internal.entity.NationalEO;
import cn.lonsun.publicInfo.internal.entity.PublicApplyEO;
import cn.lonsun.publicInfo.internal.service.ICompanyService;
import cn.lonsun.publicInfo.internal.service.INationalService;
import cn.lonsun.publicInfo.internal.service.IPublicApplyService;
import cn.lonsun.publicInfo.vo.PublicApplyQueryVO;
import cn.lonsun.publicInfo.vo.PublicApplyTypeVO;
import cn.lonsun.publicInfo.vo.PublicApplyVO;
import cn.lonsun.publicInfo.vo.PublicContentQueryVO;
import cn.lonsun.publicInfo.vo.PublicTjVO;
import cn.lonsun.publicInfo.vo.PublicTotalVO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import cn.lonsun.util.DataDictionaryUtil;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.RandomCode;

/**
 * TODO <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年12月25日 <br/>
 */
@Service
public class PublicApplyServiceImpl extends MockService<PublicApplyEO> implements IPublicApplyService {
    @Resource
    private IPublicApplyDao publicApplyDao;
    @Resource
    private INationalService nationalService;
    @Resource
    private ICompanyService companyService;
    @Resource
    private IBaseContentService baseContentService;

    @Override
    public Long saveEntity(PublicApplyVO vo) {
        Long id = vo.getId();
        String type = vo.getType();// 类型 公民、法人
        // 前台 操作
        if (null == id) {// 新增
            Long relId = vo.getRelId();// 关系id
            if (PublicApplyTypeVO.PERSON.getCode().equals(type)) {// 个人
                NationalEO eo = new NationalEO();
                // 设置个人信息
                BeanUtils.copyProperties(vo, eo, "id");
                relId = nationalService.saveEntity(eo);
            } else if (PublicApplyTypeVO.ORGAN.getCode().equals(type)) {
                CompanyEO eo = new CompanyEO();
                // 设置单位信息
                BeanUtils.copyProperties(vo, eo, "id");
                relId = companyService.saveEntity(eo);
            }
            // 增加内容主表
            BaseContentEO baseContentEO = new BaseContentEO();
            baseContentEO.setTypeCode(PublicApplyEO.PUBLIC_APPLY);
            baseContentEO.setColumnId(vo.getOrganId());//设置单位id
            BeanUtils.copyProperties(vo, baseContentEO, "id");
            // baseContentEO.setIsPublish(0);
            Long contentId = baseContentService.saveEntity(baseContentEO);
            // 生成查询编号
            vo.setCode(RandomCode.shortUrl(String.valueOf(contentId)));
            vo.setQueryCode(vo.getCode());
            // 增加申请主表
            PublicApplyEO eo = new PublicApplyEO();
            BeanUtils.copyProperties(vo, eo);
            eo.setRelId(relId);
            eo.setContentId(contentId);
            if (null != vo.getApplyDate()) {
                eo.setApplyDate(vo.getApplyDate());
            } else {
                eo.setApplyDate(new Date());
            }
            publicApplyDao.save(eo);
        }
        // 后台更新回复状态等
        else {
            PublicApplyEO eo = publicApplyDao.getEntity(PublicApplyEO.class, id);
            BeanUtils.copyProperties(vo, eo);
            publicApplyDao.save(eo);
        }
        return id;
    }

    @Override
    public void delete(Long[] ids) {
        if (null != ids && ids.length > 0) {
            for (Long id : ids) {

                PublicApplyEO eo = publicApplyDao.getEntity(PublicApplyEO.class, id);
                Long relId = eo.getRelId();
                String type = eo.getType();// 类型 公民、法人
                if (PublicApplyTypeVO.PERSON.getCode().equals(type)) {// 个人
                    nationalService.delete(NationalEO.class, relId);
                } else if (PublicApplyTypeVO.ORGAN.getCode().equals(type)) {
                    companyService.delete(CompanyEO.class, relId);
                }
                baseContentService.delete(BaseContentEO.class, eo.getContentId());
                super.delete(eo);
            }
        }
    }

    @Override
    public void deleteAll() {
        List<PublicApplyEO> applyList = publicApplyDao.getEntities(PublicApplyEO.class, new HashMap<String, Object>());
        if (null != applyList && !applyList.isEmpty()) {
            for (PublicApplyEO eo : applyList) {
                Long relId = eo.getRelId();
                String type = eo.getType();// 类型 公民、法人
                if (PublicApplyTypeVO.PERSON.getCode().equals(type)) {// 个人
                    nationalService.delete(NationalEO.class, relId);
                } else if (PublicApplyTypeVO.ORGAN.getCode().equals(type)) {
                    companyService.delete(CompanyEO.class, relId);
                }
                baseContentService.delete(BaseContentEO.class, eo.getContentId());
                super.delete(eo);
            }
        }
    }

    @Override
    public Pagination getPublicApplyInfo(PublicApplyQueryVO queryVO) {
        Pagination page = null;
        String queryCode = queryVO.getQueryCode();
        if (StringUtils.isNotEmpty(queryCode)) {// 如果查询编号不为空，按照编号和密码查询
            List<PublicApplyVO> resultList = new ArrayList<PublicApplyVO>();
            PublicApplyVO vo = publicApplyDao.getPublicApplyByQueryCode(queryVO);
            resultList.add(vo);
            page = new Pagination(resultList, 1L, queryVO.getPageSize(), queryVO.getPageIndex());
        } else {
            page = publicApplyDao.getPagination(queryVO);
        }
        List<?> list = (List<?>) page.getData();
        for (Object o : list) {
            PublicApplyVO vo = (PublicApplyVO) o;
            setVoValue(vo);
        }
        return page;
    }

    @Override
    public Pagination getPagination(PublicApplyQueryVO queryVO) throws BusinessException {
        Pagination page = null;
        String queryCode = queryVO.getQueryCode();
        if (StringUtils.isNotEmpty(queryCode)) {// 如果查询编号不为空，按照编号和密码查询
            List<PublicApplyVO> resultList = new ArrayList<PublicApplyVO>();
            PublicApplyVO vo = publicApplyDao.getPublicApplyByQueryCode(queryVO);
            if (null == vo) {
                throw new BusinessException(TipsMode.Message.toString(), "查询信息不存在，请确定查询编号和密码是否正确！");
            }
            resultList.add(vo);
            page = new Pagination(resultList, 1L, queryVO.getPageSize(), queryVO.getPageIndex());
        } else if (StringUtils.isNotEmpty(queryVO.getCardNum())) {// 按照证件号码查询
            List<PublicApplyVO> resultList = new ArrayList<PublicApplyVO>();
            PublicApplyVO vo = publicApplyDao.getPublicApplyByCardNum(queryVO);
            if (null == vo) {
                throw new BusinessException(TipsMode.Message.toString(), "查询信息不存在，请确定证件号码和密码是否正确！");
            }
            resultList.add(vo);
            page = new Pagination(resultList, 1L, queryVO.getPageSize(), queryVO.getPageIndex());
        } else {
            page = publicApplyDao.getPagination(queryVO);
        }
        List<?> list = (List<?>) page.getData();
        for (Object o : list) {
            PublicApplyVO vo = (PublicApplyVO) o;
            setVoValue(vo);
        }
        return page;
    }

    @Override
    public PublicApplyVO getPublicApply(Long id) {
        PublicApplyVO vo = publicApplyDao.getPublicApply(id);
        setVoValue(vo);
        return vo;
    }

    @Override
    public PublicTotalVO getPublicTotalVO(PublicContentQueryVO queryVO) {
        List<PublicTjVO> list = publicApplyDao.getPublicTjList(queryVO);
        PublicTotalVO vo = new PublicTotalVO();
        if (null != list && !list.isEmpty()) {
            vo.setOrganCount(list.size());
            long applyCount = 0;
            long replyCount = 0;
            for (PublicTjVO tj : list) {
                applyCount += tj.getApplyCount();
                replyCount += tj.getReplyCount();
                OrganEO organEO = CacheHandler.getEntity(OrganEO.class, tj.getOrganId());
                if (null != organEO) {
                    tj.setOrganName(organEO.getName());
                }
            }
            vo.setApplyCount(applyCount);
            vo.setReplyCount(replyCount);
            vo.setData(list);
        }
        return vo;
    }

    private void setVoValue(PublicApplyVO vo) {
        if (!StringUtils.isEmpty(vo.getReplyStatus())) {
            DataDictVO dictVO = DataDictionaryUtil.getItem("public_apply_reply_status", vo.getReplyStatus());
            if (dictVO != null) {
                vo.setReplyStatusName(dictVO.getKey());
            }
        }
        OrganEO organEO = CacheHandler.getEntity(OrganEO.class, vo.getOrganId());
        if (null != organEO) {
            vo.setReceiveOrganName(organEO.getName());
        }
        String type = vo.getType();// 类型 公民、法人
        if (PublicApplyTypeVO.PERSON.getCode().equals(type)) {// 个人
            NationalEO eo = nationalService.getEntity(NationalEO.class, vo.getRelId());
            // 设置个人信息
            BeanUtils.copyProperties(eo, vo, "id");
        } else if (PublicApplyTypeVO.ORGAN.getCode().equals(type)) {
            CompanyEO eo = companyService.getEntity(CompanyEO.class, vo.getRelId());
            // 设置单位信息
            BeanUtils.copyProperties(eo, vo, "id");
        }
        // 设置身份类型名称
        DataDictVO dictVO = DataDictionaryUtil.getItem("system_cardType", vo.getCardType());
        if (null != dictVO) {
            vo.setCardTypeName(dictVO.getKey());
        }
    }

    @Override
    public void updatePublicStatus(Long[] ids, Integer status) {
        Long siteId = LoginPersonUtil.getSiteId();
        if (null != ids && ids.length > 0) {
            // 更新主表发布状态
            baseContentService.changePublish(new ContentPageVO(siteId, null, status, ids, null));
        }
    }

    @Override
    public List<PublicTjVO> getPublicTjList(PublicContentQueryVO queryVO) {
        return publicApplyDao.getPublicTjList(queryVO);
    }

    @Override
    public List<PublicTjVO> getPublicTjByApplyStatus(PublicContentQueryVO queryVO) {
        return publicApplyDao.getPublicTjByApplyStatus(queryVO);
    }
}