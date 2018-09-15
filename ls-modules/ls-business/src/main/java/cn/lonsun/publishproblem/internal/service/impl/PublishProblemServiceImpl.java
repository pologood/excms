package cn.lonsun.publishproblem.internal.service.impl;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogEO;
import cn.lonsun.publicInfo.internal.entity.PublicContentEO;
import cn.lonsun.publicInfo.internal.service.IPublicCatalogService;
import cn.lonsun.publicInfo.internal.service.IPublicContentService;
import cn.lonsun.publishproblem.internal.dao.IPublishProblemDao;
import cn.lonsun.publishproblem.internal.service.IPublishProblemService;
import cn.lonsun.publishproblem.vo.PublishProblemVO;
import cn.lonsun.publishproblem.vo.PulishProblemQueryVO;
import cn.lonsun.rbac.indicator.service.IIndicatorService;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.system.datadictionary.internal.entity.DataDictEO;
import cn.lonsun.system.datadictionary.internal.entity.DataDictItemEO;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by huangxx on 2017/9/1.
 */
@Service
public class PublishProblemServiceImpl extends MockService<BaseContentEO> implements IPublishProblemService{


    @Resource
    private IPublishProblemDao publishProblemDao;

    @Resource
    private IIndicatorService iIndicatorService;

    @Resource
    private IBaseContentService baseContentService;

    @Resource
    private IPublicCatalogService publicCatalogService;

    @Resource
    private IOrganService organService;

    @Resource
    private IPublicContentService publicContentService;


    @Override
    public Pagination getPage(PulishProblemQueryVO queryVO) {
        Pagination page = publishProblemDao.getPage(queryVO);
        if(null != page) {
            List<PublishProblemVO> pageData = (List<PublishProblemVO>)page.getData();
            if(null != pageData && pageData.size() > 0) {
                for(PublishProblemVO vo : pageData) {
                    //信息公开文章
                    if(BaseContentEO.TypeCode.public_content.toString().equals(vo.getTypeCode())) {
                        Map<String, Object> params = new HashMap<String, Object>();
                        params.put("contentId",vo.getId());
                        params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
                        //信息公开实体
                        PublicContentEO publicContentEO = publicContentService.getEntity(PublicContentEO.class, params);
                        if(null != publicContentEO) {
                            //信息公开所属目录单位
                            OrganEO organEO = organService.getEntity(OrganEO.class, vo.getColumnId());
                            if(null != organEO) {
                                vo.setColumnName(organEO.getName());
                            }
                            //主动公开类型
                            if(PublicContentEO.Type.DRIVING_PUBLIC.toString().equals(publicContentEO.getType())) {
                                PublicCatalogEO publicCatalogEO = publicCatalogService.getEntity(PublicCatalogEO.class, publicContentEO.getCatId());
                                if(null != publicCatalogEO) {
                                    vo.setColumnName(vo.getColumnName() + "-" + publicCatalogEO.getName());
                                }
                            }
                            //其他类型，取数据字典
                            else {
                                DataDictEO dictEO = CacheHandler.getEntity(DataDictEO.class, CacheGroup.CMS_CODE, PublicContentEO.PUBLIC_ITEM_CODE);
                                List<DataDictItemEO> dictItemList = CacheHandler.getList(DataDictItemEO.class, CacheGroup.CMS_PARENTID, dictEO.getDictId());
                                if(null != dictItemList && dictItemList.size() > 0) {
                                    for(DataDictItemEO eo : dictItemList) {
                                        if(eo.getCode().toString().equals(publicContentEO.getType())) {
                                            vo.setColumnName(vo.getColumnName() + "-" + eo.getName());
                                        }
                                    }
                                }
                            }
                        }
                    }
                    //其他文章
                    else {
                        IndicatorEO indicator = CacheHandler.getEntity(IndicatorEO.class, vo.getColumnId());
                        if(!AppUtil.isEmpty(indicator)) {
                            vo.setColumnName(indicator.getName());
                            if(!LoginPersonUtil.getSiteId().equals(indicator.getParentId())) {
                                IndicatorEO parentIndicator = CacheHandler.getEntity(IndicatorEO.class, indicator.getParentId());
                                if(!AppUtil.isEmpty(parentIndicator)) {
                                    vo.setColumnName(parentIndicator.getName() + '-' + vo.getColumnName());
                                }
                            }
                        }
                    }
                }
            }
            //按照栏目搜索
            if(!AppUtil.isEmpty(queryVO.getColumnName())) {
                List<PublishProblemVO> resultList = new ArrayList<PublishProblemVO>();
                if(null != pageData && pageData.size() > 0) {
                    for(PublishProblemVO vo : pageData) {
                        if(!StringUtils.isEmpty(vo.getColumnName()) && vo.getColumnName().indexOf(queryVO.getColumnName()) != -1) {
                            resultList.add(vo);
                        }
                    }
                    page.setData(resultList);
                }
            }
        }
        return page;
    }

    @Override
    public String publish(Long[] ids, Integer publish) {
        String returnStr = "";
        Long[] contenIds = new Long[ids.length];
        BaseContentEO contentEO = null;
        for(Long id : ids) {
            contentEO = baseContentService.getEntity(BaseContentEO.class,id);
            if(publish != null && publish.intValue() == 1) {
                if(contentEO.getPublishDate() == null) {
                    contentEO.setPublishDate(new Date());
                    baseContentService.updateEntity(contentEO);
                }
            }
            // 生成静态
            returnStr += contentEO.getSiteId()+ "_" + contentEO.getColumnId() + "_" + contentEO.getId() + ",";
        }
        //目前业务 ： 发布时候，状态设为发布中（2）  取消发布时候，状态设为发布中（2）
        if(publish.intValue() == 1) {
            publish = 2;
        }else {
            publish = 2;
        }
        baseContentService.changePublish(new ContentPageVO(null, null, Integer.parseInt(String.valueOf(publish)), ids, null));

        if(!AppUtil.isEmpty(returnStr)){//去除最后的逗号
            returnStr = returnStr.substring(0,returnStr.length()-1);
        }
        return returnStr;
    }
}
