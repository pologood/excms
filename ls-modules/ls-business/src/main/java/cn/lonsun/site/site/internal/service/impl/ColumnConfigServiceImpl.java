package cn.lonsun.site.site.internal.service.impl;

import cn.lonsun.activemq.MessageSender;
import cn.lonsun.base.anno.DbInject;
import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.message.internal.entity.MessageStaticEO;
import cn.lonsun.site.contentModel.internal.entity.ContentModelEO;
import cn.lonsun.site.contentModel.internal.entity.ModelTemplateEO;
import cn.lonsun.site.contentModel.vo.ContentModelVO;
import cn.lonsun.site.site.internal.dao.IColumnConfigDao;
import cn.lonsun.site.site.internal.entity.*;
import cn.lonsun.site.site.internal.service.IColumnConfigRelService;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import cn.lonsun.site.site.vo.ColumnVO;
import cn.lonsun.solr.SolrBaseIndexUtil;
import cn.lonsun.system.role.internal.service.ISiteRightsService;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.ColumnRelUtil;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.ModelConfigUtil;
import cn.lonsun.util.SysLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

import static cn.lonsun.cache.client.CacheHandler.getList;
import static cn.lonsun.util.ColumnRelUtil.getByIndicatorId;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-8-24 <br/>
 */
@Service("columnConfigService")
public class ColumnConfigServiceImpl extends MockService<ColumnConfigEO> implements IColumnConfigService {

    @DbInject("columnConfig")
    private IColumnConfigDao configDao;

    @Resource(name = "ex_8_IndicatorServiceImpl")
    private cn.lonsun.rbac.indicator.service.IIndicatorService indicatorService;

    @Autowired
    private IColumnConfigRelService relService;

    @Autowired
    private ISiteRightsService siteRightsService;


    /**
     * 校验一个站点下栏目名称是否重复
     *
     * @param name
     * @param parentId
     * @param indicatorId
     * @return
     */
    @Override
    public boolean checkColumnNameExist(String name, Long parentId, Long indicatorId) {
        Map<String, Object> map = new HashMap<String, Object>();
        String name1 = name.trim();// 去掉空格
        map.put("name", name1);
        map.put("parentId", parentId);
        map.put("type", IndicatorEO.Type.CMS_Section.toString());
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        List<IndicatorEO> list = indicatorService.getEntities(IndicatorEO.class, map);
        if (list == null || list.size() == 0) {
            return true;
        }
        IndicatorEO eo = list.get(0);
        if (eo.getIndicatorId().equals(indicatorId)) {
            return true;
        }
        return false;
    }

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
                SolrBaseIndexUtil.handleIndex4ColumnIsShow(columnVO, id);
            }
        }
        CacheHandler.reload("ColumnMgrEO");
        CacheHandler.reload("IndicatorEO");
        return id;
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
                relEO.setIsToFile(columnVO.getIsToFile());
                relEO.setToFileDate(columnVO.getToFileDate());
                relEO.setToFileId(columnVO.getToFileId());
                if (columnVO.getIsStartUrl() == 1) {
                    relEO.setTransUrl(columnVO.getTransUrl());
                    relEO.setTransWindow(columnVO.getTransWindow());
                }
                relService.updateEntity(relEO);
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
                relEO.setIsToFile(columnVO.getIsToFile());
                relEO.setToFileDate(columnVO.getToFileDate());
                relEO.setToFileId(columnVO.getToFileId());
                if (columnVO.getIsStartUrl() == 1) {
                    relEO.setTransUrl(columnVO.getTransUrl());
                    relEO.setTransWindow(columnVO.getTransWindow());
                }
                relService.saveEntity(relEO);
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
        IndicatorEO pindicatorEO = indicatorService.getEntity(IndicatorEO.class, parentId);
        if (pindicatorEO != null && IndicatorEO.Type.COM_Section.toString().equals(pindicatorEO.getType())) {
            ColumnConfigRelEO relEO = getByIndicatorId(parentId, columnVO.getSiteId());
            if (relEO != null && relEO.getIsParent() == 0) {
                relEO.setIsParent(1);
                relService.updateEntity(relEO);
            } else if (relEO == null) {
                relEO = new ColumnConfigRelEO();
                relEO.setIsParent(1);
                relEO.setIndicatorId(parentId);
                relEO.setName(pindicatorEO.getName());
                relEO.setColumnTypeCode(BaseContentEO.TypeCode.articleNews.toString());
                relEO.setSortNum(pindicatorEO.getSortNum());
                relEO.setSiteId(columnVO.getSiteId());
                relEO.setIsToFile(columnVO.getIsToFile());
                relEO.setToFileDate(columnVO.getToFileDate());
                relEO.setToFileId(columnVO.getToFileId());
                relService.saveEntity(relEO);
            }
        } else {
            if (pindicatorEO != null && pindicatorEO.getIsParent() == 0) {
                pindicatorEO.setIsParent(1);
                indicatorService.save(pindicatorEO);
            }
        }
        // 新增子栏目
        IndicatorEO indicatorEO = new IndicatorEO();
        AppUtil.copyProperties(indicatorEO, columnVO);
        indicatorEO.setType(IndicatorEO.Type.CMS_Section.toString());
        indicatorEO.setRecordStatus(AMockEntity.RecordStatus.Normal.toString());
        indicatorService.save(indicatorEO);
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
                configEO.setReferColumnIds(columnVO.getReferColumnIds());
                configEO.setReferColumnNames(columnVO.getReferColumnNames());
                configEO.setReferOrganCatIds(columnVO.getReferOrganCatIds());
                configEO.setReferOrganCatNames(columnVO.getReferOrganCatNames());
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
        configEO.setIsToFile(columnVO.getIsToFile());
        configEO.setToFileDate(columnVO.getToFileDate());
        configEO.setToFileId(columnVO.getToFileId());
        configEO.setUpdateCycle(columnVO.getUpdateCycle());//更新周期
        configEO.setYellowCardWarning(columnVO.getYellowCardWarning());////黄牌警示天数
        configEO.setRedCardWarning(columnVO.getRedCardWarning());//红牌警示天数
        configEO.setColumnClassCode(columnVO.getColumnClassCode());
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

    /**
     * 保存导入的栏目，返回栏目信息实体类
     *
     * @param columnVO
     * @return
     */
    private ColumnMgrEO saveColumnEO(ColumnMgrEO columnVO) {
        // 修改上级属性
        Long parentId = columnVO.getParentId();
        IndicatorEO pindicatorEO = CacheHandler.getEntity(IndicatorEO.class, parentId);
        if (pindicatorEO != null && pindicatorEO.getIsParent() == 0) {
            pindicatorEO.setIsParent(1);
            indicatorService.updateEntity(pindicatorEO);
            CacheHandler.saveOrUpdate(IndicatorEO.class, pindicatorEO);
        }
        // 新增子栏目
        IndicatorEO indicatorEO = new IndicatorEO();
        AppUtil.copyProperties(indicatorEO, columnVO);
        indicatorEO.setType(IndicatorEO.Type.CMS_Section.toString());
        indicatorEO.setRecordStatus(AMockEntity.RecordStatus.Normal.toString());
        indicatorService.save(indicatorEO);
        CacheHandler.saveOrUpdate(IndicatorEO.class, indicatorEO);

        // 栏目配置信息
        ColumnConfigEO configEO = new ColumnConfigEO();
        configEO.setIndicatorId(indicatorEO.getIndicatorId());
        configEO.setColumnTypeCode(columnVO.getColumnTypeCode());
        configEO.setContentModelCode(columnVO.getContentModelCode());
        configEO.setKeyWords(columnVO.getKeyWords());
        configEO.setDescription(columnVO.getDescription());
        saveEntity(configEO);
        SysLog.log("【站群管理】新增栏目，名称：" + indicatorEO.getName(), "ColumnConfigEO", CmsLogEO.Operation.Add.toString());
        CacheHandler.saveOrUpdate(ColumnConfigEO.class, configEO);
        ColumnMgrEO eo = new ColumnMgrEO();
        eo.setIndicatorId(configEO.getIndicatorId());
        eo.setColumnConfigId(configEO.getColumnConfigId());

        return eo;
    }

    /**
     * 删除
     *
     * @param indicatorId
     */
    @Override
    public void deleteEO(Long indicatorId) {
        // 本栏目
        IndicatorEO eo = CacheHandler.getEntity(IndicatorEO.class, indicatorId);
        if (eo == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "该栏目不存在");
        }
        //如果是标准栏目
        if (IndicatorEO.Type.CMS_Section.toString().equals(eo.getType())) {
            // 父栏目
            IndicatorEO pEO = CacheHandler.getEntity(IndicatorEO.class, eo.getParentId());
            // 配置类
            ColumnConfigEO columnConfigEO = CacheHandler.getEntity(ColumnConfigEO.class, CacheGroup.CMS_INDICATORID, indicatorId);
            if (columnConfigEO != null) {
                delete(ColumnConfigEO.class, columnConfigEO.getColumnConfigId());

            }
            indicatorService.delete(indicatorId);
            SysLog.log("【站群管理】删除栏目，名称：" + eo.getName(), "IndicatorId", CmsLogEO.Operation.Delete.toString());
            // 如果父节点没有了子节点，将isParent设为0
            if (pEO != null) {
                List<IndicatorEO> list = CacheHandler.getList(IndicatorEO.class, CacheGroup.CMS_PARENTID, pEO.getIndicatorId());
                if (list == null || list.size() == 0) {
                    pEO.setIsParent(0);
                    indicatorService.save(pEO);
                } else {
                    if (list.size() == 1 && list.get(0).getIndicatorId().equals(indicatorId)) {
                        pEO.setIsParent(0);
                        indicatorService.save(pEO);
                    }
                }
            }
            //如果是虚拟栏目
        } else {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("indicatorId", indicatorId);
            map.put("siteId", LoginPersonUtil.getSiteId());
            map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
            List<ColumnConfigRelEO> list = relService.getEntities(ColumnConfigRelEO.class, map);
            if (list != null && list.size() > 0) {
                ColumnConfigRelEO relEO = list.get(0);
                relEO.setIsHide(true);
                relService.updateEntity(relEO);
            } else {
                ColumnConfigRelEO relEO = new ColumnConfigRelEO();
                relEO.setIsParent(1);
                relEO.setIndicatorId(indicatorId);
                relEO.setSiteId(LoginPersonUtil.getSiteId());
                relEO.setSortNum(eo.getSortNum());
                relEO.setName(eo.getName());
                ColumnMgrEO mgrEO = CacheHandler.getEntity(ColumnMgrEO.class, indicatorId);
                if (mgrEO != null) {
                    relEO.setColumnTypeCode(mgrEO.getColumnTypeCode());
                    relEO.setContentModelCode(mgrEO.getContentModelCode());
                    relEO.setSynColumnIds(mgrEO.getSynColumnIds());
                    relEO.setSynColumnNames(mgrEO.getSynColumnNames());
                    relEO.setGenePageIds(mgrEO.getGenePageIds());
                    relEO.setGenePageNames(mgrEO.getGenePageNames());
                    relEO.setKeyWords(mgrEO.getKeyWords());
                    relEO.setDescription(mgrEO.getDescription());
                    relEO.setTransUrl(mgrEO.getTransUrl());
                    relEO.setTransWindow(mgrEO.getTransWindow());
                    relEO.setIsStartUrl(mgrEO.getIsStartUrl());
                    relEO.setIsShow(mgrEO.getIsShow());
                    relEO.setIsHide(true);
                    relService.saveEntity(relEO);
                }
            }
        }
        CacheHandler.reload("ColumnMgrEO");
        CacheHandler.reload("IndicatorEO");
        MessageStaticEO msg = new MessageStaticEO(LoginPersonUtil.getSiteId(), eo.getIndicatorId(), null);
        msg.setType(MessageEnum.UNPUBLISH.value());
        msg.setTodb(false);
        msg.setUserId(LoginPersonUtil.getUserId());
        MessageSender.sendMessage(msg);
    }

    /**
     * 删除公共栏目
     *
     * @param indicatorId
     */
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
        indicatorService.delete(indicatorId);
        SysLog.log("【站群管理】删除栏目，名称：" + eo.getName(), "IndicatorId", CmsLogEO.Operation.Delete.toString());
        // 如果父节点没有了子节点，将isParent设为0
        if (pEO != null) {
            List<IndicatorEO> list = CacheHandler.getList(IndicatorEO.class, CacheGroup.CMS_PARENTID, pEO.getIndicatorId());
            if (list == null || list.size() == 0) {
                pEO.setIsParent(0);
                indicatorService.save(pEO);
            } else {
                if (list.size() == 1 && list.get(0).getIndicatorId().equals(indicatorId)) {
                    pEO.setIsParent(0);
                    indicatorService.save(pEO);
                }
            }
        }
        CacheHandler.reload("ColumnMgrEO");
        CacheHandler.reload("IndicatorEO");
    }

    /**
     * 根据父栏目获取所有子栏目 flag:true（本站点及站点为空的）
     *
     * @param indicatorId
     * @param flag
     * @return
     */
    @Override
    public List<ColumnMgrEO> getColumnByParentId(Long indicatorId, boolean flag) {
        return configDao.getColumnByParentId(indicatorId, flag, null);
    }

    /**
     * 获取某个栏目下所有的栏目
     *
     * @param columnId
     * @return
     */
    @Override
    public List<ColumnMgrEO> getAllColumnBySite(Long columnId) {
        return configDao.getAllColumnBySite(columnId);
    }

    /**
     * 查询某个站点下内容模型code值为code的所有栏目
     *
     * @param siteId
     * @param code
     * @return
     */
    @Override
    public List<ColumnMgrEO> getColumnByContentModelCode(Long siteId, String code) {
        if (AppUtil.isEmpty(code)) {
            return null;
        }
        List<ColumnMgrEO> list = configDao.getColumnByContentModelCode(siteId, code);
        return list;
    }

    /**
     * 获取栏目类型为columnTypeCode的所有栏目
     *
     * @param columnTypeCode
     * @return
     */
    @Override
    public List<ColumnMgrEO> getAllTree(String columnTypeCode) {
        //获取当前用户拥有的站点权限
        Long[] siteIds = siteRightsService.getCurUserSiteIds();
        List<ColumnMgrEO> list = new ArrayList<ColumnMgrEO>();
        if (LoginPersonUtil.isSuperAdmin() || LoginPersonUtil.isRoot()) {
            //获取所有标准站
            List<IndicatorEO> siteList = getList(IndicatorEO.class, CacheGroup.CMS_TYPE.toString(), IndicatorEO.Type.CMS_Site.toString());
            //获取所有标准子站
            List<IndicatorEO> siteList1 = getList(IndicatorEO.class, CacheGroup.CMS_TYPE.toString(), IndicatorEO.Type.SUB_Site.toString());
            if (siteList != null && siteList1 != null) {
                siteList.addAll(siteList1);
            } else if (siteList == null && siteList1 != null) {
                siteList = new ArrayList<IndicatorEO>();
                siteList.addAll(siteList1);
            }
            Long[] ids = new Long[siteList.size()];
            int i = 0;
            for (IndicatorEO eo : siteList) {
                ids[i++] = eo.getIndicatorId();
            }
            list = getColumnTreeByType(ids, null, columnTypeCode, false);
        } else {
            if (siteIds == null || siteIds.length < 0) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "站点权限为空");
            }
            list = getColumnTreeByType(siteIds, null, columnTypeCode, false);
        }
        return list;
    }

    /**
     * 获取序号
     *
     * @param parentId
     * @return
     */
    @Override
    public Integer getNewSortNum(Long parentId, boolean isCom) {
        return configDao.getNewSortNum(parentId, isCom);
    }

    /**
     * 根据栏目类型获取该站点下的栏目树 1、为复制文章提供接口：flag为true表示排除栏目主键为columnId的栏目
     *
     * @param siteIds
     * @param columnId
     * @param columnTypeCode
     * @return
     */
    @Override
    public List<ColumnMgrEO> getColumnTreeByType(Long[] siteIds, Long columnId, String columnTypeCode, Boolean flag) {
        // 根据栏目类型获取指定站点下的栏目树
        List<ColumnMgrEO> list = configDao.getColumnTreeByType(siteIds, columnId, columnTypeCode, flag);
        // 获取站点
        List<IndicatorEO> list2 = indicatorService.getEntities(IndicatorEO.class, siteIds);
        List<ColumnMgrEO> list1 = new ArrayList<ColumnMgrEO>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        List<ColumnConfigRelEO> relList = relService.getEntities(ColumnConfigRelEO.class, map);
        Map<String, ColumnConfigRelEO> relMap = new HashMap<String, ColumnConfigRelEO>();
        if (null != relList && !relList.isEmpty()) {
            for (ColumnConfigRelEO organRel : relList) {
                if (organRel.getIndicatorId() != null) {
                    //key值是：主键ID_站点ID
                    relMap.put(organRel.getIndicatorId() + "_" + organRel.getSiteId(), organRel);
                }
            }
        }
        if (list2 != null && list2.size() > 0) {
            Map<String, Object> mapId = new HashMap<String, Object>();
            for (IndicatorEO eo : list2) {
                if (eo == null) {
                    break;
                }
                ColumnMgrEO mgrEO = new ColumnMgrEO();
                if (IndicatorEO.Type.SUB_Site.toString().equals(eo.getType())) {
                    mgrEO.setIsParent(1);
                } else {
                    mgrEO.setIsParent(eo.getIsParent());
                }
                mgrEO.setParentId(eo.getParentId());
                mgrEO.setIndicatorId(eo.getIndicatorId());
                mgrEO.setSiteId(eo.getIndicatorId());
                mgrEO.setColumnStrId(eo.getIndicatorId() + "_" + eo.getIndicatorId());
                mgrEO.setParentStrId(eo.getParentId() + "_" + eo.getParentId());
                mgrEO.setType(eo.getType());
                mgrEO.setName(eo.getName());
                list1.add(mgrEO);
                //如果是虚拟子站
                if (IndicatorEO.Type.SUB_Site.toString().equals(eo.getType())) {
                    SiteConfigEO configEO = CacheHandler.getEntity(SiteConfigEO.class, CacheGroup.CMS_INDICATORID, eo.getIndicatorId());
                    //获取虚拟子站公共栏目下的所有栏目
                    List<ColumnMgrEO> listSub = getColumnTreeBySite(configEO.getComColumnId());
                    List<ColumnMgrEO> listSub1 = new ArrayList<ColumnMgrEO>();
                    if (listSub != null && listSub.size() > 0) {
                        listSub1.addAll(listSub);
                        for (Iterator<ColumnMgrEO> it = listSub.iterator(); it.hasNext(); ) {
                            ColumnMgrEO eo1 = it.next();
                            // 删除不显示的目录
                            if (relMap.containsKey(eo1.getIndicatorId() + "_" + eo.getIndicatorId())) {
                                if (relMap.get(eo1.getIndicatorId() + "_" + eo.getIndicatorId()).getIsHide()) {
                                    // it.remove();
                                    listSub1.remove(eo1);
                                } else {
                                    ColumnConfigRelEO relEO = relMap.get(eo1.getIndicatorId() + "_" + eo.getIndicatorId());
                                    if (eo1.getParentId() == null) {
                                        eo1.setParentId(eo.getIndicatorId());
                                    }
                                    eo1.setColumnStrId(eo1.getIndicatorId() + "_" + eo.getIndicatorId());
                                    eo1.setParentStrId(eo1.getParentId() + "_" + eo.getIndicatorId());
                                    eo1.setName(relEO.getName());
                                    eo1.setSiteId(eo.getIndicatorId());
                                    eo1.setSortNum(relEO.getSortNum());
                                    eo1.setColumnTypeCode(relEO.getColumnTypeCode());
                                    eo1.setContentModelCode(relEO.getContentModelCode());
                                    eo1.setIsParent(relEO.getIsParent());
                                }
                            } else if (!relMap.containsKey(eo.getIndicatorId())) {
                                //如果是公共栏目
                                if (IndicatorEO.Type.COM_Section.toString().equals(eo1.getType())) {
                                    if (eo1.getParentId() == null) {
                                        eo1.setParentId(eo.getIndicatorId());
                                    }
                                    eo1.setColumnStrId(eo1.getIndicatorId() + "_" + eo.getIndicatorId());
                                    eo1.setParentStrId(eo1.getParentId() + "_" + eo.getIndicatorId());
                                    eo1.setSiteId(eo.getIndicatorId());
                                } else {
                                    if (mapId.size() > 0 && mapId.containsKey(eo1.getColumnStrId())) {
                                        listSub1.remove(eo1);
                                    }
                                }
                            }
                            if (!mapId.containsKey(eo1.getColumnStrId())) {
                                mapId.put(eo1.getColumnStrId(), eo1);
                            }
                        }
                        list1.addAll(listSub1);
                    }
                }
            }
        }
        List<ColumnMgrEO> newList = new ArrayList<ColumnMgrEO>();
        if (list != null && list.size() > 0) {
            list.addAll(list1);
            Map<String, ColumnMgrEO> parentMap = new HashMap<String, ColumnMgrEO>();
            for (ColumnMgrEO eo : list) {
                if (eo.getIsParent() == 0) {
                    newList.add(eo);
                } else {
                    parentMap.put(eo.getColumnStrId(), eo);
                }
            }
            List<String> parentIdList = new ArrayList<String>();
            List<ColumnMgrEO> parentList = new ArrayList<ColumnMgrEO>();
            newList = siteRightsService.getCurUserColumnOpt(newList);
            getParents(parentMap, newList, parentIdList, parentList);
            newList.addAll(parentList);

            //遍历删除以保证顺序跟综合信息中保持一致
            List<ColumnMgrEO> tempList = new ArrayList<ColumnMgrEO>();
            tempList.addAll(list);
            for (ColumnMgrEO columnMgrEO : tempList) {
                if (!newList.contains(columnMgrEO)) {
                    list.remove(columnMgrEO);
                }
            }
        } else {
            return list1;
        }
        return list;
    }

    private void getParents(Map<String, ColumnMgrEO> parentMap, List<ColumnMgrEO> newList, List<String> parentIdList, List<ColumnMgrEO> parentList) {
        for (ColumnMgrEO eo : newList) {
            String parentStrId = eo.getParentStrId();
            if (parentMap.containsKey(parentStrId)) {
                if (!parentIdList.contains(parentStrId)) {
                    ColumnMgrEO parent = parentMap.get(parentStrId);
                    if (null != parent) {
                        parentList.add(parent);
                        parentIdList.add(parentStrId);
                        List<ColumnMgrEO> tempList = new ArrayList<ColumnMgrEO>();
                        tempList.add(parent);
                        getParents(parentMap, tempList, parentIdList, parentList);
                    }
                }
            }
        }
    }

    /**
     * 获取栏目树的结构（异步加载） 1、给栏目管理提供栏目树
     *
     * @param
     * @return
     */
    @Override
    public List<ColumnMgrEO> getColumnTree(Long indicatorId, String name) {
        Long siteId = LoginPersonUtil.getSiteId();
        SiteMgrEO siteEO = CacheHandler.getEntity(SiteMgrEO.class, siteId);
        // 子栏目
        List<ColumnMgrEO> list = new ArrayList<ColumnMgrEO>();

        if (IndicatorEO.Type.SUB_Site.toString().equals(siteEO.getType())) {

            // 公共栏目--一级栏目
            if (siteEO.getComColumnId() != null && siteId.equals(indicatorId)) {
                list = new ArrayList<ColumnMgrEO>();
                IndicatorEO indicatorEO = CacheHandler.getEntity(IndicatorEO.class, siteEO.getComColumnId());
                ColumnConfigEO configEO = CacheHandler.getEntity(ColumnConfigEO.class, CacheGroup.CMS_INDICATORID, siteEO.getComColumnId());
                ColumnMgrEO mgrEO = new ColumnMgrEO();
                mgrEO.setName(indicatorEO.getName());
                mgrEO.setSortNum(indicatorEO.getSortNum());
                mgrEO.setIndicatorId(siteEO.getComColumnId());
                mgrEO.setParentId(indicatorEO.getParentId());
                mgrEO.setIsParent(indicatorEO.getIsParent());
                mgrEO.setColumnTypeCode(configEO.getColumnTypeCode());
                mgrEO.setType(indicatorEO.getType());
                if(StringUtils.isEmpty(indicatorEO.getParentNamesPinyin())){
                    mgrEO.setUrlPath(indicatorEO.getNamePinyin());
                }else{
                    mgrEO.setUrlPath(indicatorEO.getParentNamesPinyin() + "/" + indicatorEO.getNamePinyin());
                }
                list.add(mgrEO);
            } else if (!siteId.equals(indicatorId)) {
                // 一级以下栏目
                List<ColumnMgrEO> list1 = configDao.getColumnByParentId(indicatorId, true, null);// 添加公共栏目
                list = new ArrayList<ColumnMgrEO>();
                if (list1 != null && list1.size() > 0) {
                    list.addAll(list1);
                }
            }
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
            map.put("siteId", LoginPersonUtil.getSiteId());
            List<ColumnConfigRelEO> relList = relService.getEntities(ColumnConfigRelEO.class, map);
            Map<Long, ColumnConfigRelEO> relMap = new HashMap<Long, ColumnConfigRelEO>();
            if (null != relList && !relList.isEmpty()) {
                for (ColumnConfigRelEO organRel : relList) {
                    if (organRel.getIndicatorId() != null) {
                        relMap.put(organRel.getIndicatorId(), organRel);
                    }
                }
                if (null != list && !list.isEmpty()) {
                    for (Iterator<ColumnMgrEO> it = list.iterator(); it.hasNext(); ) {
                        ColumnMgrEO eo = it.next();
                        // 删除不显示的目录
                        if (relMap.containsKey(eo.getIndicatorId())) {
                            if (relMap.get(eo.getIndicatorId()).getIsHide()) {
                                it.remove();
                            } else {
                                ColumnConfigRelEO relEO = relMap.get(eo.getIndicatorId());
                                eo.setIndicatorId(relEO.getIndicatorId());
                                eo.setName(relEO.getName());
                                eo.setSortNum(relEO.getSortNum());
                                eo.setIsParent(relEO.getIsParent());
                                eo.setColumnTypeCode(relEO.getColumnTypeCode());
                                eo.setContentModelCode(relEO.getContentModelCode());
                                eo.setKeyWords(relEO.getKeyWords());
                                eo.setDescription(relEO.getDescription());
                                eo.setIsShow(relEO.getIsShow());
                                eo.setTransUrl(relEO.getTransUrl());
                                eo.setTransWindow(relEO.getTransWindow());
                                eo.setGenePageIds(relEO.getGenePageIds());
                                eo.setGenePageNames(relEO.getGenePageNames());
                                eo.setSynColumnIds(relEO.getSynColumnIds());
                                eo.setSynColumnNames(relEO.getSynColumnNames());
                            }
                        }
                    }
                }
            }

        } else {
            list = CacheHandler.getList(ColumnMgrEO.class, CacheGroup.CMS_PARENTID, indicatorId);
        }
        return list;
    }

    /**
     * 供权限管理查看树
     *
     * @param siteId
     * @return
     */
    public List<ColumnMgrEO> getTree(Long siteId) {
        if (siteId == null) {// 获取所有的栏目和站点
            // 获取所有的栏目
            List<ColumnMgrEO> listC = CacheHandler.getList(ColumnMgrEO.class, CacheGroup.CMS_TYPE, IndicatorEO.Type.CMS_Section.toString());
            List<ColumnMgrEO> list = listC;
            // 获取所有的标准站
            List<IndicatorEO> listS = CacheHandler.getList(IndicatorEO.class, CacheGroup.CMS_TYPE, IndicatorEO.Type.CMS_Site.toString());
            // 获取所有的虚拟子站
            List<IndicatorEO> listSub = CacheHandler.getList(IndicatorEO.class, CacheGroup.CMS_TYPE, IndicatorEO.Type.SUB_Site.toString());
            if (listS != null && listS.size() > 0) {
                if (listSub != null && listSub.size() > 0) {
                    listS.addAll(listSub);
                }
                for (IndicatorEO eo : listS) {
                    ColumnMgrEO mgrEO = new ColumnMgrEO();
                    mgrEO.setName(eo.getName());
                    mgrEO.setIndicatorId(eo.getIndicatorId());
                    mgrEO.setParentId(eo.getParentId());
                    if (IndicatorEO.Type.SUB_Site.toString().equals(eo.getType())) {
                        mgrEO.setIsParent(0);
                    } else {
                        mgrEO.setIsParent(eo.getIsParent());
                    }
                    mgrEO.setType(eo.getType());
                    list.add(mgrEO);
                }
            }
            return list;
        } else {// 获取站点下所有的栏目
            List<ColumnMgrEO> list = CacheHandler.getList(ColumnMgrEO.class, CacheGroup.CMS_SITE_ID, siteId);
            return list;
        }
    }

    /**
     * 根据主表Id数组获取站点和栏目 1、为角色管理提供接口
     *
     * @param ids
     * @return
     */
    @Override
    public List<ColumnMgrEO> getTreeByIds(Long[] ids) {
        String idStr = null;
        for (Long l : ids) {
            if (idStr == null) {
                idStr = String.valueOf(l);
            } else {
                idStr += "," + String.valueOf(l);
            }
        }
        idStr = idStr.substring(0, idStr.length());
        //根据主键ID获取栏目
        List<ColumnMgrEO> list = configDao.getColumnByIds(idStr);
        //根据主键ID获取站点
        List<ColumnMgrEO> listS = configDao.getSiteByIds(idStr);
        if (list != null && list != null) {
            list.addAll(listS);
        } else {
            return listS;
        }
        return list;
    }

    /**
     * 根据站点ID查询站点下的栏目树（异步加载） 1、为内容协同提供接口
     *
     * @param indicatorId
     * @return
     */
    @Override
    public List<ColumnMgrEO> getColumnTreeBySite(Long indicatorId) {
        List<ColumnMgrEO> list = new ArrayList<ColumnMgrEO>();
        SiteMgrEO siteMgrEO = CacheHandler.getEntity(SiteMgrEO.class, indicatorId);
        if (siteMgrEO != null) {
            // 获取不是虚拟模块的所有非公共栏目
            String[] contentModelCode = new String[]{"net_work", "InteractiveVirtual"};
            //标准站
            if (IndicatorEO.Type.CMS_Site.toString().equals(siteMgrEO.getType())) {
                list = configDao.getVirtualColumn(indicatorId, false, contentModelCode);
                return list;
                //虚拟子站
            } else if (IndicatorEO.Type.SUB_Site.toString().equals(siteMgrEO.getType())) {
                if (siteMgrEO.getComColumnId() != null) {
                    // 获取站点绑定的公共栏目获取子栏目
                    List<ColumnMgrEO> listCom = configDao.getColumnByParentId(siteMgrEO.getComColumnId(), true, null);
                    if (listCom != null && listCom.size() > 0) {
                        for (ColumnMgrEO columnMgrEO : listCom) {
                            if (!StringUtils.isEmpty(columnMgrEO.getContentModelCode())) {
                                String code = columnMgrEO.getContentModelCode();
                                ContentModelEO contentModelEO = ModelConfigUtil.getEOByCode(code, indicatorId);
                                if (contentModelEO != null) {
                                    columnMgrEO.setContent(contentModelEO.getContent());
                                }
                            }
                        }
                    }
                    //虚拟子站，对公共栏目信息要根据栏目配置中间表对其重新赋值
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
                    map.put("siteId", LoginPersonUtil.getSiteId());
                    List<ColumnConfigRelEO> relList = relService.getEntities(ColumnConfigRelEO.class, map);
                    Map<Long, ColumnConfigRelEO> relMap = new HashMap<Long, ColumnConfigRelEO>();
                    if (null != relList && !relList.isEmpty()) {
                        for (ColumnConfigRelEO organRel : relList) {
                            if (organRel.getIndicatorId() != null) {
                                relMap.put(organRel.getIndicatorId(), organRel);
                            }
                        }
                        if (null != listCom && !listCom.isEmpty()) {
                            for (Iterator<ColumnMgrEO> it = listCom.iterator(); it.hasNext(); ) {
                                ColumnMgrEO eo = it.next();
                                // 删除不显示的目录
                                if (relMap.containsKey(eo.getIndicatorId())) {
                                    if (relMap.get(eo.getIndicatorId()).getIsHide()) {
                                        it.remove();
                                    } else {
                                        ColumnConfigRelEO relEO = relMap.get(eo.getIndicatorId());
                                        eo.setIndicatorId(relEO.getIndicatorId());
                                        eo.setName(relEO.getName());
                                        eo.setIsParent(relEO.getIsParent());
                                        eo.setSortNum(relEO.getSortNum());
                                        eo.setColumnTypeCode(relEO.getColumnTypeCode());
                                        eo.setContentModelCode(relEO.getContentModelCode());
                                        eo.setKeyWords(relEO.getKeyWords());
                                        eo.setDescription(relEO.getDescription());
                                        eo.setIsShow(relEO.getIsShow());
                                        eo.setTransUrl(relEO.getTransUrl());
                                        eo.setTransWindow(relEO.getTransWindow());
                                        eo.setGenePageIds(relEO.getGenePageIds());
                                        eo.setGenePageNames(relEO.getGenePageNames());
                                        eo.setSynColumnIds(relEO.getSynColumnIds());
                                        eo.setSynColumnNames(relEO.getSynColumnNames());
                                        if (eo.getContentModelCode() != null) {
                                            ContentModelEO contentModelEO =
                                                    CacheHandler.getEntity(ContentModelEO.class, CacheGroup.CMS_CODE, eo.getContentModelCode());
                                            if (contentModelEO != null) {
                                                eo.setContent(contentModelEO.getContent());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    list.addAll(listCom);
                }
            }

        } else {
            // 获取ID获取一级子栏目
            list = configDao.getColumnByParentId(indicatorId, true, null);

            //虚拟子站，对公共栏目信息要根据栏目配置中间表对其重新赋值
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
            map.put("isHide", false);
            map.put("siteId", LoginPersonUtil.getSiteId());
            List<ColumnConfigRelEO> relList = relService.getEntities(ColumnConfigRelEO.class, map);
            Map<Long, ColumnConfigRelEO> relMap = new HashMap<Long, ColumnConfigRelEO>();
            if (null != relList && !relList.isEmpty()) {
                for (ColumnConfigRelEO organRel : relList) {
                    if (organRel.getIndicatorId() != null) {
                        relMap.put(organRel.getIndicatorId(), organRel);
                    }
                }
                if (null != list && !list.isEmpty()) {
                    for (Iterator<ColumnMgrEO> it = list.iterator(); it.hasNext(); ) {
                        ColumnMgrEO eo = it.next();
                        // 删除不显示的目录
                        if (relMap.containsKey(eo.getIndicatorId())) {
                            if (relMap.get(eo.getIndicatorId()).getIsHide()) {
                                it.remove();
                            } else {
                                ColumnConfigRelEO relEO = relMap.get(eo.getIndicatorId());
                                eo.setIndicatorId(relEO.getIndicatorId());
                                eo.setName(relEO.getName());
                                eo.setSortNum(relEO.getSortNum());
                                eo.setColumnTypeCode(relEO.getColumnTypeCode());
                                eo.setContentModelCode(relEO.getContentModelCode());
                                eo.setKeyWords(relEO.getKeyWords());
                                eo.setDescription(relEO.getDescription());
                                eo.setIsShow(relEO.getIsShow());
                                eo.setTransUrl(relEO.getTransUrl());
                                eo.setTransWindow(relEO.getTransWindow());
                                eo.setGenePageIds(relEO.getGenePageIds());
                                eo.setGenePageNames(relEO.getGenePageNames());
                                eo.setSynColumnIds(relEO.getSynColumnIds());
                                eo.setSynColumnNames(relEO.getSynColumnNames());
                            }
                        }
                    }
                }
            }

        }
        return list;
    }

    /**
     * 得到一个站点下的所有栏目
     *
     * @param name
     * @param siteId
     * @return
     */
    @Override
    public List<ColumnMgrEO> getAllColumnTree(Long siteId, String name) {
        SiteMgrEO siteEO = CacheHandler.getEntity(SiteMgrEO.class, siteId);
        if (siteEO == null) {
            return null;
        }
        List<ColumnMgrEO> list = new ArrayList<ColumnMgrEO>();
        //标准子站
        if (IndicatorEO.Type.CMS_Site.toString().equals(siteEO.getType())) {
            list = configDao.getAllColumnTree(siteId, name);
        } else {
            //虚拟子站，对公共栏目信息要根据栏目配置中间表对其重新赋值
            List<ColumnMgrEO> list1 = new ArrayList<ColumnMgrEO>();
            if (!AppUtil.isEmpty(siteEO.getComColumnId())) {
                //获取站点绑定的公共栏目下所有的子栏目
                List<ColumnMgrEO> listSub = configDao.getAllColumnBySite(siteEO.getComColumnId());
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("siteId", siteId);
                map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
                List<ColumnConfigRelEO> relList = relService.getEntities(ColumnConfigRelEO.class, map);
                Map<Long, ColumnConfigRelEO> relMap = new HashMap<Long, ColumnConfigRelEO>();
                if (null != relList && !relList.isEmpty()) {
                    for (ColumnConfigRelEO organRel : relList) {
                        if (organRel.getIndicatorId() != null) {
                            relMap.put(organRel.getIndicatorId(), organRel);
                        }
                    }
                }
                List<ColumnMgrEO> listSub1 = new ArrayList<ColumnMgrEO>();
                if (listSub != null && listSub.size() > 0) {
                    listSub1.addAll(listSub);
                    for (Iterator<ColumnMgrEO> it = listSub.iterator(); it.hasNext(); ) {
                        ColumnMgrEO eo1 = it.next();
                        if (siteEO.getComColumnId().equals(eo1.getIndicatorId())) {
                            eo1.setParentId(siteId);
                        }
                        // 删除不显示的目录
                        if (relMap.containsKey(eo1.getIndicatorId())) {
                            if (relMap.get(eo1.getIndicatorId()).getIsHide()) {
                                // it.remove();
                                listSub1.remove(eo1);
                            } else {
                                ColumnConfigRelEO relEO = relMap.get(eo1.getIndicatorId());
                                if (eo1.getParentId() == null) {
                                    eo1.setParentId(siteId);
                                }
                                eo1.setName(relEO.getName());
                                eo1.setSiteId(siteId);
                                eo1.setSortNum(relEO.getSortNum());
                                eo1.setColumnTypeCode(relEO.getColumnTypeCode());
                                eo1.setIsParent(relEO.getIsParent());
                                //设置前台是否展示
                                eo1.setIsShow(relEO.getIsShow());
                            }
                        }
                    }
                    list1.addAll(listSub1);
                }
                //根据栏目名称检索
                if (!StringUtils.isEmpty(name)) {
                    if (list1 != null && list1.size() > 0) {
                        for (ColumnMgrEO eo : list1) {
                            if (name.equals(eo.getName())) {
                                list.add(eo);
                            }
                        }
                    }
                } else {
                    list.addAll(list1);
                }
            }
        }

        return list;
    }

    /**
     * 得到一个站点下的所有栏目和该站点 1、为栏目管理的"生成页面"提供栏目树
     *
     * @param name
     * @param siteId
     * @return
     */
    @Override
    public List<ColumnMgrEO> searchColumnTree(Long siteId, String name) {
        SiteMgrEO siteEO = CacheHandler.getEntity(SiteMgrEO.class, siteId);
        if (siteEO == null) {
            return null;
        }
        List<ColumnMgrEO> list = new ArrayList<ColumnMgrEO>();
        //标准站点
        if (IndicatorEO.Type.CMS_Site.toString().equals(siteEO.getType())) {
            list = configDao.searchColumnTree(siteId, name);
        } else {
            //虚拟子站，对公共栏目信息要根据栏目配置中间表对其重新赋值
            List<ColumnMgrEO> list1 = new ArrayList<ColumnMgrEO>();
            if (!AppUtil.isEmpty(siteEO.getComColumnId())) {
                // 获取站点绑定的公共栏目下所有的子栏目
                List<ColumnMgrEO> listSub = configDao.getAllColumnBySite(siteEO.getComColumnId());
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("siteId", siteId);
                map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
                List<ColumnConfigRelEO> relList = relService.getEntities(ColumnConfigRelEO.class, map);
                Map<Long, ColumnConfigRelEO> relMap = new HashMap<Long, ColumnConfigRelEO>();
                if (null != relList && !relList.isEmpty()) {
                    for (ColumnConfigRelEO organRel : relList) {
                        if (organRel.getIndicatorId() != null) {
                            relMap.put(organRel.getIndicatorId(), organRel);
                        }
                    }
                }
                List<ColumnMgrEO> listSub1 = new ArrayList<ColumnMgrEO>();
                if (listSub != null && listSub.size() > 0) {
                    listSub1.addAll(listSub);
                    for (Iterator<ColumnMgrEO> it = listSub.iterator(); it.hasNext(); ) {
                        ColumnMgrEO eo1 = it.next();
                        // 删除不显示的目录
                        if (relMap.containsKey(eo1.getIndicatorId())) {
                            if (relMap.get(eo1.getIndicatorId()).getIsHide()) {
                                // it.remove();
                                listSub1.remove(eo1);
                            } else {
                                ColumnConfigRelEO relEO = relMap.get(eo1.getIndicatorId());
                                if (eo1.getParentId() == null) {
                                    eo1.setParentId(siteId);
                                }
                                eo1.setName(relEO.getName());
                                eo1.setSiteId(siteId);
                                eo1.setSortNum(relEO.getSortNum());
                                eo1.setColumnTypeCode(relEO.getColumnTypeCode());
                                eo1.setIsParent(relEO.getIsParent());
                            }
                        }
                    }
                    list1.addAll(listSub1);
                }
                //根据栏目名称检索
                if (!StringUtils.isEmpty(name)) {
                    if (list1 != null && list1.size() > 0) {
                        for (ColumnMgrEO eo : list1) {
                            if (name.equals(eo.getName())) {
                                list.add(eo);
                            }
                        }
                    }
                } else {
                    list.addAll(list1);
                }
            }
        }

        return list;
    }

    /**
     * 返回站点及站点下所有栏目
     *
     * @param siteIdArr
     * @return
     */
    @Override
    public List<ColumnMgrEO> getColumnBySiteIds(Long[] siteIdArr) {
        String idStr = null;
        for (Long l : siteIdArr) {
            if (idStr == null) {
                idStr = String.valueOf(l);
            } else {
                idStr += "," + String.valueOf(l);
            }
        }
        // 获取指定站点下所有的栏目
        List<ColumnMgrEO> listC = configDao.getColumnBySiteIds(idStr);
        // 获取指定的站点
        List<ColumnMgrEO> listS = configDao.getSiteByIds(idStr);
        if (listS != null && listS.size() > 0) {
            for (ColumnMgrEO siteEO : listS) {
                if (IndicatorEO.Type.SUB_Site.toString().equals(siteEO.getType())) {
                    List<ColumnMgrEO> list1 = getAllColumnTree(siteEO.getIndicatorId(), null);
                    if (list1 != null && list1.size() > 0) {
                        listC.addAll(list1);
                    }
                }
            }
        }
        if (listC != null && listC.size() > 0) {
            if (listS != null) {
                listS.addAll(listC);
            }
        }
        return listS;
    }

    /**
     * 根据站点Id和内容模型的code值查找栏目树
     *
     * @param siteId
     * @param code
     * @return
     */
    @Override
    public List<ColumnMgrEO> getColumnByTypeCode(Long siteId, String code) {
        if (siteId == null || AppUtil.isEmpty(code)) {
            return null;
        }
        List<ColumnMgrEO> list = configDao.getColumnByTypeCode(siteId, code);
        return list;
    }

    /**
     * 根据站点或栏目Id和内容模型的code值查找栏目树(异步加载，给评论管理提供)
     *
     * @param indicatorId
     * @param codes
     * @return
     */
    @Override
    public List<ColumnMgrEO> getColumnByModelCodes(Long indicatorId, String codes) {
        if (indicatorId == null || AppUtil.isEmpty(codes)) {
            return null;
        }
        List<ColumnMgrEO> list = configDao.getColumnByModelCodes(indicatorId, codes);
        return list;
    }

    /**
     * 根据站点Id，查找允许评论的栏目树
     *
     * @param indicatorId
     * @return
     */
    @Override
    public List<ColumnMgrEO> getColumnByIsComment(Long indicatorId) {
        if (indicatorId == null) {
            return null;
        }
        Long siteId = LoginPersonUtil.getSiteId();
        //查找本站点下允许评论的内容模型
        List<ContentModelEO> list = ModelConfigUtil.getModelBySiteId(siteId);
        List<ColumnMgrEO> newList = null;
        String codes = "";
        if (list != null && list.size() > 0) {
            for (ContentModelEO eo : list) {
                codes += "'" + eo.getCode() + "',";
            }
            //根据站点或栏目Id和内容模型的code值查找栏目树(异步)
            newList = getColumnByModelCodes(indicatorId, codes);
        }
        return newList;
    }

    /**
     * 获取文章类型的栏目站点树
     *
     * @return
     */
    @Override
    public List<ColumnMgrEO> getArticleTree() {
        List<ColumnMgrEO> list = getColumnTreeByType(new Long[]{}, null, "articleNews", false);
        // 获取所有的站点
        List<IndicatorEO> listS = CacheHandler.getList(IndicatorEO.class, CacheGroup.CMS_TYPE, IndicatorEO.Type.CMS_Site.toString());
        if (listS != null && listS.size() > 0) {
            for (IndicatorEO eo : listS) {
                ColumnMgrEO mgrEO = new ColumnMgrEO();
                mgrEO.setName(eo.getName());
                mgrEO.setIndicatorId(eo.getIndicatorId());
                mgrEO.setParentId(eo.getParentId());
                mgrEO.setIsParent(eo.getIsParent());
                mgrEO.setType(eo.getType());
                list.add(mgrEO);
            }
        }
        return list;
    }

    /**
     * 获取层级的栏目树
     *
     * @param indicatorId
     * @param level
     * @param condition
     * @param vo
     * @return
     */
    @Override
    public List<ColumnMgrEO> getLevelColumnTree(Long indicatorId, int[] level, String condition, PageQueryVO vo) {
        List<ColumnMgrEO> list = configDao.getLevelColumnTree(indicatorId, level, condition, vo);
        return list;
    }

    /**
     * 获取父栏目下所有的栏目
     *
     * @param indicatorId
     * @return
     */
    @Override
    public List<ColumnMgrEO> getParentColumns(Long indicatorId) {
        return configDao.getParentColumns(indicatorId);
    }

    /**
     * 根据该栏目ID获取所有父级栏目ID
     *
     * @param indicatorId
     * @return
     */
    @Override
    public Map<Long, Boolean> getParentIndicatorIds(Long indicatorId) {
        return configDao.getParentIndicatorIds(indicatorId);
    }

    /**
     * 查询子节点
     *
     * @param indicatorId
     * @param parentFlag  {0,1} 查询所有 {0} 查询 isparent = 0
     * @return
     */
    @Override
    public List<ColumnMgrEO> getChildColumn(Long indicatorId, int[] parentFlag) {
        ColumnMgrEO columnMgrEO = CacheHandler.getEntity(ColumnMgrEO.class, indicatorId);
        if (null != columnMgrEO && columnMgrEO.getIsParent() == 0) {// 已经是子节点无需查询
            return Arrays.asList(columnMgrEO);
        }
        return configDao.getChildColumn(indicatorId, parentFlag);
    }

    /**
     * 保存公共栏目
     *
     * @param columnVO
     * @return
     */
    @Override
    public Long saveComEO(ColumnMgrEO columnVO) {
        Long parentId = columnVO.getParentId();
        IndicatorEO pindicatorEO = CacheHandler.getEntity(IndicatorEO.class, parentId);
        if (pindicatorEO != null && pindicatorEO.getIsParent() == 0) {
            pindicatorEO.setIsParent(1);
            indicatorService.save(pindicatorEO);
        }

        // 新增子栏目
        IndicatorEO indicatorEO = new IndicatorEO();
        AppUtil.copyProperties(indicatorEO, columnVO);
        indicatorEO.setSiteId(null);
        indicatorEO.setType(IndicatorEO.Type.COM_Section.toString());
        indicatorEO.setRecordStatus(AMockEntity.RecordStatus.Normal.toString());
        indicatorService.save(indicatorEO);

        // 栏目配置信息
        ColumnConfigEO configEO = new ColumnConfigEO();
        if (columnVO.getColumnConfigId() != null) {
            configEO = CacheHandler.getEntity(ColumnConfigEO.class, columnVO.getColumnConfigId());
        }
        configEO.setIndicatorId(indicatorEO.getIndicatorId());
        configEO.setContentModelCode(columnVO.getContentModelCode());
        if (StringUtils.isEmpty(columnVO.getColumnTypeCode())) {
            //根据内容模型code值获取模型模板关联信息
            List<ModelTemplateEO> list1 = ModelConfigUtil.getTemplateListByCode(columnVO.getContentModelCode(), null);
            if (list1 != null && list1.size() > 0) {
                configEO.setColumnTypeCode(list1.get(0).getModelTypeCode());
            }
        } else {
            configEO.setColumnTypeCode(columnVO.getColumnTypeCode());
        }
        configEO.setIsStartUrl(columnVO.getIsStartUrl());
        configEO.setKeyWords(columnVO.getKeyWords());
        configEO.setDescription(columnVO.getDescription());
        if (columnVO.getIsStartUrl() == 1) {
            configEO.setTransUrl(columnVO.getTransUrl());
            configEO.setTransWindow(columnVO.getTransWindow());
        }
        // 如果是链接类型
        if (BaseContentEO.TypeCode.linksMgr.toString().equals(configEO.getColumnTypeCode())) {
            configEO.setIsLogo(columnVO.getIsLogo());
            configEO.setNum(columnVO.getNum());
            if (columnVO.getIsLogo() == 1) {
                configEO.setHeight(columnVO.getHeight());
                configEO.setWidth(columnVO.getWidth());
            }
        }
        if (columnVO.getColumnConfigId() == null) {
            saveEntity(configEO);
//            SysLog.log("添加栏目配置信息 >> ID：" + configEO.getColumnConfigId(), "ColumnConfigEO", CmsLogEO.Operation.Add.toString());
        } else {
            updateEntity(configEO);
//            SysLog.log("修改栏目配置信息 >> ID：" + configEO.getColumnConfigId(), "ColumnConfigEO", CmsLogEO.Operation.Update.toString());
        }
        return configEO.getIndicatorId();
    }

    /**
     * 获取公共栏目树
     *
     * @param indicatorId
     * @return
     */
    @Override
    public List<ColumnVO> getComColumnTree(Long indicatorId) {
        return configDao.getComColumnTree(indicatorId);
    }

    /**
     * 获取某个站点下的层级栏目树
     *
     * @param siteId
     * @param i
     * @return
     */
    @Override
    public List<ColumnMgrEO> getLevComColumn(Long siteId, int[] i) {
        return configDao.getLevComColumn(siteId, i);
    }

    /**
     * 获取在线办事、政民互动的栏目树
     *
     * @param indicatorId
     * @param isShow
     * @param contentModelCode
     * @return
     */
    @Override
    public List<ColumnMgrEO> getVirtualColumn(Long indicatorId, boolean isShow, String[] contentModelCode) {
        return configDao.getVirtualColumn(indicatorId, isShow, contentModelCode);
    }

    /**
     * 根据父栏目获取所有子栏目
     *
     * @param indicatorId
     * @return
     */
    @Override
    public List<ColumnMgrEO> getColumnByPId(Long indicatorId) {
        List<ColumnMgrEO> list = CacheHandler.getList(ColumnMgrEO.class, CacheGroup.CMS_PARENTID, indicatorId);
        return list;
    }

    /**
     * 给栏目采集提供接口：获取栏目名称
     *
     * @param columnId
     * @param siteId
     * @return
     */
    @Override
    public String getNamesByKeyId(Long columnId, Long siteId) {
        List<IndicatorEO> listStr = new ArrayList<IndicatorEO>();
        while (columnId != null) {
            IndicatorEO indicatorEO = CacheHandler.getEntity(IndicatorEO.class, columnId);
            if (null != indicatorEO) {
                //公共栏目
                if (IndicatorEO.Type.COM_Section.toString().equals(indicatorEO.getType())) {
                    ColumnConfigRelEO relEO = ColumnRelUtil.getByIndicatorId(columnId, siteId);
                    if (relEO != null) {
                        indicatorEO.setName(relEO.getName());
                    }
                    if (indicatorEO.getParentId() == null) {
                        indicatorEO.setParentId(siteId);
                    }
                }
                listStr.add(indicatorEO);
                columnId = indicatorEO.getParentId();
                if (columnId == 1) {
                    columnId = null;
                }
            } else {
                break;
            }
        }
        //构建固定形式的栏目名称
        String strName = "";
        if (listStr != null && listStr.size() > 0) {
            for (int i = listStr.size() - 1; i >= 0; i--) {
                strName += listStr.get(i).getName() + ">";
            }
            strName = strName.substring(0, strName.length() - 1);
        }
        return strName;
    }

    /**
     * 获取栏目移动树
     *
     * @param indicatorId
     * @param columnId
     * @return
     */
    @Override
    public List<ColumnMgrEO> getMoveTree(Long indicatorId, Long columnId) {
        SiteMgrEO siteEO = CacheHandler.getEntity(SiteMgrEO.class, indicatorId);
        List<ColumnMgrEO> list = new ArrayList<ColumnMgrEO>();

        // 如果indicatorId是虚拟子站的站点Id
        if (siteEO != null && IndicatorEO.Type.SUB_Site.toString().equals(siteEO.getType())) {
            ColumnMgrEO mgrEO = CacheHandler.getEntity(ColumnMgrEO.class, siteEO.getComColumnId());
            list.add(mgrEO);
        }
        List<ColumnMgrEO> newList = configDao.getMoveTree(indicatorId, columnId);
        if (newList != null && newList.size() > 0) {
            list.addAll(newList);
        }
        if (siteEO == null) {
            SiteMgrEO siteMgrEO = CacheHandler.getEntity(SiteMgrEO.class, LoginPersonUtil.getSiteId());
            // 本站为虚拟子站
            if (siteMgrEO != null && IndicatorEO.Type.SUB_Site.toString().equals(siteMgrEO.getType())) {
                ColumnMgrEO pEO = CacheHandler.getEntity(ColumnMgrEO.class, indicatorId);
                // 父节点为公共栏目
                if (IndicatorEO.Type.COM_Section.toString().equals(pEO.getType())) {
                    if (list != null && list.size() > 0) {
                        for (ColumnMgrEO eo : list) {
                            // 公共栏目配置项重新赋值
                            if (IndicatorEO.Type.COM_Section.toString().equals(eo.getType())) {
                                ColumnConfigRelEO relEO = ColumnRelUtil.getByIndicatorId(eo.getIndicatorId(), LoginPersonUtil.getSiteId());
                                if (relEO != null) {
                                    eo.setName(relEO.getName());
                                    eo.setSortNum(relEO.getSortNum());
                                    eo.setColumnTypeCode(relEO.getColumnTypeCode());
                                }
                            }
                        }
                    }
                }
            }
        }

        return list;
    }

    /**
     * 检查栏目下是否有内容
     *
     * @param columnId
     * @return
     */
    @Override
    @Deprecated
    public boolean isHaveContent(Long columnId) {
//        ColumnMgrEO mgrEO = CacheHandler.getEntity(ColumnMgrEO.class, columnId);
//        if (mgrEO != null) {
//            if (mgrEO.getIsParent() == 1) {
//                return false;
//            }
//            Long count = baseContentService.getCountByColumnId(columnId);
//            if (count == null || count <= 0) {
//                return true;
//            }
//        }
        return false;
    }

    /**
     * 判断站点下是否有栏目
     *
     * @param siteId
     * @return
     */
    @Override
    public Boolean getIsHaveColumn(Long siteId) {
        Boolean isHave = configDao.getIsHaveColumn(siteId);
        return isHave;
    }


    /**
     * 根据栏目类型code值获取某站点下的栏目
     *
     * @param codes
     * @param siteId
     * @return
     */
    @Override
    public List<ColumnMgrEO> getByColumnTypeCodes(String[] codes, Long siteId, Boolean flag) {
        List<ColumnMgrEO> list = new ArrayList<ColumnMgrEO>();
        if (flag != null && flag) {
            SiteMgrEO siteEO = CacheHandler.getEntity(SiteMgrEO.class, siteId);
            if (siteEO != null) {
                ColumnMgrEO columnMgrEO = new ColumnMgrEO();
                columnMgrEO.setIndicatorId(siteEO.getIndicatorId());
                columnMgrEO.setName(siteEO.getName());
                columnMgrEO.setParentId(siteEO.getParentId());
                columnMgrEO.setIsParent(siteEO.getIsParent());
                list.add(columnMgrEO);
            }
        }
        List<ColumnMgrEO> list1 = configDao.getByColumnTypeCodes(codes, siteId, flag);
        if (list1 != null && list1.size() > 0) {
            list.addAll(list1);
        }
        return list;
    }

    @Override
    public List<ColumnMgrEO> getColumns(List<String> codes, Long siteId) {
        //获取站点下的所有子节点
        List<ColumnMgrEO> sonColumns = configDao.getColumns(codes, siteId);
        //获取站点下的所有节点
        List<ColumnMgrEO> columns = configDao.getColumns(null, siteId);
        //将所有栏目封装到map中
        Map<Long, ColumnMgrEO> map = new HashMap<Long, ColumnMgrEO>();
        for (ColumnMgrEO columnMgrEO : columns) {
            map.put(columnMgrEO.getIndicatorId(), columnMgrEO);
        }

        List<ColumnMgrEO> list = new ArrayList<ColumnMgrEO>(columns.size());
        //将所有叶子节点放进去
        list.addAll(sonColumns);
        //从叶子节点开始往上层递归
        for (ColumnMgrEO eo : sonColumns) {
            getColumnsByParentId(eo, map, list);
        }
        return list;
    }

    @Override
    public List<ColumnMgrEO> getSiteMap(String lev, Long siteId, Long columnId, String columnIds, Boolean link, Boolean isCom) {
        return configDao.getSiteMap(lev, siteId, columnId, columnIds, link, isCom);

    }

    @Override
    public ColumnMgrEO getById(Long indicatorId) {
        return configDao.getById(indicatorId);
    }

    @Override
    public List<ColumnMgrEO> saveUploadColumn(List<ColumnMgrEO> list, int startRow, Long siteId, Long columnId) {
        List<ColumnMgrEO> newList = new ArrayList<ColumnMgrEO>();
        /** 获取文字新闻的内容模型code值*/
        List<ContentModelVO> modelList = ModelConfigUtil.getByColumnTypeCode(siteId, BaseContentEO.TypeCode.articleNews.toString());
        String modelCode = null;
        if (modelList != null && modelList.size() > 0) {
            modelCode = modelList.get(0).getCode();
        }
        for (ColumnMgrEO eo : list) {
            ColumnMgrEO newEO = new ColumnMgrEO();
            if (StringUtils.isEmpty(eo.getName())) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "栏目名称不能为空");
            }
            if (eo.getRowIndex() == null || eo.getRowIndex() == 0) {
                newEO.setParentId(columnId);
            }
            AppUtil.copyProperties(newEO, eo);
            newEO.setType(IndicatorEO.Type.CMS_Section.toString());
            newEO.setColumnTypeCode(BaseContentEO.TypeCode.articleNews.toString());
            if (!StringUtils.isEmpty(modelCode)) {
                newEO.setContentModelCode(modelCode);
            } else {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "请先建立文字新闻的内容模型");
            }

            ColumnMgrEO eo_ = saveColumnEO(newEO);
            newEO.setIndicatorId(eo_.getIndicatorId());
            newEO.setColumnConfigId(eo_.getColumnConfigId());
            newList.add(newEO);
        }
        return newList;

    }

    @Override
    public Long getSourceColumnCount(String referColumnId){
        return configDao.getSourceColumnCount(referColumnId);
    }

    @Override
    public Long getSourceOrganCatCount(String referOrganCatId){
        return configDao.getSourceOrganCatCount(referOrganCatId);
    }

    private void getColumnsByParentId(ColumnMgrEO eo, Map<Long, ColumnMgrEO> map, List<ColumnMgrEO> list) {

        ColumnMgrEO columnMgrEO = map.get(eo.getParentId());

        //如果map中存在则向上递归
        if (columnMgrEO != null) {
            for (ColumnMgrEO mgrEO : list) {
                Long indicatorId = mgrEO.getIndicatorId();
                if (indicatorId.equals(columnMgrEO.getIndicatorId())) {
                    getColumnsByParentId(map.get(eo.getParentId()), map, list);
                    return;
                }
            }
            list.add(columnMgrEO);
            getColumnsByParentId(map.get(eo.getParentId()), map, list);
        }
    }

}
