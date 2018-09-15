/*
 * PublicCatalogUtil.java         2016年9月29日 <br/>
 *
 * Copyright (c) 1994-1999 AnHui LonSun, Inc. <br/>
 * All rights reserved.	<br/>
 *
 * This software is the confidential and proprietary information of AnHui	<br/>
 * LonSun, Inc. ("Confidential Information").  You shall not	<br/>
 * disclose such Confidential Information and shall use it only in	<br/>
 * accordance with the terms of the license agreement you entered into	<br/>
 * with Sun. <br/>
 */

package cn.lonsun.util;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.publicInfo.internal.entity.OrganConfigEO;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogEO;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogOrganRelEO;
import cn.lonsun.publicInfo.internal.service.IOrganConfigService;
import cn.lonsun.publicInfo.vo.OrganVO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.*;

/**
 * 公开目录工具类 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2016年9月29日 <br/>
 */
public class PublicCatalogUtil {
    // 单位配置
    private static IOrganConfigService organConfigService = SpringContextHolder.getBean(IOrganConfigService.class);

    /**
     * 单位配置分组
     *
     * @return
     */
    public static Map<Long, OrganConfigEO> groupByOrganConfig() {
        List<OrganConfigEO> organConfigList = organConfigService.getEntities(OrganConfigEO.class, new HashMap<String, Object>());
        if (null == organConfigList || organConfigList.isEmpty()) {
            return Collections.EMPTY_MAP;
        }
        Map<Long, OrganConfigEO> configMap = new HashMap<Long, OrganConfigEO>();
        for (OrganConfigEO config : organConfigList) {
            if (config.getIsEnable()) {
                configMap.put(config.getOrganId(), config);
            }
        }
        return configMap;
    }

    /**
     * 信息公开单位列表按照单位所属目录分组并排序
     *
     * @param organList 单位列表
     * @param configMap 单位配置信息
     * @return
     */
    public static Map<Long, List<OrganEO>> groupByOrganCatSort(List<OrganEO> organList, Map<Long, OrganConfigEO> configMap) {
        if (null == organList || organList.isEmpty() || null == configMap || configMap.isEmpty()) {
            return Collections.EMPTY_MAP;
        }
        Map<Long, List<OrganEO>> organCatIdMap = new TreeMap<Long, List<OrganEO>>(new CatKeyComparator());
        for (OrganEO organEO : organList) {// 放入目录与单位对应关系
            Long organId = organEO.getOrganId();
            if (configMap.containsKey(organId)) {
                OrganConfigEO configEO = configMap.get(organId);
                if (!configEO.getIsEnable()) {
                    continue;
                }
                Long catId = configEO.getCatId();
                if (!organCatIdMap.containsKey(catId)) {
                    List<OrganEO> organEOList = new ArrayList<OrganEO>();
                    organCatIdMap.put(catId, organEOList);
                }
                OrganEO newEO = new OrganEO();
                BeanUtils.copyProperties(organEO, newEO);
                if (null != configEO.getSortNum()) {
                    newEO.setSortNum(configEO.getSortNum());
                }
                organCatIdMap.get(catId).add(newEO);
            }
        }
        return organCatIdMap;
    }

    /**
     * 过滤单位信息
     *
     * @param organList
     * @param voList
     * @return
     * @author fangtinghua
     */
    public static void filterOrgan(List<OrganEO> organList, List<OrganVO> voList) {
        if (null == organList || organList.isEmpty()) {
            return;
        }
        Map<Long, OrganConfigEO> configMap = new HashMap<Long, OrganConfigEO>();
        List<OrganConfigEO> organConfigList = organConfigService.getEntities(OrganConfigEO.class, new HashMap<String, Object>());
        if (null != organConfigList && !organConfigList.isEmpty()) {
            for (OrganConfigEO config : organConfigList) {
                configMap.put(config.getOrganId(), config);
            }
        }
        for (OrganEO eo : organList) {
            Long organId = eo.getOrganId();
            OrganVO vo = new OrganVO();
            vo.setOrganId(eo.getOrganId());
            vo.setName(eo.getName());
            vo.setParentId(eo.getParentId());
            vo.setType(eo.getType());
            // 设置单位配置信息
            OrganConfigEO organConfigEO = configMap.get(organId);
            if (null == organConfigEO) {
                vo.setConfig(false);
                vo.setSortNum(eo.getSortNum());
                organConfigEO = new OrganConfigEO();
            } else {
                vo.setConfig(true);
                vo.setSortNum(ObjectUtils.defaultIfNull(organConfigEO.getSortNum(), eo.getSortNum()));
                getColumnName(organConfigEO, organConfigEO.getLinkPageIds());
            }
            vo.setData(organConfigEO);
            voList.add(vo);
        }
    }

    /**
     * 设置单位关联的栏目名称
     *
     * @param organConfigEO 单位配置信息
     * @param linkPageIds   关联的栏目ids
     */
    private static void getColumnName(OrganConfigEO organConfigEO, String linkPageIds) {
        if (StringUtils.isNotEmpty(linkPageIds)) {
            String[] idArr = linkPageIds.split(",");
            List<String> nameList = new ArrayList<String>();
            for (String id : idArr) {
                IndicatorEO indicatorEO = CacheHandler.getEntity(IndicatorEO.class, id);
                if (null != indicatorEO) {
                    nameList.add(indicatorEO.getName());
                }
            }
            organConfigEO.setLinkPageNames(StringUtils.join(nameList.toArray(), ","));
        }
    }

    /**
     * 单位排序
     *
     * @param voList
     * @return
     * @author fangtinghua
     */
    public static void sortOrgan(List<OrganVO> voList) {
        if (null == voList || voList.isEmpty()) {
            return;
        }
        Collections.sort(voList, new Comparator<OrganVO>() {

            @Override
            public int compare(OrganVO seft, OrganVO that) {
                return seft.getSortNum().compareTo(that.getSortNum());
            }
        });
    }

    /**
     * 查询出单位配置的私有目录和隐藏目录
     *
     * @param organId
     * @return
     * @author fangtinghua
     */
    public static Map<Long, PublicCatalogOrganRelEO> getCatalogRelMap(Long organId) {
        List<PublicCatalogOrganRelEO> relList = CacheHandler.getList(PublicCatalogOrganRelEO.class, CacheGroup.CMS_PARENTID, organId);
        if (null == relList || relList.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Long, PublicCatalogOrganRelEO> relMap = new HashMap<Long, PublicCatalogOrganRelEO>();
        for (PublicCatalogOrganRelEO organRel : relList) {
            relMap.put(organRel.getCatId(), organRel);
        }
        return relMap;
    }

    /**
     * 过滤目录属性
     *
     * @param catalogEO 目录
     * @param organId   根据单位id来
     */
    public static void filterCatalog(PublicCatalogEO catalogEO, Long organId) {
        filterCatalog(catalogEO, organId, true);//不覆盖字段值，默认覆盖
    }

    /**
     * 过滤目录属性，是否覆盖目录的分管领导、责任人和电话字段值
     * 当coverCatalogValue为true要覆盖三个字段，不区分该单位是否有私有目录
     *
     * @param catalogEO
     * @param organId
     * @param coverCatalogValue
     */
    public static void filterCatalog(PublicCatalogEO catalogEO, Long organId, boolean coverCatalogValue) {
        if (null == catalogEO) {// 排除
            return;
        }
        PublicCatalogOrganRelEO relEO = null;
        if (null != organId && organId > 0L) {// 过滤单位时
            relEO = CacheHandler.getEntity(PublicCatalogOrganRelEO.class, CacheGroup.CMS_JOIN_ID, organId, catalogEO.getId());
        }
        filterCatalog(catalogEO, relEO, organId, coverCatalogValue);
    }

    /**
     * 过滤目录属性，是否覆盖目录的分管领导、责任人和电话字段值
     * 当coverCatalogValue为true要覆盖三个字段，不区分该单位是否有私有目录
     *
     * @param catalogEO         目录对象
     * @param relEO             目录对应关系对象
     * @param coverCatalogValue 是否覆盖值
     */
    public static void filterCatalog(PublicCatalogEO catalogEO, PublicCatalogOrganRelEO relEO, Long organId, boolean coverCatalogValue) {
        if (null == catalogEO) {// 排除
            return;
        }
        if (null != relEO) {// 当私有目录时
            catalogEO.setCode(relEO.getCode());
            catalogEO.setName(relEO.getName());
            catalogEO.setSortNum(relEO.getSortNum());
            catalogEO.setLink(relEO.getLink());
            catalogEO.setIsShow(relEO.getIsShow());
            catalogEO.setDescription(relEO.getDescription());
            catalogEO.setIsParent(relEO.getIsParent());
            catalogEO.setLeader(relEO.getLeader());
            catalogEO.setPersonLiable(relEO.getPersonLiable());
            catalogEO.setPhone(relEO.getPhone());
            catalogEO.setRelCatIds(relEO.getRelCatIds());
            catalogEO.setRelCatNames(relEO.getRelCatNames());
            catalogEO.setReferColumnIds(relEO.getReferColumnIds());
            catalogEO.setReferColumnNames(relEO.getReferColumnNames());
            catalogEO.setReferOrganCatIds(relEO.getReferOrganCatIds());
            catalogEO.setReferOrganCatNames(relEO.getReferOrganCatNames());
            catalogEO.setUpdateCycle(relEO.getUpdateCycle());
            catalogEO.setYellowCardWarning(relEO.getYellowCardWarning());
            catalogEO.setRedCardWarning(relEO.getRedCardWarning());
            catalogEO.setAttribute(relEO.getAttribute());
            catalogEO.setColumnTypeIds(relEO.getColumnTypeIds());
            catalogEO.setKeyWords(relEO.getKeyWords());
            catalogEO.setBackup1(relEO.getBackup1());
            catalogEO.setBackup2(relEO.getBackup2());
        }
        if (coverCatalogValue) {// 需要覆盖三个属性
            coverCatalogValue(catalogEO, catalogEO.getParentId(), organId);
        }
    }

    /**
     * 递归查找父目录的属性，以分管领导为准，当分管领导为空，往上递增。
     *
     * @param catalogEO 目录
     * @param parentId  父目录id
     * @param organId   单位id
     */
    private static void coverCatalogValue(PublicCatalogEO catalogEO, Long parentId, Long organId) {
        String leader = catalogEO.getLeader();// 分管领导
        if (null == parentId || parentId <= 0L || StringUtils.isNotEmpty(leader)) {
            return;
        }
        PublicCatalogEO parentEO = CacheHandler.getEntity(PublicCatalogEO.class, parentId);
        if (null == parentEO) {
            return;
        }
        filterCatalog(parentEO, organId, false);// 填充父目录值
        if (StringUtils.isEmpty(parentEO.getLeader())) {//父目录为空，往上递归
            coverCatalogValue(catalogEO, parentEO.getParentId(), organId);
        } else {
            catalogEO.setLeader(parentEO.getLeader());
            catalogEO.setPersonLiable(parentEO.getPersonLiable());
            catalogEO.setPhone(parentEO.getPhone());
        }
    }

    /**
     * 过滤目录属性
     *
     * @param catalogEO 目录
     * @param relEO     目录对应关系
     * @author fangtinghua
     */
    public static void filterCatalogRel(PublicCatalogOrganRelEO relEO, PublicCatalogEO catalogEO) {
        if (null == relEO || null == catalogEO) {
            return;
        }
        relEO.setCode(catalogEO.getCode());
        relEO.setName(catalogEO.getName());
        relEO.setSortNum(catalogEO.getSortNum());
        relEO.setLink(catalogEO.getLink());
        relEO.setIsShow(catalogEO.getIsShow());
        relEO.setIsParent(catalogEO.getIsParent());
        relEO.setLeader(catalogEO.getLeader());
        relEO.setPersonLiable(catalogEO.getPersonLiable());
        relEO.setPhone(catalogEO.getPhone());
        relEO.setDescription(catalogEO.getDescription());
        relEO.setRelCatIds(catalogEO.getRelCatIds());
        relEO.setRelCatNames(catalogEO.getRelCatNames());
        relEO.setReferColumnIds(catalogEO.getReferColumnIds());
        relEO.setReferColumnNames(catalogEO.getReferColumnNames());
        relEO.setReferOrganCatIds(catalogEO.getReferOrganCatIds());
        relEO.setReferOrganCatNames(catalogEO.getReferOrganCatNames());
        relEO.setUpdateCycle(catalogEO.getUpdateCycle());
        relEO.setYellowCardWarning(catalogEO.getYellowCardWarning());
        relEO.setRedCardWarning(catalogEO.getRedCardWarning());
        relEO.setAttribute(catalogEO.getAttribute());
        relEO.setColumnTypeIds(catalogEO.getColumnTypeIds());
        relEO.setKeyWords(catalogEO.getKeyWords());
        relEO.setBackup1(catalogEO.getBackup1());
        relEO.setBackup2(catalogEO.getBackup2());
    }


    /**
     * 过滤目录列表，删除隐藏的私有目录和公有目录
     *
     * @param catalogList 目录列表
     * @param organId     单位id
     * @param filterHide  是否隐藏
     * @author fangtinghua
     */
    public static void filterCatalogList(List<PublicCatalogEO> catalogList, Long organId, boolean filterHide) {
        if (null == catalogList || catalogList.isEmpty()) {
            return;
        }
        Map<Long, PublicCatalogOrganRelEO> relMap = PublicCatalogUtil.getCatalogRelMap(organId);// 所有本单位的关系对象
        for (Iterator<PublicCatalogEO> it = catalogList.iterator(); it.hasNext(); ) {
            PublicCatalogEO eo = it.next();
            if(eo == null){
                it.remove();
                continue;
            }
            if (null != relMap && relMap.containsKey(eo.getId())) {// 本单位私有
                PublicCatalogOrganRelEO relEO = relMap.get(eo.getId());
                // 不显示或者不是本单位的目录
                if (filterHide && !relEO.getIsShow()) {
                    it.remove();
                    continue;
                }
                filterCatalog(eo, relEO, organId, false);// 填充目录属性
            } else if ((eo.getType() != null && eo.getType() == 2) || (filterHide && eo.getIsShow() != null && !eo.getIsShow())) {// 删除不是本单位私有目录，或者隐藏的公有目录
                it.remove();
            }
        }
    }

    /**
     * 获取单位对应目录信息
     *
     * @param organId
     * @param catId
     * @return
     */
    public static PublicCatalogEO getPrivateCatalogByOrganId(Long organId, Long catId) {
        PublicCatalogEO publicCatalogEO = CacheHandler.getEntity(PublicCatalogEO.class, catId);
        PublicCatalogOrganRelEO relEO = CacheHandler.getEntity(PublicCatalogOrganRelEO.class, CacheGroup.CMS_JOIN_ID, organId, catId);
        filterCatalog(publicCatalogEO, relEO, organId, true);
        return publicCatalogEO;
    }

    /**
     * map按照key进行排序
     */
    public static class CatKeyComparator implements Comparator<Long> {

        @Override
        public int compare(Long seftId, Long thatId) {
            PublicCatalogEO self = CacheHandler.getEntity(PublicCatalogEO.class, seftId);
            PublicCatalogEO that = CacheHandler.getEntity(PublicCatalogEO.class, thatId);
            return self.getSortNum().compareTo(that.getSortNum());
        }
    }


    /**
     * 目录排序，从小到大排序
     *
     * @param catalogList
     * @author fangtinghua
     */
    public static void sortCatalog(List<PublicCatalogEO> catalogList) {
        if (null == catalogList || catalogList.isEmpty()) {
            return;
        }
        Collections.sort(catalogList, new Comparator<PublicCatalogEO>() {

            @Override
            public int compare(PublicCatalogEO seft, PublicCatalogEO that) {
                return seft.getSortNum().compareTo(that.getSortNum());
            }
        });
    }

    /**
     * 单位排序
     *
     * @param resultList
     */
    public static void sortList(List<OrganEO> resultList) {
        Collections.sort(resultList, new Comparator<OrganEO>() {

            @Override
            public int compare(OrganEO o1, OrganEO o2) {
                return o1.getSortNum().compareTo(o2.getSortNum());
            }
        });
    }

    /**
     * 获取目录层级
     *
     * @param organId
     * @param catId
     * @return
     */
    public static String getCatalogPath(Long organId, Long catId) {// 获取信息公开目录全路径
        if (null == organId || null == catId) {
            return "";
        }
        OrganConfigEO organConfigEO = CacheHandler.getEntity(OrganConfigEO.class, CacheGroup.CMS_ORGAN_ID, organId);
        if (null == organConfigEO) {
            return "";
        }
        List<String> resultList = new ArrayList<String>();
        getParent(resultList, organId, organConfigEO.getCatId(), catId);
        Collections.reverse(resultList);// 反转
        return StringUtils.join(resultList, " > ");
    }

    /**
     * 获取目录层级
     *
     * @param resultList
     * @param organId
     * @param parentId
     * @param catId
     */
    public static void getParent(List<String> resultList, Long organId, Long parentId, Long catId) {
        if (null == parentId || parentId.equals(catId)) {
            return;
        }
        PublicCatalogEO catalogEO = CacheHandler.getEntity(PublicCatalogEO.class, catId);
        if (null == catalogEO) {
            return;
        }
        PublicCatalogOrganRelEO relEO = CacheHandler.getEntity(PublicCatalogOrganRelEO.class, CacheGroup.CMS_JOIN_ID, organId, catId);
        resultList.add(null == relEO ? catalogEO.getName() : relEO.getName());
        getParent(resultList, organId, parentId, catalogEO.getParentId());
    }


    /**
     * 获取目录排序组合字符串
     *
     * @param organId
     * @param catId
     * @return
     */
    public static String getCatalogSortStr(Long organId, Long catId) {// 获取信息公开目录全路径
        if (null == organId || null == catId) {
            return "";
        }
        OrganConfigEO organConfigEO = CacheHandler.getEntity(OrganConfigEO.class, CacheGroup.CMS_ORGAN_ID, organId);
        if (null == organConfigEO) {
            return "";
        }
        List<String> resultList = new ArrayList<String>();
        getParentSortNum(resultList, organId, organConfigEO.getCatId(), catId);
        Collections.reverse(resultList);// 反转
        return StringUtils.join(resultList, "-");
    }

    /**
     * 获取目录排序组合字符串
     *
     * @param resultList
     * @param organId
     * @param parentId
     * @param catId
     */
    public static void getParentSortNum(List<String> resultList, Long organId, Long parentId, Long catId) {
        if (null == parentId || parentId.equals(catId)) {
            return;
        }
        PublicCatalogEO catalogEO = CacheHandler.getEntity(PublicCatalogEO.class, catId);
        if (null == catalogEO) {
            return;
        }
        PublicCatalogOrganRelEO relEO = CacheHandler.getEntity(PublicCatalogOrganRelEO.class, CacheGroup.CMS_JOIN_ID, organId, catId);
        String sortStr;
        if(null == relEO){
            sortStr = String.format("%05d", catalogEO.getSortNum());
        }else {
            sortStr = String.format("%05d", relEO.getSortNum());
        }
        resultList.add(sortStr);
        getParentSortNum(resultList, organId, parentId, catalogEO.getParentId());
    }


    /**
     * 获取顶层目录id
     * @return
     */
    public static Long getTopCatId(Long catId){
        PublicCatalogEO catalogEO = CacheHandler.getEntity(PublicCatalogEO.class, catId);
        Long parentId = catalogEO.getParentId();
        Long id = catalogEO.getId();
        while (parentId != 0){
            catalogEO = CacheHandler.getEntity(PublicCatalogEO.class, parentId);
            parentId = catalogEO.getParentId();
            id = catalogEO.getId();
        }
        return id;
    }

    /**
     * 过滤目录列表，删除隐藏的私有目录和公有目录
     *
     * @param catalogList 目录列表
     * @author fangtinghua
     */
    public static Map<Long, Long> getChildNumMap(List<PublicCatalogEO> catalogList) {
        Map<Long, Long> map = new HashMap<Long, Long>();
        for (PublicCatalogEO item : catalogList) {
            Long key = item.getParentId() == null ? 0l: item.getParentId();
            if(!map.containsKey(key)){
                map.put(key, 1l);
            }
            map.put(key, map.get(key) + 1l);
        }
        return map;
    }
}