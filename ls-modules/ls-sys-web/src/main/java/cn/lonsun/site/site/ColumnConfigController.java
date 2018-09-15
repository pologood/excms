package cn.lonsun.site.site;

import cn.lonsun.activemq.MessageSenderUtil;
import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.entity.ContentReferRelationEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IContentReferRelationService;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.mongodb.base.IMongoDbFileServer;
import cn.lonsun.mongodb.vo.MongoFileVO;
import cn.lonsun.publicInfo.internal.service.IPublicCatalogOrganRelService;
import cn.lonsun.rbac.indicator.service.IIndicatorService;
import cn.lonsun.site.contentModel.internal.entity.ContentModelEO;
import cn.lonsun.site.contentModel.internal.entity.ModelTemplateEO;
import cn.lonsun.site.contentModel.internal.service.IContentModelService;
import cn.lonsun.site.contentModel.vo.ContentModelVO;
import cn.lonsun.site.site.internal.entity.ColumnConfigRelEO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.entity.ColumnTypeEO;
import cn.lonsun.site.site.internal.service.IColumnConfigRelService;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import cn.lonsun.site.site.internal.service.IColumnGuiDangConfigService;
import cn.lonsun.site.site.internal.service.IColumnTypeService;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.*;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import static cn.lonsun.cache.client.CacheHandler.getEntity;
import static cn.lonsun.cache.client.CacheHandler.getList;


/**
 * 栏目配置控制层<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-8-24 <br/>
 */
@Controller
@RequestMapping("columnConfig")
public class ColumnConfigController extends BaseController {
    @Autowired
    private IColumnConfigService columnConfigService;

    @Autowired
    private IColumnTypeService columnTypeService;

    @Autowired
    private IBaseContentService baseContentService;

    @Autowired
    private IColumnConfigRelService relService;

    @Autowired
    private IContentModelService modelService;

    @Resource(name = "ex_8_IndicatorServiceImpl")
    private IIndicatorService indicatorService;
    @Autowired
    private IMongoDbFileServer mongoDbFileServer;
    @Autowired
    private IColumnGuiDangConfigService columnGuiDangConfigService;
    @Autowired
    private IPublicCatalogOrganRelService publicCatalogOrganRelService;
    @Autowired
    private IContentReferRelationService contentReferRelationService;

    /**
     * 保存栏目信息
     *
     * @param columnVO
     * @return
     */
    @RequestMapping("saveColumnEO")
    @ResponseBody
    public Object saveColumnEO(ColumnMgrEO columnVO) {
        boolean editFlag = false;
        if(!AppUtil.isEmpty(columnVO.getIndicatorId())){
            editFlag = true;
        }

        //修改的时候，判断栏目引用是否存在闭环（循环引用）
        if(editFlag&&(!AppUtil.isEmpty(columnVO.getReferColumnIds())||!AppUtil.isEmpty(columnVO.getReferOrganCatIds()))){
            Long siteId = columnVO.getSiteId()==null?LoginPersonUtil.getSiteId():columnVO.getSiteId();
            String columnIdStr = columnVO.getIndicatorId()+"_"+siteId;
            Map<String,String> resultMap = baseContentService.getReferColumnCats(columnVO.getReferColumnIds(),columnVO.getReferOrganCatIds(),columnIdStr);
            String referColumnIds = resultMap.get("referColumnIds");
            String columnSiteId = columnVO.getIndicatorId()+"_"+siteId;//当前站点的标识串
            //如果查询出来所有的引用栏目中包含当前修改的栏目，则存在循环引用，抛出异常
            if(!AppUtil.isEmpty(referColumnIds)&&referColumnIds.contains(columnSiteId)){
                return ajaxErr("栏目引用配置错误，当前引用关系存在循环引用情况，请重新配置");
            }

        }

        Long count = baseContentService.getCountByColumnId(columnVO.getParentId());
        if (columnVO.getIndicatorId() == null && count != null && count > 0) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "该栏目下有内容，不能添加子栏目");
        }

        if (StringUtils.isEmpty(columnVO.getName())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "栏目名称不能为空");
        }

        if (columnVO.getSortNum() == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "序号不能为空");
        }

        if (columnVO.getIsStartUrl() != 1 && StringUtils.isEmpty(columnVO.getContentModelCode())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "内容模型不能为空");
        }

        if (columnVO.getIsStartUrl() == 1) {
            columnVO.setColumnTypeCode("redirect");
        }

        if ("net_work".equals(columnVO.getContentModelCode()) || "InteractiveVirtual".equals(columnVO.getContentModelCode())
                || "tableResources".equals(columnVO.getContentModelCode()) || "relatedRule".equals(columnVO.getContentModelCode())) {
            List<ColumnMgrEO> list = columnConfigService.getColumnByContentModelCode(LoginPersonUtil.getSiteId(), columnVO.getContentModelCode());
            if (list != null && list.size() > 0) {
                if (list.size() == 1 && list.get(0).getIndicatorId().equals(columnVO.getIndicatorId())) {

                } else {
                    throw new BaseRunTimeException(TipsMode.Message.toString(), "该栏目不能重复添加");
                }
            }
        }
//	    if("netClassify".equals(columnVO.getContentModelCode())||
//				"tableResources".equals(columnVO.getContentModelCode())||"relatedRule".equals(columnVO.getContentModelCode())
//				||"workGuide".equals(columnVO.getContentModelCode())||"sceneService".equals(columnVO.getContentModelCode())){
//			ColumnMgrEO pEO=CacheHandler.getEntity(ColumnMgrEO.class,columnVO.getParentId());
//			if(pEO==null||!"net_work".equals(pEO.getContentModelCode())){
//				//throw new BaseRunTimeException(TipsMode.Message.toString(), "请在网上办事栏目下添加该栏目");
//			}
//		}
        Date date = new Date();
        ColumnMgrEO buf = getEntity(ColumnMgrEO.class, columnVO.getIndicatorId());

        if (buf != null) {
            if ((null != buf.getIsToFile() && !buf.getIsToFile().equals(columnVO.getIsToFile())) || (null != buf.getToFileDate() && !buf.getToFileDate().equals(columnVO.getToFileDate()))) {
                if (columnVO.getIsToFile() == 1) {
                    if (columnVO.getToFileDate() == null) {
                        columnVO.setToFileDate(date);
                    } else {
                        date = columnVO.getToFileDate();
                    }
                    String str = pressText("归档时间:" + new SimpleDateFormat("yyyy年MM月dd日").format(date), buf);
                    columnVO.setToFileId(str);
                    if (columnVO.getIsParent() == 1) {
                        columnGuiDangConfigService.updateColumnConfigEObyPid(columnVO.getIndicatorId(), 1, date, str);
                    }
                    columnGuiDangConfigService.updateBaseContentEObyPid(columnVO.getIndicatorId(), 1, date, str);
                } else {
                    columnVO.setToFileDate(null);
                    if (!StringUtils.isEmpty(columnVO.getToFileId())) {
                        /*if (columnVO.getIsParent() == 0)
                            mongoDbFileServer.delete(columnVO.getToFileId(), null);*/
                        columnVO.setToFileId(null);
                    }
                    if (columnVO.getIsParent() == 1) {
                        columnGuiDangConfigService.updateColumnConfigEObyPid(columnVO.getIndicatorId(), 0, null, null);
                    }
                    columnGuiDangConfigService.updateBaseContentEObyPid(columnVO.getIndicatorId(), 0, null, null);
                }
            }
        } else {
            if (columnVO.getIsToFile() == 1) {
                if (columnVO.getToFileDate() == null) {
                    columnVO.setToFileDate(date);
                } else {
                    date = columnVO.getToFileDate();
                }
                String str = pressText("归档时间:" + new SimpleDateFormat("yyyy年MM月dd日").format(date), buf);
                columnVO.setToFileId(str);
            } else {
                columnVO.setToFileDate(null);
                if (!StringUtils.isEmpty(columnVO.getToFileId())) {
                    /*if (columnVO.getIsParent() == 0)
                        mongoDbFileServer.delete(columnVO.getToFileId(), null);*/
                    columnVO.setToFileId(null);
                }
            }
        }


        String oldReferColumnIds = "";
        String oldReferOrganCatIds = "";
        if(editFlag){
            ColumnMgrEO columnMgrEO = CacheHandler.getEntity(ColumnMgrEO.class,columnVO.getIndicatorId());
            oldReferColumnIds = columnMgrEO.getReferColumnIds();
            oldReferOrganCatIds = columnMgrEO.getReferOrganCatIds();
        }


        Long indicatorId = columnConfigService.saveEO(columnVO);

        //栏目引用关系发生修改时删除栏目引用数据
        if(editFlag){
            delColumnReferDatas(oldReferColumnIds,oldReferOrganCatIds,columnVO.getReferColumnIds(),
                    columnVO.getReferOrganCatIds(),indicatorId);
        }

        return getObject(indicatorId);
    }

    /**
     * 取消栏目引用关系时删除栏目引用数
     * @param oldReferColumnIds
     * @param oldReferOrganCatIds
     * @param referColumnIds
     * @param referOrganCatIds
     */
    private void delColumnReferDatas(String oldReferColumnIds,String oldReferOrganCatIds,
                                     String referColumnIds,String referOrganCatIds,Long indicatorId){
        try {
            String columnIdStr = indicatorId+"_"+LoginPersonUtil.getSiteId();

            Map<String,String> resultMap = baseContentService.getReferColumnCats(oldReferColumnIds,oldReferOrganCatIds,columnIdStr);
            oldReferColumnIds = resultMap.get("referColumnIds");
            oldReferOrganCatIds = resultMap.get("referOrganCatIds");

            Map<String,String> resultMap_ = baseContentService.getReferColumnCats(referColumnIds,referOrganCatIds,columnIdStr);
            referColumnIds = resultMap_.get("referColumnIds");
            referOrganCatIds = resultMap_.get("referOrganCatIds");
            referColumnIds = referColumnIds==null?"":referColumnIds;
            referOrganCatIds = referOrganCatIds==null?"":referOrganCatIds;

            List<Long> ids = new ArrayList<Long>();
            if(!AppUtil.isEmpty(oldReferColumnIds)){
                String[] columnIds = oldReferColumnIds.split(",");
                for(int i=0;i<columnIds.length;i++){
                    if(!referColumnIds.contains(columnIds[i])){//取消栏目引用关系，需删除引用数据
                        String[] columnSiteId = columnIds[i].split("_");
                        Long columnId = Long.valueOf(columnSiteId[0]);
                        Long siteId = Long.valueOf(columnSiteId[1]);

                        Map<String,Object> param = new HashMap<String,Object>();
                        param.put("type", ContentReferRelationEO.TYPE.REFER.toString());
                        param.put("causeByColumnId", indicatorId);
                        param.put("columnId", columnId);
                        param.put("isColumnOpt", 1);//栏目引用过去的数据
                        List<ContentReferRelationEO> referRelationEOS = contentReferRelationService.getEntities(ContentReferRelationEO.class,param);
                        if(referRelationEOS!=null&&referRelationEOS.size()>0){
                            for(ContentReferRelationEO relationEO:referRelationEOS){
                                if(!ids.contains(relationEO.getReferId())){
                                    ids.add(relationEO.getReferId());
                                }
                            }
                        }

                        //查询通过上级栏目引用到当前栏目或者目录的数据
                        List<ContentReferRelationEO> referRelationEOS_ = contentReferRelationService.getByParentReferColumn(columnId,null,indicatorId);
                        if(referRelationEOS_!=null&&referRelationEOS_.size()>0){
                            for(ContentReferRelationEO relationEO:referRelationEOS_){
                                if(!ids.contains(relationEO.getReferId())){
                                    ids.add(relationEO.getReferId());
                                }
                            }
                        }
                    }
                }
            }

            if(!AppUtil.isEmpty(oldReferOrganCatIds)){
                String[] organCatIds = oldReferOrganCatIds.split(",");
                for(int i=0;i<organCatIds.length;i++){
                    if(!referOrganCatIds.contains(organCatIds[i])){//取消栏目引用关系，需删除引用数据
                        String[] organCatId = organCatIds[i].split("_");
                        Long organId = Long.valueOf(organCatId[0]);
                        Long catId = Long.valueOf(organCatId[1]);

                        Map<String,Object> param = new HashMap<String,Object>();
                        param.put("type", ContentReferRelationEO.TYPE.REFER.toString());
                        param.put("causeByColumnId", indicatorId);
                        param.put("columnId", organId);
                        param.put("catId", catId);
                        param.put("isColumnOpt", 1);//栏目引用过去的数据
                        List<ContentReferRelationEO> referRelationEOS = contentReferRelationService.getEntities(ContentReferRelationEO.class,param);
                        if(referRelationEOS!=null&&referRelationEOS.size()>0){
                            for(ContentReferRelationEO relationEO:referRelationEOS){
                                ids.add(relationEO.getReferId());
                            }
                        }

                        //查询通过上级栏目引用到当前栏目或者目录的数据
                        List<ContentReferRelationEO> referRelationEOS_ = contentReferRelationService.getByParentReferColumn(organId,catId,indicatorId);
                        if(referRelationEOS_!=null&&referRelationEOS_.size()>0){
                            for(ContentReferRelationEO relationEO:referRelationEOS_){
                                if(!ids.contains(relationEO.getReferId())){
                                    ids.add(relationEO.getReferId());
                                }
                            }
                        }
                    }
                }
            }
            if(ids.size()>0){
                //删除数据
                String msg = baseContentService.delContent(ids.toArray(new Long[]{}));
                MessageSenderUtil.unPublishCopyNews(msg);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 打印文字水印图片
     *
     * @param pressText --文字
     */
    public String pressText(String pressText, ColumnMgrEO cme) {
        try {
            InputStream in = null;
            org.springframework.core.io.Resource res = new ClassPathResource("/bg/guidang.png");
            in = res.getInputStream();
            Image src = ImageIO.read(in);
            int width = src.getWidth(null);
            int height = src.getHeight(null);
            BufferedImage image = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = image.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(src, 0, 0, width, height, null);
            Color color = new Color(Integer.parseInt("0697ff", 16));
            g.setColor(color);
            g.setFont(FontUtil.buildFont("宋体", Font.BOLD, 12));

            g.drawString(pressText, width - 12 - 182, height - 12
                    / 2 - 60);
            g.dispose();
            BufferedImage buf = Thumbnails.of(image).scale(1).rotate(-16).asBufferedImage();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(buf, "png", out);
            byte[] bt = out.toByteArray();
            out.flush();
            out.close();
            MongoFileVO vo = mongoDbFileServer.uploadByteFile(bt, RandomDigitUtil.getRandomDigit() + ".png", null, null);
            /*MongoFileVO vo = FileUploadUtil.uploadUtil(bt, RandomDigitUtil.getRandomDigit() + ".png", FileCenterEO.Type.Image.toString(), FileCenterEO.Code.ThumbUpload.toString(), cme.getSiteId(), cme.getIndicatorId(),
                    null, "归档", "");*/
            return vo.getFileName();
        } catch (Exception e) {
        }
        return "";
    }

    /**
     * 检查栏目名称是否存在
     *
     * @param name
     * @param parentId
     * @return
     */
    @RequestMapping("checkColumnNameExist")
    @ResponseBody
    public Object checkColumnNameExist(String name, Long parentId, Long indicatorId) {
        boolean b = columnConfigService.checkColumnNameExist(name, parentId, indicatorId);
        return getObject(b);
    }

    /**
     * 删除栏目
     *
     * @param indicatorId
     * @return
     */
    @RequestMapping("delColumnEO")
    @ResponseBody
    public Object delColumnEO(Long indicatorId) {
        if (indicatorId == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请选择删除要删除的栏目");
        }
        ColumnMgrEO eo = getEntity(ColumnMgrEO.class, indicatorId);
        if (eo == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "栏目不存在");
        }
        if (BaseContentEO.TypeCode.ordinaryPage.toString().equals(eo.getColumnTypeCode())) {
            columnConfigService.deleteEO(indicatorId);
            return getObject();
        }
        /*
         不能根据父节点的isparent来判断是否有子节点
        if (eo.getIsParent() == 1) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "该栏目下有子栏目");
       }
        */
        List<IndicatorEO> list = CacheHandler.getList(IndicatorEO.class, CacheGroup.CMS_PARENTID, indicatorId);
        if(list!=null&&list.size()>0){//是父栏目
            throw new BaseRunTimeException(TipsMode.Message.toString(), "该栏目下有子栏目");
        }


        Long count = baseContentService.getCountByColumnId(indicatorId);
        if (count == null || count <= 0) {
            columnConfigService.deleteEO(indicatorId);
            CacheHandler.reload("ColumnMgrEO");
            CacheHandler.reload("ColumnConfigEO");
            CacheHandler.reload("IndicatorEO");
            return getObject();
        } else {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "该栏目下面有内容");
        }


    }

    /**
     * 根据主键ID 获取栏目实体类
     *
     * @param indicatorId
     * @return
     */
    @RequestMapping("getColumnEO")
    @ResponseBody
    public Object getColumnEO(Long indicatorId) {
        if (indicatorId == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请选择先选择栏目");
        }
        //ColumnVO columnVO = columnConfigService.getColumnVO(indicatorId);
        ColumnMgrEO eo = getEntity(ColumnMgrEO.class, indicatorId);
        if (IndicatorEO.Type.COM_Section.toString().equals(eo.getType())) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("isHide", false);
        map.put("indicatorId", indicatorId);
        List<ColumnConfigRelEO> relList = relService.getEntities(ColumnConfigRelEO.class, map);
        if (relList != null && relList.size() > 0) {
            eo.setIndicatorId(relList.get(0).getIndicatorId());
            eo.setName(relList.get(0).getName());
            eo.setSortNum(relList.get(0).getSortNum());
            eo.setContentModelCode(relList.get(0).getContentModelCode());
            eo.setKeyWords(relList.get(0).getKeyWords());
            eo.setDescription(relList.get(0).getDescription());
            eo.setIsShow(relList.get(0).getIsShow());
            eo.setTransUrl(relList.get(0).getTransUrl());
            eo.setTransWindow(relList.get(0).getTransWindow());
//                ContentModelEO modelEO=modelService.getByCode(eo.getContentModelCode());
//                if(modelEO!=null){
//                    eo.setContentModelName(modelEO.getName());
//                }
        }

    }
    //政务论坛，办理单位
        setUnits(eo);
        Long columnContentNum = baseContentService.getCountByColumnId(indicatorId);
        boolean isHave = columnContentNum != null && columnContentNum > 0;
        eo.setIsHave(isHave);
        if (!StringUtils.isEmpty(eo.getContentModelCode())) {
            ContentModelEO modelEO = ModelConfigUtil.getEOByCode(eo.getContentModelCode(), eo.getSiteId());
            if (modelEO != null) {
                eo.setContentModelName(modelEO.getName());
            }

        }
        if (!StringUtils.isEmpty(eo.getColumnClassCode())) {
            Long[] codeArr = AppUtil.getLongs(eo.getColumnClassCode(), ",");
            if (codeArr != null && codeArr.length > 0) {
                String names = "";
                for (Long code : codeArr) {
                    ColumnTypeEO columnTypeEO = CacheHandler.getEntity(ColumnTypeEO.class, code);
                    if (columnTypeEO != null) {
                        names += columnTypeEO.getTypeName() + ",";
                    }
                }
                if (!StringUtils.isEmpty(names)) {
                    eo.setColumnClassName(names.substring(0, names.length() - 1));
                }
            }
        } else {
            eo.setColumnClassName("");
        }

        //判断是否是引用栏目
        String columnSiteStr = eo.getIndicatorId()+"_"+eo.getSiteId();
        Long sourceColumnCount = columnConfigService.getSourceColumnCount(columnSiteStr);
        if(sourceColumnCount!=null&&sourceColumnCount.intValue()>0){
            eo.setIsReferColumn(1);
        }

        Long sourceCatCount = publicCatalogOrganRelService.getSourceColumnCount(columnSiteStr);
        if(sourceCatCount!=null&&sourceCatCount.intValue()>0){
            eo.setIsReferColumn(1);
        }

        return getObject(eo);
    }

    private void setUnits(ColumnMgrEO eo) {
        if (eo != null && !StringUtils.isEmpty(eo.getColumnTypeCode())) {
            List<ModelTemplateEO> modelTemplates = ModelConfigUtil.getTemplateListByCode(eo.getContentModelCode(), LoginPersonUtil.getSiteId());
            if (modelTemplates != null && modelTemplates.size() > 0) {
                String type = modelTemplates.get(0).getModelTypeCode();
            }
        }
    }

    /**
     * 生成序列号
     *
     * @param parentId
     * @return
     */
    @RequestMapping("getNewSortNum")
    @ResponseBody
    public Object getNewSortNum(Long parentId, boolean isCom) {
        Integer num = columnConfigService.getNewSortNum(parentId, isCom);
        ColumnMgrEO eo = new ColumnMgrEO();
        eo.setSortNum(num);
        return getObject(eo);
    }


    /**
     * 获取某站点下的内容模型
     *
     * @param siteId
     * @param isPublic
     * @return
     */
    @RequestMapping("getContentModel")
    @ResponseBody
    public Object getContentModel(Long siteId, @RequestParam(defaultValue = "0") Integer isPublic) {
        List<ContentModelEO> list = new ArrayList<ContentModelEO>();
        if (isPublic == 1) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
            map.put("isPublic", 1);
            list = modelService.getEntities(ContentModelEO.class, map);

        } else {
            if (siteId == null) {
                siteId = LoginPersonUtil.getSiteId();
            }
            list = getList(ContentModelEO.class, CacheGroup.CMS_SITE_ID, siteId);
        }
        List<ContentModelVO> newList = new ArrayList<ContentModelVO>();
        if (list != null && list.size() > 0) {
            for (ContentModelEO modelEO : list) {
                List<ModelTemplateEO> tplList = CacheHandler.getList(ModelTemplateEO.class, CacheGroup.CMS_MODEL_ID, modelEO.getId());
                if (tplList != null && tplList.size() > 0) {
                    ContentModelVO modelVO = new ContentModelVO();
                    AppUtil.copyProperties(modelVO, modelEO);
                    modelVO.setModelTypeCode(tplList.get(0).getModelTypeCode());
                    newList.add(modelVO);
                }
            }
        }
        return getObject(newList);
    }


    /**
     * 获取某站点下的内容模型
     *
     * @return
     */
    @RequestMapping("getColumnClass")
    @ResponseBody
    public Object getColumnClass() {
        List<ColumnTypeEO> list = columnTypeService.getColumnTypeEOs(LoginPersonUtil.getSiteId());
        return getObject(list);
    }

    /**
     * 根据内容模型code值获取栏目类型
     *
     * @param contentModelCode
     * @return
     */
    @RequestMapping("checkColumnType")
    @ResponseBody
    public Object checkColumnType(String contentModelCode) {
        if (StringUtils.isEmpty(contentModelCode)) {
            return getObject("redirect");
        }
        ContentModelEO modelEO = getEntity(ContentModelEO.class, CacheGroup.CMS_CODE, contentModelCode);
        if (modelEO != null) {
            List<ModelTemplateEO> list = getList(ModelTemplateEO.class, CacheGroup.CMS_MODEL_ID, modelEO.getId());
            if (list != null && list.size() > 0) {
                for (ModelTemplateEO tplEO : list) {
                    if (tplEO.getType() == 1) {
                        return getObject(tplEO.getModelTypeCode());
                    }
                }
            } else {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "内容模型未配置栏目类型");
            }

        } else {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "该内容模型不存在");
        }
        return null;
    }

    /**
     * 去往移动页面
     *
     * @param indicatorId
     * @param columnId
     * @param model
     * @return
     */
    @RequestMapping("toMove")
    public Object toMove(Long indicatorId, Long columnId, Model model) {
        Long siteId = LoginPersonUtil.getSiteId();
        model.addAttribute("indicatorId", siteId);
        IndicatorEO eo = CacheHandler.getEntity(IndicatorEO.class, siteId);
        if (eo != null) {
            model.addAttribute("siteName", eo.getName());
            model.addAttribute("type", eo.getType());
        }
        model.addAttribute("parentId", indicatorId);
        model.addAttribute("columnId", columnId);
        return "site/site/column_move";
    }

    /**
     * 保存移动
     *
     * @param id
     * @param columnId
     * @return
     */
    @RequestMapping("saveMove")
    @ResponseBody
    public Object saveMove(Long id, Long columnId) {
        if (id == null || columnId == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "参数错误");
        }
        IndicatorEO desEO = CacheHandler.getEntity(IndicatorEO.class, id);
        IndicatorEO columnEO = CacheHandler.getEntity(IndicatorEO.class, columnId);
        if (desEO != null && columnEO != null) {
            IndicatorEO pEO = CacheHandler.getEntity(IndicatorEO.class, columnEO.getParentId());
            columnEO.setParentId(id);
            columnEO.setSortNum(columnConfigService.getNewSortNum(id, false));
            indicatorService.updateEntity(columnEO);
            if (desEO.getIsParent() == 0) {
                if (IndicatorEO.Type.COM_Section.toString().equals(desEO.getType())) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("isHide", false);
                    map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
                    map.put("indicatorId", id);
                    map.put("siteId", LoginPersonUtil.getSiteId());
                    List<ColumnConfigRelEO> list = relService.getEntities(ColumnConfigRelEO.class, map);
                    if (list != null && list.size() > 0) {
                        ColumnConfigRelEO relEO = list.get(0);
                        if (relEO.getIsParent() == 0) {
                            relEO.setIsParent(1);
                            relService.updateEntity(relEO);
                        }
                    } else {
                        ColumnConfigRelEO relEO = new ColumnConfigRelEO();
                        relEO.setIsParent(1);
                        relEO.setIndicatorId(id);
                        relEO.setSiteId(LoginPersonUtil.getSiteId());
                        relEO.setSortNum(desEO.getSortNum());
                        relEO.setName(desEO.getName());
                        ColumnMgrEO mgrEO = CacheHandler.getEntity(ColumnMgrEO.class, id);
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
                            relEO.setIsHide(false);
                            relService.saveEntity(relEO);
                        }
                    }
                } else {
                    desEO.setIsParent(1);
                    indicatorService.updateEntity(desEO);
                }
            }
            if (pEO != null && pEO.getIsParent() == 1) {
                List<IndicatorEO> list = CacheHandler.getList(IndicatorEO.class, CacheGroup.CMS_PARENTID, pEO.getIndicatorId());
                if (list == null || list.size() == 0) {
                    pEO.setIsParent(0);
                    indicatorService.updateEntity(pEO);
                }
            }

            SysLog.log("【站群管理】移动栏目，名称："+columnEO.getName()+"，移动到：" + desEO.getName(), "IndicatorEO", CmsLogEO.Operation.Update.toString());
        }
        CacheHandler.reload("ColumnMgrEO");
        CacheHandler.reload("IndicatorEO");
        return getObject();
    }


    /**
     * 去往栏目编辑页面或栏目新增页面
     *
     * @param columnId
     * @param parentId
     * @param isCom
     * @param model
     * @return
     */
    @RequestMapping("toEditColumn")
    public String toEditColumn(Long columnId, Long parentId, boolean isCom, Model model) {
        if (columnId == null) {
            model.addAttribute("parentId", parentId);
            ColumnMgrEO columnMgrEO = CacheHandler.getEntity(ColumnMgrEO.class,parentId);
            if(columnMgrEO!=null){
                model.addAttribute("columnClassCode", columnMgrEO.getColumnClassCode());
            }
            return "site/site/column_add";
        } else {
            model.addAttribute("columnId", columnId);
            return "site/site/column_edit";
        }
    }

    /**
     * 批量导入栏目
     *
     * @param Filedata 导入文件
     * @param siteId   站点ID
     * @param columnId 栏目ID
     * @return
     * @throws IOException
     */
    @RequestMapping("uploadColumn")
    @ResponseBody
    public Object uploadColumn(MultipartFile Filedata, Long siteId, Long columnId) throws IOException {
        /** 配置字段与列对应关系*/
        Map<Integer, String> map = new HashMap<Integer, String>();
        map.put(0, "name");//名称
        map.put(1, "rowIndex");//父栏目对应行号
        map.put(2, "shortName");//简称
        map.put(3, "sortNum");//序号
        map.put(4, "keyWords");//关键词
        map.put(5, "description");//描述
        int startRow = 1;//开始行号
        /**读取Excel*/
        Map<String, List<ColumnMgrEO>> resultMap = ExcelUtil.readExcel(ColumnMgrEO.class, Filedata.getOriginalFilename(), Filedata.getInputStream(), map, startRow);
        if (null == resultMap || resultMap.isEmpty()) {
            return getObject();
        }
        /**把所有要更新的IndicatorEO放进去*/
        List<IndicatorEO> listAll = new ArrayList<IndicatorEO>();
        StringBuffer importIndicatorNameStr = new StringBuffer();
        IndicatorEO pEO = getEntity(IndicatorEO.class, columnId); //获取要导入的栏目实体信息
        for (Map.Entry<String, List<ColumnMgrEO>> entry : resultMap.entrySet()) {
            List<ColumnMgrEO> newList = columnConfigService.saveUploadColumn(entry.getValue(), startRow, siteId, columnId);// 导入
            /** 将List<ColumnMgrEO>转变为List<IndicatorEO>,方便更新IndicatorEO*/
            Long[] ids = new Long[newList.size()];
            int k = 0;
            for (ColumnMgrEO eo : newList) {
                ids[k++] = eo.getIndicatorId();
            }
            List<IndicatorEO> indicatorList = indicatorService.getEntities(IndicatorEO.class, ids);
            //key:行号，value:IndicatorEO
            Map<Integer, IndicatorEO> cacheMap = new HashMap<Integer, IndicatorEO>();
            //key:栏目ID,value:IndicatorEO
            Map<Long, IndicatorEO> indicatorMap = new HashMap<Long, IndicatorEO>();
            int listIndex = 1;
            for (IndicatorEO eo : indicatorList) {
                cacheMap.put(listIndex++, eo);
                indicatorMap.put(eo.getIndicatorId(), eo);
            }
            for (ColumnMgrEO eo : newList) {
                int rowIndex = eo.getRowIndex();
                IndicatorEO indicatorEO = indicatorMap.get(eo.getIndicatorId());
                /** 设置父栏目、更改栏目为父栏目*/
                if (eo.getRowIndex() == null || rowIndex == 0) {//父栏目对应行号为空或为0
                    indicatorEO.setParentId(columnId);
                    listAll.add(indicatorEO);
                    importIndicatorNameStr.append(indicatorEO.getName() + ",");
                    if (pEO.getIsParent() == 0) {
                        pEO.setIsParent(1);
                        listAll.add(pEO);
                    }
                } else if (cacheMap.containsKey(rowIndex)) {//父栏目对应行号不为空
                    IndicatorEO parentEO = cacheMap.get(rowIndex);
                    indicatorEO.setParentId(parentEO.getIndicatorId());
                    listAll.add(indicatorEO);
                    importIndicatorNameStr.append(indicatorEO.getName() + ",");
                    if (parentEO.getIsParent() == 0) {
                        parentEO.setIsParent(1);
                    }
                }
            }
        }
        if (listAll.size() > 0) {
            indicatorService.updateList(listAll);
        }
        CacheHandler.reload("ColumnMgrEO");
        CacheHandler.reload("IndicatorEO");
        SysLog.log("【站群管理】导入栏目，名称："+importIndicatorNameStr.toString()+"导入到：" + pEO.getName(), "IndicatorEO", CmsLogEO.Operation.Update.toString());

        return getObject();
    }


}
