package cn.lonsun.util;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.site.site.internal.entity.ColumnConfigEO;
import cn.lonsun.site.site.internal.entity.ColumnConfigRelEO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @author gu.fei
 * @version 2016-5-11 11:43
 */
public class ColumnUtil {

    private static IColumnConfigService columnConfigService = SpringContextHolder.getBean("columnConfigService");

    /**
     * 获取栏目名称
     *
     * @param resultList
     * @param parentId
     * @author fangtinghua
     */
    private static void getParent(List<String> resultList, Long parentId, Long siteId) {
        if (null == parentId) {
            return;
        }
        IndicatorEO eo = CacheHandler.getEntity(IndicatorEO.class, parentId);
        if (null == eo) {
            return;
        }
        if (IndicatorEO.Type.COM_Section.toString().equals(eo.getType())) {
            ColumnConfigRelEO relEO = ColumnRelUtil.getByIndicatorId(parentId, siteId);// 公共栏目
            if (null != relEO) {
                resultList.add(relEO.getName());
            } else {
                resultList.add(eo.getName());
            }
            if (null != eo.getParentId()) {
                getParent(resultList, eo.getParentId(), siteId);
            }
        } else if (IndicatorEO.Type.CMS_Section.toString().equals(eo.getType())) {
            resultList.add(eo.getName());
            getParent(resultList, eo.getParentId(), siteId);
        }
    }

    /**
     * 获取栏目名称
     *
     * @param columnId
     * @param siteId
     * @return
     * @author fangtinghua
     */
    public static String getColumnName(Long columnId, Long siteId) {
        List<String> resultList = new ArrayList<String>();
        getParent(resultList, columnId, siteId);
        Collections.reverse(resultList);
        return StringUtils.join(resultList.toArray(), " > ");
    }

    /**
     * 根据id、isChild、typeCode获取栏目数组
     *
     * @param id
     * @param isChild
     * @param typeCode
     * @return
     * @author fangtinghua
     */
    public static Long[] getQueryColumnIdByChild(String id, Boolean isChild, String typeCode) {
        if (StringUtils.isEmpty(id)) {
            return null;
        }
        String[] ids = StringUtils.split(id, ",");
        if (null == isChild || !isChild) {// 判断是否查询子栏目
            return (Long[]) ConvertUtils.convert(ids, Long.class);
        }
        Set<Long> resultSet = new HashSet<Long>();
        for (String parentId : ids) {
//            List<IndicatorEO> childrenList = CacheHandler.getList(IndicatorEO.class, CacheGroup.CMS_PARENTID, parentId);
            //查询所有子节点
            List<ColumnMgrEO> childrenList = columnConfigService.getChildColumn(Long.parseLong(parentId),new int[]{0,1});
            if (null != childrenList && !childrenList.isEmpty()) {
                // 判断文章类型
                for (ColumnMgrEO eo : childrenList) {
//                    ColumnConfigEO columnConfigEO = CacheHandler.getEntity(ColumnConfigEO.class, CacheGroup.CMS_INDICATORID, eo.getIndicatorId());
                    // 如果是叶子节点，直接添加，类型为空或者类型相等也添加
                    if (eo.getIsParent().equals(0) || StringUtils.isEmpty(typeCode)
                            || typeCode.indexOf(eo.getColumnTypeCode()) > -1) {
                        resultSet.add(eo.getIndicatorId());
                    }
                }
            }else{
                resultSet.add(Long.parseLong(parentId));//没有子节点则查询本身节点
            }
        }
//        if (resultSet.isEmpty()) {// 当没有子项时，默认返回本栏目
//            return (Long[]) ConvertUtils.convert(ids, Long.class);
//        }
        return resultSet.toArray(new Long[] {});
    }

    // 根据栏目ID返回站点域名
    public static String getSiteUrl(Long columnId, Long siteId) {
        if (AppUtil.isEmpty(columnId) && AppUtil.isEmpty(siteId)) {
            return null;
        }
        String siteUrl = "";
        if (AppUtil.isEmpty(siteId)) {
            IndicatorEO columnEO = CacheHandler.getEntity(IndicatorEO.class, columnId);
            if (!AppUtil.isEmpty(columnEO)) {
                siteUrl = CacheHandler.getEntity(IndicatorEO.class, columnEO.getSiteId()).getUri();
            }
        } else {
            siteUrl = CacheHandler.getEntity(IndicatorEO.class, siteId).getUri();
        }
        return siteUrl;
    }
}