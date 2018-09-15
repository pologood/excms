package cn.lonsun.site.contentModel.internal.service.impl;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.vo.JSONConvertUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.site.contentModel.internal.dao.IContentModelDao;
import cn.lonsun.site.contentModel.internal.entity.ContentModelEO;
import cn.lonsun.site.contentModel.internal.entity.ModelTemplateEO;
import cn.lonsun.site.contentModel.internal.service.IContentModelService;
import cn.lonsun.site.contentModel.internal.service.IModelTemplateService;
import cn.lonsun.site.contentModel.vo.ColumnTypeConfigVO;
import cn.lonsun.site.contentModel.vo.ContentModelParaVO;
import cn.lonsun.site.contentModel.vo.ContentModelVO;
import cn.lonsun.site.site.internal.entity.ColumnConfigRelEO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.service.IColumnConfigRelService;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.SysLog;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static cn.lonsun.common.util.AppUtil.getLongs;

/**
 * 内容模型Service层<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-10-8<br/>
 */
@Service("contentModelService")
public class ContentModelServiceImpl extends MockService<ContentModelEO> implements IContentModelService {

    @Autowired
    private IContentModelDao contModelDao;

    @Autowired
    private IModelTemplateService modelTplService;

    @Autowired
    private IOrganService organService;

    @Autowired
    private IColumnConfigRelService relService;

    @Override
    public Pagination getPage(Long pageIndex, Integer pageSize, String name, Long siteId, Integer isPublic) {
        return contModelDao.getPage(pageIndex, pageSize, name, siteId, isPublic);
    }

    /**
     * 删除
     *
     * @param modelId
     * @return
     */
    @Override
    public String delEO(Long modelId) {
        ContentModelEO eo = getEntity(ContentModelEO.class,modelId);
        delete(ContentModelEO.class, modelId);
        SysLog.log("【站群管理】删除内容模型，模型名称：" + eo.getName(), "ContentModelEO", CmsLogEO.Operation.Delete.toString());
        modelTplService.delTpls(modelId);
        return "1";
    }

    /**
     * 验证名称是否存在
     *
     * @param name
     * @param modelId
     * @param isPublic
     * @return
     */
    @Override
    public boolean checkNameExisted(String name, Long modelId, Long siteId, Integer isPublic) {
        Map<String, Object> map = new HashMap<String, Object>();
        String name1 = name.trim();
        map.put("name", name1);
        map.put("isPublic", isPublic);
        map.put("siteId", siteId);
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        List<ContentModelEO> list = contModelDao.getEntities(ContentModelEO.class, map);
        if (list == null || list.size() == 0) {
            return true;
        }
        ContentModelEO eo = list.get(0);
        if (eo.getId().equals(modelId)) {
            return true;
        }
        return false;
    }

    /**
     * 验证code值是否存在
     *
     * @param code
     * @param modelId
     * @param isPublic
     * @return
     */
    @Override
    public boolean checkCodeExisted(String code, Long modelId, Long siteId, Integer isPublic) {
        Map<String, Object> map = new HashMap<String, Object>();
        String code1 = code.trim();
        map.put("code", code1);
        map.put("siteId", siteId);
        map.put("isPublic", isPublic);
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        List<ContentModelEO> list = contModelDao.getEntities(ContentModelEO.class, map);
        if (list == null || list.size() == 0) {
            return true;
        }
        ContentModelEO eo = list.get(0);
        if (eo.getId().equals(modelId)) {
            return true;
        }
        return false;
    }

    @Override
    public String getFirstModelType(String code) {
        String modelTypeCode = null;
        if (code == null) {
            return null;
        }
        ContentModelEO modelEO = CacheHandler.getEntity(ContentModelEO.class, CacheGroup.CMS_CODE, code);
        if (modelEO != null) {
            Map<String, Object> map1 = new HashMap<String, Object>();
            map1.put("modelId", modelEO.getId());
            map1.put("type", 1);
            map1.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
            List<ModelTemplateEO> list1 = modelTplService.getEntities(ModelTemplateEO.class, map1);
            if (list1 != null && list1.size() > 0) {
                ModelTemplateEO modelTplEO = list1.get(0);
                modelTypeCode = modelTplEO.getModelTypeCode();
            }
        }
        return modelTypeCode;
    }

    @Override
    public ContentModelVO getVO(Long id, Long siteId) {
        return contModelDao.getVO(id, siteId);
    }


    /**
     * 保存
     *
     * @param vo
     */
    @Override
    public ContentModelEO saveEO(ContentModelVO vo) {
        ContentModelEO eo = null;
        if (vo.getId() == null) {
            eo = new ContentModelEO();
            eo.setName(vo.getName());
            eo.setDescription(vo.getDescription());
            eo.setCode(new Date().getTime() + "");
            eo.setSiteId(vo.getSiteId());
            eo.setIsPublic(vo.getIsPublic());
//			if("net_work".equals(vo.getModelTypeCode())||"netClassify".equals(vo.getModelTypeCode())||
//					"tableResources".equals(vo.getModelTypeCode())||"relatedRule".equals(vo.getModelTypeCode())
//					||"sceneService".equals(vo.getModelTypeCode())||"tableResources".equals(vo.getModelTypeCode())
//					){
//				eo.setCode(vo.getModelTypeCode());
//			}else if("InteractiveVirtual".equals(vo.getModelTypeCode())){
//				eo.setCode(vo.getModelTypeCode());
//			}
            if ("net_work".equals(vo.getModelTypeCode()) || "InteractiveVirtual".equals(vo.getModelTypeCode()) ||
                "virtualBBS".equals(vo.getModelTypeCode()) || "specialVirtual".equals(vo.getModelTypeCode())) {
                eo.setCode(vo.getModelTypeCode());
            }
            ColumnTypeConfigVO configVO = getConfigVO(vo);
            configVO.setIsEnableBeauty(vo.getIsEnableBeauty());
            configVO.setOrderTypeCode(vo.getOrderTypeCode());

            eo.setContent(JSON.toJSONString(configVO));
            saveEntity(eo);
            SysLog.log("【站群管理】新增内容模型，模型名称：" + eo.getName(), "ContentModelEO", CmsLogEO.Operation.Add.toString());

        } else {
            eo = getEntity(ContentModelEO.class, vo.getId());
            String oldCode = "";
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("modelId", vo.getId());
            List<ModelTemplateEO> tplList = modelTplService.getEntities(ModelTemplateEO.class, map);
            if (tplList != null && tplList.size() > 0) {
                ModelTemplateEO tplEO = tplList.get(0);
                oldCode = tplEO.getModelTypeCode();
                if (!tplEO.getModelTypeCode().equals(vo.getModelTypeCode())) {

					/*if("net_work".equals(tplEO.getModelTypeCode())||"netClassify".equals(tplEO.getModelTypeCode())||
                            "tableResources".equals(tplEO.getModelTypeCode())||"relatedRule".equals(tplEO.getModelTypeCode())
							||"sceneService".equals(tplEO.getModelTypeCode())
							||"InteractiveVirtual".equals(tplEO.getModelTypeCode())||"tableResources".equals(tplEO.getModelTypeCode())){
						if("net_work".equals(vo.getModelTypeCode())||"netClassify".equals(vo.getModelTypeCode())||
								"tableResources".equals(vo.getModelTypeCode())||"relatedRule".equals(vo.getModelTypeCode())
								||"sceneService".equals(vo.getModelTypeCode())
								||"InteractiveVirtual".equals(vo.getModelTypeCode())||"tableResources".equals(vo.getModelTypeCode())){
							eo.setCode(vo.getModelTypeCode());
						}else{
							eo.setCode(new Date().getTime()+"");
						}
					}else{
						if("net_work".equals(vo.getModelTypeCode())||"netClassify".equals(vo.getModelTypeCode())||
								"tableResources".equals(vo.getModelTypeCode())||"relatedRule".equals(vo.getModelTypeCode())
								||"sceneService".equals(vo.getModelTypeCode())
								||"InteractiveVirtual".equals(vo.getModelTypeCode())){
							eo.setCode(vo.getModelTypeCode());
						}
					}*/
                    if ("InteractiveVirtual".equals(tplEO.getModelTypeCode()) || "net_work".equals(vo.getModelTypeCode()) ||
                        "virtualBBS".equals(tplEO.getModelTypeCode())) {
                        if ("net_work".equals(vo.getModelTypeCode()) || "InteractiveVirtual".equals(vo.getModelTypeCode()) ||
                            "virtualBBS".equals(vo.getModelTypeCode())) {
                            eo.setCode(vo.getModelTypeCode());
                        } else {
                            eo.setCode(new Date().getTime() + "");
                        }
                    } else {
                        if ("net_work".equals(vo.getModelTypeCode()) || "InteractiveVirtual".equals(vo.getModelTypeCode()) ||
                            "virtualBBS".equals(vo.getModelTypeCode()) || "specialVirtual".equals(vo.getModelTypeCode())) {
                            eo.setCode(vo.getModelTypeCode());
                        }
                    }
                }

            }
            eo.setName(vo.getName());
            eo.setDescription(vo.getDescription());
            eo.setSiteId(vo.getSiteId());
            ColumnTypeConfigVO configVO = getConfigVO(vo);
            configVO.setIsEnableBeauty(vo.getIsEnableBeauty());
            configVO.setIsSensitiveWord(vo.getIsSensitiveWord());
            configVO.setIsHotWord(vo.getIsHotWord());
            configVO.setIsEasyWord(vo.getIsEasyWord());
            eo.setContent(JSON.toJSONString(configVO));
            updateEntity(eo);
            SysLog.log("【站群管理】修改内容模型，模型名称：" + eo.getName(), "ContentModelEO", CmsLogEO.Operation.Update.toString());

        }
        modelTplService.saveVO(vo, eo.getId());
        return eo;
    }

    public ColumnTypeConfigVO getConfigVO(ContentModelVO vo) {
        ColumnTypeConfigVO configVO = new ColumnTypeConfigVO();
        if ("guestBook".equals(vo.getModelTypeCode()) || "messageBoard".equals(vo.getModelTypeCode())) {
            if (null != vo.getRecType() && vo.getRecType() == 0) {
                configVO.setRecUnitIds(vo.getRecUnitIds());
                configVO.setRecUnitNames(vo.getRecUnitNames());
            } else if (null != vo.getRecType() && vo.getRecType() == 1) {
                configVO.setRecUserIds(vo.getRecUserIds());
                configVO.setRecUserNames(vo.getRecUserNames());
                if (null != vo.getTurn() && vo.getTurn() == 1) {
                    configVO.setTurnUnitIds(vo.getTurnUnitIds());
                    configVO.setTurnUnitNames(vo.getTurnUnitNames());
                }


            }
            configVO.setTurn(vo.getTurn());
            configVO.setRecType(vo.getRecType());
            configVO.setIsLoginGuest(vo.getIsLoginGuest());
            configVO.setClassCodes(vo.getClassCodes());
            configVO.setClassNames(vo.getClassNames());

            configVO.setIsRedYellow(vo.getIsRedYellow());
            configVO.setYellowCardDay(vo.getYellowCardDay());
            configVO.setRedCardDay(vo.getRedCardDay());
            configVO.setLimitDay(vo.getLimitDay());
            configVO.setDatePool(vo.getDatePool());
            configVO.setIsAssess(vo.getIsAssess());
            configVO.setAssessDay(vo.getAssessDay());
            configVO.setStatusCode(vo.getStatusCode());
            configVO.setStatusName(vo.getStatusName());
            configVO.setIsLocalUnit(vo.getIsLocalUnit());

        } else if ("linksMgr".equals(vo.getModelTypeCode())) {
            configVO.setHeight(vo.getHeight());
            configVO.setWidth(vo.getWidth());
            configVO.setIsLogo(vo.getIsLogo());
            configVO.setNum(vo.getNum());
        } else if ("articleNews".equals(vo.getModelTypeCode()) || "pictureNews".equals(vo.getModelTypeCode())) {
            configVO.setPicHeight(vo.getPicHeight());
            configVO.setPicWidth(vo.getPicWidth());
            configVO.setContentWidth(vo.getContentWidth());
            configVO.setIsComment(vo.getIsComment());
            configVO.setOrderTypeCode(vo.getOrderTypeCode());
            configVO.setIsWater(vo.getIsWater());
            configVO.setIsSensitiveWord(vo.getIsSensitiveWord());
            configVO.setIsHotWord(vo.getIsHotWord());
            configVO.setIsEasyWord(vo.getIsEasyWord());
        } else if ("videoNews".equals(vo.getModelTypeCode())) {
            configVO.setPicHeight(vo.getPicHeight());
            configVO.setPicWidth(vo.getPicWidth());
            configVO.setContentWidth(vo.getContentWidth());
            configVO.setOrderTypeCode(vo.getOrderTypeCode());
            configVO.setIsWater(vo.getIsWater());
        } else if ("onlinePetition".equals(vo.getModelTypeCode()) || "onlineDeclaration".equals(vo.getModelTypeCode())) {
            configVO.setRecUnitIds(vo.getRecUnitIds());
            configVO.setRecUnitNames(vo.getRecUnitNames());
            configVO.setRecType(0);
            configVO.setStatusCode(vo.getStatusCode());
            configVO.setStatusName(vo.getStatusName());
        } else if ("workGuide".equals(vo.getModelTypeCode()) || "relatedRule".equals(vo.getModelTypeCode())
            || "sceneService".equals(vo.getModelTypeCode()) || "tableResources".equals(vo.getModelTypeCode())) {
            configVO.setRecUnitIds(vo.getRecUnitIds());
            configVO.setRecUnitNames(vo.getRecUnitNames());
            if (!StringUtils.isEmpty(configVO.getRecUnitIds())) {
                configVO.setRecType(0);
            }
            if ("workGuide".equals(vo.getModelTypeCode()) || "sceneService".equals(vo.getModelTypeCode())) {
                configVO.setIsConsole(vo.getIsConsole());
                configVO.setIsComplaint(vo.getIsComplaint());
                configVO.setIsDeclaration(vo.getIsDeclaration());
                configVO.setIsVisit(vo.getIsVisit());
                if (vo.getIsConsole() != null && vo.getIsConsole() == 1) {
                    configVO.setConsoleLink(vo.getConsoleLink());
                }
                if (vo.getIsComplaint() != null && vo.getIsComplaint() == 1) {
                    configVO.setComplaintLink(vo.getComplaintLink());
                }
                if (vo.getIsDeclaration() != null && vo.getIsDeclaration() == 1) {
                    configVO.setDeclarationLink(vo.getDeclarationLink());
                }
                if (vo.getIsVisit() != null && vo.getIsVisit() == 1) {
                    configVO.setVisitCount(vo.getVisitCount());
                }
            }
        } else if ("fileDownload".toString().equals(vo.getModelTypeCode())) {
            configVO.setOrderTypeCode(vo.getOrderTypeCode());
            configVO.setPicHeight(vo.getPicHeight());
            configVO.setPicWidth(vo.getPicWidth());
            configVO.setIsWater(vo.getIsWater());
        } else if ("ordinaryPage".toString().equals(vo.getModelTypeCode()) ||
            "collectInfo".equals(vo.getModelTypeCode()) || "interviewInfo".equals(vo.getModelTypeCode()) ||
            "leaderInfo".equals(vo.getModelTypeCode())) {
            configVO.setIsLoginGuest(vo.getIsLoginGuest());
            configVO.setPicHeight(vo.getPicHeight());
            configVO.setPicWidth(vo.getPicWidth());
            configVO.setContentWidth(vo.getContentWidth());
            configVO.setIsWater(vo.getIsWater());
        } else if ("public_content".toString().equals(vo.getModelTypeCode())) {
            configVO.setOrderTypeCode(vo.getOrderTypeCode());
        }
        return configVO;
    }

    /**
     * 获取列表
     *
     * @return
     */
    @Override
    public List<ContentModelEO> getList(Long siteId) {
        if (siteId == null) {
            siteId = LoginPersonUtil.getSiteId();
        }
        return CacheHandler.getList(ContentModelEO.class, CacheGroup.CMS_SITE_ID, siteId);
    }

    @Override
    public ContentModelEO getByCode(String code) {
        ContentModelEO eo = CacheHandler.getEntity(ContentModelEO.class, CacheGroup.CMS_CODE, code);
        return eo;
    }

    @Override
    public List<ContentModelParaVO> getParam(Long columnId, Long siteId, Integer isTurn) {
        ColumnMgrEO eo = CacheHandler.getEntity(ColumnMgrEO.class, columnId);
        List<ContentModelParaVO> list = new ArrayList<ContentModelParaVO>();
        if (eo == null) {
            return null;
        }
        if (IndicatorEO.Type.COM_Section.toString().equals(eo.getType())) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("isHide", false);
            map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
            map.put("siteId", siteId);
            List<ColumnConfigRelEO> relList = relService.getEntities(ColumnConfigRelEO.class, map);
            if (relList != null && relList.size() > 0) {
                eo.setContentModelCode(relList.get(0).getContentModelCode());
            }
        }
        if (AppUtil.isEmpty(eo.getContent())) {
            if (!StringUtils.isEmpty(eo.getContentModelCode())) {
                String code = eo.getContentModelCode();
                ContentModelEO contentModelEO = null;
                if ("net_work".equals(code) || "netClassify".equals(code) || "tableResources".equals(code) || "relatedRule".equals(code)
                    || "workGuide".equals(code) || "sceneService".equals(code) || "InteractiveVirtual".equals(code)) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("siteId", siteId);
                    map.put("code", code);
                    map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
                    List<ContentModelEO> list1 = getEntities(ContentModelEO.class, map);
                    if (list1 != null && list1.size() > 0) {
                        contentModelEO = list1.get(0);
                    }
                } else {
                    contentModelEO = CacheHandler.getEntity(ContentModelEO.class, CacheGroup.CMS_CODE, eo.getContentModelCode());
                }
                if (contentModelEO == null) {
                    return list;
                } else {
                    eo.setContent(contentModelEO.getContent());
                }
            } else {
                return list;
            }
        }
        ColumnTypeConfigVO configVO = JSONConvertUtil.toObejct(eo.getContent(), ColumnTypeConfigVO.class);
        String[] nameArr = null;
        if (configVO.getRecType() != null) {
            if (configVO.getRecType() == 0 && !StringUtils.isEmpty(configVO.getRecUnitIds())) {
                OrganEO organEO = null;
                Long[] idArr = null;
                //nameArr = AppUtil.getStrings(configVO.getRecUnitNames(), ",");
                idArr = getLongs(configVO.getRecUnitIds(), ",");
                for (int i = 0; i < idArr.length; i++) {
                    organEO = CacheHandler.getEntity(OrganEO.class, idArr[i]);
                    if (organEO != null) {
                        ContentModelParaVO newVO = new ContentModelParaVO();
                        newVO.setRecUnitId(idArr[i]);
                        newVO.setRecUnitName(organEO.getName());
                        newVO.setRecType(configVO.getRecType());
                        list.add(newVO);
                    }
                }
            } else if (configVO.getRecType() == 1 && !StringUtils.isEmpty(configVO.getRecUserIds())) {
                String[] idArr = null;
                if (isTurn != null && isTurn == 1) {
                    if (configVO.getTurn() == 1 && !AppUtil.isEmpty(configVO.getTurnUnitIds())) {
                        idArr = AppUtil.getStrings(configVO.getTurnUnitIds(), ",");
                        nameArr = AppUtil.getStrings(configVO.getTurnUnitNames(), ",");
                        if (nameArr.length == idArr.length) {
                            for (int i = 0; i < nameArr.length; i++) {
                                ContentModelParaVO newVO = new ContentModelParaVO();
                                newVO.setTurnUnitId(idArr[i]);
                                newVO.setTurnUnitName(nameArr[i]);
                                newVO.setTurn(configVO.getTurn());
                                list.add(newVO);
                            }
                        }
                    }
                } else {
                    nameArr = AppUtil.getStrings(configVO.getRecUserNames(), ",");
                    idArr = AppUtil.getStrings(configVO.getRecUserIds(), ",");
                    if (nameArr.length == idArr.length) {
                        for (int i = 0; i < nameArr.length; i++) {
                            ContentModelParaVO newVO = new ContentModelParaVO();
                            newVO.setRecUserId(idArr[i]);
                            newVO.setRecUserName(nameArr[i]);
                            newVO.setRecType(configVO.getRecType());
                            list.add(newVO);
                        }
                    }
                }

            }
        }
        return list;
    }

    @Override
    public List<ContentModelParaVO> getClassCode(Long columnId, Long siteId) {
        ColumnMgrEO eo = CacheHandler.getEntity(ColumnMgrEO.class, columnId);
        List<ContentModelParaVO> list = new ArrayList<ContentModelParaVO>();
        if (eo == null) {
            return null;
        }
        if (StringUtils.isEmpty(eo.getContent())) {
            if (!StringUtils.isEmpty(eo.getContentModelCode())) {
                ContentModelEO contentModelEO = null;
                String code = eo.getContentModelCode();
                if ("net_work".equals(code) || "netClassify".equals(code) || "tableResources".equals(code) || "relatedRule".equals(code)
                    || "workGuide".equals(code) || "sceneService".equals(code) || "InteractiveVirtual".equals(code)) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("siteId", siteId);
                    map.put("code", code);
                    map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
                    List<ContentModelEO> list1 = getEntities(ContentModelEO.class, map);
                    if (list1 != null && list1.size() > 0) {
                        contentModelEO = list1.get(0);
                    }
                } else {
                    contentModelEO = CacheHandler.getEntity(ContentModelEO.class, CacheGroup.CMS_CODE, code);
                }
                if (contentModelEO == null) {
                    return list;
                } else {
                    eo.setContent(contentModelEO.getContent());
                }
            } else {
                return list;
            }
        }
        ColumnTypeConfigVO configVO = JSONConvertUtil.toObejct(eo.getContent(), ColumnTypeConfigVO.class);
        String codes = configVO.getClassCodes();
        String names = configVO.getClassNames();
        if (AppUtil.isEmpty(codes) || AppUtil.isEmpty(names)) {
            return list;
        } else {
            String[] codeArr = AppUtil.getStrings(codes, ",");
            String[] nameArr = AppUtil.getStrings(names, ",");
            if (codeArr.length != nameArr.length) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "内容模型中留言类型保存出错");
            } else {
                for (int i = 0; i < codeArr.length; i++) {
                    ContentModelParaVO vo = new ContentModelParaVO();
                    vo.setClassCode(codeArr[i]);
                    vo.setClassName(nameArr[i]);
                    list.add(vo);
                }
            }
        }
        return list;
    }

    @Override
    public List<ContentModelParaVO> getDealStatus(Long columnId, Long siteId) {
        ColumnMgrEO eo = CacheHandler.getEntity(ColumnMgrEO.class, columnId);
        List<ContentModelParaVO> list = new ArrayList<ContentModelParaVO>();
        if (eo == null) {
            return null;
        }
        if (StringUtils.isEmpty(eo.getContent())) {
            if (!StringUtils.isEmpty(eo.getContentModelCode())) {
                ContentModelEO contentModelEO = null;
                String code = eo.getContentModelCode();
                if ("net_work".equals(code) || "netClassify".equals(code) || "tableResources".equals(code) || "relatedRule".equals(code)
                    || "workGuide".equals(code) || "sceneService".equals(code) || "InteractiveVirtual".equals(code)) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("siteId", siteId);
                    map.put("code", code);
                    map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
                    List<ContentModelEO> list1 = getEntities(ContentModelEO.class, map);
                    if (list1 != null && list1.size() > 0) {
                        contentModelEO = list1.get(0);
                    }
                } else {
                    contentModelEO = CacheHandler.getEntity(ContentModelEO.class, CacheGroup.CMS_CODE, code);
                }
                if (contentModelEO == null) {
                    return list;
                } else {
                    eo.setContent(contentModelEO.getContent());
                }
            } else {
                return list;
            }
        }
        ColumnTypeConfigVO configVO = JSONConvertUtil.toObejct(eo.getContent(), ColumnTypeConfigVO.class);
        String codes = configVO.getStatusCode();
        String names = configVO.getStatusName();
        if (AppUtil.isEmpty(codes) || AppUtil.isEmpty(names)) {
            return list;
        } else {
            String[] codeArr = AppUtil.getStrings(codes, ",");
            String[] nameArr = AppUtil.getStrings(names, ",");
            if (codeArr.length != nameArr.length) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "内容模型中办理状态保存出错");
            } else {
                for (int i = 0; i < codeArr.length; i++) {
                    ContentModelParaVO vo = new ContentModelParaVO();
                    vo.setStatusCode(codeArr[i]);
                    vo.setStatusName(nameArr[i]);
                    list.add(vo);
                }
            }
        }
        return list;
    }


    @Override
    public Boolean IsLoginComment(Long columnId) {
        ColumnMgrEO eo = CacheHandler.getEntity(ColumnMgrEO.class, columnId);
        if (!AppUtil.isEmpty(eo.getContent())) {
            ColumnTypeConfigVO configVO = JSONConvertUtil.toObejct(eo.getContent(), ColumnTypeConfigVO.class);
            if (!AppUtil.isEmpty(configVO)) {
                Integer b = configVO.getIsLoginGuest();
                if (b.equals(1)) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public boolean checkModelType(String modelTypeCode, Long siteId, Long modelId, Integer isPublic) {
        return contModelDao.checkModelType(modelTypeCode, siteId, modelId, isPublic);
    }

    @Override
    public void setContentModelVO(ContentModelVO vo) {
    }

    @Override
    public List<OrganEO> getAllBindUnit(Long siteId) {
        List<OrganEO> listOrgan = null;
        String codes = "workGuide";
        List<ContentModelVO> list = contModelDao.getByColumnTypeCode(siteId, codes);
        String ids = "";
        if (list != null && list.size() > 0) {
            for (ContentModelVO modelVO : list) {
                if (!StringUtils.isEmpty(modelVO.getContent())) {
                    ColumnTypeConfigVO configVO = JSONConvertUtil.toObejct(modelVO.getContent(), ColumnTypeConfigVO.class);
                    if (configVO != null && !StringUtils.isEmpty(configVO.getRecUnitIds())) {
                        Long[] unitIdArr = AppUtil.getLongs(configVO.getRecUnitIds(), ",");
                        for (Long unitId : unitIdArr) {
                            if (!ids.contains(unitId + ",")) {
                                ids += unitId + ",";
                            }
                        }
                    }
                }
            }
        }
        if (!StringUtils.isEmpty(ids)) {
            Long[] idAr = AppUtil.getLongs(ids, ",");
            listOrgan = organService.getEntities(OrganEO.class, idAr);
        }
        return listOrgan;
    }

    @Override
    public List<ContentModelVO> getByCodes(Long siteId, String[] codes) {
        return contModelDao.getByCodes(siteId, codes);
    }

    @Override
    public List<ContentModelVO> getByColumnTypeCode(Long siteId, String columnTypeCode) {
        return contModelDao.getByColumnTypeCode(siteId,columnTypeCode);
    }
}
