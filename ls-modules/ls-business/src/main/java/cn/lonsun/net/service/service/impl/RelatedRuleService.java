package cn.lonsun.net.service.service.impl;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.net.service.dao.IRelatedRuleDao;
import cn.lonsun.net.service.entity.CmsRelatedRuleEO;
import cn.lonsun.net.service.entity.CmsResourcesClassifyEO;
import cn.lonsun.net.service.service.IRelatedRuleService;
import cn.lonsun.net.service.service.IResourcesClassifyService;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author gu.fei
 * @version 2015-11-18 13:47
 */
@Service
public class RelatedRuleService extends BaseService<CmsRelatedRuleEO> implements IRelatedRuleService {

    @Autowired
    private IRelatedRuleDao relatedRuleDao;

    @Autowired
    private IResourcesClassifyService resourcesClassifyService;

    @Autowired
    private IBaseContentService baseContentService;

    @Override
    public List<CmsRelatedRuleEO> getEOs() {
        return relatedRuleDao.getEOs();
    }

    @Override
    public Pagination getPageEOs(ParamDto dto) {

        Pagination page = relatedRuleDao.getPageEOs(dto);
        List<CmsRelatedRuleEO> list = (List<CmsRelatedRuleEO>) page.getData();

        for (CmsRelatedRuleEO eo : list) {
            if (eo.getPublish() == 0) {
                list.remove(eo);
                break;
            }
            if (eo.getOrganId() != null) {
                OrganEO organEO = CacheHandler.getEntity(OrganEO.class, eo.getOrganId());
                if (organEO != null) {
                    eo.setOrganName(organEO.getName());
                }
            }

        }

        return page;
    }

    @Override
    public String saveEO(CmsRelatedRuleEO eo, String cIds) {
        String returnStr = "";
        Long siteId = null != eo.getSiteId() ? eo.getSiteId() : LoginPersonUtil.getSiteId();
        eo.setSiteId(siteId);

        BaseContentEO contentEO = new BaseContentEO();
        contentEO.setTitle(eo.getName());
        contentEO.setColumnId(eo.getColumnId());
        contentEO.setSiteId(siteId);
        contentEO.setIsPublish(eo.getPublish());
        contentEO.setRecordStatus(AMockEntity.RecordStatus.Normal.toString());
        contentEO.setTypeCode(BaseContentEO.TypeCode.relatedRule.toString());

        if (contentEO.getIsPublish() == 1) {
            contentEO.setPublishDate(new Date());
        }

        Long contentId = baseContentService.saveEntity(contentEO);
        eo.setContentId(contentId);

        Long pid = this.saveEntity(eo);
        //发布生成静态
        if (contentEO.getIsPublish() == 1) {
            returnStr = publish(new Long[]{pid}, Long.valueOf(contentEO.getIsPublish()));
        }

        if (!AppUtil.isEmpty(cIds)) {
            List<CmsResourcesClassifyEO> listc = new ArrayList<CmsResourcesClassifyEO>();
            Long[] ids = StringUtils.getArrayWithLong(cIds, ",");
            for (Long id : ids) {
                CmsResourcesClassifyEO cEO = new CmsResourcesClassifyEO();
                cEO.setpId(pid);
                cEO.setcId(id);
                cEO.setType(CmsResourcesClassifyEO.Type.RULE.toString());
                listc.add(cEO);
            }
            resourcesClassifyService.saveEntities(listc);
        }
        return returnStr;
    }

    @Override
    public String updateEO(CmsRelatedRuleEO eo, String cIds) {
        String returnStr = "";
        Long siteId = null != eo.getSiteId() ? eo.getSiteId() : LoginPersonUtil.getSiteId();
        eo.setSiteId(siteId);
        this.updateEntity(eo);

        BaseContentEO contentEO = baseContentService.getEntity(BaseContentEO.class, eo.getContentId());
        contentEO.setTitle(eo.getName());
        contentEO.setIsPublish(eo.getPublish());

        if (contentEO.getPublishDate() == null && contentEO.getIsPublish() == 1) {
            contentEO.setPublishDate(new Date());
        }

        baseContentService.updateEntity(contentEO);
        returnStr = publish(new Long[]{eo.getId()}, Long.valueOf(contentEO.getIsPublish()));
        CacheHandler.delete(BaseContentEO.class, contentEO);
        resourcesClassifyService.deleteByPid(eo.getId());
        if (!AppUtil.isEmpty(cIds)) {
            List<CmsResourcesClassifyEO> listc = new ArrayList<CmsResourcesClassifyEO>();
            Long[] ids = StringUtils.getArrayWithLong(cIds, ",");
            for (Long id : ids) {
                CmsResourcesClassifyEO cEO = new CmsResourcesClassifyEO();
                cEO.setpId(eo.getId());
                cEO.setcId(id);
                cEO.setType(CmsResourcesClassifyEO.Type.RULE.toString());
                listc.add(cEO);
            }
            resourcesClassifyService.saveEntities(listc);
        }
        return returnStr;
    }

    @Override
    public String deleteEO(Long[] ids) {
        String returnStr = "";
        for (Long id : ids) {

            CmsRelatedRuleEO eo = this.getEntity(CmsRelatedRuleEO.class, id);
            this.delete(CmsRelatedRuleEO.class, id);
            resourcesClassifyService.deleteByPid(id);

            BaseContentEO contentEO = baseContentService.getEntity(BaseContentEO.class, eo.getContentId());
            contentEO.setRecordStatus(AMockEntity.RecordStatus.Removed.toString());
            baseContentService.updateEntity(contentEO);
            if (contentEO.getIsPublish() == 1) {
                // 生成静态
                returnStr += contentEO.getSiteId() + "_" + contentEO.getColumnId() + "_" + contentEO.getId() + ",";
            }
        }

        if (!AppUtil.isEmpty(returnStr)) {//去除最后的逗号
            returnStr = returnStr.substring(0, returnStr.length() - 1);
        }
        return returnStr;
    }

    @Override
    public String publish(Long[] ids, Long publish) {
        String returnStr = "";
        Long[] contenIds = new Long[ids.length];
        int count = 0;
        for (Long id : ids) {
            CmsRelatedRuleEO relatedRuleEO = this.getEntity(CmsRelatedRuleEO.class, id);
            contenIds[count++] = relatedRuleEO.getContentId();
            BaseContentEO contentEO = baseContentService.getEntity(BaseContentEO.class, relatedRuleEO.getContentId());
            if (publish != null && publish.intValue() == 1) {
                if (contentEO.getPublishDate() == null) {
                    contentEO.setPublishDate(new Date());
                    baseContentService.updateEntity(contentEO);
                }
            }
            // 生成静态
            returnStr += contentEO.getSiteId() + "_" + contentEO.getColumnId() + "_" + contentEO.getId() + ",";

            //目前业务 ： 发布时候，状态设为发布中（2）  取消发布时候，状态设为发布中（2）
            if (publish.intValue() == 1) {
                publish = 2L;
            } else {
                publish = 2L;
            }
            baseContentService.changePublish(new ContentPageVO(null, null, Integer.parseInt(String.valueOf(publish)), contenIds, null));
            relatedRuleDao.publish(ids, publish);
        }
        if (!AppUtil.isEmpty(returnStr)) {//去除最后的逗号
            returnStr = returnStr.substring(0, returnStr.length() - 1);
        }
        return returnStr;
    }
}
