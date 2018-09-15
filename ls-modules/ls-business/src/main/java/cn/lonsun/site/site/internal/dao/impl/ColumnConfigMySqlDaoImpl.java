package cn.lonsun.site.site.internal.dao.impl;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.*;

import static cn.lonsun.common.util.AppUtil.getStrings;


/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2017-5-8<br/>
 */
@Repository("columnConfigMySqlDao")
public class ColumnConfigMySqlDaoImpl extends ColumnConfigDaoImpl {

    //站点为1
    private static final String levelFormula = "(case when parent_id = site_id then 2 else length(parent_ids) - length(REPLACE(parent_ids, ',', '')) + 3 end)";


    /**
     * 获取指定节点下指定层的子栏目
     *
     * @param indicatorId
     * @param level
     * @param condition
     * @param vo
     * @return
     * @update 2017-12-20 by zhongjun
     */
    public List<ColumnMgrEO> getLevelColumnTree(Long indicatorId, int[] level, String condition, PageQueryVO vo) {
        if (level == null || level.length == 0) {
            return Collections.emptyList();
        }
        IndicatorEO indicatorEO = CacheHandler.getEntity(IndicatorEO.class, indicatorId);
        Long siteId = null;
        if (indicatorEO != null) {
            siteId = indicatorEO.getSiteId();
        } else {
            siteId = indicatorId;
        }
        //获取当前层级,根据parent_ids 中逗号数量来决定层级
        Integer lev = getLev(siteId, indicatorId);
        //生成节点相对站点的层级查询条件
        StringBuffer sql = new StringBuffer();
        sql.append("select " + levelFormula + " as lev, is_show as isShow,indicator_Id as indicatorId,name as name,parent_id as parentId,url_path as urlPath,sort_num as sortNum,is_parent as isParent,create_date as createDate ");
        sql.append(",site_id as siteId,type as type,column_config_id as columnConfigId,syn_column_ids as synColumnIds ,syn_column_names as synColumnNames");
        sql.append(",refer_Column_Ids as referColumnIds,refer_Column_Names as referColumnNames,refer_Organ_Cat_Ids as referOrganCatIds,refer_Organ_Cat_Names as referOrganCatNames ");
        sql.append(",content_model_code as contentModelCode,gene_page_ids as genePageIds,column_type_code as columnTypeCode");
        sql.append(",gene_page_names as genePageNames,is_start_url as isStartUrl,trans_url as transUrl,trans_window as transWindow ");
        //查询 菜单类型为 CMS_Section 当前站点的  （父节点包含节点id 或者当前节点就是节点id） 节点层级在范围内
        sql.append(" from cms_column_mgr where type = '" + IndicatorEO.Type.CMS_Section.toString() + "' and site_id = " + siteId);
        sql.append(" and ( concat(concat(site_id,','),parent_ids) like '%" + indicatorId.longValue() + "%' or indicator_Id = " + indicatorId.longValue() + ")");
        StringBuilder sb = new StringBuilder();
        for (int i : level) {
            //lev 为indicatorId 相对于站点的层级， i 为相对于为indicatorId的层级（1 表示当前节点）
            sb.append(i + lev - 1).append(",");
        }
        sql.append(" and lev in (" + sb.substring(0, sb.length() - 1) + ")");
        if (!AppUtil.isEmpty(condition)) {
            sql.append(" and " + condition);
        }
        sql.append("  order by sort_num asc ");

        List<String> fields = new ArrayList<String>();
        fields.add("lev");
        fields.add("isShow");
        fields.add("indicatorId");
        fields.add("name");
        fields.add("parentId");
        fields.add("urlPath");
        fields.add("sortNum");
        fields.add("isParent");
        fields.add("createDate");
        fields.add("siteId");
        fields.add("type");
        fields.add("columnConfigId");
        fields.add("synColumnIds");
        fields.add("synColumnNames");
        fields.add("referColumnIds");
        fields.add("referColumnNames");
        fields.add("referOrganCatIds");
        fields.add("referOrganCatNames");
        fields.add("contentModelCode");
        fields.add("genePageIds");
        fields.add("columnTypeCode");
        fields.add("genePageNames");
        fields.add("isStartUrl");
        fields.add("transUrl");
        fields.add("transWindow");
        String[] strArr = new String[fields.size()];
        List<ColumnMgrEO> list = null;

        if (null == vo) {
            list = (List<ColumnMgrEO>) getBeansBySql(sql.toString(), new Object[]{}, ColumnMgrEO.class, fields.toArray(strArr));
        } else {
            Pagination page = getPaginationBySql(vo.getPageIndex(), vo.getPageSize(), sql.toString(), new Object[]{}, ColumnMgrEO.class, fields.toArray(strArr));
            list = (List<ColumnMgrEO>) page.getData();
        }
        return AppUtil.isEmpty(list) ? new ArrayList<ColumnMgrEO>() : list;
    }

    /**
     * 获取指定节点的所有父级节点
     *
     * @param indicatorId
     * @return
     */
    public List<ColumnMgrEO> getParentColumns(Long indicatorId) {
        String paths = getPaths(indicatorId);
        if (StringUtils.isEmpty(paths)) {
            return null;
        }
        StringBuffer sb = new StringBuffer("")
                .append(" select t.is_show as isShow,t.indicator_id as indicatorId,t.name as name,t.parent_id as parentId, t.url_path as urlPath")
                .append(",t.sort_num as sortNum,t.is_parent as isParent,t.create_date as createDate,t.site_id as siteId,t.type as type")
                .append(",t.column_config_id as columnConfigId,t.syn_column_ids as synColumnIds ,t.syn_column_names as synColumnNames")
                .append(",refer_Column_Ids as referColumnIds,refer_Column_Names as referColumnNames,refer_Organ_Cat_Ids as referOrganCatIds,refer_Organ_Cat_Names as referOrganCatNames ")
                .append(",t.content_model_code as contentModelCode,t.gene_page_ids as genePageIds,t.column_type_code as columnTypeCode")
                .append(",t.gene_page_names as genePageNames,t.is_start_url as isStartUrl,t.trans_url as transUrl,t.trans_window as transWindow ")
                .append(" from cms_column_mgr t where t.record_status = ?");
        sb.append(" and t.indicator_id in(" + paths + ")");
        sb.append(" and t.type=?");

        List<String> fields = new ArrayList<String>();
        fields.add("isShow");
        fields.add("indicatorId");
        fields.add("name");
        fields.add("parentId");
        fields.add("urlPath");
        fields.add("sortNum");
        fields.add("isParent");
        fields.add("createDate");
        fields.add("siteId");
        fields.add("type");
        fields.add("columnConfigId");
        fields.add("synColumnIds");
        fields.add("synColumnNames");
        fields.add("referColumnIds");
        fields.add("referColumnNames");
        fields.add("referOrganCatIds");
        fields.add("referOrganCatNames");
        fields.add("contentModelCode");
        fields.add("genePageIds");
        fields.add("columnTypeCode");
        fields.add("genePageNames");
        fields.add("isStartUrl");
        fields.add("transUrl");
        fields.add("transWindow");
        String[] strArr = new String[fields.size()];
        List<ColumnMgrEO> list = (List<ColumnMgrEO>) getBeansBySql(sb.toString(), new Object[]{
                AMockEntity.RecordStatus.Normal.toString(), IndicatorEO.Type.CMS_Section.toString()}, ColumnMgrEO.class, fields.toArray(strArr));
        return AppUtil.isEmpty(list) ? new ArrayList<ColumnMgrEO>() : list;
    }

    /**
     * 获取指定节点下的所有父级节点，返回Map集合
     *
     * @param indicatorId
     * @return
     */
    public Map<Long, Boolean> getParentIndicatorIds(Long indicatorId) {
        Map<Long, Boolean> map = new HashMap<Long, Boolean>();
        String paths = getPaths(indicatorId);
        if (!StringUtils.isEmpty(paths)) {
            Long[] ids = AppUtil.getLongs(paths, ",");
            for (int i = 0; i < ids.length; i++) {
                if (ids[i] != 0) {
                    map.put(ids[i], true);
                }
            }
        }
        return map;
    }

    /**
     * 获取指定节点下的所有子节点（叶子节点/父节点）
     *
     * @param indicatorId
     * @param parentFlag
     * @return
     */
    public List<ColumnMgrEO> getChildColumn(Long indicatorId, int[] parentFlag) {
        IndicatorEO indicatorEO = CacheHandler.getEntity(IndicatorEO.class, indicatorId);
        if (indicatorEO == null)
            return null;
        List<ColumnMgrEO> columnMgrList = getColumnMgrList(indicatorEO.getSiteId());
        Map<Long, List<ColumnMgrEO>> map = parseColumnMgrListToMap(columnMgrList);
        List<ColumnMgrEO> childList = new ArrayList<ColumnMgrEO>();
        String flag = getIntArrayStr(parentFlag);
        getChildColumn(indicatorId, map, childList, flag);
        return childList;
    }


    public List<ColumnMgrEO> getColumnMgrList(Long siteId) {
        List<Object> values = new ArrayList<Object>();
        StringBuffer sb = new StringBuffer("")
                .append(" select t.indicator_id as indicatorId,t.name as name,t.parent_id as parentId,t.sort_num as sortNum,is_parent as isParent, t.url_path as urlPath,t.create_date as createDate ")
                .append(",t.site_id as siteId,t.type as type,t.column_config_id as columnConfigId,t.syn_column_ids as synColumnIds ,t.syn_column_names as synColumnNames")
                .append(",refer_Column_Ids as referColumnIds,refer_Column_Names as referColumnNames,refer_Organ_Cat_Ids as referOrganCatIds,refer_Organ_Cat_Names as referOrganCatNames ")
                .append(",t.content_model_code as contentModelCode,t.gene_page_ids as genePageIds,t.column_type_code as columnTypeCode")
                .append(",t.gene_page_names as genePageNames,t.is_start_url as isStartUrl,t.trans_url as transUrl,t.trans_window as transWindow ")
                .append(" from cms_column_mgr t  where t.record_status = ? and t.type= ? ");
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(IndicatorEO.Type.CMS_Section.toString());
        if (null != siteId) {
            sb.append(" and t.site_id = ? ");
            values.add(siteId);
        }
        List<String> fields = new ArrayList<String>();
        fields.add("indicatorId");
        fields.add("name");
        fields.add("parentId");
        fields.add("urlPath");
        fields.add("sortNum");
        fields.add("isParent");
        fields.add("createDate");
        fields.add("siteId");
        fields.add("type");
        fields.add("columnConfigId");
        fields.add("synColumnIds");
        fields.add("synColumnNames");
        fields.add("referColumnIds");
        fields.add("referColumnNames");
        fields.add("referOrganCatIds");
        fields.add("referOrganCatNames");
        fields.add("contentModelCode");
        fields.add("genePageIds");
        fields.add("columnTypeCode");
        fields.add("genePageNames");
        fields.add("isStartUrl");
        fields.add("transUrl");
        fields.add("transWindow");
        String[] strArr = new String[fields.size()];
        List<ColumnMgrEO> list = (List<ColumnMgrEO>) getBeansBySql(sb.toString(), values.toArray(), ColumnMgrEO.class, fields.toArray(strArr));
        return AppUtil.isEmpty(list) ? new ArrayList<ColumnMgrEO>() : list;
    }

    /**
     * 将栏目列表转换成Map
     *
     * @param columnMgrList
     * @return
     */
    public Map<Long, List<ColumnMgrEO>> parseColumnMgrListToMap(List<ColumnMgrEO> columnMgrList) {
        Map<Long, List<ColumnMgrEO>> map = new HashMap<Long, List<ColumnMgrEO>>();
        List<ColumnMgrEO> tempList = null;
        for (ColumnMgrEO eo : columnMgrList) {
            if (map.containsKey(eo.getParentId())) {
                map.get(eo.getParentId()).add(eo);
            } else {
                tempList = new ArrayList<ColumnMgrEO>();
                tempList.add(eo);
                map.put(eo.getParentId(), tempList);
            }
        }
        return map;
    }


    /**
     * 获取指定栏目的子栏目
     *
     * @param indicatorId
     * @param columnMgrMap
     * @param parentFlag（0：获取为类型为叶子节点的子栏目 1：获取类型为非叶子节点的子栏目 0,1:获取所有子栏目）
     * @return
     */
    public void getChildColumn(Long indicatorId, Map<Long, List<ColumnMgrEO>> columnMgrMap, List<ColumnMgrEO> childList, String parentFlag) {
        List<ColumnMgrEO> list = columnMgrMap.get(indicatorId);
        for (ColumnMgrEO eo : list) {
            boolean isAdd = true;
            if ("0".equals(parentFlag)) {
                isAdd = Integer.valueOf(0).equals(eo.getIsParent());
            } else if ("1".equals(parentFlag)) {
                isAdd = Integer.valueOf(1).equals(eo.getIsParent());
            }
            if (isAdd) {
                childList.add(eo);
            }
            if (eo.getIsParent() == 1) {
                getChildColumn(eo.getIndicatorId(), columnMgrMap, childList, parentFlag);
            }
        }
    }

    /**
     * 获取指定站点下的指定层公共栏目
     *
     * @param siteId
     * @param i
     * @return
     */
    public List<ColumnMgrEO> getLevComColumn(Long siteId, int[] i) {

        StringBuffer sb1 = new StringBuffer("");
        StringBuffer sb2 = new StringBuffer("");
        ColumnMgrEO columnMgrEO = CacheHandler.getEntity(ColumnMgrEO.class, siteId);
        String str = "";
        if (columnMgrEO == null) {//是站点
            str = getIntArrayStr(i);
            sb1 = getQuerySql(true, true, siteId);
            sb2 = getQuerySql(false, true, siteId);
        } else {//非站点
            Integer lev = getLev(columnMgrEO.getSiteId(), siteId);
            str = getIntArrayStr2(i, lev);
            sb1 = getQuerySql(true, true, columnMgrEO.getSiteId());
            sb2 = getQuerySql(false, true, columnMgrEO.getSiteId());
        }
        StringBuffer sb = new StringBuffer("select (temp.levels+2) as lev, t.indicator_id as indicatorId,t.name as name,t.parent_id as parentId, t.url_path as urlPath,")
                .append("t.sort_num as sortNum,t.is_parent as isParent,t.create_date as createDate ,t.site_id as siteId,t.type as type,")
                .append("t.column_config_id as columnConfigId,t.content_model_code as contentModelCode,t.column_type_code as columnTypeCode")
                .append(" from cms_column_mgr t inner join (").append(sb1).append(") temp on t.indicator_id=temp.indicator_id where t.record_status = ?");
        sb.append(" and temp.paths like CONCAT((select likeStr from (").append(sb2).append(") likeTemp where likeTemp.indicator_id = ?),'%')");

        sb.append(" and (temp.levels+2) in(" + str + ") ")
                .append(" and t.type=?");
        List<String> fields = new ArrayList<String>();
        fields.add("lev");
        fields.add("indicatorId");
        fields.add("name");
        fields.add("parentId");
        fields.add("urlPath");
        fields.add("sortNum");
        fields.add("isParent");
        fields.add("createDate");
        fields.add("siteId");
        fields.add("type");
        fields.add("columnConfigId");
        fields.add("contentModelCode");
        fields.add("columnTypeCode");
        String[] strArr = new String[fields.size()];
        List<ColumnMgrEO> list = null;

        list = (List<ColumnMgrEO>) getBeansBySql(sb.toString(), new Object[]{AMockEntity.RecordStatus.Normal.toString(),
                siteId, IndicatorEO.Type.COM_Section.toString()}, ColumnMgrEO.class, fields.toArray(strArr));

        return AppUtil.isEmpty(list) ? new ArrayList<ColumnMgrEO>() : list;
    }

    /**
     * 获取某个节点下的所有子栏目
     *
     * @param columnId
     * @return
     */
    public List<ColumnMgrEO> getAllColumnBySite(Long columnId) {
        IndicatorEO indicatorEO = CacheHandler.getEntity(IndicatorEO.class, columnId);
        Long siteId = null;
        if (indicatorEO != null) {
            siteId = indicatorEO.getSiteId();
        } else {
            siteId = columnId;
        }
        StringBuffer sb1 = getQuerySql(true, true, siteId);
        StringBuffer sb2 = getQuerySql(false, true, siteId);

        StringBuffer sb = new StringBuffer("")
                .append(" select r.indicator_id as indicatorId,r.parent_id parentId, r.url_path as urlPath, r.name name,r.type type,r.sort_num sortNum,r.is_parent isParent,r.column_type_code  columnTypeCode")
                .append(",r.content_model_code as contentModelCode,r.site_id siteId ,r.indicator_id||'_'||r.site_id  columnStrId,r.parent_id||'_'||r.site_id parentStrId")
                .append(" from cms_column_mgr r inner join (").append(sb1).append(") temp on r.indicator_id=temp.indicator_Id where r.record_status = ?");
        sb.append(" and temp.paths like CONCAT((select likeStr from (").append(sb2).append(") likeTemp where likeTemp.indicator_id = ?),'%')")
                .append("order by r.sort_num asc,r.create_date asc");

        String[] fileds = new String[]{"indicatorId", "parentId", "name", "type", "sortNum", "isParent", "columnTypeCode", "contentModelCode", "siteId", "columnStrId", "parentStrId"};
        Object[] values = new Object[]{AMockEntity.RecordStatus.Normal.toString(), columnId};
        return (List<ColumnMgrEO>) getBeansBySql(sb.toString(), values, ColumnMgrEO.class, fileds);
    }

    public List<ColumnMgrEO> getColumns1(List<String> codes, Long siteId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("siteId", siteId);

        StringBuffer sb1 = getQuerySql(true, true, siteId);

        StringBuffer sb = new StringBuffer("")
                .append("select t.indicator_id as indicatorId,t.name as name,t.parent_id as parentId,t.url_path as urlPath, t.sort_num as sortNum,")
                .append("t.is_parent as isParent,t.create_date as createDate ,t.site_id as siteId,t.type as type,")
                .append("t.content_model_code as contentModelCode,t.column_type_code as columnTypeCode")
                .append(" from cms_column_mgr t inner join (").append(sb1).append(") temp on t.indicator_id=temp.indicator_Id where is_parent = 0 ");
        if (!AppUtil.isEmpty(codes)) {
            String codestr = null;
            for (String s : codes) {
                if (null == codestr) {
                    codestr = s;
                } else {
                    codestr = "," + s;
                }
            }
            sb.append(" and t.column_type_code in('" + codestr + "') ");
        }
        sb.append(" and t.site_id=:siteId ");
        sb.append(" group by ");
        sb.append(" t.indicator_Id,");
        sb.append(" t.name,");
        sb.append(" t.parent_id,");
        sb.append(" t.sort_num,");
        sb.append(" t.is_parent,");
        sb.append(" t.create_date,");
        sb.append(" t.site_id,");
        sb.append(" t.type,");
        sb.append(" t.content_model_code,");
        sb.append(" t.column_type_code");
        sb.append("  order by t.sort_num asc  ");

        List<String> fields = new ArrayList<String>();
        fields.add("indicatorId");
        fields.add("name");
        fields.add("parentId");
        fields.add("urlPath");
        fields.add("sortNum");
        fields.add("isParent");
        fields.add("createDate");
        fields.add("siteId");
        fields.add("type");
        fields.add("contentModelCode");
        fields.add("columnTypeCode");
        String[] strArr = new String[fields.size()];
        List<ColumnMgrEO> list = (List<ColumnMgrEO>) getBeansBySql(sb.toString(), params, ColumnMgrEO.class, fields.toArray(strArr));

        return list;
    }

    /**
     * 获取某个节点的路径
     *
     * @param indicatorId
     * @return
     */
    private String getPaths(Long indicatorId) {
        ColumnMgrEO columnMgrEO = CacheHandler.getEntity(ColumnMgrEO.class, indicatorId);
        Long siteId = null;
        if (columnMgrEO != null) {
            siteId = columnMgrEO.getSiteId();
        } else {
            siteId = indicatorId;
        }
        StringBuffer sb1 = getQuerySql(true, true, siteId);
        StringBuffer sb2 = new StringBuffer("select paths from(").append(sb1).append(") temp where temp.indicator_id=" + indicatorId);
        List list2 = this.getCurrentSession().createSQLQuery(sb2.toString()).list();
        String paths = "";
        if (list2 != null && list2.size() > 0) {
            paths = list2.get(0).toString();
        }
        return paths;
    }

    /**
     * 获取某个节点所在层级
     *
     * @param indicatorId
     * @return
     */
    private Integer getLev(Long siteId, Long indicatorId) {
        StringBuffer sql = new StringBuffer();
//        sql.append(" SELECT indicator_id as indicatorId,(levels+2) as lev");
//        sql.append(" from (select indicator_id,parent_id, ");
//        sql.append(" @le \\:= IF (parent_id = " + siteId + ",0,IF (LOCATE(CONCAT('|', parent_id, '\\:') ,@pathlevel) > 0, ");
//        sql.append(" SUBSTRING_INDEX(SUBSTRING_INDEX(@pathlevel, CONCAT('|', parent_id, '\\:') ,-1),'|',1) + 1 ,@le + 1)) levels, ");
//        sql.append(" @pathlevel \\:= CONCAT( @pathlevel, '|', indicator_id, '\\:', @le, '|') pathlevel, ");
//        sql.append(" @pathnodes \\:= IF (parent_id = " + siteId + ",0,CONCAT_WS(',',IF (LOCATE(CONCAT('|', parent_id, '\\:') ,@pathall) > 0, ");
//        sql.append(" SUBSTRING_INDEX(SUBSTRING_INDEX(@pathall,CONCAT('|', parent_id, '\\:') ,-1),'|',1) ,@pathnodes), parent_id)) paths, ");
//        sql.append(" @pathall \\:= CONCAT(@pathall,'|',indicator_id,'\\:',@pathnodes,'|') pathall from cms_column_mgr,(SELECT @le \\:= 0 , ");
//        sql.append(" @pathlevel \\:= '',");
//        sql.append(" @pathall \\:= '' ,@pathnodes \\:= '') vv ");
//        sql.append(" where indicator_id=" + indicatorId);
//        sql.append(" order by parent_id, indicator_id) src ");
        sql.append("SELECT t.indicator_id AS indicatorId, " + levelFormula + " as lev ");
        sql.append("FROM cms_column_mgr t WHERE t.site_id  = " + siteId.longValue() + " and t.indicator_id = ").append(indicatorId.longValue());

        List<String> fields = new ArrayList<String>();
        fields.add("indicatorId");
        fields.add("lev");
        String[] strArr = new String[fields.size()];
        List<ColumnMgrEO> list = (List<ColumnMgrEO>) getBeansBySql(sql.toString(), new Object[]{}, ColumnMgrEO.class, fields.toArray(strArr));
        if (list == null && list.isEmpty()) {
            return 1;
        }
        return list.get(0).getLev();

    }

    /**
     * 获取sql语句
     *
     * @param path
     * @return
     */

    private StringBuffer getQuerySql(boolean path, boolean lev, Long siteId) {
        StringBuffer sql = new StringBuffer();
        if (path) {
            sql.append(" SELECT indicator_id as indicator_id,paths as paths");
        } else {
            sql.append(" SELECT indicator_id as indicator_id,paths as paths,concat(paths, ',', indicator_id) as likeStr");
        }
        if (lev) {
            sql.append(",levels as levels");
            sql.append(" from (select indicator_id,parent_id, ");
            sql.append(" @le \\:= IF (parent_id = ").append(siteId).append(",0,IF (LOCATE(CONCAT('|', parent_id, '\\:') ,@pathlevel) > 0, ");
            sql.append(" SUBSTRING_INDEX(SUBSTRING_INDEX(@pathlevel, CONCAT('|', parent_id, '\\:') ,-1),'|',1) + 1 ,@le + 1)) levels, ");
            sql.append(" @pathlevel \\:= CONCAT( @pathlevel, '|', indicator_id, '\\:', @le, '|') pathlevel, ");
            sql.append(" @pathnodes \\:= IF (parent_id = ").append(siteId).append(",0,CONCAT_WS(',',IF (LOCATE(CONCAT('|', parent_id, '\\:') ,@pathall) > 0, ");
            sql.append(" SUBSTRING_INDEX(SUBSTRING_INDEX(@pathall,CONCAT('|', parent_id, '\\:') ,-1),'|',1) ,@pathnodes), parent_id)) paths, ");
            sql.append(" @pathall \\:= CONCAT(@pathall,'|',indicator_id,'\\:',@pathnodes,'|') pathall from cms_column_mgr,(SELECT @le \\:= 0 , ");
            sql.append(" @pathlevel \\:= '',");
            sql.append(" @pathall \\:= '' ,@pathnodes \\:= '') vv ");
            sql.append(" order by parent_id, indicator_id) src ");
        } else {
            sql.append(" from (select indicator_id,parent_id ,");
//            sql.append(" @le \\:= IF (parent_id = ").append(siteId).append(",0,IF (LOCATE(CONCAT('|', parent_id, '\\:') ,@pathlevel) > 0, ");
            //sql.append(" SUBSTRING_INDEX(SUBSTRING_INDEX(@pathlevel, CONCAT('|', parent_id, '\\:') ,-1),'|',1) + 1 ,@le + 1)) levels, ");
            //sql.append(" @pathlevel \\:= CONCAT( @pathlevel, '|', indicator_id, '\\:', @le, '|') pathlevel, ");
            sql.append(" @pathnodes \\:= IF (parent_id = ").append(siteId).append(",0,CONCAT_WS(',',IF (LOCATE(CONCAT('|', parent_id, '\\:') ,@pathall) > 0, ");
            sql.append(" SUBSTRING_INDEX(SUBSTRING_INDEX(@pathall,CONCAT('|', parent_id, '\\:') ,-1),'|',1) ,@pathnodes), parent_id)) paths, ");
            sql.append(" @pathall \\:= CONCAT(@pathall,'|',indicator_id,'\\:',@pathnodes,'|') pathall from cms_public_catalog,(SELECT @le \\:= 0 , ");
            //sql.append(" @pathlevel \\:= '',");
            sql.append(" @pathall \\:= '' ,@pathnodes \\:= '') vv ");
            sql.append(" order by parent_id, indicator_id) src ");

        }
        return sql;

    }

    public String getIntArrayStr2(int[] obj, Integer lev) {
        String str = null;
        for (int i : obj) {
            if (null != str) {
                str += "," + (i + lev);
            } else {
                str = (i + lev) + "";
            }
        }

        return str;
    }

    public String getIntArrayStr3(String[] obj, Integer lev) {
        String str = null;
        for (String i : obj) {
            if (null != str) {
                str += "," + (Integer.parseInt(i) + lev);
            } else {
                str = (Integer.parseInt(i) + lev) + "";
            }
        }
        return str;
    }


    public List<ColumnMgrEO> getSiteMap(String lev, Long siteId, Long columnId, String columnIds, Boolean link, Boolean isCom) {
        Long indicatorId = null;
        if (isCom) {//公共栏目
            indicatorId = columnId;
        }
        indicatorId = siteId;
        StringBuffer sb1 = getQuerySql(true, true, siteId);
        StringBuffer sb2 = getQuerySql(false, true, siteId);
        // Integer levels=getLev(siteId,indicatorId);
        String[] levArr = getStrings(lev, ",");
        String str = getIntArrayStr3(levArr, 0);
        StringBuffer sb = new StringBuffer("select (temp.levels+2) as lev, r.indicator_id as indicatorId,r.name as name,")
                .append("r.parent_id as parentId, r.url_path as urlPath, r.sort_num as sortNum,r.is_parent as isParent,r.create_date as createDate ,")
                .append("r.site_id as siteId,r.type as type,r.record_status as recordStatus,r.is_show as isShow,r.column_type_code as columnTypeCode,r.is_start_url as isStartUrl, r.trans_url as transUrl ")
                .append(" from cms_column_mgr r inner join (").append(sb1).append(") temp on r.indicator_id=temp.indicator_id");
        if (isCom) {
            sb.append(" where temp.paths like CONCAT((select likeStr from (").append(sb2).append(") likeTemp where likeTemp.indicator_id = " + columnId + "),'%')");
        } else {
            sb.append(" and r.site_id=" + siteId);
        }
        sb.append(" and r.is_show=1 and (temp.levels+2) in(" + str + ") ");
        if (!isCom) {
            sb.append(" and r.type='" + IndicatorEO.Type.CMS_Section.toString() + "' ");
        }
        if (link != null && link) {
            sb.append(" and r.column_type_code <> '" + BaseContentEO.TypeCode.linksMgr.toString() + "'");
        } else {
            sb.append(" and r.column_type_code not in ('" + BaseContentEO.TypeCode.linksMgr.toString() + "','redirect')");
        }
        if (!StringUtils.isEmpty(columnIds)) {
            String[] columnArr = AppUtil.getStrings(columnIds, ",");
            for (String cStr : columnArr) {
                sb.append(" and locate('," + cStr + "',temp.paths)=0");
            }
            sb.append(" and r.indicator_id not in (" + columnIds + ")");
        }
        sb.append(" order by r.sort_num");
        List<String> fields = new ArrayList<String>();
        fields.add("lev");
        fields.add("indicatorId");
        fields.add("name");
        fields.add("parentId");
        fields.add("sortNum");
        fields.add("isParent");
        fields.add("urlPath");
        fields.add("createDate");
        fields.add("siteId");
        fields.add("type");
        fields.add("recordStatus");
        fields.add("isShow");
        fields.add("columnTypeCode");
        fields.add("isStartUrl");
        fields.add("transUrl");
        String[] strArr = new String[fields.size()];
        List<ColumnMgrEO> list = (List<ColumnMgrEO>) getBeansBySql(sb.toString(), new Object[]{}, ColumnMgrEO.class, fields.toArray(strArr));

        return AppUtil.isEmpty(list) ? new ArrayList<ColumnMgrEO>() : list;
    }


}
