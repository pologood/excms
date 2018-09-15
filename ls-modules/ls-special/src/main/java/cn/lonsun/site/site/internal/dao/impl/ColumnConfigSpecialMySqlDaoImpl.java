package cn.lonsun.site.site.internal.dao.impl;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.site.site.internal.dao.IColumnConfigSpecialDao;
import cn.lonsun.site.site.internal.entity.ColumnConfigEO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrator on 2017/9/30.
 */
@Repository("columnConfigSpecialMySqlDao")
public class ColumnConfigSpecialMySqlDaoImpl  extends ColumnConfigSpecialDaoImpl {

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
                .append(" select r.indicator_id as indicatorId,r.parent_id parentId, url_path as urlPath,r.name name,r.type type,r.sort_num sortNum,r.is_parent isParent,r.column_type_code  columnTypeCode")
                .append(",r.content_model_code as contentModelCode,r.site_id siteId ,r.indicator_id||'_'||r.site_id  columnStrId,r.parent_id||'_'||r.site_id parentStrId")
                .append(" from cms_column_mgr r inner join (").append(sb1).append(") temp on r.indicator_id=temp.indicator_Id where r.record_status = ?");
        sb.append(" and temp.paths like CONCAT((select likeStr from (").append(sb2).append(") likeTemp where likeTemp.indicator_id = ?),'%')")
                .append("order by r.sort_num asc,r.create_date asc");

        String[] fileds = new String[]{"indicatorId", "parentId", "name", "type", "sortNum", "isParent", "columnTypeCode", "contentModelCode", "siteId", "columnStrId", "parentStrId"};
        Object[] values = new Object[]{AMockEntity.RecordStatus.Normal.toString(), columnId};
        return (List<ColumnMgrEO>) getBeansBySql(sb.toString(), values, ColumnMgrEO.class, fileds);
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

}
