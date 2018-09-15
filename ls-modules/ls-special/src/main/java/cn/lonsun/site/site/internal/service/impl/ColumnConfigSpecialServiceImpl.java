package cn.lonsun.site.site.internal.service.impl;

import cn.lonsun.base.anno.DbInject;
import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentSpecialService;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.mongodb.entity.ContentMongoEO;
import cn.lonsun.mongodb.service.ContentMongoServiceImpl;
import cn.lonsun.rbac.indicator.service.IIndicatorSpecialService;
import cn.lonsun.site.contentModel.internal.entity.ModelTemplateEO;
import cn.lonsun.site.site.internal.dao.IColumnConfigSpecialDao;
import cn.lonsun.site.site.internal.entity.ColumnConfigEO;
import cn.lonsun.site.site.internal.entity.ColumnConfigRelEO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.service.IColumnConfigRelSpecialService;
import cn.lonsun.site.site.internal.service.IColumnConfigSpecialService;
import cn.lonsun.solr.SolrFactory;
import cn.lonsun.solr.vo.SolrIndexVO;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.HtmlUtil;
import cn.lonsun.util.ModelConfigUtil;
import cn.lonsun.util.SysLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

import static cn.lonsun.util.ColumnRelUtil.getByIndicatorId;

/**
 * Created by Administrator on 2017/9/26.
 */
@Service
public class ColumnConfigSpecialServiceImpl extends MockService<ColumnConfigEO> implements IColumnConfigSpecialService {
    @DbInject("columnConfigSpecial")
    private IColumnConfigSpecialDao configSpecialDao;
    @Resource
    private IBaseContentSpecialService baseContentSpecialService;
    @Resource
    private IColumnConfigRelSpecialService relSpecialService;

    @Resource
    private IIndicatorSpecialService indicatorSpecialService;

    private static ContentMongoServiceImpl fileServer = SpringContextHolder.getBean("contentMongoServiceImpl");

    /**
     * 栏目管理：保存
     *
     * @param columnVO
     */
    @Override
    public Long saveEO(ColumnMgrEO columnVO) {
        Long id = null;
        if (IndicatorEO.Type.CMS_Section.toString().equals(columnVO.getType())) {
            id = saveEO1(columnVO);
        } else if (IndicatorEO.Type.COM_Section.toString().equals(columnVO.getType())) {
            id = saveEO2(columnVO);
        }
        ColumnConfigEO columnConfigEO = getEntity(ColumnConfigEO.class, columnVO.getColumnConfigId());
        if (null != columnConfigEO) {
            if (null != columnVO.getIsShow() && columnVO.getIsShow() != columnConfigEO.getIsShow()) {
                handleIndex4ColumnIsShow(columnVO, id);
            }
        }
        return id;
    }
    /**
     * 获取序号
     *
     * @param parentId
     * @return
     */
    @Override
    public Integer getNewSortNum(Long parentId, boolean isCom) {
        return configSpecialDao.getNewSortNum(parentId, isCom);
    }

    @Override
    public List<ColumnMgrEO> getColumnByContentModelCode(Long siteId, String code) {
        if (AppUtil.isEmpty(code)) {
            return null;
        }
        List<ColumnMgrEO> list = configSpecialDao.getColumnByContentModelCode(siteId, code);
        return list;
    }

    @Override
    public List<ColumnMgrEO> getAllColumnBySite(Long columnId) {
        return configSpecialDao.getAllColumnBySite(columnId);
    }

    @Override
    public void deleteComEO(Long indicatorId) {
        IndicatorEO eo = CacheHandler.getEntity(IndicatorEO.class, indicatorId);
        if (eo == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "该栏目不存在");
        }
        // 获取父栏目
        IndicatorEO pEO = null;
        if (eo.getParentId() != null) {
            pEO = CacheHandler.getEntity(IndicatorEO.class, eo.getParentId());
        }
        // 配置类
        ColumnConfigEO columnConfigEO = CacheHandler.getEntity(ColumnConfigEO.class, CacheGroup.CMS_INDICATORID, indicatorId);
        if (columnConfigEO != null) {
            delete(ColumnConfigEO.class, columnConfigEO.getColumnConfigId());

        }
        indicatorSpecialService.delete(indicatorId);
        SysLog.log("【站群管理】删除栏目，名称：" + eo.getName(), "IndicatorId", CmsLogEO.Operation.Delete.toString());
        // 如果父节点没有了子节点，将isParent设为0
        if (pEO != null) {
            List<IndicatorEO> list = CacheHandler.getList(IndicatorEO.class, CacheGroup.CMS_PARENTID, pEO.getIndicatorId());
            if (list == null || list.size() == 0) {
                pEO.setIsParent(0);
                indicatorSpecialService.save(pEO);
            } else {
                if (list.size() == 1 && list.get(0).getIndicatorId().equals(indicatorId)) {
                    pEO.setIsParent(0);
                    indicatorSpecialService.save(pEO);
                }
            }
        }
    }

    /**
     * 保存栏目配置时对之下的文章页索引处理
     *
     * @param columnVO
     * @param columnId
     */
    private void handleIndex4ColumnIsShow(ColumnMgrEO columnVO, Long columnId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("siteId", columnVO.getSiteId());
        params.put("columnId", columnId);
        params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        //判断isshow 1则新建该栏目下所有文章页索引，0删除该栏目下所有文章页索引
        if (null != columnVO.getIsShow() && columnVO.getIsShow() == 0) {//前台不显示
            try {
                //删除该栏目下文章页的索引
                SolrFactory.deleteIndex(columnId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (null != columnVO.getIsShow() && columnVO.getIsShow() == 1) {//前台显示
            List<BaseContentEO> list = baseContentSpecialService.getEntities(BaseContentEO.class, params);
            Set<SolrIndexVO> vos = new HashSet<SolrIndexVO>();
            for (BaseContentEO eo : list) {
                ContentMongoEO contentMongoEO = fileServer.queryById(eo.getId());
                SolrIndexVO vo = new SolrIndexVO();
                if (null != eo.getSiteId()) {
                    vo.setId(eo.getId() + "");
                    vo.setColumnId(eo.getColumnId());
                    vo.setRemark(eo.getRemarks());
                    vo.setTitle(eo.getTitle());
                    if (null != contentMongoEO) {
                        vo.setContent(contentMongoEO.getContent());
                    } else {
                        vo.setContent(HtmlUtil.getTextFromTHML(eo.getArticle()));
                    }
                    vo.setSiteId(eo.getSiteId());
                    vo.setTypeCode(eo.getTypeCode());
                    vo.setCreateDate(eo.getCreateDate());
                    if (null != eo.getRedirectLink()) {
                        vo.setUrl(eo.getRedirectLink());
                    }
                    vos.add(vo);
                }
            }
            //先删除再创建
            try {
                SolrFactory.deleteIndex(columnId);
                SolrFactory.createIndex(vos);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 栏目管理：保存公共栏目
     *
     * @param columnVO
     * @return
     */
    private Long saveEO2(ColumnMgrEO columnVO) {
        if (columnVO.getIndicatorId() == null || columnVO.getColumnConfigId() == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "参数出错");
        } else {
            // 修改
            ColumnConfigRelEO relEO = getByIndicatorId(columnVO.getIndicatorId(), columnVO.getSiteId());
            if (relEO != null) {
                relEO.setIndicatorId(columnVO.getIndicatorId());
                relEO.setName(columnVO.getName());
                relEO.setSortNum(columnVO.getSortNum());
                relEO.setContentModelCode(columnVO.getContentModelCode());
                relEO.setSiteId(columnVO.getSiteId());
                if (StringUtils.isEmpty(columnVO.getColumnTypeCode())) {
                    List<ModelTemplateEO> list1 = ModelConfigUtil.getTemplateListByCode(columnVO.getContentModelCode(), columnVO.getSiteId());// 根据栏目Id获取默认的模板信息
                    if (list1 != null && list1.size() > 0) {
                        relEO.setColumnTypeCode(list1.get(0).getModelTypeCode());
                    }
                } else {
                    relEO.setColumnTypeCode(columnVO.getColumnTypeCode());
                }
                if (!StringUtils.isEmpty(relEO.getColumnTypeCode())) {
                    if (BaseContentEO.TypeCode.articleNews.toString().equals(relEO.getColumnTypeCode())) {
                        relEO.setGenePageIds(columnVO.getGenePageIds());
                        relEO.setGenePageNames(columnVO.getGenePageNames());
                        relEO.setSynColumnIds(columnVO.getSynColumnIds());
                        relEO.setSynColumnNames(columnVO.getSynColumnNames());
                    } else if (BaseContentEO.TypeCode.linksMgr.toString().equals(relEO.getColumnTypeCode())) {
                        relEO.setIsLogo(columnVO.getIsLogo());
                        relEO.setNum(columnVO.getNum());
                        if (columnVO.getIsLogo() == 1) {
                            relEO.setHeight(columnVO.getHeight());
                            relEO.setWidth(columnVO.getWidth());
                        }
                    }
                }
                relEO.setKeyWords(columnVO.getKeyWords());
                relEO.setDescription(columnVO.getDescription());
                relEO.setIsShow(columnVO.getIsShow());
                relEO.setIsStartUrl(columnVO.getIsStartUrl());
                if (columnVO.getIsStartUrl() == 1) {
                    relEO.setTransUrl(columnVO.getTransUrl());
                    relEO.setTransWindow(columnVO.getTransWindow());
                }
                relSpecialService.updateEntity(relEO);
            } else {
                // 添加
                relEO = new ColumnConfigRelEO();
                relEO.setIndicatorId(columnVO.getIndicatorId());
                relEO.setName(columnVO.getName());
                relEO.setSortNum(columnVO.getSortNum());
                relEO.setContentModelCode(columnVO.getContentModelCode());
                relEO.setSiteId(columnVO.getSiteId());
                relEO.setIsParent(columnVO.getIsParent());
                if (StringUtils.isEmpty(columnVO.getColumnTypeCode())) {
                    // 获取模板模型
                    List<ModelTemplateEO> list1 = ModelConfigUtil.getTemplateListByCode(columnVO.getContentModelCode(), columnVO.getSiteId());
                    if (list1 != null && list1.size() > 0) {
                        relEO.setColumnTypeCode(list1.get(0).getModelTypeCode());
                    }
                } else {
                    relEO.setColumnTypeCode(columnVO.getColumnTypeCode());
                }
                if (!StringUtils.isEmpty(relEO.getColumnTypeCode())) {
                    //如果是文章新闻类
                    if (BaseContentEO.TypeCode.articleNews.toString().equals(relEO.getColumnTypeCode())) {
                        relEO.setGenePageIds(columnVO.getGenePageIds());
                        relEO.setGenePageNames(columnVO.getGenePageNames());
                        relEO.setSynColumnIds(columnVO.getSynColumnIds());
                        relEO.setSynColumnNames(columnVO.getSynColumnNames());
                        //如果是链接管理类型
                    } else if (BaseContentEO.TypeCode.linksMgr.toString().equals(relEO.getColumnTypeCode())) {
                        relEO.setIsLogo(columnVO.getIsLogo());
                        relEO.setNum(columnVO.getNum());
                        if (columnVO.getIsLogo() == 1) {
                            relEO.setHeight(columnVO.getHeight());
                            relEO.setWidth(columnVO.getWidth());
                        }
                    }
                }
                relEO.setKeyWords(columnVO.getKeyWords());
                relEO.setDescription(columnVO.getDescription());
                relEO.setIsShow(columnVO.getIsShow());
                relEO.setIsStartUrl(columnVO.getIsStartUrl());
                if (columnVO.getIsStartUrl() == 1) {
                    relEO.setTransUrl(columnVO.getTransUrl());
                    relEO.setTransWindow(columnVO.getTransWindow());
                }
                relSpecialService.saveEntity(relEO);
            }
        }
        return columnVO.getIndicatorId();
    }

    /**
     * 保存自建栏目
     *
     * @param columnVO
     */
    public Long saveEO1(ColumnMgrEO columnVO) {
        // 修改上级属性
        Long parentId = columnVO.getParentId();
        //IndicatorEO pindicatorEO = CacheHandler.getEntity(IndicatorEO.class, parentId);
        IndicatorEO pindicatorEO = indicatorSpecialService.getEntity(IndicatorEO.class, parentId);
        if (pindicatorEO != null && IndicatorEO.Type.COM_Section.toString().equals(pindicatorEO.getType())) {
            ColumnConfigRelEO relEO = getByIndicatorId(parentId, columnVO.getSiteId());
            if (relEO != null && relEO.getIsParent() == 0) {
                relEO.setIsParent(1);
                relSpecialService.updateEntity(relEO);
            } else if (relEO == null) {
                relEO = new ColumnConfigRelEO();
                relEO.setIsParent(1);
                relEO.setIndicatorId(parentId);
                relEO.setName(pindicatorEO.getName());
                relEO.setColumnTypeCode(BaseContentEO.TypeCode.articleNews.toString());
                relEO.setSortNum(pindicatorEO.getSortNum());
                relEO.setSiteId(columnVO.getSiteId());
                relSpecialService.saveEntity(relEO);
            }
        } else {
            if (pindicatorEO != null && pindicatorEO.getIsParent() == 0) {
                pindicatorEO.setIsParent(1);
                indicatorSpecialService.save(pindicatorEO);
            }
        }
        // 新增子栏目
        IndicatorEO indicatorEO = new IndicatorEO();
        AppUtil.copyProperties(indicatorEO, columnVO);
        indicatorEO.setType(IndicatorEO.Type.CMS_Section.toString());
        indicatorEO.setRecordStatus(AMockEntity.RecordStatus.Normal.toString());
        indicatorSpecialService.save(indicatorEO);
        CacheHandler.saveOrUpdate(IndicatorEO.class, indicatorEO);
        // 栏目配置信息
        ColumnConfigEO configEO = new ColumnConfigEO();
        //String oldColumnTypeCode = "";
        if (columnVO.getColumnConfigId() != null) {
            configEO = CacheHandler.getEntity(ColumnConfigEO.class, columnVO.getColumnConfigId());
            //   oldColumnTypeCode = configEO.getColumnTypeCode();
        }
        configEO.setIndicatorId(indicatorEO.getIndicatorId());
        if (StringUtils.isEmpty(columnVO.getColumnTypeCode())) {
            // 获取模板模型
            List<ModelTemplateEO> list1 = ModelConfigUtil.getTemplateListByCode(columnVO.getContentModelCode(), columnVO.getSiteId());
            if (list1 != null && list1.size() > 0) {
                configEO.setColumnTypeCode(list1.get(0).getModelTypeCode());
            }
        } else {
            configEO.setColumnTypeCode(columnVO.getColumnTypeCode());
        }
        if(!StringUtils.isEmpty(columnVO.getContentModelCode())&& !StringUtils.isEmpty(configEO.getContentModelCode())&&
                columnVO.getContentModelCode()!=configEO.getContentModelCode()){
            List<ModelTemplateEO> list1 = ModelConfigUtil.getTemplateListByCode(columnVO.getContentModelCode(), columnVO.getSiteId());
            if (list1 != null && list1.size() > 0) {
                configEO.setColumnTypeCode(list1.get(0).getModelTypeCode());
            }
        }
        configEO.setContentModelCode(columnVO.getContentModelCode());
        if (!StringUtils.isEmpty(configEO.getColumnTypeCode())) {
            //如果是文章新闻类
            if (BaseContentEO.TypeCode.articleNews.toString().equals(configEO.getColumnTypeCode())) {
                configEO.setGenePageIds(columnVO.getGenePageIds());
                configEO.setGenePageNames(columnVO.getGenePageNames());
                configEO.setSynColumnIds(columnVO.getSynColumnIds());
                configEO.setSynColumnNames(columnVO.getSynColumnNames());
                //如果是链接管理类型
            } else if (BaseContentEO.TypeCode.linksMgr.toString().equals(configEO.getColumnTypeCode())) {
                configEO.setIsLogo(columnVO.getIsLogo());
                configEO.setNum(columnVO.getNum());
                configEO.setLinkCode(columnVO.getLinkCode());
                configEO.setTitleCount(columnVO.getTitleCount());
                configEO.setRemarksCount(columnVO.getRemarksCount());
                if (columnVO.getIsLogo() == 1) {
                    configEO.setHeight(columnVO.getHeight());
                    configEO.setWidth(columnVO.getWidth());
                }
            }
        }
//        configEO.setIsOA(columnVO.getIsOA());
        configEO.setIsShow(columnVO.getIsShow());
        configEO.setKeyWords(columnVO.getKeyWords());
        configEO.setDescription(columnVO.getDescription());
        configEO.setIsStartUrl(columnVO.getIsStartUrl());
        //工作流配置
        configEO.setProcessId(columnVO.getProcessId());
        configEO.setProcessName(columnVO.getProcessName());
        if (columnVO.getIsStartUrl() == 1) {
            configEO.setContentModelCode(null);
            configEO.setTransUrl(columnVO.getTransUrl());
            configEO.setTransWindow(columnVO.getTransWindow());
        }
        if (columnVO.getColumnConfigId() == null) {
            saveEntity(configEO);
            SysLog.log("【站群管理】新增栏目，名称：" + indicatorEO.getName(), "ColumnConfigEO", CmsLogEO.Operation.Add.toString());
        } else {
            updateEntity(configEO);
            SysLog.log("【站群管理】修改栏目，名称：" + indicatorEO.getName(), "ColumnConfigEO", CmsLogEO.Operation.Update.toString());
        }
        CacheHandler.saveOrUpdate(ColumnConfigEO.class, configEO);
        return configEO.getIndicatorId();
    }
}
