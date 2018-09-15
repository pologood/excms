package cn.lonsun.staticcenter.generate.tag.impl.publicInfo;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.publicInfo.internal.entity.OrganConfigEO;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogEO;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogOrganRelEO;
import cn.lonsun.publicInfo.internal.service.IOrganConfigService;
import cn.lonsun.publicInfo.internal.service.IPublicCatalogService;
import cn.lonsun.publicInfo.internal.service.IPublicContentService;
import cn.lonsun.publicInfo.util.PublicUtil;
import cn.lonsun.publicInfo.vo.PublicContentQueryVO;
import cn.lonsun.publicInfo.vo.PublicContentVO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.entity.SiteConfigEO;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.thread.ThreadHolder;
import cn.lonsun.staticcenter.generate.util.*;
import cn.lonsun.util.PublicCatalogUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.*;
import java.util.Map.Entry;

/**
 * 获取信息公开内容列表 ADD REASON. <br/>
 *
 * @author fangtinghua
 * @date: 2016年7月6日 上午11:49:30 <br/>
 */
@Component
public class PublicInfoListBeanService extends AbstractBeanService {
    @Resource
    private IOrganService organService;
    @Resource
    private IOrganConfigService organConfigService;
    @Resource
    private IPublicCatalogService publicCatalogService;
    @Resource
    private IPublicContentService publicContentService;

    /**
     * 前置查询
     *
     * @param paramObj
     * @return
     * @throws GenerateException
     */
    @Override
    public boolean before(JSONObject paramObj) {
        // 合并map
        MapUtil.unionContextToJson(paramObj);
        // 只有当查询单位与目录对应关系时，才需要进行下面的查询，并且只会查询一次
        String action = paramObj.getString(GenerateConstant.ACTION);// 动作
        if (!PublicConstant.ACTION_REL.equals(action)) {
            return super.before(paramObj);
        }
        Long siteId = ObjectUtils.defaultIfNull(paramObj.getLong("siteId"), ContextHolder.getContext().getSiteId());
        SiteConfigEO siteConfigEO = CacheHandler.getEntity(SiteConfigEO.class, CacheGroup.CMS_INDICATORID, siteId);
        AssertUtil.isEmpty(siteConfigEO, "站点配置信息为空！");
        AssertUtil.isEmpty(siteConfigEO.getUnitIds(), "站点未关联信息公开单位！");
        List<OrganEO> organList = organService.getPublicOrgans(Long.valueOf(siteConfigEO.getUnitIds()));
        AssertUtil.isEmpty(organList, "站点单位未开启信息公开属性！");
        Map<Long, OrganConfigEO> configMap = PublicCatalogUtil.groupByOrganConfig();
        AssertUtil.isEmpty(configMap, "站点没有单位配置信息！");
        // 单位列表按照单位所属的信息公开目录分组排序
        Map<Long, List<OrganEO>> organCatIdMap = PublicCatalogUtil.groupByOrganCatSort(organList, configMap);
        ThreadHolder.setContext(ThreadHolder.LocalParamsKey.publicInfo.toString(), organCatIdMap);
        return true;
    }

    /**
     * 区分动态以及静态请求，动态请求单位id从页面传入，静态请求单位id从标签写入
     * 公开指南为单页面，主动公开分为查询部门与单位关系页面、列表页、查询条数，公开年报和公开制度为列表页和查询条数，使用action参数区分
     * 依申请公开特殊处理，传入模板文件名
     * // /public/column/organId?type=1&catId=1&cId=1&action=list&pageIndex=1
     *
     * @param paramObj
     * @return
     */
    @Override
    public Object getObject(JSONObject paramObj) {
        String type = paramObj.getString(GenerateConstant.TYPE);// 类型
        AssertUtil.isEmpty(type, "查询类型不能为空！");
        String value = PublicConstant.PublicTypeEnum.getType(type);
        AssertUtil.isEmpty(type, "查询类型不匹配！");
        String action = paramObj.getString(GenerateConstant.ACTION);// 动作
        action = ObjectUtils.defaultIfNull(action, PublicConstant.ACTION_NUM);// 默认查询条数
        if (PublicConstant.ACTION_REL.equals(action)) {// 如果是查询单位与目录对应关系，则只需要站点id
            return this.getOrganRelResult(paramObj);
        }
        Context context = ContextHolder.getContext();
        Long siteId = ObjectUtils.defaultIfNull(paramObj.getLong("siteId"), context.getSiteId());
        context.setSiteId(siteId); // 设置站点id
        Long organId = paramObj.getLong(GenerateConstant.ORGAN_ID);
        if (MessageEnum.PUBLICINFO.value().equals(context.getSource())) {// 新闻栏目调用信息公开时，不要从栏目id中获取单位
            organId = ObjectUtils.defaultIfNull(organId, context.getColumnId());
        }
        // 当且仅当为信息公开、并且action为查询条数或者列表时，可以不传入单位id，其余情况必须传入单位id
        if (!(PublicConstant.PublicTypeEnum.DRIVING_PUBLIC.getKey().equals(type) &&
                (PublicConstant.ACTION_LIST.equals(action) || PublicConstant.ACTION_NUM.equals(action)))) {
            AssertUtil.isEmpty(organId, "单位id不能为空！");
        }
        // 依申请公开返回单位信息
        if (PublicConstant.PublicTypeEnum.PUBLIC_APPLY.getKey().equals(type)) {
            String file = paramObj.getString(PublicConstant.APPLYFILE);
            AssertUtil.isEmpty(file, "依申请公开必须传入文件名称参数！");
            return CacheHandler.getEntity(OrganEO.class, organId);
        }
        PublicContentQueryVO queryVO = new PublicContentQueryVO();
        Long catId = paramObj.getLong(PublicConstant.CATID);// 目录id
        String catIds = paramObj.getString("catIds");// 目录ids
        Long organType = paramObj.getLong("organType");
        if (null != organType) {// 查询市政府部门或者事业单位下所有公开信息
            List<Long> organIds = organConfigService.getOrganIdsByCatId(organType);
            queryVO.setOrganIds(organIds);
        }
        // 时间范围搜索 by zhongjun at 2017.11.01
        String startDate = paramObj.getString("startDate");
        String endDate = paramObj.getString("endDate");
        String queryDatePattern = ObjectUtils.defaultIfNull(paramObj.getString("queryDatePattern"), "yyyy-MM-dd");
        try {
            if (StringUtils.isNotEmpty(startDate)) {
                queryVO.setStartDate(DateUtils.parseDate(startDate, queryDatePattern));
            }
            if (StringUtils.isNotEmpty(startDate)) {
                queryVO.setEndDate(DateUtils.parseDate(endDate, queryDatePattern));
            }
        } catch (ParseException e) {
            throw new GenerateException("日期格式化错误！", e);
        }
        // 时间范围 end;
        queryVO.setSiteId(siteId);
        queryVO.setIsPublish(1);// 查询已发布的文章
        queryVO.setOrganId(organId);
        queryVO.setType(value);// 设置类型
        queryVO.setCatId(catId);// 设置目录id
        queryVO.setTitle(paramObj.getString("title"));// 根据标题模糊查询
        queryVO.setKey(paramObj.getString("keyWords"));// 根据关键字搜索标题、文号、索引号查询
        if (StringUtils.isNotEmpty(catIds)) {
            queryVO.setCatIds(cn.lonsun.core.base.util.StringUtils.getArrayWithLong(catIds, ","));
        } else if (null != catId && catId > 0L && PublicConstant.PublicTypeEnum.DRIVING_PUBLIC.getKey().equals(type)) {// 查询该目录所有子目录
            this.processChildCatIdList(organId, catId, queryVO);
        }
        Long cId = paramObj.getLong(PublicConstant.CID);// 所属分类，后续或许要根据分类id找到所有子分类
        queryVO.setClassId(cId);
        if (PublicConstant.PublicTypeEnum.PUBLIC_GUIDE.getKey().equals(type)) {// 公开指南是单页面
            return publicContentService.getPublicContent(queryVO);
        }
        if (PublicConstant.ACTION_NUM.equals(action)) {// 查询列表
            Integer num = paramObj.getInteger(GenerateConstant.NUM);// 标签中定义
            return publicContentService.getList(queryVO, num);
        }
        // 查询分页
        paramObj.put(GenerateConstant.ACTION, PublicConstant.ACTION_LIST);
        queryVO.setPageSize(paramObj.getInteger(GenerateConstant.PAGE_SIZE));// 标签中定义
        queryVO.setPageIndex(context.getPageIndex() - 1);
        return publicContentService.getPagination(queryVO);
    }

    /**
     * 预处理主动公开跳转链接
     *
     * @param resultObj
     * @param paramObj
     * @return
     */
    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        // 公开指南为单页面直接返回
        String type = paramObj.getString(GenerateConstant.TYPE);
        String action = paramObj.getString(GenerateConstant.ACTION);// 动作
        Map<String, Object> resultMap = super.doProcess(resultObj, paramObj);
        resultMap.put(GenerateConstant.TYPE, type);// 放入类型在页面进行判断
        resultMap.put(GenerateConstant.ACTION, action);// 放入动作在页面进行判断
        if (PublicConstant.PublicTypeEnum.PUBLIC_GUIDE.getKey().equals(type)) {// 公开指南返回
            return resultMap;
        }
        String uri = context.getUri();
        Long siteId = context.getSiteId();
        Long source = context.getSource();
        context.setSource(MessageEnum.PUBLICINFO.value());
        if (null != siteId && StringUtils.isEmpty(uri)) {
            IndicatorEO indicatorEO = CacheHandler.getEntity(IndicatorEO.class, siteId);
            AssertUtil.isEmpty(indicatorEO, "站点id:" + siteId + ",不存在.");
            context.setUri(indicatorEO.getUri());// 域名
        }
        Long catId = paramObj.getLong("catId");
        Long organId = ObjectUtils.defaultIfNull(paramObj.getLong(GenerateConstant.ORGAN_ID), context.getColumnId());
        boolean hasOrganId = null != organId && organId >= 0L;
        if (hasOrganId) {// 放入单位
            resultMap.put("organ", CacheHandler.getEntity(OrganEO.class, organId));
        }
        List<?> resultList = PublicConstant.ACTION_NUM.equals(action) ? (List<?>) resultObj : null;// 结果值
        boolean isDriving = PublicConstant.PublicTypeEnum.DRIVING_PUBLIC.getKey().equals(type);
        if (PublicConstant.ACTION_LIST.equals(action)) {// 分页时
            Pagination pagination = (Pagination) resultObj;
            pagination.setLinkPrefix(context.getPath());// 放入分页访问地址
            if (isDriving && hasOrganId) {
                resultMap.put("catalog", PublicCatalogUtil.getPrivateCatalogByOrganId(organId, catId));
            }
            resultList = pagination.getData();
        }
        boolean hasContent = paramObj.getBooleanValue("hasContent");// 是否需要查询内容
        boolean hasFormatContent = paramObj.getBooleanValue("hasFormatContent");// 是否需要格式化内容
        setValueToPublicVO(resultList, isDriving, hasContent, hasFormatContent);
        context.setUri(uri);
        context.setSource(source);
        return resultMap;
    }

    /**
     * 填充信息公开结果值
     *
     * @param resultList
     * @param isDriving
     * @param hasContent
     * @param hasFormatContent
     */
    private void setValueToPublicVO(List<?> resultList, boolean isDriving, boolean hasContent, boolean hasFormatContent) {
        if (null == resultList || resultList.isEmpty()) {
            return;
        }
        Map<Long, String> organIdNameMap = new HashMap<Long, String>();//单位名称缓存
        Map<Long, PublicCatalogOrganRelEO> relEOMap = new HashMap<Long, PublicCatalogOrganRelEO>();// 目录关系map
        for (Object o : resultList) { // 循环填充信息公开内容
            PublicContentVO vo = (PublicContentVO) o;
            Long organId = vo.getOrganId();
            if (!organIdNameMap.containsKey(organId)) {
                OrganEO eo = CacheHandler.getEntity(OrganEO.class, organId);
                organIdNameMap.put(organId, null == eo ? "" : eo.getName());
                if (isDriving) {
                    relEOMap.putAll(PublicCatalogUtil.getCatalogRelMap(organId));
                }
            }
            vo.setOrganName(organIdNameMap.get(organId));
            if (isDriving) {
                vo.setCatName(PublicUtil.getCatName(vo.getOrganId(), vo.getCatId()));
                vo.setClassNames(PublicUtil.getClassName(vo.getClassIds()));
                PublicCatalogEO catalog = CacheHandler.getEntity(PublicCatalogEO.class, vo.getCatId());
                vo.setAttribute(catalog.getAttribute());
                if (relEOMap.containsKey(vo.getCatId())) {
                    vo.setAttribute(relEOMap.get(vo.getCatId()).getAttribute());
                }
                //政策解读文件路径生成
                if(StringUtils.isNotEmpty(vo.getRelContentId())){
                    String[] relIds = vo.getRelContentId().split(",");
                    StringBuilder filePath = new StringBuilder();
                    for(String id : relIds){
                        BaseContentEO item = CacheHandler.getEntity(BaseContentEO.class, Long.valueOf(id));
                        if(item.getTypeCode().equals(BaseContentEO.TypeCode.public_content.toString())){
                            filePath.append("/").append("public").append("/").append(item.getColumnId());
                        }else{
                            ColumnMgrEO column = CacheHandler.getEntity(ColumnMgrEO.class, item.getColumnId());
                            //如果获取不到栏目，就认为是信息公开的单位
                            if(column != null){
                                filePath.append("/").append(column.getUrlPath());
                            }
                        }
                        filePath.append("/").append(id);
                        filePath.append(".html,");
                    }
                    vo.setFilePath(filePath.substring(0, filePath.length() - 1));
                }else{
                    vo.setFilePath("");
                }
            }
            if (hasContent) {// 根据id去mongodb读取文件内容
                String content = MongoUtil.queryById(vo.getContentId());
                if (!AppUtil.isEmpty(content) && hasFormatContent) {
                    content = content.replaceAll("</?[^>]+>", ""); // 剔出<html>的标签
                    content = content.replaceAll("\t|\r|\n", "");// 去除字符串中的回车,换行符,制表符
                }
                vo.setContent(content);
            }
            String redirectLink = vo.getRedirectLink();
            if (StringUtils.isEmpty(redirectLink)) {
                Context context = ContextHolder.getContext();
                Long tempSource = context.getSource();
                context.setSource(MessageEnum.PUBLICINFO.value());
                vo.setLink(PathUtil.getLinkPath(vo.getOrganId(), vo.getContentId()));
                context.setSource(tempSource);
            } else {
                vo.setLink(redirectLink);
            }
        }
    }

    /**
     * 重写，特殊处理依申请公开
     *
     * @param content
     * @param resultObj
     * @param paramObj
     * @return
     */
    @Override
    public String objToStr(String content, Object resultObj, JSONObject paramObj) {
        String type = paramObj.getString(GenerateConstant.TYPE);
        if (PublicConstant.PublicTypeEnum.PUBLIC_APPLY.getKey().equals(type)) {// 依申请公开特殊处理
            String file = paramObj.getString(PublicConstant.APPLYFILE);
            String parse = RegexUtil.parseContent(VelocityUtil.getTemplateContent(file));// 先解析标签
            Map<String, Object> map = super.doProcess(resultObj, paramObj);
            map.put("resultObj", resultObj);// 这里是单位信息
            map.put("paramObj", paramObj);
            map.put("context", ContextHolder.getContext());
            return VelocityUtil.mergeString(parse, map);// 后置处理参数替换
        }
        return super.objToStr(content, resultObj, paramObj);
    }

    /**
     * 获取目录对应关系
     *
     * @param paramObj
     * @return
     */
    private Object getOrganRelResult(JSONObject paramObj) {
        String relCatIds = paramObj.getString(PublicConstant.RELCATIDS);
        String includeIds = paramObj.getString(PublicConstant.INCLUDEIDS);
        String excludeIds = paramObj.getString(PublicConstant.EXCLUDEIDS);
        String includeOrganIds = paramObj.getString("includeOrganIds");
        String includeParentOrganIds = paramObj.getString("includeParentOrganIds");
        String excludeOrganIds = paramObj.getString("excludeOrganIds");
        String key = ThreadHolder.LocalParamsKey.publicInfo.toString();
        Map<Long, List<OrganEO>> organCatIdMap = ThreadHolder.getContext(Map.class, key);
        if (StringUtils.isNotEmpty(relCatIds)) {// 传入的目录id不为空
            List<OrganEO> resultList = new ArrayList<OrganEO>();
            String[] catIds = StringUtils.split(relCatIds, ",");
            for (String catId : catIds) {
                Long id = Long.valueOf(catId);
                if (organCatIdMap.containsKey(id)) {
                    resultList.addAll(organCatIdMap.get(id));
                }
            }
            PublicCatalogUtil.sortList(resultList);
            return resultList;
        }
        // 按照键值进行排序
        Map<String, List<OrganEO>> catNameOrganMap = new LinkedHashMap<String, List<OrganEO>>();
        for (Entry<Long, List<OrganEO>> entry : organCatIdMap.entrySet()) {
            Long catId = entry.getKey();
            if (StringUtils.isNotEmpty(includeIds) && !includeIds.contains(catId + "")) {
                continue;
            }
            if (StringUtils.isNotEmpty(excludeIds) && excludeIds.contains(catId + "")) {
                continue;
            }
            PublicCatalogEO catalogEO = CacheHandler.getEntity(PublicCatalogEO.class, catId);
            if (null == catalogEO) {
                continue;
            }
            List<OrganEO> buf = entry.getValue();
            List<OrganEO> resultList = new ArrayList<OrganEO>();
            for (OrganEO eo : buf) {
                if (StringUtils.isNotEmpty(includeOrganIds) && !includeOrganIds.contains(eo.getOrganId() + "")) {
                    continue;
                }
                if (StringUtils.isNotEmpty(includeParentOrganIds) && !includeParentOrganIds.contains(eo.getParentId() + "")) {
                    continue;
                }
                if (StringUtils.isNotEmpty(includeParentOrganIds) && excludeOrganIds.contains(eo.getOrganId() + "")) {
                    continue;
                }
                resultList.add(eo);
            }
            PublicCatalogUtil.sortList(resultList);
            catNameOrganMap.put(catalogEO.getName(), resultList);
        }
        return catNameOrganMap;
    }

    /**
     * 根据目录id，查询出该目录下所有叶子节点的信息
     *
     * @param organId
     * @param catId
     * @param queryVO
     */
    private void processChildCatIdList(Long organId, Long catId, PublicContentQueryVO queryVO) {
        // 判断该栏目是否是父栏目
        PublicCatalogEO publicCatalogEO = PublicCatalogUtil.getPrivateCatalogByOrganId(organId, catId);
        if (null != publicCatalogEO && publicCatalogEO.getIsParent()) {
            List<PublicCatalogEO> publicCatalogList = publicCatalogService.getAllLeafListByCatId(catId);
            if (null != organId && organId > 0L) {// 有单位则过滤
                PublicCatalogUtil.filterCatalogList(publicCatalogList, organId, true);// 过滤目录
            }
            if (null != publicCatalogList && !publicCatalogList.isEmpty()) {
                List<Long> resultCatIdList = new ArrayList<Long>();
                for (PublicCatalogEO catalogEO : publicCatalogList) {
                    resultCatIdList.add(catalogEO.getId());
                }
                queryVO.setCatIds(resultCatIdList.toArray(new Long[]{}));
            }
        }
    }
}