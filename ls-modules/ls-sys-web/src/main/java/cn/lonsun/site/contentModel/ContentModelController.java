package cn.lonsun.site.contentModel;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.vo.JSONConvertUtil;
import cn.lonsun.content.leaderwin.internal.service.ILeaderInfoService;
import cn.lonsun.content.leaderwin.vo.LeaderInfoVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.site.contentModel.internal.entity.ContentModelEO;
import cn.lonsun.site.contentModel.internal.service.IContentModelService;
import cn.lonsun.site.contentModel.vo.ColumnTypeConfigVO;
import cn.lonsun.site.contentModel.vo.ContentModelParaVO;
import cn.lonsun.site.contentModel.vo.ContentModelVO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import cn.lonsun.site.template.internal.entity.TemplateConfEO;
import cn.lonsun.site.template.internal.service.ITplConfService;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import cn.lonsun.util.DataDictionaryUtil;
import cn.lonsun.util.LoginPersonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

import static cn.lonsun.cache.client.CacheHandler.getEntity;
import static cn.lonsun.util.DataDictionaryUtil.getDDList;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-10-8<br/>
 */
@Controller
@RequestMapping("contentModel")
public class ContentModelController extends BaseController {
    @Autowired
    private IContentModelService contModelService;

    @Autowired
    private IOrganService organService;

    @Autowired
    private IColumnConfigService columnService;

    @Autowired
    private ITplConfService tplConfService;

    @Autowired
    private ILeaderInfoService leaderInfoService;

    /**
     * 去往列表页面
     *
     * @param isPublic
     * @param model
     * @return
     */
    @RequestMapping("getList")
    public String getList(Integer isPublic, Model model) {
        if (isPublic == null) {
            isPublic = 0;
        }
        model.addAttribute("isPublic", isPublic);
        Long siteId = LoginPersonUtil.getSiteId();
        if (siteId != null) {
            SiteMgrEO siteMgrEO = getEntity(SiteMgrEO.class, siteId);
            if (siteMgrEO != null) {
                Integer isWap = siteMgrEO.getIsWap();
                model.addAttribute("isWap", isWap);
            }
        }
        if (isPublic == 1) {//公共内容模型
            return "site/contModel/public_model_list";
        }
        return "site/contModel/model_list";
    }

    /**
     * 获取分页列表
     *
     * @param pageIndex
     * @param pageSize
     * @param name
     * @param isPublic
     * @return
     */
    @RequestMapping("getPage")
    @ResponseBody
    public Object getPage(Long pageIndex, Integer pageSize, String name, Integer isPublic) {
        Long siteId = LoginPersonUtil.getSiteId();
        Pagination page = contModelService.getPage(pageIndex, pageSize, name, siteId, isPublic);
        return page;
    }

    /**
     * 去往编辑页面
     *
     * @param id
     * @param siteId
     * @param isPublic
     * @param model
     * @return
     */
    @RequestMapping("toEdit")
    public String toEdit(Long id, Long siteId, Integer isPublic, Model model) {
        if (id == null) {
            model.addAttribute("id", 0L);
        } else {
            model.addAttribute("id", id);
        }
        model.addAttribute("isFlag", "0");
        model.addAttribute("siteId", siteId);
        model.addAttribute("isPublic", isPublic);
        model.addAttribute("isAddModel", false);
        if (null != isPublic && isPublic == 1) {
            return "site/contModel/public_model_edit";
        }

        List<TemplateConfEO> columnList = CacheHandler.getList(TemplateConfEO.class, CacheGroup.CMS_JOIN_ID, siteId, "column");
        List<TemplateConfEO> contentList = CacheHandler.getList(TemplateConfEO.class, CacheGroup.CMS_JOIN_ID, siteId, "content");
        model.addAttribute("columnList", columnList);
        model.addAttribute("contentList", contentList);
        SiteMgrEO siteMgrEO = CacheHandler.getEntity(SiteMgrEO.class, siteId);
        if (siteMgrEO != null) {
            model.addAttribute("isWap", siteMgrEO.getIsWap());
        }
        return "site/contModel/model_edit";
    }

    /**
     * 获取内容模型实体类
     *
     * @param id
     * @param isPublic
     * @return
     */
    @RequestMapping("getVO")
    @ResponseBody
    public Object getVO(Long id, Integer isPublic) {
        ContentModelVO vo = new ContentModelVO();
        Long siteId = LoginPersonUtil.getSiteId();
        if (id == null || id == 0) {
            vo.setType(1);
            if (isPublic == 1) {
                //设置站点绑定的一套模板
                vo.setSiteId(null);
            } else {
                vo.setSiteId(siteId);
            }
            return getObject(vo);
        }
        vo = contModelService.getVO(id, siteId);
        if (vo == null) {
            return getObject();
        }
        if (!AppUtil.isEmpty(vo.getContent())) {
            ColumnTypeConfigVO configVO = JSONConvertUtil.toObejct(vo.getContent(), ColumnTypeConfigVO.class);
            if ("guestBook".equals(vo.getModelTypeCode()) || "messageBoard".equals(vo.getModelTypeCode())) {
                if (null != configVO.getRecType() && configVO.getRecType() == 0) {
                    vo.setRecUnitIds(configVO.getRecUnitIds());
                    if (!StringUtils.isEmpty(configVO.getRecUnitIds())) {
                        Long[] idArr = AppUtil.getLongs(configVO.getRecUnitIds(), ",");
                        String nameStr = getOrganName(idArr);
                        vo.setRecUnitNames(nameStr);
                    }

                } else if (null != configVO.getRecType() && configVO.getRecType() == 1) {
                    vo.setRecUserIds(configVO.getRecUserIds());
//					String[] userIds=AppUtil.getStrings(configVO.getRecUserIds(),",");
//					String userNames="";
//					if(userIds!=null&&userIds.length>0){
//						for(int i=0;i<userIds.length;i++){
//							DataDictVO dictVO=DataDictionaryUtil.getItem("guest_book_rec_users",userIds[i]);
//							if(dictVO!=null){
//								userNames+=dictVO.getKey()+",";
//							}
//						}
//						userNames=userNames.substring(0,userNames.length());
//					}
//
//					userNames=userNames+","+configVO.getRecUserNames();
                    vo.setRecUserNames(configVO.getRecUserNames());
                    if (null != configVO.getTurn() && configVO.getTurn() == 1) {
                        vo.setTurnUnitIds(configVO.getTurnUnitIds());
                        if (!StringUtils.isEmpty(configVO.getTurnUnitIds())) {
                            Long[] idArr = AppUtil.getLongs(configVO.getTurnUnitIds(), ",");
                            String nameStr = getOrganName(idArr);
                            vo.setTurnUnitNames(nameStr);
                        }
                    }

                }
                vo.setTurn(configVO.getTurn());
                vo.setRecType(configVO.getRecType());
                vo.setIsLoginGuest(configVO.getIsLoginGuest());
                vo.setClassCodes(configVO.getClassCodes());
                vo.setClassNames(configVO.getClassNames());
                vo.setIsRedYellow(configVO.getIsRedYellow());
                vo.setLimitDay(configVO.getLimitDay());
                vo.setRedCardDay(configVO.getRedCardDay());
                vo.setYellowCardDay(configVO.getYellowCardDay());
                vo.setDatePool(configVO.getDatePool());
                vo.setIsAssess(configVO.getIsAssess());
                vo.setAssessDay(configVO.getAssessDay());
                vo.setStatusCode(configVO.getStatusCode());
                vo.setStatusName(configVO.getStatusName());
                vo.setIsLocalUnit(configVO.getIsLocalUnit());
            } else if ("articleNews".equals(vo.getModelTypeCode()) || "pictureNews".equals(vo.getModelTypeCode())) {
                vo.setPicHeight(configVO.getPicHeight());
                vo.setPicWidth(configVO.getPicWidth());
                vo.setContentWidth(configVO.getContentWidth());
                vo.setIsComment(configVO.getIsComment());
                vo.setIsWater(configVO.getIsWater());
                vo.setIsHotWord(configVO.getIsHotWord());
                vo.setIsSensitiveWord(configVO.getIsSensitiveWord());
                vo.setIsEasyWord(configVO.getIsEasyWord());
            } else if ("videoNews".equals(vo.getModelTypeCode()) || "ordinaryPage".equals(vo.getModelTypeCode()) || "fileDownload".equals(vo.getModelTypeCode())
                    || "collectInfo".equals(vo.getModelTypeCode()) || "interviewInfo".equals(vo.getModelTypeCode()) || "leaderInfo".equals(vo.getModelTypeCode())) {
                vo.setIsLoginGuest(configVO.getIsLoginGuest());
                vo.setPicHeight(configVO.getPicHeight());
                vo.setPicWidth(configVO.getPicWidth());
                vo.setContentWidth(configVO.getContentWidth());
                vo.setIsWater(configVO.getIsWater());
            } else if ("onlinePetition".equals(vo.getModelTypeCode()) || "onlineDeclaration".equals(vo.getModelTypeCode())) {
                vo.setRecUnitIds(configVO.getRecUnitIds());
                if (!StringUtils.isEmpty(configVO.getRecUnitIds())) {
                    Long[] idArr = AppUtil.getLongs(configVO.getRecUnitIds(), ",");
                    String nameStr = getOrganName(idArr);
                    vo.setRecUnitNames(nameStr);
                }
                vo.setRecType(configVO.getRecType());
                vo.setStatusCode(configVO.getStatusCode());
                vo.setStatusName(configVO.getStatusName());
            } else if ("workGuide".equals(vo.getModelTypeCode()) || "relatedRule".equals(vo.getModelTypeCode())
                    || "sceneService".equals(vo.getModelTypeCode()) || "tableResources".equals(vo.getModelTypeCode())) {
                vo.setRecUnitIds(configVO.getRecUnitIds());
                if (!StringUtils.isEmpty(configVO.getRecUnitIds())) {
                    Long[] idArr = AppUtil.getLongs(configVO.getRecUnitIds(), ",");
                    String nameStr = getOrganName(idArr);
                    vo.setRecUnitNames(nameStr);
                }
                vo.setRecType(configVO.getRecType());
                if ("workGuide".equals(vo.getModelTypeCode()) || "sceneService".equals(vo.getModelTypeCode())) {
                    vo.setIsConsole(configVO.getIsConsole());
                    vo.setIsComplaint(configVO.getIsComplaint());
                    vo.setIsDeclaration(configVO.getIsDeclaration());
                    vo.setIsVisit(configVO.getIsVisit());
                    vo.setConsoleLink(configVO.getConsoleLink());
                    vo.setComplaintLink(configVO.getComplaintLink());
                    vo.setDeclarationLink(configVO.getDeclarationLink());
                    vo.setVisitCount(configVO.getVisitCount());
                }
            } else if ("bbs".equals(vo.getModelTypeCode())) {
                contModelService.setContentModelVO(vo);
            }
            vo.setOrderTypeCode(configVO.getOrderTypeCode());
            vo.setIsEnableBeauty(configVO.getIsEnableBeauty());
        }
        return getObject(vo);
    }

    /**
     * 删除内容模型
     *
     * @param modelId
     * @return
     */
    @RequestMapping("delEO")
    @ResponseBody
    public Object delEO(Long modelId) {
        if (null == modelId) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请选择要删除的内容模型");
        }
        ContentModelEO eo = getEntity(ContentModelEO.class, modelId);
        if (eo == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "要删除的内容模型不存在");
        }
        List<ColumnMgrEO> list = columnService.getColumnByContentModelCode(eo.getSiteId(), eo.getCode());
        if (list != null && list.size() > 0) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "该内容模型已被栏目绑定,请先解除栏目绑定");
        }
        String flag = contModelService.delEO(modelId);
        return getObject(flag);
    }

    /**
     * 获取模板
     *
     * @param type
     * @return
     */
    @RequestMapping("getTemplate")
    @ResponseBody
    public Object getTemplate(Integer type) {
        Long siteId = LoginPersonUtil.getSiteId();
        String tempType = "";
        if (type == 0) {
            tempType = "site_index";
        } else if (type == 1) {//栏目模板
            tempType = "column";
        } else if (type == 2) {//文章页模板
            tempType = "content";
        } else if (type == 3) {//公共模板
            tempType = "common";
        } else {
            return ajaxErr("参数出错");
        }
//		List<TemplateConfEO> list= CacheHandler.getList(TemplateConfEO.class,CacheGroup.CMS_JOIN_ID, siteId , tempType);
        List<TemplateConfEO> list = (List<TemplateConfEO>) tplConfService.getByTemptype(siteId, tempType);
        return getObject(list);
    }

    /**
     * 校验名称是否存在
     *
     * @param name
     * @param modelId
     * @param siteId
     * @param isPublic
     * @return
     */
    @RequestMapping("checkNameExisted")
    @ResponseBody
    public Object checkNameExisted(String name, Long modelId, Long siteId, Integer isPublic) {
        boolean b = contModelService.checkNameExisted(name, modelId, siteId, isPublic);
        return getObject(b);
    }

    /**
     * 校验Code值是否存在
     *
     * @param code
     * @param modelId
     * @param siteId
     * @param isPublic
     * @return
     */
    @RequestMapping("checkCodeExisted")
    @ResponseBody
    public Object checkCodeExisted(String code, Long modelId, Long siteId, Integer isPublic) {
        boolean b = contModelService.checkCodeExisted(code, modelId, siteId, isPublic);
        return getObject(b);
    }

    /**
     * 检查栏目类型是否存在
     *
     * @param modelTypeCode
     * @param siteId
     * @param modelId
     * @param isPublic
     * @return
     */
    @RequestMapping("checkModelType")
    @ResponseBody
    public Object checkModelType(String modelTypeCode, Long siteId, Long modelId, Integer isPublic) {
        if (StringUtils.isEmpty(modelTypeCode) || siteId == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请选择栏目类型");
        }
        boolean b = true;
        if ("net_work".equals(modelTypeCode) ||
                "virtualBBS".equals(modelTypeCode) ||
                //"netClassify".equals(modelTypeCode)||
                //"tableResources".equals(modelTypeCode)||"relatedRule".equals(modelTypeCode)||
                //"workGuide".equals(modelTypeCode)||"sceneService".equals(modelTypeCode)||
                "InteractiveVirtual".equals(modelTypeCode)) {
            b = contModelService.checkModelType(modelTypeCode, siteId, modelId, isPublic);
        }
        return getObject(b);
    }

    /**
     * 保存修改内容模型
     *
     * @param modelVO
     * @return
     */
    @RequestMapping("saveEO")
    @ResponseBody
    public Object saveEO(ContentModelVO modelVO) {
        if (modelVO.getName() == null || modelVO.getName().trim() == "") {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "名称不为空");
        }
        if (StringUtils.isEmpty(modelVO.getModelTypeCode())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "栏目类型不能为空");
        }
        if (1 != modelVO.getIsPublic() && modelVO.getColumnTempId() == null && !"linksMgr".equals(modelVO.getModelTypeCode())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "二级页面模板不能为空");
        }
        ContentModelEO eo = contModelService.saveEO(modelVO);
        return getObject(eo);
    }

    /**
     * 获取栏目类型
     *
     * @return
     */
    @RequestMapping("getModelType")
    @ResponseBody
    public Object getColumnType() {
        return getDDList("column_type");

    }

    /**
     * 去往选择接收单位页面
     *
     * @return
     */
    @RequestMapping("getUnitIds")
    public String getUnitIds() {
//		if(AppUtil.isEmpty(recUnitIds)){
//			model.addAttribute("recUnitIds","");
//			model.addAttribute("unitList",JSON.toJSON(new ArrayList<Object>()));
//		}else{
//			model.addAttribute("recUnitIds",recUnitIds);
//			List<OrganEO> list=organService.getEntities(OrganEO.class,AppUtil.getLongs(recUnitIds,","));
//			model.addAttribute("unitList", JSON.toJSON(list));
//		}
        return "site/contModel/choose_unit";
    }

    /**
     * 去往选择流转单位页面
     *
     * @return
     */
    @RequestMapping("getTurnUnitIds")
    public String getTurnUnitIds() {

        return "site/contModel/turn_unit";
    }

    /**
     * 获取选择的接收单位
     *
     * @param recUnitIds
     * @return
     */
    @RequestMapping("getChooseUnit")
    @ResponseBody
    public Object getChooseUnit(String recUnitIds) {
        if (StringUtils.isEmpty(recUnitIds)) {
            return getObject();
        }
        List<OrganEO> list = organService.getEntities(OrganEO.class, AppUtil.getLongs(recUnitIds, ","));
        if (list != null) {
            return getObject(list);
        } else {
            return getObject();
        }

    }

    /**
     * 去往选择接收人员页面
     *
     * @param model
     * @param siteId
     * @return
     */
    @RequestMapping("getUserIds")
    public String getUserIds(Model model, Long siteId) {
        model.addAttribute("siteId", siteId);
        return "site/contModel/choose_user";
    }

    /**
     * 去往选择留言类型页面
     *
     * @param model
     * @param classCodes
     * @return
     */
    @RequestMapping("getClassCodes")
    public String getClassCodes(Model model, String classCodes) {
        if (AppUtil.isEmpty(classCodes)) {
            model.addAttribute("classCodes", "");
        } else {
            model.addAttribute("classCodes", classCodes);
        }
        return "site/contModel/choose_codes";
    }

    /**
     * 去往选择处理状态页面
     *
     * @param model
     * @param dealStatus
     * @return
     */
    @RequestMapping("getDealStatus")
    public String getDealStatus(Model model, String dealStatus) {
        if (AppUtil.isEmpty(dealStatus)) {
            model.addAttribute("classCodes", "");
        } else {
            model.addAttribute("classCodes", dealStatus);
        }
        return "site/contModel/deal_status";
    }


    /**
     * 获取当前站点绑定单位下的所有部门
     *
     * @return
     */
    @RequestMapping("getOrgansByUnitId")
    @ResponseBody
    public Object getOrgansByUnitId() {
        Long siteId = LoginPersonUtil.getSiteId();
        SiteMgrEO eo = getEntity(SiteMgrEO.class, siteId);
        String unitIds = eo.getUnitIds();
        List<OrganEO> list = new ArrayList<OrganEO>();
        if (!StringUtils.isEmpty(unitIds)) {
            Long[] arr = AppUtil.getLongs(unitIds, ",");
            if (arr != null && arr.length > 0) {
                list = organService.getOrgansByDn(arr[0], OrganEO.Type.Organ.toString());
            }
        }
        return getObject(list);
    }

    /**
     * 获取在数据字典里配置的留言接收人
     *
     * @return
     */
    @RequestMapping("getPersonsByUnitId")
    @ResponseBody
    public Object getPersonsByUnitId() {
        List<DataDictVO> list = getDDList("guest_book_rec_users");
        return getObject(list);
    }

    /**
     * 获取领导之窗的所有领导信息
     *
     * @param siteId
     * @return
     */
    @RequestMapping("getLeaderInfo")
    @ResponseBody
    public Object getLeaderInfo(Long siteId) {
        List<LeaderInfoVO> list = leaderInfoService.getLeaderInfo(siteId);
        return getObject(list);


    }


    /**
     * 获取在内容模型中绑定的接收单位
     *
     * @param columnId
     * @return
     */
    @RequestMapping("getRecUnits")
    @ResponseBody
    public List<ContentModelParaVO> getRecUnits(Long columnId) {
        List<ContentModelParaVO> list = contModelService.getParam(columnId, LoginPersonUtil.getSiteId(), null);
        return list;
    }

    /**
     * 根据组织ID数组获取组织名称字符串
     *
     * @param idArr
     * @param organEO
     * @return
     */
    private String getOrganName(Long[] idArr) {
        OrganEO organEO = null;
        String nameStr = "";
        if (idArr != null && idArr.length > 0) {
            for (int i = 0; i < idArr.length; i++) {
                organEO = organService.getEntity(OrganEO.class, idArr[i]);
                if (organEO != null) {
                    nameStr += organEO.getName() + ",";
                }
            }
            if (nameStr.length() > 1) {
                nameStr = nameStr.substring(0, nameStr.length() - 1);
            }
        }
        return nameStr;
    }

    /**
     * 根据栏目类型Code值获取数据字典里配置的相匹配的排序方式
     *
     * @param code
     * @return
     */
    @RequestMapping("getOrderType")
    @ResponseBody
    public Object getOrderType(String code) {
        List<DataDictVO> list = DataDictionaryUtil.getItemList("order_type", LoginPersonUtil.getSiteId());
        List<DataDictVO> list1 = new ArrayList<DataDictVO>();
        if (list != null && list.size() > 0) {
            for (DataDictVO vo : list) {
                if (vo.getCode().contains(code)) {
                    list1.add(vo);
                }
            }
        }
        return getObject(list1);
    }

    /**
     * 获取数据字典里配置的办理状态
     *
     * @return
     */
    @RequestMapping("getStatusList")
    @ResponseBody
    public Object getStatusList() {
        return DataDictionaryUtil.getItemList("deal_status", LoginPersonUtil.getSiteId());

    }

}
