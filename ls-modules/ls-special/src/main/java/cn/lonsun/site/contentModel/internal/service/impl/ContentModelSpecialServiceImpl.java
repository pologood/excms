package cn.lonsun.site.contentModel.internal.service.impl;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.site.contentModel.internal.dao.IContentModelSpecialDao;
import cn.lonsun.site.contentModel.internal.entity.ContentModelEO;
import cn.lonsun.site.contentModel.internal.entity.ModelTemplateEO;
import cn.lonsun.site.contentModel.internal.service.IContentModelSpecialService;
import cn.lonsun.site.contentModel.internal.service.IModelTemplateSpecialService;
import cn.lonsun.site.contentModel.vo.ColumnTypeConfigVO;
import cn.lonsun.site.contentModel.vo.ContentModelVO;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.SysLog;
import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.swing.text.html.parser.ContentModel;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/26.
 */
@Service
public class ContentModelSpecialServiceImpl extends MockService<ContentModelEO> implements IContentModelSpecialService {
    @Resource
    private IContentModelSpecialDao contentModelSpecialDao;
    @Resource
    private IModelTemplateSpecialService modelTplSpecialService;

    @Override
    public List<ContentModelVO> getByCodes(Long siteId, String[] codes) {
        return contentModelSpecialDao.getByCodes(siteId, codes);
    }

    @Override
    public ContentModelEO getByCode(String code) {
        ContentModelEO eo = CacheHandler.getEntity(ContentModelEO.class, CacheGroup.CMS_CODE, code);
        return eo;
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

        modelTplSpecialService.delTpls(modelId);
        return "1";
    }

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
            List<ModelTemplateEO> tplList = modelTplSpecialService.getEntities(ModelTemplateEO.class, map);
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
        modelTplSpecialService.saveVO(vo, eo.getId());
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
}
