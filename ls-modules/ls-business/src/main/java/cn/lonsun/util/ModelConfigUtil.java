package cn.lonsun.util;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.vo.JSONConvertUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.publicInfo.internal.entity.OrganConfigEO;
import cn.lonsun.site.contentModel.internal.entity.ContentModelEO;
import cn.lonsun.site.contentModel.internal.entity.ModelTemplateEO;
import cn.lonsun.site.contentModel.internal.service.IContentModelService;
import cn.lonsun.site.contentModel.vo.ColumnTypeConfigVO;
import cn.lonsun.site.contentModel.vo.ContentModelParaVO;
import cn.lonsun.site.contentModel.vo.ContentModelVO;
import cn.lonsun.site.site.internal.entity.ColumnConfigEO;
import cn.lonsun.site.site.internal.entity.ColumnConfigRelEO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.system.datadictionary.vo.DataDictVO;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.lonsun.cache.client.CacheHandler.getEntity;

/**
 * 工具类：获取内容模型配置信息 <br/>
 * 
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-12-3<br/>
 */

public class ModelConfigUtil {

    /**
     * 文字新闻默认排序
     */
    public static String PIC_OR_ARTICLE_ORDER_DEFAULT = "c.isTop desc,c.num desc,c.publishDate desc,c.id desc";

    /**
     * 视频新闻默认排序
     */
    public static String VIDEONEWS_ORDER_DEFAULT = "c.isTop desc,c.num desc, c.publishDate desc,c.id desc";

    /**
     * 视频新闻默认排序
     */
    public static String FILEDOWNLOAD_ORDER_DEFAULT = "c.isTop desc,c.createDate desc,c.id desc";

    /**
     * 信息公开新闻默认排序
     */
    public static String PUBLIC_CONTENT_ORDER_DEFAULT = " p.sortNum desc,b.publishDate desc";

    private static IContentModelService modelService = SpringContextHolder.getBean(IContentModelService.class);

    /**
     * 根据内容模型code获取内容模型配置信息
     * 
     * @param contentModelCode
     * @param siteId
     * @return
     */
    public static ColumnTypeConfigVO getCongfigVO(String contentModelCode, Long siteId) {
        ColumnTypeConfigVO configVO = new ColumnTypeConfigVO();
        ContentModelEO modelEO = getEntity(ContentModelEO.class, CacheGroup.CMS_CODE, contentModelCode);
        if (StringUtils.isEmpty(contentModelCode)) {
            if ("net_work".equals(contentModelCode) || "netClassify".equals(contentModelCode) || "tableResources".equals(contentModelCode)
                    || "relatedRule".equals(contentModelCode) || "workGuide".equals(contentModelCode) || "sceneService".equals(contentModelCode)
                    || "InteractiveVirtual".equals(contentModelCode)) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("siteId", siteId);
                map.put("code", contentModelCode);
                map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
                List<ContentModelEO> list = modelService.getEntities(ContentModelEO.class, map);
                if (list != null && list.size() > 0) {
                    modelEO = list.get(0);
                }
            } else {
                modelEO = CacheHandler.getEntity(ContentModelEO.class, CacheGroup.CMS_CODE, contentModelCode);
            }
        }
        if (null != modelEO && !AppUtil.isEmpty(modelEO.getContent())) {
            configVO = JSONConvertUtil.toObejct(modelEO.getContent(), ColumnTypeConfigVO.class);
        }
        return configVO;
    }

    /**
     * 根据栏目ID获取内容模型配置信息
     * 
     * @param columnId
     * @return
     */
    public static ColumnTypeConfigVO getCongfigVO(Long columnId, Long siteId) {
        ColumnTypeConfigVO configVO = new ColumnTypeConfigVO();
        ColumnMgrEO columnMgrEO = CacheHandler.getEntity(ColumnMgrEO.class, columnId);
        if (columnMgrEO == null) {
            return null;
        }
        String code = null;
        // 公共栏目
        if (IndicatorEO.Type.COM_Section.toString().equals(columnMgrEO.getType())) {
            ColumnConfigRelEO relEO = ColumnRelUtil.getByIndicatorId(columnMgrEO.getIndicatorId(), siteId);
            if (relEO != null) {
                code = relEO.getContentModelCode();
            }
            // 标准栏目
        } else {
            code = columnMgrEO.getContentModelCode();
        }
        ContentModelEO modelEO = null;
        if (!StringUtils.isEmpty(code)) {
            // 根据某站点下的内容模型的code值获取模型模板关联信息
            modelEO = getEOByCode(code, siteId);
            if (!StringUtils.isEmpty(modelEO.getContent())) {

                configVO = JSONConvertUtil.toObejct(modelEO.getContent(), ColumnTypeConfigVO.class);
            }
        }
        return configVO;
    }

    /**
     * 获取某个栏目下的接收人员或接受单位信息
     * 
     * @param columnId
     * @return
     */
    public static List<ContentModelParaVO> getParam(Long columnId, Long siteId, Integer isTurn) {
        List<ContentModelParaVO> list = modelService.getParam(columnId, siteId, isTurn);
        return list;
    }

    /**
     * 获取留言在内容模型中配置的留言类型
     * 
     * @param columnId
     * @param siteId
     * @return
     */
    public static List<ContentModelParaVO> getGuestBookType(Long columnId, Long siteId) {
        List<ContentModelParaVO> list = modelService.getClassCode(columnId, siteId);
        return list;
    }

    /**
     * 获取多回复留言在内容模型中配置的留言类型
     * 
     * @param columnId
     * @param siteId
     * @return
     */
    public static List<ContentModelParaVO> getMessageBoardType(Long columnId, Long siteId) {
        List<ContentModelParaVO> list = modelService.getClassCode(columnId, siteId);
        return list;
    }

    /**
     * 获取留言在内容模型中配置的处理状态
     * 
     * @param columnId
     * @param siteId
     * @return
     */
    public static List<ContentModelParaVO> getDealStatus(Long columnId, Long siteId) {
        List<ContentModelParaVO> list = modelService.getDealStatus(columnId, siteId);
        return list;
    }

    /**
     * 查找本站点下允许评论的内容模型
     * 
     * @param siteId
     * @return
     */
    public static List<ContentModelEO> getModelBySiteId(Long siteId) {
        if (null == siteId) {
            return null;
        }
        List<ContentModelEO> list = CacheHandler.getList(ContentModelEO.class, CacheGroup.CMS_SITE_ID, siteId);
        List<ContentModelEO> newList = new ArrayList<ContentModelEO>();
        if (null != list && !list.isEmpty()) {
            for (ContentModelEO modelEO : list) {
                ColumnTypeConfigVO configVO = JSONConvertUtil.toObejct(modelEO.getContent(), ColumnTypeConfigVO.class);
                if (null != configVO && configVO.getIsComment() == 1) {
                    newList.add(modelEO);
                }
            }
        }
        return newList;
    }

    /**
     * 根据栏目Id获取模板列表（栏目类型、文章页、栏目页）
     * 
     * @param columnId
     * @return
     */
    public static List<ModelTemplateEO> getTemplateListByColumnId(Long columnId, Long siteId) {
        if (null == columnId) {
            return null;
        }
        ColumnConfigEO eo = getEntity(ColumnConfigEO.class, CacheGroup.CMS_INDICATORID, columnId);
        if (null == eo) {
            return null;
        }
        ContentModelEO modelEO = null;
        if (!StringUtils.isEmpty(eo.getContentModelCode())) {
            modelEO = getEOByCode(eo.getContentModelCode(), siteId);
        }
        if (null == modelEO) {
            return null;
        }
        return CacheHandler.getList(ModelTemplateEO.class, CacheGroup.CMS_MODEL_ID, modelEO.getId());
    }

    /**
     * 根据栏目Id获取默认的模板信息（栏目类型、文章页、栏目页）
     * 
     * @param columnId
     * @return
     */
    public static ModelTemplateEO getTemplateByColumnId(Long columnId, Long siteId) {
        List<ModelTemplateEO> list = getTemplateListByColumnId(columnId, siteId);
        return getTemplateByType(list, 1);
    }

    /**
     * 根据内容模型code值获取模型模板关联信息列表
     * 
     * @param code
     * @param siteId
     * @return
     */
    public static List<ModelTemplateEO> getTemplateListByCode(String code, Long siteId) {
        ContentModelEO modelEO = getEOByCode(code, siteId);
        if (null == modelEO) {
            return null;
        }
        return CacheHandler.getList(ModelTemplateEO.class, CacheGroup.CMS_MODEL_ID, modelEO.getId());
    }

    /**
     * 根据内容模型code值获取第一个模型模板关联信息
     * 
     * @param code
     * @param siteId
     * @return
     */
    public static ModelTemplateEO getTemplateByCode(String code, Long siteId) {
        List<ModelTemplateEO> list = getTemplateListByCode(code, siteId);
        return getTemplateByType(list, 1);
    }

    /**
     * 根据内容模型ID获取模型模板关联信息
     * 
     * @param modelId
     * @return
     */
    public static ModelTemplateEO getTemplateByModelId(Long modelId) {
        List<ModelTemplateEO> list = CacheHandler.getList(ModelTemplateEO.class, CacheGroup.CMS_MODEL_ID, modelId);
        return getTemplateByType(list, 1);
    }

    /**
     * 获取第一个类型为type的模型模板关联信息
     * 
     * @param list
     * @param type
     * @return
     */
    private static ModelTemplateEO getTemplateByType(List<ModelTemplateEO> list, Integer type) {
        if (null == list || list.isEmpty()) {
            return null;
        }
        for (ModelTemplateEO modelTemplate : list) {
            if (modelTemplate.getType() == type) {
                return modelTemplate;
            }
        }
        return null;
    }

    /**
     * 根据某站点下的内容模型的code值获取模型模板关联信息
     * 
     * @param code
     * @param siteId
     * @return
     */
    public static ContentModelEO getEOByCode(String code, Long siteId) {
        ContentModelEO modelEO = null;
        if ("net_work".equals(code) || "netClassify".equals(code) || "tableResources".equals(code) || "relatedRule".equals(code) || "workGuide".equals(code)
                || "sceneService".equals(code) || "InteractiveVirtual".equals(code)) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("siteId", siteId);
            map.put("code", code);
            map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
            List<ContentModelEO> list = modelService.getEntities(ContentModelEO.class, map);
            if (list != null && list.size() > 0) {
                modelEO = list.get(0);
            }
        } else {
            modelEO = CacheHandler.getEntity(ContentModelEO.class, CacheGroup.CMS_CODE, code);
        }
        return modelEO;
    }

    /**
     * 根据栏目ID获取在内容模型中绑定的排序语句
     * 
     * @param columnId
     * @param siteId
     * @return
     */
    public static String getOrderTypeValue(Long columnId, Long siteId) {
        if (columnId == null || siteId == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "参数出错");

        }
        ColumnTypeConfigVO vo = getCongfigVO(columnId, siteId);
        if (vo != null) {
            if (!StringUtils.isEmpty(vo.getOrderTypeCode())) {
                DataDictVO dictVO = DataDictionaryUtil.getItem("order_type", vo.getOrderTypeCode());
                if (dictVO != null) {
                    return dictVO.getValue();
                }
            } else {
                List<DataDictVO> list = DataDictionaryUtil.getItemList("order_type", siteId);
                ColumnMgrEO columnMgrEO = CacheHandler.getEntity(ColumnMgrEO.class, columnId);
                if (list != null && list.size() > 0) {
                    for (DataDictVO dictVO : list) {
                        if (dictVO.getCode().contains(columnMgrEO.getColumnTypeCode())) {
                            return dictVO.getValue();
                        }
                    }
                }
            }
        }
        return "";
    }

    /**
     * 根据某站点下的栏目ID，获取指定栏目类型在内容模型中绑定的排序语句
     * 
     * @param columnId
     * @param siteId
     * @param type
     *            所有文字类型
     * @return
     */
    public static String getOrderByHql(Long columnId, Long siteId, String type) {
        if (StringUtils.isEmpty(type)) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "type不能为空");
        }
        String hql = " order by ";
        String orderStr = "";
        try {
            orderStr = ModelConfigUtil.getOrderTypeValue(columnId, siteId);
        } catch (Exception e) {
        }
        if (!StringUtils.isEmpty(orderStr)) {
            hql += orderStr;
        } else {
            if (type.equals(BaseContentEO.TypeCode.articleNews.toString()) || type.equals(BaseContentEO.TypeCode.pictureNews.toString())) {
                hql += PIC_OR_ARTICLE_ORDER_DEFAULT;
            } else if (type.equals(BaseContentEO.TypeCode.videoNews.toString())) {
                hql += VIDEONEWS_ORDER_DEFAULT;
            } else if (type.equals(BaseContentEO.TypeCode.fileDownload.toString())) {
                hql += FILEDOWNLOAD_ORDER_DEFAULT;
            } else {
                hql = "";
            }
        }
        return hql;
    }

    /**
     * 获取信息公开排序
     * 
     * @param organId
     * @param siteId
     * @return
     */
    public static String getOrderByHqlForPublic(Long organId, Long siteId) {
        String orderStr = "";
        try {
            if (null != organId) {// 当有单位的前提下
                OrganConfigEO config = CacheHandler.getEntity(OrganConfigEO.class, CacheGroup.CMS_ORGAN_ID, organId);
                if (null != config && StringUtils.isNotEmpty(config.getContentModelCode())) {
                    ColumnTypeConfigVO vo = ModelConfigUtil.getCongfigVO(config.getContentModelCode(), siteId);
                    if (null != vo && StringUtils.isNotEmpty(vo.getOrderTypeCode())) {
                        DataDictVO dictVO = DataDictionaryUtil.getItem("order_type", vo.getOrderTypeCode());
                        if (null != dictVO) {
                            orderStr = dictVO.getValue();
                        }
                    }
                }
            } else {// 如果没有单位查询出数据字典默认选项
                List<DataDictVO> list = DataDictionaryUtil.getItemList("order_type", siteId);
                if (null != list && !list.isEmpty()) {
                    for (DataDictVO dictVO : list) {
                        if (dictVO.getCode().contains(BaseContentEO.TypeCode.public_content.toString())) {
                            orderStr = dictVO.getValue();
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "获取排序字段错误");
        }
        return " order by " + (StringUtils.isEmpty(orderStr) ? PUBLIC_CONTENT_ORDER_DEFAULT : orderStr);
    }

    public static List<ContentModelVO>  getByColumnTypeCode(Long siteId ,String columnTypeCode){
        List<ContentModelVO> list= modelService.getByColumnTypeCode(siteId,columnTypeCode);
        return list;
    }
}