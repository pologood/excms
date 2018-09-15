package cn.lonsun.net.service.service.impl;

import cn.lonsun.activemq.MessageSenderUtil;
import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.message.internal.entity.MessageStaticEO;
import cn.lonsun.net.service.dao.IWorkGuideDao;
import cn.lonsun.net.service.entity.*;
import cn.lonsun.net.service.service.IGuideResRelatedService;
import cn.lonsun.net.service.service.IRelatedRuleService;
import cn.lonsun.net.service.service.IResourcesClassifyService;
import cn.lonsun.net.service.service.IWorkGuideService;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.solr.SolrFactory;
import cn.lonsun.solr.vo.SolrIndexVO;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.ColumnUtil;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.SysLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class WorkGuideService extends BaseService<CmsWorkGuideEO> implements IWorkGuideService {

    private static Logger logger = LoggerFactory.getLogger(WorkGuideService.class);

    @Autowired
    private IWorkGuideDao workGuideDao;

    @Autowired
    private IGuideResRelatedService guideResRelatedService;

    @Autowired
    private IResourcesClassifyService resourcesClassifyService;

    @Autowired
    private IBaseContentService baseContentService;

    @Autowired
    private IRelatedRuleService relatedRuleService;

    @Override
    public List<CmsWorkGuideEO> getEOs() {
        return workGuideDao.getEOs();
    }

    @Override
    public CmsWorkGuideEO getByContentId(Long contentId) {
        return workGuideDao.getByContentId(contentId);
    }

    @Override
    public List<CmsWorkGuideEO> getEOsByCIds(ParamDto dto, List<Long> cIds) {
        List<CmsWorkGuideEO> list = workGuideDao.getEOsByCIds(dto, cIds);
        tranferId2name(list);
        return list;
    }

    @Override
    public Pagination getPageEOsByCIds(ParamDto dto, List<Long> cIds) {
        return workGuideDao.getPageEOsByCIds(dto, cIds);
    }

    @Override
    public Pagination getPageEOs(ParamDto dto) {
        Pagination page = workGuideDao.getPageEOs(dto);
        List<CmsWorkGuideEO> list = (List<CmsWorkGuideEO>) page.getData();
        tranferId2name(list);
        return page;
    }

    @Override
    public Pagination getPageEOs(ParamDto dto, List<Long> cIds, String condition) {
        Pagination page = workGuideDao.getPageEOs(dto, cIds, condition);
        List<CmsWorkGuideEO> list = (List<CmsWorkGuideEO>) page.getData();
        tranferId2name(list);
        return page;
    }

    @Override
    public Pagination getPageEOs(ParamDto dto, Long organId, String name, String condition, String typeCode) {
        Pagination page = workGuideDao.getPageEOs(dto, organId, name, condition, typeCode);
        List<CmsWorkGuideEO> list = (List<CmsWorkGuideEO>) page.getData();
        tranferId2name(list);
        return page;
    }

    @Override
    public Pagination getPageSEOs(ParamDto dto, String organIds, String name, String condition, String typeCode) {
        return null;
    }

    private void tranferId2name(List<CmsWorkGuideEO> list) {
        for (CmsWorkGuideEO eo : list) {
            List<CmsGuideResRelatedEO> relatedEOs = CacheHandler.getList(CmsGuideResRelatedEO.class, CacheGroup.CMS_PARENTID, eo.getId());
            String tableIds = null;
            String ruleIds = null;
            String tableNames = null;
            String ruleNames = null;

            if (!AppUtil.isEmpty(eo.getOrganId())) {
                OrganEO organEO = CacheHandler.getEntity(OrganEO.class, eo.getOrganId());
                if (organEO != null) {
                    eo.setOrganName(organEO.getName());
                }
            }

            if (null == relatedEOs) {
                continue;
            }
            for (CmsGuideResRelatedEO relatedEO : relatedEOs) {
                if (relatedEO.getType().equals(CmsGuideResRelatedEO.TYPE.TABLE.toString())) {
                    if (null == tableIds && null == tableNames) {
                        CmsTableResourcesEO tableEO = CacheHandler.getEntity(CmsTableResourcesEO.class, relatedEO.getResId());
                        if (null != tableEO) {
                            tableIds = relatedEO.getResId().toString();
                            tableNames = tableEO.getName();
                            eo.getTableResourcesEOs().add(tableEO);
                        }
                    } else {
                        CmsTableResourcesEO tableEO = CacheHandler.getEntity(CmsTableResourcesEO.class, relatedEO.getResId());
                        if (null != tableEO) {
                            tableIds += "," + relatedEO.getResId().toString();
                            tableNames += "," + tableEO.getName();
                            eo.getTableResourcesEOs().add(tableEO);
                        }
                    }
                } else if (relatedEO.getType().equals(CmsGuideResRelatedEO.TYPE.RULE.toString())) {
                    if (null == ruleIds && null == ruleNames) {
//                        CmsRelatedRuleEO ruleEO = relatedRuleService.getEntity(CmsRelatedRuleEO.class,relatedEO.getResId());
                        CmsRelatedRuleEO ruleEO = CacheHandler.getEntity(CmsRelatedRuleEO.class, relatedEO.getResId());
                        if (null != ruleEO) {
                            ruleIds = relatedEO.getResId().toString();
                            ruleNames = ruleEO.getName();
                            eo.getRelatedRuleEOs().add(ruleEO);
                        }
                    } else {
//                        CmsRelatedRuleEO ruleEO = relatedRuleService.getEntity(CmsRelatedRuleEO.class,relatedEO.getResId());
                        CmsRelatedRuleEO ruleEO = CacheHandler.getEntity(CmsRelatedRuleEO.class, relatedEO.getResId());
                        if (null != ruleEO) {
                            ruleIds += "," + relatedEO.getResId().toString();
                            ruleNames += "," + ruleEO.getName();
                            eo.getRelatedRuleEOs().add(ruleEO);
                        }
                    }
                }

                eo.setTableIds(tableIds);
                eo.setTableNames(tableNames);
                eo.setRuleIds(ruleIds);
                eo.setRuleNames(ruleNames);
            }
        }
    }

    @Override
    public String saveEO(CmsWorkGuideEO eo, String cIds) {
        String returnStr = "";
        Long siteId = null != eo.getSiteId() ? eo.getSiteId() : LoginPersonUtil.getSiteId();
        eo.setSiteId(siteId);

        BaseContentEO contentEO = new BaseContentEO();
        contentEO.setTitle(eo.getName());
        contentEO.setColumnId(eo.getColumnId());
        contentEO.setSiteId(siteId);
        contentEO.setIsPublish(Integer.parseInt(String.valueOf(eo.getPublish())));
        contentEO.setRecordStatus(AMockEntity.RecordStatus.Normal.toString());
        contentEO.setTypeCode(eo.getTypeCode());
        if (contentEO.getIsPublish() == 1) {
            contentEO.setPublishDate(new Date());
        }
        //id为内容模型的ID
        Long id = baseContentService.saveEntity(contentEO);

        CacheHandler.saveOrUpdate(BaseContentEO.class, contentEO);

        eo.setContentId(id);

        //gid(办事指南的ID)
        Long gid = this.saveEntity(eo);

        if (contentEO.getIsPublish() == 1) {
            //publish(new Long[]{gid},Long.valueOf(contentEO.getIsPublish()));
            returnStr = publish(new Long[]{gid}, Long.valueOf(contentEO.getIsPublish()));
        }

        //办事指南-关联资源表单
        List<CmsGuideResRelatedEO> list = new ArrayList<CmsGuideResRelatedEO>();
        if (!AppUtil.isEmpty(eo.getTableIds())) {
            Long[] ids = StringUtils.getArrayWithLong(eo.getTableIds(), ",");
            if (!AppUtil.isEmpty(ids)) {
                for (Long rid : ids) {
                    CmsGuideResRelatedEO relatedEO = new CmsGuideResRelatedEO();
                    relatedEO.setGuideId(gid);
                    relatedEO.setResId(rid);
                    relatedEO.setType(CmsGuideResRelatedEO.TYPE.TABLE.toString());
                    list.add(relatedEO);
                }
            }
        }

        if (!AppUtil.isEmpty(eo.getRuleIds())) {
            Long[] ids = StringUtils.getArrayWithLong(eo.getRuleIds(), ",");
            if (!AppUtil.isEmpty(ids)) {
                for (Long rid : ids) {
                    CmsGuideResRelatedEO relatedEO = new CmsGuideResRelatedEO();
                    relatedEO.setGuideId(gid);
                    relatedEO.setResId(rid);
                    relatedEO.setType(CmsGuideResRelatedEO.TYPE.RULE.toString());
                    list.add(relatedEO);
                }
            }
        }

        guideResRelatedService.saveEntities(list);
        saveClassify(gid, cIds);
        CacheHandler.reload(CmsGuideResRelatedEO.class.getName());

        SysLog.log("办事指南：添加（"+contentEO.getTitle()+"），类别（"+
                ColumnUtil.getColumnName(contentEO.getColumnId(),contentEO.getSiteId())+"）" ,
                "CmsWorkGuideEO", CmsLogEO.Operation.Add.toString());

        return returnStr;
    }

    @Override
    public void saveClassify(Long pid, String cIds) {
        //pid
        resourcesClassifyService.deleteByPid(pid);

        if (!AppUtil.isEmpty(cIds)) {
            List<CmsResourcesClassifyEO> listc = new ArrayList<CmsResourcesClassifyEO>();
            Long[] ids = StringUtils.getArrayWithLong(cIds, ",");
            for (Long id : ids) {
                CmsResourcesClassifyEO cEO = new CmsResourcesClassifyEO();
                cEO.setpId(pid);
                cEO.setcId(id);
                cEO.setType(CmsResourcesClassifyEO.Type.TABLE.toString());
                listc.add(cEO);
            }
            resourcesClassifyService.saveEntities(listc);
        }
    }

    @Override
    public String updateEO(CmsWorkGuideEO eo, String cIds) {
        String returnStr = "";
        Long siteId = LoginPersonUtil.getSiteId();
        eo.setSiteId(siteId);
        this.updateEntity(eo);

        BaseContentEO contentEO = baseContentService.getEntity(BaseContentEO.class, eo.getContentId());
        contentEO.setTitle(eo.getName());
        contentEO.setColumnId(eo.getColumnId());
        contentEO.setSiteId(siteId);
        contentEO.setIsPublish(Integer.parseInt(String.valueOf(eo.getPublish())));

        if (contentEO.getPublishDate() == null && contentEO.getIsPublish() == 1) {
            contentEO.setPublishDate(new Date());
        }

        contentEO.setRecordStatus(AMockEntity.RecordStatus.Normal.toString());
        contentEO.setTypeCode(eo.getTypeCode());
        baseContentService.updateEntity(contentEO);
        CacheHandler.saveOrUpdate(BaseContentEO.class, contentEO);

        guideResRelatedService.deleteByGID(eo.getId());
        List<CmsGuideResRelatedEO> list = new ArrayList<CmsGuideResRelatedEO>();
        if (!AppUtil.isEmpty(eo.getTableIds())) {
            Long[] ids = StringUtils.getArrayWithLong(eo.getTableIds(), ",");
            if (!AppUtil.isEmpty(ids)) {
                for (Long rid : ids) {
                    CmsGuideResRelatedEO relatedEO = new CmsGuideResRelatedEO();
                    relatedEO.setGuideId(eo.getId());
                    relatedEO.setResId(rid);
                    relatedEO.setType(CmsGuideResRelatedEO.TYPE.TABLE.toString());
                    list.add(relatedEO);
                }
            }
        }

        if (!AppUtil.isEmpty(eo.getRuleIds())) {
            Long[] ids = StringUtils.getArrayWithLong(eo.getRuleIds(), ",");
            if (!AppUtil.isEmpty(ids)) {
                for (Long rid : ids) {
                    CmsGuideResRelatedEO relatedEO = new CmsGuideResRelatedEO();
                    relatedEO.setGuideId(eo.getId());
                    relatedEO.setResId(rid);
                    relatedEO.setType(CmsGuideResRelatedEO.TYPE.RULE.toString());
                    list.add(relatedEO);
                }
            }
        }

        guideResRelatedService.saveEntities(list);
        saveClassify(eo.getId(), cIds);
        //publish(new Long[]{eo.getId()},Long.valueOf(contentEO.getIsPublish()));
        returnStr = publish(new Long[]{eo.getId()}, Long.valueOf(contentEO.getIsPublish()));
        CacheHandler.reload(CmsGuideResRelatedEO.class.getName());

        SysLog.log("办事指南：修改（"+contentEO.getTitle()+"），类别（"+
                        ColumnUtil.getColumnName(contentEO.getColumnId(),contentEO.getSiteId())+"）" ,
                "CmsWorkGuideEO", CmsLogEO.Operation.Update.toString());

        return returnStr;
    }

    @Override
    public String deleteEO(Long[] ids) {
        String returnStr = "";
        String optType="删除";
        if(ids!=null&&ids.length>1){
            optType = "批量删除";
        }
        for (Long id : ids) {
            CmsWorkGuideEO guideEO = this.getEntity(CmsWorkGuideEO.class, id);
            BaseContentEO contentEO = baseContentService.getEntity(BaseContentEO.class, guideEO.getContentId());
            contentEO.setRecordStatus(AMockEntity.RecordStatus.Removed.toString());
            baseContentService.updateEntity(contentEO);
            CacheHandler.delete(BaseContentEO.class, contentEO);

            if (contentEO.getIsPublish() == 1) {
                // 生成静态
                returnStr += contentEO.getSiteId() + "_" + contentEO.getColumnId() + "_" + contentEO.getId() + ",";
            }

            SysLog.log("办事指南："+optType+"（"+contentEO.getTitle()+"），类别（"+
                            ColumnUtil.getColumnName(contentEO.getColumnId(),contentEO.getSiteId())+"）" ,
                    "CmsWorkGuideEO", CmsLogEO.Operation.Update.toString());
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
        String optType="";
        if(ids!=null&&ids.length>1){
            optType = "批量";
        }

        String publisStr;
        if (publish != null && publish.intValue() == 1) {
            publisStr = "发布";
        }else{
            publisStr = "取消发布";
        }

        int count = 0;
        for (Long id : ids) {
            CmsWorkGuideEO guideEO = this.getEntity(CmsWorkGuideEO.class, id);
            contenIds[count++] = guideEO.getContentId();
            BaseContentEO contentEO = baseContentService.getEntity(BaseContentEO.class, guideEO.getContentId());
            if (publish != null && publish.intValue() == 1) {
                if (contentEO.getPublishDate() == null) {
                    contentEO.setPublishDate(new Date());
                    baseContentService.updateEntity(contentEO);
                }
            }
            SysLog.log("办事指南："+optType+publisStr+"（"+contentEO.getTitle()+"），类别（"+
                            ColumnUtil.getColumnName(contentEO.getColumnId(),contentEO.getSiteId())+"）" ,
                    "CmsWorkGuideEO", CmsLogEO.Operation.Update.toString());
            // 生成静态
            returnStr += contentEO.getSiteId() + "_" + contentEO.getColumnId() + "_" + contentEO.getId() + ",";
        }
        //目前业务 ： 发布时候，状态设为发布中（2）  取消发布时候，状态设为发布中（2）
        if (publish.intValue() == 1) {
            publish = 2L;
        } else {
            publish = 2L;
        }
        baseContentService.changePublish(new ContentPageVO(null, null, Integer.parseInt(String.valueOf(publish)), contenIds, null));
        workGuideDao.publish(ids, publish);

        if (!AppUtil.isEmpty(returnStr)) {//去除最后的逗号
            returnStr = returnStr.substring(0, returnStr.length() - 1);
        }
        return returnStr;
    }
}
