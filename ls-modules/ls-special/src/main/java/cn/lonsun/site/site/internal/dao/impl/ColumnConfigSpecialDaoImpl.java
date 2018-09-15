package cn.lonsun.site.site.internal.dao.impl;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.site.site.internal.dao.IColumnConfigSpecialDao;
import cn.lonsun.site.site.internal.entity.ColumnConfigEO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrator on 2017/9/26.
 */
@Repository("columnConfigSpecialDao")
public class ColumnConfigSpecialDaoImpl extends MockDao<ColumnConfigEO> implements IColumnConfigSpecialDao {
    final String whereStr = " where recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "' and  isEnable=1 ";

    /**
     * 获取序号
     *
     * @param parentId
     * @return
     */
    @Override
    public Integer getNewSortNum(Long parentId, boolean isCom) {
        //判断当前的最大的排序号
        StringBuilder sb = new StringBuilder("select max(sortNum) from cn.lonsun.indicator.internal.entity.IndicatorEO ");
        sb.append(whereStr);
        sb.append(" and parentId=").append(parentId);
        if (isCom) {
            sb.append(" and type='").append(IndicatorEO.Type.COM_Section.toString()).append("' ");
        } else {
            sb.append(" and type='").append(IndicatorEO.Type.CMS_Section.toString()).append("' ");
        }

        Query query = this.getCurrentSession().createQuery(sb.toString());
        Integer maxSortNum = (Integer) query.uniqueResult();
        //判断当前的排序号是否为空，若为空，则返回2，不为空则返回当前最大排序号加2
        return maxSortNum == null ? 2 : (maxSortNum + 2);
    }

    @Override
    public List<ColumnMgrEO> getColumnByContentModelCode(Long siteId, String code) {
        String hql = " select r.indicatorId as indicatorId,r.name as name,r.parentId as parentId,r.sortNum as sortNum,r.isParent as isParent,r.createDate as createDate "
                + ",r.siteId as siteId,r.type as type,c.columnConfigId as columnConfigId"
                + ",c.contentModelCode as contentModelCode,c.columnTypeCode as columnTypeCode"
                + " from IndicatorEO r,ColumnConfigEO c "
                + " where r.indicatorId=c.indicatorId and c.contentModelCode='" + code + "'"
                + " and r.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "' and c.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "'";
        if (null == siteId) {
            hql += " and r.type='" + IndicatorEO.Type.COM_Section.toString() + "'";
        } else {
            hql += " and r.siteId=" + siteId;
        }
        List<ColumnMgrEO> list = (List<ColumnMgrEO>) getBeansByHql(hql, new Object[]{}, ColumnMgrEO.class);
        return list;
    }

    @Override
    public List<ColumnMgrEO> getAllColumnBySite(Long columnId) {
        StringBuffer sql = new StringBuffer();
        sql.append("select r.indicator_id as indicatorId,r.parent_id parentId,r.name name,r.type type,r.sort_num sortNum,r.is_parent isParent,c.column_type_code  columnTypeCode")
                .append(",c.content_model_code as contentModelCode,r.site_id siteId ,r.indicator_id||'_'||r.site_id  columnStrId,r.parent_id||'_'||r.site_id parentStrId");
        sql.append(" from rbac_indicator r,cms_column_config c where r.indicator_id =c.indicator_id  and r.record_status = ? start with r.indicator_id = ? connect by prior r.indicator_id = r.parent_id")
                .append(" order by r.sort_num asc,r.create_date asc");
        String[] fileds = new String[]{"indicatorId", "parentId", "name", "type", "sortNum", "isParent", "columnTypeCode", "contentModelCode", "siteId", "columnStrId", "parentStrId"};
        Object[] values = new Object[]{AMockEntity.RecordStatus.Normal.toString(), columnId};
        return (List<ColumnMgrEO>) getBeansBySql(sql.toString(), values, ColumnMgrEO.class, fileds);

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
