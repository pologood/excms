package cn.lonsun.site.site.internal.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SqlUtil;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.site.site.internal.dao.IColumnConfigDao;
import cn.lonsun.site.site.internal.entity.ColumnConfigEO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.vo.ColumnVO;
import cn.lonsun.util.LoginPersonUtil;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 栏目配置Dao实现类<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-8-24 <br/>
 */
@Repository("columnConfigDao")
public class ColumnConfigDaoImpl extends MockDao<ColumnConfigEO> implements IColumnConfigDao {
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
    public ColumnConfigEO getColumnConfigByIndicatorId(Long indicatorId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("indicatorId", indicatorId);
        List<ColumnConfigEO> list = getEntities(ColumnConfigEO.class, map);
        if (list == null || list.size() == 0) {
            return null;
        }
        ColumnConfigEO eo = list.get(0);
        return eo;
    }

    /**
     * 获取栏目树的结构（异步加载）
     * 1、给栏目管理提供栏目树
     *
     * @param
     * @return
     */
    @Override
    public List<ColumnMgrEO> getColumnTreeByType(Long[] siteIds, Long columnId, String columnTypeCode, Boolean flag) {
        String hql = " select r.indicatorId as indicatorId,r.name as name,r.parentId as parentId,r.sortNum as sortNum,r.isParent as isParent,r.createDate as createDate "
            + ",r.siteId as siteId,r.type as type,c.columnConfigId as columnConfigId"
            + ",c.contentModelCode as contentModelCode,c.columnTypeCode as columnTypeCode,r.indicatorId||'_'||r.siteId as columnStrId,r.parentId||'_'||r.siteId as parentStrId"
            + " from IndicatorEO r,ColumnConfigEO c where r.indicatorId=c.indicatorId and r.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "'"
            + " and c.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "' and r.type='" + IndicatorEO.Type.CMS_Section.toString() + "'";
        String siteStr = "";
        if (siteIds != null && siteIds.length > 0) {
            if (siteIds.length == 1) {
                siteStr += siteIds[0];
            } else {
                for (int i = 0; i < siteIds.length - 1; i++) {
                    siteStr += siteIds[i] + ",";
                }
                siteStr += siteIds[siteIds.length - 1];
            }
            hql += " and r.siteId in (" + siteStr + ")";
        }
        if (columnId != null && flag) {
            hql += " and r.indicatorId<>" + columnId;
        }
        // hql+=" and c.columnTypeCode in ('articleNews','pictureNews','videoNews')" ;
        if (!StringUtils.isEmpty(columnTypeCode)) {
            hql += " and ( c.columnTypeCode in('" + columnTypeCode + "') or r.isParent=1) ";
        }
        //hql += " and c.contentModelCode is not null ";
        hql += " and r.siteId is not null";
        hql += " order by r.sortNum asc ,r.createDate asc ";
        List<ColumnMgrEO> list = (List<ColumnMgrEO>) getBeansByHql(hql, new Object[]{}, ColumnMgrEO.class);
        return list;
    }


    /**
     * 供权限管理查看树
     *
     * @param siteId
     * @return
     */
    public List<ColumnVO> getTree(Long siteId) {
        String hql = " select r.indicatorId as indicatorId,r.name as name,r.parentId as parentId,r.sortNum as sortNum,r.isParent as isParent,r.createDate as createDate "
            + ",r.siteId as siteId,r.open as open,r.type as type,c.columnConfigId as columnConfigId,c.synColumnIds as synColumnIds "
            + ",c.referColumnIds as referColumnIds,c.referColumnNames as referColumnNames,c.referOrganCatIds as referOrganCatIds,c.referOrganCatNames as referOrganCatNames "
            + ",c.contentModelCode as contentModelCode,c.genePageIds as genePageIds"
            + ",c.genePageNames as genePageNames,c.isStartUrl as isStartUrl,c.transUrl as transUrl,c.transWindow as transWindow "
            + " from IndicatorEO  r , ColumnConfigEO  c "
            + " where 1=1 and r.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "' and c.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "'";
        if (siteId != null) {
            hql += " and r.siteId=" + siteId + " and r.type='CMS_Section' and r.indicatorId=c.indicatorId ";
        } else {
            hql += " and r.type in('CMS_Site','CMS_Section') and r.indicatorId=c.indicatorId ";
        }
        hql += " order by r.sortNum asc ,r.createDate asc";
        List<ColumnVO> list = (List<ColumnVO>) getBeansByHql(hql, new Object[]{}, ColumnVO.class);
        return list;
    }

    /**
     * 权限管理：获取所有的站点和栏目
     *
     * @return
     */
    @Override
    public List<ColumnMgrEO> getTree() {
        String hql = " select r.indicatorId as indicatorId,r.name as name,r.parentId as parentId,r.sortNum as sortNum,r.isParent as isParent,r.createDate as createDate "
            + ",r.siteId as siteId,r.open as open,r.type as type,c.columnConfigId as columnConfigId,c.synColumnIds as synColumnIds ,c.synColumnNames as synColumnNames"
            + ",c.referColumnIds as referColumnIds,c.referColumnNames as referColumnNames,c.referOrganCatIds as referOrganCatIds,c.referOrganCatNames as referOrganCatNames "
            + ",c.contentModelCode as contentModelCode,c.genePageIds as genePageIds"
            + ",c.genePageNames as genePageNames,c.isStartUrl as isStartUrl,c.transUrl as transUrl,c.transWindow as transWindow "
            + " from IndicatorEO  r , ColumnConfigEO  c "
            + " where 1=1 and r.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "' and c.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "' and r.type = '" + IndicatorEO.Type.CMS_Section.toString() + "'"
            + " and r.indicatorId=c.indicatorId";
        hql += " order by r.sortNum asc ,r.createDate asc";
        List<ColumnMgrEO> list = (List<ColumnMgrEO>) getBeansByHql(hql, new Object[]{}, ColumnMgrEO.class);
        String hql2 = " select r.indicatorId as indicatorId,r.name as name,r.parentId as parentId,r.sortNum as sortNum,r.isParent as isParent,r.createDate as createDate"
            + " ,r.siteId as siteId,r.open as open,r.type as type from IndicatorEO  r where r.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "' and r.type='" + IndicatorEO.Type.CMS_Site.toString() + "'";
        List<ColumnMgrEO> list2 = (List<ColumnMgrEO>) getBeansByHql(hql2, new Object[]{}, ColumnMgrEO.class);
        if (list != null && list.size() > 0) {
            list.addAll(list2);
            return list;
        } else {
            return list2;
        }
    }

    /**
     * 根据主表Id数组获取站点和栏目
     * 1、为角色管理提供接口
     *
     * @param
     * @return
     */
    @Override
    public List<ColumnMgrEO> getColumnByIds(String idStr) {
        String hql = " select r.indicatorId as indicatorId,r.name as name,r.parentId as parentId, r.urlPath as urlPath,r.sortNum as sortNum,r.isParent as isParent,r.createDate as createDate "
            + ",r.siteId as siteId,r.type as type,r.columnConfigId as columnConfigId,r.synColumnIds as synColumnIds ,r.synColumnNames as synColumnNames"
            + ",r.referColumnIds as referColumnIds,r.referColumnNames as referColumnNames,r.referOrganCatIds as referOrganCatIds,r.referOrganCatNames as referOrganCatNames "
            + ",r.contentModelCode as contentModelCode,r.genePageIds as genePageIds,r.columnTypeCode as columnTypeCode"
            + ",r.genePageNames as genePageNames,r.isStartUrl as isStartUrl,r.transUrl as transUrl,r.transWindow as transWindow "
            + " from ColumnMgrEO r where r.indicatorId in(" + idStr + ") ";
        List<ColumnMgrEO> list = (List<ColumnMgrEO>) getBeansByHql(hql, new Object[]{}, ColumnMgrEO.class);
        return list;
    }

    /**
     * 根据站点ID查询站点下的栏目树（异步加载）
     * 1、为内容协同提供接口
     *
     * @param indicatorId
     * @return
     */
    @Override
    public List<ColumnVO> getColumnTreeBySite(Long indicatorId) {
    /*    String hql=" select r.indicatorId as indicatorId,r.name as name,r.parentId as parentId,r.sortNum as sortNum,r.isParent as isParent,r.createDate as createDate "
                +",r.siteId as siteId,r.open as open,r.type as type,c.columnConfigId as columnConfigId,c.synColumnIds as synColumnIds ,c.synColumnNames as synColumnNames"
                +",c.contentModelCode as contentModelCode,c.genePageIds as genePageIds"
                +",c.genePageNames as genePageNames,c.isStartUrl as isStartUrl,c.transUrl as transUrl,c.transWindow as transWindow "
                +" from IndicatorEO  r , ColumnConfigEO  c "
                +" where 1=1 and r.recordStatus='"+AMockEntity.RecordStatus.Normal.toString()+"' and c.recordStatus='"+ AMockEntity.RecordStatus.Normal.toString()+"'";
        hql+=" and r.type='"+IndicatorEO.Type.CMS_Section.toString()+"' and r.parentId="+indicatorId+"  and r.indicatorId=c.indicatorId ";
        hql+= " order by r.sortNum asc ,r.createDate asc";*/
        String hql = " select r.indicatorId as indicatorId,r.name as name,r.parentId as parentId, r.urlPath as urlPath,r.sortNum as sortNum,r.isParent as isParent,r.createDate as createDate "
            + ",r.siteId as siteId,r.type as type,r.columnConfigId as columnConfigId,r.synColumnIds as synColumnIds ,r.synColumnNames as synColumnNames"
            + ",r.contentModelCode as contentModelCode,r.genePageIds as genePageIds,r.columnTypeCode as columnTypeCode"
            + ",r.referColumnIds as referColumnIds,r.referColumnNames as referColumnNames,r.referOrganCatIds as referOrganCatIds,r.referOrganCatNames as referOrganCatNames "
            + ",r.genePageNames as genePageNames,r.isStartUrl as isStartUrl,r.transUrl as transUrl,r.transWindow as transWindow "
            + " from ColumnMgrEO r ";
        hql += "where r.parentId=" + indicatorId + " and r.type='" + IndicatorEO.Type.CMS_Section.toString() + "'";
        hql += " order by r.sortNum asc ,r.createDate asc";
        List<ColumnVO> list = (List<ColumnVO>) getBeansByHql(hql, new Object[]{}, ColumnVO.class);
        return list;
    }

    /**
     * 得到一个站点下的所有栏目
     *
     * @param name
     * @param siteId
     * @return
     */
    @Override
    public List<ColumnMgrEO> getAllColumnTree(Long siteId, String name) {
        String hql = " select r.indicatorId as indicatorId,r.name as name,r.parentId as parentId,r.sortNum as sortNum,r.isParent as isParent, r.urlPath as urlPath,r.createDate as createDate "
            + ",r.siteId as siteId,r.type as type,r.columnConfigId as columnConfigId,r.synColumnIds as synColumnIds ,r.synColumnNames as synColumnNames,r.isShow as isShow"
            + ",r.referColumnIds as referColumnIds,r.referColumnNames as referColumnNames,r.referOrganCatIds as referOrganCatIds,r.referOrganCatNames as referOrganCatNames "
            + ",r.contentModelCode as contentModelCode,r.genePageIds as genePageIds,r.columnTypeCode as columnTypeCode"
            + ",r.genePageNames as genePageNames,r.isStartUrl as isStartUrl,r.transUrl as transUrl,r.transWindow as transWindow "
            + " from ColumnMgrEO r where r.siteId=" + siteId + " and r.type='" + IndicatorEO.Type.CMS_Section.toString() + "'";
        if (name != null && !("".equals(name.trim()))) {
            hql += " and r.name like '%" + SqlUtil.prepareParam4Query(name) + "%'";
        }
        hql += " order by r.sortNum asc ,r.createDate asc";
        List<ColumnMgrEO> list = (List<ColumnMgrEO>) getBeansByHql(hql, new Object[]{}, ColumnMgrEO.class);
        return list;
    }

    /**
     * 得到一个站点下的所有栏目和该站点
     * 1、为栏目管理的"生成页面"提供栏目树
     *
     * @param name
     * @param siteId
     * @return
     */
    @Override
    public List<ColumnMgrEO> searchColumnTree(Long siteId, String name) {
        String hql = " select r.indicatorId as indicatorId,r.name as name,r.parentId as parentId, r.urlPath as urlPath,r.sortNum as sortNum,r.isParent as isParent,r.createDate as createDate "
            + ",r.siteId as siteId,r.type as type,r.columnConfigId as columnConfigId,r.synColumnIds as synColumnIds ,r.synColumnNames as synColumnNames"
            + ",r.referColumnIds as referColumnIds,r.referColumnNames as referColumnNames,r.referOrganCatIds as referOrganCatIds,r.referOrganCatNames as referOrganCatNames "
            + ",r.contentModelCode as contentModelCode,r.genePageIds as genePageIds,r.columnTypeCode as columnTypeCode"
            + ",r.genePageNames as genePageNames,r.isStartUrl as isStartUrl,r.transUrl as transUrl,r.transWindow as transWindow "
            + " from ColumnMgrEO r where r.siteId=" + siteId + " and r.type='" + IndicatorEO.Type.CMS_Section.toString() + "'";
        if (name != null && !("".equals(name.trim()))) {
            hql += " and r.name like '%" + SqlUtil.prepareParam4Query(name) + "%'";
        }
        hql += " order by r.sortNum asc ,r.createDate asc";
        List<ColumnMgrEO> list = (List<ColumnMgrEO>) getBeansByHql(hql, new Object[]{}, ColumnMgrEO.class);
        return list;
    }

    public ColumnMgrEO getSiteEO(Long indicatorId) {
        String hql = " select r.indicatorId as indicatorId,r.name as name,r.parentId as parentId,r.sortNum as sortNum"
            + ",r.type as type ,r.isParent as isParent,r.createDate as createDate ,r.siteId as siteId,r.open as open"
            + " from IndicatorEO r where r.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "' and r.indicatorId=" + indicatorId;
        List<ColumnMgrEO> list = (List<ColumnMgrEO>) getBeansByHql(hql, new Object[]{}, ColumnMgrEO.class);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public List<ColumnMgrEO> getSiteByIds(String idStr) {
        String hql = " select r.indicatorId as indicatorId,r.name as name,r.parentId as parentId,r.sortNum as sortNum"
            + ",r.type as type ,r.isParent as isParent,r.createDate as createDate ,r.siteId as siteId"
            + " from IndicatorEO r where r.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() +
            "' and r.indicatorId in(" + idStr + ") and r.type=?";
        List<ColumnMgrEO> list = (List<ColumnMgrEO>) getBeansByHql(hql, new Object[]{IndicatorEO.Type.CMS_Site.toString()}, ColumnMgrEO.class);
        return list;
    }


    @Override
    public List<ColumnMgrEO> getColumnByTypeCode(Long siteId, String code) {
        String hql = " select r.indicatorId as indicatorId,r.name as name,r.parentId as parentId, r.urlPath as urlPath,r.sortNum as sortNum,r.isParent as isParent,r.createDate as createDate "
            + ",r.siteId as siteId,r.type as type,r.columnConfigId as columnConfigId,r.synColumnIds as synColumnIds ,r.synColumnNames as synColumnNames"
            + ",r.referColumnIds as referColumnIds,r.referColumnNames as referColumnNames,r.referOrganCatIds as referOrganCatIds,r.referOrganCatNames as referOrganCatNames "
            + ",r.contentModelCode as contentModelCode,r.genePageIds as genePageIds,r.columnTypeCode as columnTypeCode"
            + ",r.genePageNames as genePageNames,r.isStartUrl as isStartUrl,r.transUrl as transUrl,r.transWindow as transWindow "
            + " from ColumnMgrEO r "
            + " where 1=1 and r.siteId=" + siteId + " and r.columnTypeCode='" + code + "'";
        List<ColumnMgrEO> list = (List<ColumnMgrEO>) getBeansByHql(hql, new Object[]{}, ColumnMgrEO.class);
        return list;
    }

    @Override
    public List<ColumnMgrEO> getColumnByModelCodes(Long indicatorId, String codes) {
        String str = codes.substring(0, codes.length() - 1);
        String hql = " select r.indicatorId as indicatorId,r.name as name,r.parentId as parentId, r.urlPath as urlPath,r.sortNum as sortNum,r.isParent as isParent,r.createDate as createDate "
            + ",r.siteId as siteId,r.type as type,r.columnConfigId as columnConfigId,r.synColumnIds as synColumnIds ,r.synColumnNames as synColumnNames"
            + ",r.referColumnIds as referColumnIds,r.referColumnNames as referColumnNames,r.referOrganCatIds as referOrganCatIds,r.referOrganCatNames as referOrganCatNames "
            + ",r.contentModelCode as contentModelCode,r.genePageIds as genePageIds,r.columnTypeCode as columnTypeCode"
            + ",r.genePageNames as genePageNames,r.isStartUrl as isStartUrl,r.transUrl as transUrl,r.transWindow as transWindow "
            + " from ColumnMgrEO r "
            + " where 1=1 and r.parentId=" + indicatorId + " and r.contentModelCode in(" + str + ") ";

        List<ColumnMgrEO> list = (List<ColumnMgrEO>) getBeansByHql(hql, new Object[]{}, ColumnMgrEO.class);
        return list;
    }

    @Override
    public List<ColumnMgrEO> getLevelColumnTree(Long indicatorId, int[] level, String condition, PageQueryVO vo) {
        StringBuffer sql = new StringBuffer("select * from (")
            .append("select level as lev, is_show as isShow,indicator_Id as indicatorId,name as name,parent_id as parentId,url_path as urlPath,sort_num as sortNum,is_parent as isParent,create_date as createDate ")
            .append(",site_id as siteId,type as type,column_config_id as columnConfigId,syn_column_ids as synColumnIds ,syn_column_names as synColumnNames")
            .append(",refer_Column_Ids as referColumnIds,refer_Column_Names as referColumnNames,refer_Organ_Cat_Ids as referOrganCatIds,refer_Organ_Cat_Names as referOrganCatNames ")
            .append(",content_model_code as contentModelCode,gene_page_ids as genePageIds,column_type_code as columnTypeCode")
            .append(",gene_page_names as genePageNames,is_start_url as isStartUrl,trans_url as transUrl,trans_window as transWindow ")
            .append(" from cms_column_mgr connect by prior indicator_id = parent_id start with  indicator_id = " + indicatorId + "");
        if (!AppUtil.isEmpty(condition)) {
            sql.append(" where 1=1 and " + condition);
        }
        sql.append("  order by sort_num asc ");
        String str = getIntArrayStr(level);
        sql.append(" ) where lev in (" + str + ")");
        sql.append(" and type='" + IndicatorEO.Type.CMS_Section.toString() + "'");
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

    @Override
    public List<ColumnMgrEO> getParentColumns(Long indicatorId) {
        StringBuffer sql = new StringBuffer("select * from (")
            .append("select level as lev, is_show as isShow,indicator_Id as indicatorId,name as name,parent_id as parentId, url_path as urlPath,sort_num as sortNum,is_parent as isParent,create_date as createDate ")
            .append(",site_id as siteId,type as type,column_config_id as columnConfigId,syn_column_ids as synColumnIds ,syn_column_names as synColumnNames")
            .append(",refer_Column_Ids as referColumnIds,refer_Column_Names as referColumnNames,refer_Organ_Cat_Ids as referOrganCatIds,refer_Organ_Cat_Names as referOrganCatNames ")
            .append(",content_model_code as contentModelCode,gene_page_ids as genePageIds,column_type_code as columnTypeCode")
            .append(",gene_page_names as genePageNames,is_start_url as isStartUrl,trans_url as transUrl,trans_window as transWindow ")
            .append(" from cms_column_mgr   connect by prior parent_id = indicator_id start with  indicator_id = " + indicatorId + ")");
        sql.append(" where 1=1 ");
        sql.append(" and type='" + IndicatorEO.Type.CMS_Section.toString() + "'");
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
        List<ColumnMgrEO> list = (List<ColumnMgrEO>) getBeansBySql(sql.toString(), new Object[]{}, ColumnMgrEO.class, fields.toArray(strArr));
        return AppUtil.isEmpty(list) ? new ArrayList<ColumnMgrEO>() : list;
    }

    @Override
    public Map<Long, Boolean> getParentIndicatorIds(Long indicatorId) {
        Map<Long, Boolean> map = new HashMap<Long, Boolean>();
        String sql = "select indicator_Id from cms_column_mgr connect by prior  parent_id = indicator_id  start with  indicator_id = " + indicatorId;
        List list = this.getCurrentSession().createSQLQuery(sql).list();

        for (int i = 0; i < list.size(); i++) {
            map.put(Long.parseLong(list.get(i).toString()), true);
        }

        return map;
    }

    @Override
    public List<ColumnMgrEO> getChildColumn(Long indicatorId, int[] parentFlag) {
        StringBuffer sql = new StringBuffer("select * from (")
            .append(" select  indicator_Id as indicatorId,name as name,parent_id as parentId, url_path as urlPath, sort_num as sortNum,is_parent as isParent,create_date as createDate ")
            .append(",site_id as siteId,type as type,column_config_id as columnConfigId,syn_column_ids as synColumnIds ,syn_column_names as synColumnNames")
            .append(",refer_Column_Ids as referColumnIds,refer_Column_Names as referColumnNames,refer_Organ_Cat_Ids as referOrganCatIds,refer_Organ_Cat_Names as referOrganCatNames ")
            .append(",content_model_code as contentModelCode,gene_page_ids as genePageIds,column_type_code as columnTypeCode")
            .append(",gene_page_names as genePageNames,is_start_url as isStartUrl,trans_url as transUrl,trans_window as transWindow ")
            .append(" from cms_column_mgr   connect by prior indicator_id = parent_id start with  indicator_id = " + indicatorId + ")");

        String str = getIntArrayStr(parentFlag);
        sql.append(" where isParent in (" + str + ")");
        sql.append(" and type='" + IndicatorEO.Type.CMS_Section.toString() + "'");
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

        List<ColumnMgrEO> list = (List<ColumnMgrEO>) getBeansBySql(sql.toString(), new Object[]{}, ColumnMgrEO.class, fields.toArray(strArr));

        return AppUtil.isEmpty(list) ? new ArrayList<ColumnMgrEO>() : list;
    }

    @Override
    public List<ColumnVO> getComColumnTree(Long indicatorId) {
        String hql = " select r.indicatorId as indicatorId,r.name as name,r.parentId as parentId,r.sortNum as sortNum,r.isParent as isParent,r.createDate as createDate "
            + ",r.siteId as siteId,r.type as type,c.columnTypeCode as columnTypeCode,c.contentModelCode as contentModelCode "
            + " from IndicatorEO  r, ColumnConfigEO c"
            + " where r.indicatorId=c.indicatorId and r.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "'"
            + " and r.type='" + IndicatorEO.Type.COM_Section.toString() + "' and r.parentId=" + indicatorId;
        hql += " order by r.sortNum asc ,r.createDate asc";
        List<ColumnVO> list = (List<ColumnVO>) getBeansByHql(hql, new Object[]{}, ColumnVO.class);
        return list;
    }

    @Override
    public List<ColumnMgrEO> getLevComColumn(Long siteId, int[] i) {
        StringBuffer sql = new StringBuffer("select * from (")
            .append("select level as lev, indicator_Id as indicatorId,name as name,parent_id as parentId, url_path as urlPath,sort_num as sortNum,is_parent as isParent,create_date as createDate ")
            .append(",site_id as siteId,type as type,column_config_id as columnConfigId")
            .append(",content_model_code as contentModelCode,column_type_code as columnTypeCode")
            .append(" from cms_column_mgr   connect by prior indicator_id = parent_id start with  parent_id = " + siteId + ")");
        String str = getIntArrayStr(i);
        sql.append(" where lev in (" + str + ")");

        sql.append(" and type='" + IndicatorEO.Type.COM_Section.toString() + "'");
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

        List<ColumnMgrEO> list = (List<ColumnMgrEO>) getBeansBySql(sql.toString(), new Object[]{}, ColumnMgrEO.class, fields.toArray(strArr));

        return AppUtil.isEmpty(list) ? new ArrayList<ColumnMgrEO>() : list;
    }

    @Override
    public List<ColumnMgrEO> getVirtualColumn(Long indicatorId, boolean isShow, String[] contentModelCode) {
        StringBuffer str = new StringBuffer(" select r.indicatorId as indicatorId,r.name as name,r.parentId as parentId, r.urlPath as urlPath,r.sortNum as sortNum,r.siteId as siteId,")
            .append("r.isParent as isParent,r.createDate as createDate,r.type as type,r.contentModelCode as contentModelCode,r.columnTypeCode as columnTypeCode,")
            .append("r.isStartUrl as isStartUrl,r.transWindow as transWindow,r.transUrl as transUrl,r.genePageIds as genePageIds,r.genePageNames as genePageNames,")
            .append("r.synColumnIds as synColumnIds,r.synColumnNames as synColumnNames,r.keyWords as keyWords,r.description as description,r.content as content,")
            .append("r.referColumnIds as referColumnIds,r.referColumnNames as referColumnNames,r.referOrganCatIds as referOrganCatIds,r.referOrganCatNames as referOrganCatNames ")
            .append(" from ColumnMgrEO r where r.parentId=" + indicatorId);
        if (isShow) {
            str.append(" and r.columnTypeCode='" + contentModelCode[0].toString() + "'");
        } else {
            if (contentModelCode != null && contentModelCode.length > 0) {
                for (int i = 0; i < contentModelCode.length; i++) {
                    str.append(" and r.columnTypeCode<>'" + contentModelCode[i] + "'");
                }
            }
            str.append("  and r.type='" + IndicatorEO.Type.CMS_Section.toString() + "'");
        }
        str.append(" order by r.sortNum asc ,r.createDate asc");

        List<ColumnMgrEO> list = (List<ColumnMgrEO>) getBeansByHql(str.toString(), new Object[]{}, ColumnMgrEO.class);
        return list;
    }

    @Override
    public List<ColumnMgrEO> getColumnByParentId(Long indicatorId, boolean flag, Long siteId) {
        StringBuffer str = new StringBuffer(" select r.indicatorId as indicatorId,r.name as name,r.parentId as parentId, r.urlPath as urlPath,r.sortNum as sortNum,")
            .append("r.isParent as isParent,r.createDate as createDate,r.type as type,r.contentModelCode as contentModelCode,r.columnTypeCode as columnTypeCode,")
            .append("r.isStartUrl as isStartUrl,r.transWindow as transWindow,r.transUrl as transUrl,r.genePageIds as genePageIds,r.genePageNames as genePageNames,")
            .append("r.synColumnIds as synColumnIds,r.synColumnNames as synColumnNames,r.keyWords as keyWords,r.description as description,r.content as content,")
            .append("r.referColumnIds as referColumnIds,r.referColumnNames as referColumnNames,r.referOrganCatIds as referOrganCatIds,r.referOrganCatNames as referOrganCatNames ")
            .append(" from ColumnMgrEO r where r.parentId=" + indicatorId);
        if (siteId == null) {
            siteId = LoginPersonUtil.getSiteId();
        }
        if (flag) {
            str.append(" and (r.siteId is null or r.siteId=" + siteId + ")");
        } else {
            str.append(" and r.siteId=" + siteId);
        }

        str.append(" order by r.sortNum asc,r.createDate asc");
        List<ColumnMgrEO> list = (List<ColumnMgrEO>) getBeansByHql(str.toString(), new Object[]{}, ColumnMgrEO.class);
        return list;

    }

    @Override
    public List<ColumnMgrEO> getColumnBySiteIds(String idStr) {
        String hql = " select r.indicatorId as indicatorId,r.name as name,r.parentId as parentId, r.urlPath as urlPath,r.sortNum as sortNum,r.isParent as isParent,r.createDate as createDate "
            + ",r.siteId as siteId,r.type as type,r.columnConfigId as columnConfigId,r.synColumnIds as synColumnIds ,r.synColumnNames as synColumnNames"
            + ",r.referColumnIds as referColumnIds,r.referColumnNames as referColumnNames,r.referOrganCatIds as referOrganCatIds,r.referOrganCatNames as referOrganCatNames "
            + ",r.contentModelCode as contentModelCode,r.genePageIds as genePageIds,r.columnTypeCode as columnTypeCode"
            + ",r.genePageNames as genePageNames,r.isStartUrl as isStartUrl,r.transUrl as transUrl,r.transWindow as transWindow "
            + " from ColumnMgrEO r where r.siteId in(" + idStr + ") and r.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "'"
            + " order by r.sortNum asc,r.createDate asc";
        List<ColumnMgrEO> list = (List<ColumnMgrEO>) getBeansByHql(hql, new Object[]{}, ColumnMgrEO.class);
        return list;
    }

    @Override
    public List<ColumnMgrEO> getAllColumnBySite(Long columnId) {
        StringBuffer sql = new StringBuffer();
        sql.append("select r.indicator_id as indicatorId,r.parent_id parentId, " )
            .append("(case when (r.parent_names_pinyin is not null) then concat(concat(r.parent_names_pinyin,'/'),r.name_pinyin) else r.name_pinyin end) AS urlPath,")
            .append(" r.name name,r.type type,r.sort_num sortNum,r.is_parent isParent,c.column_type_code  columnTypeCode")
            .append(",c.content_model_code as contentModelCode,r.site_id siteId ,r.indicator_id||'_'||r.site_id  columnStrId,r.parent_id||'_'||r.site_id parentStrId");
        sql.append(" from rbac_indicator r,cms_column_config c where r.indicator_id =c.indicator_id  and r.record_status = ? start with r.indicator_id = ? connect by prior r.indicator_id = r.parent_id")
            .append(" order by r.sort_num asc,r.create_date asc");
        String[] fileds = new String[]{"indicatorId", "parentId", "name", "type", "sortNum", "isParent", "columnTypeCode", "contentModelCode", "siteId", "columnStrId", "parentStrId"};
        Object[] values = new Object[]{AMockEntity.RecordStatus.Normal.toString(), columnId};
        return (List<ColumnMgrEO>) getBeansBySql(sql.toString(), values, ColumnMgrEO.class, fileds);
    }

    @Override
    public List<ColumnMgrEO> getColumnByContentModelCode(Long siteId, String code) {
        String hql = " select r.indicatorId as indicatorId,r.name as name,r.parentId as parentId, r.urlPath as urlPath,r.sortNum as sortNum,r.isParent as isParent,r.createDate as createDate "
            + ",r.siteId as siteId,r.type as type,c.columnConfigId as columnConfigId"
            + ",c.contentModelCode as contentModelCode,c.columnTypeCode as columnTypeCode"
            + " from ColumnMgrEO r,ColumnConfigEO c "
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
    public List<ColumnMgrEO> getMoveTree(Long indicatorId, Long columnId) {
//        Long siteId=LoginPersonUtil.getSiteId();
//        String hql=" select r.indicator_id as indicatorId,r.name as name,r.parent_id as parentId,r.sort_num as sortNum,r.is_parent as isParent "
//                +",r.create_date as createDate,r.site_id as siteId,r.type as type,r.column_type_code as columnTypeCode"
//                +" from cms_column_mgr r ";
//        if(columnId!=null){
//            hql+=" where r.indicator_id<>"+columnId;
//        }
//        hql+=" connect by prior r.indicator_id = r.parent_id start with  r.parent_id="+siteId;
//        hql+=" order by r.sort_num asc,r.create_date asc";
//        List<String> fields = new ArrayList<String>();
//        fields.add("indicatorId");
//        fields.add("name");
//        fields.add("parentId");
//        fields.add("sortNum");
//        fields.add("isParent");
//        fields.add("createDate");
//        fields.add("siteId");
//        fields.add("type");
//        fields.add("columnTypeCode");
//
//        String[] strArr = new String[fields.size()];
//        List<ColumnMgrEO> list= (List<ColumnMgrEO>) getBeansBySql(hql.toString(), new Object[]{}, ColumnMgrEO.class,fields.toArray(strArr));
        String hql = " select r.indicatorId as indicatorId,r.name as name,r.parentId as parentId, r.urlPath as urlPath,r.sortNum as sortNum,r.isParent as isParent "
            + ",r.createDate as createDate,r.siteId as siteId,r.type as type,r.columnTypeCode as columnTypeCode"
            + " from ColumnMgrEO r where r.parentId=" + indicatorId;
        if (columnId != null) {
            hql += " and r.indicatorId<>" + columnId;
        }
        List<ColumnMgrEO> list = (List<ColumnMgrEO>) getBeansByHql(hql, new Object[]{}, ColumnMgrEO.class);
        return list;
    }

    @Override
    public Boolean getIsHaveColumn(Long siteId) {
        String hql = " select r.indicatorId as indicatorId,r.name as name,r.parentId as parentId,r.sortNum as sortNum,r.isParent as isParent "
            + ",r.createDate as createDate,r.siteId as siteId,r.type as type,r.columnTypeCode as columnTypeCode"
            + " from ColumnMgrEO r where r.siteId=" + siteId + " and r.type='" + IndicatorEO.Type.CMS_Section.toString() + "'";
        List<ColumnMgrEO> list = (List<ColumnMgrEO>) getBeansByHql(hql, new Object[]{}, ColumnMgrEO.class);
        if (list == null || list.size() == 0) {
            return true;
        }
        return false;
    }

    @Override
    public List<ColumnMgrEO> getByColumnTypeCodes(String[] codes, Long siteId, Boolean flag) {
        String codeStr = "";
        if (codes != null && codes.length > 0) {
            for (int i = 0; i < codes.length; i++) {
                codeStr += "'" + codes[i] + "',";
            }
            codeStr = codeStr.substring(0, codeStr.length() - 1);
        }

        String hql = " select r.indicatorId as indicatorId,r.name as name,r.parentId as parentId, r.urlPath as urlPath,r.sortNum as sortNum,r.isParent as isParent "
            + ",r.createDate as createDate,r.siteId as siteId,r.type as type,r.columnTypeCode as columnTypeCode from ColumnMgrEO r where "
            + "r.type='" + IndicatorEO.Type.CMS_Section.toString() + "'";
        if (!StringUtils.isEmpty(codeStr)) {
            if (flag != null && flag) {
                hql += " and r.columnTypeCode in (" + codeStr + ")";
            } else {
                hql += " and( r.isParent=1 or r.columnTypeCode in (" + codeStr + "))";
            }
        }
        if (siteId != null) {
            hql += " and r.siteId=" + siteId;
        }
        hql += " order by r.sortNum asc,r.createDate asc";
        List<ColumnMgrEO> list = (List<ColumnMgrEO>) getBeansByHql(hql, new Object[]{}, ColumnMgrEO.class);
        return list;
    }

    @Override
    public List<ColumnMgrEO> getColumns(List<String> codes, Long siteId) {
        Map<String,Object> params = new HashMap<String, Object>();
        StringBuffer sql = new StringBuffer("")
                .append("select indicator_Id as indicatorId,name as name,parent_id as parentId, url_path as urlPath,sort_num as sortNum,is_parent as isParent,create_date as createDate ")
                .append(",site_id as siteId,type as type")
                .append(",content_model_code as contentModelCode,column_type_code as columnTypeCode")
                .append(" from cms_column_mgr where 1=1");
        if(!AppUtil.isEmpty(codes)){
            sql.append(" and column_type_code in(:codes)");
            params.put("codes",codes);
        }
        if(siteId!=null){
            sql.append(" and site_id = :siteId");
            params.put("siteId",siteId);
        }
        sql.append("  order by sort_num asc");

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
        List<ColumnMgrEO>  list = (List<ColumnMgrEO>) getBeansBySql(sql.toString(), params, ColumnMgrEO.class, fields.toArray(strArr));
        return list;
    }

    @Override
    public List<ColumnMgrEO> getSiteMap(String lev, Long siteId, Long columnId, String columnIds, Boolean link,Boolean isCom) {

        Long indicatorId=null;
        if(isCom){//公共栏目
            indicatorId=columnId;
        }
        indicatorId=siteId;
        StringBuffer sql = new StringBuffer("select * from (")
                .append("select level as lev, r.indicator_Id as indicatorId,r.name as name,r.parent_id as parentId, ")
                .append(" (case when r.parent_names_pinyin is not null then concat(concat(r.parent_names_pinyin, '/'), r.name_pinyin) else r.name_pinyin end) urlPath, ")
                .append(" r.sort_num as sortNum,r.is_parent as isParent,r.create_date as createDate , ")
                .append("r.site_id as siteId,r.type as type,r.record_status as recordStatus,c.is_show as isShow,c.column_type_code as columnTypeCode,c.is_start_url as isStartUrl, c.trans_url as transUrl ")
                .append(" from rbac_indicator  r left join cms_column_config c on r.indicator_id=c.indicator_id connect by prior r.indicator_id = r.parent_id start with  r.indicator_id = " + indicatorId + ") ");
        sql.append(" where lev in (" + lev + ") and isShow=1 and recordStatus='"+AMockEntity.RecordStatus.Normal.toString()+"' ");
        if(!isCom){
            sql.append(" and type='" + IndicatorEO.Type.CMS_Section.toString()+"'");
        }

        if (link != null && link) {
            sql.append(" and columnTypeCode <> '" + BaseContentEO.TypeCode.linksMgr.toString() + "'");
        } else {
            sql.append(" and columnTypeCode not in ('" + BaseContentEO.TypeCode.linksMgr.toString() + "','redirect')");
        }
        if (!StringUtils.isEmpty(columnIds)) {
            sql.append(" and indicatorId not in (" + columnIds + ")");
        }
        sql.append(" order by lev asc, sortNum asc,createDate asc");
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
        fields.add("recordStatus");
        fields.add("isShow");
        fields.add("columnTypeCode");
        fields.add("isStartUrl");
        fields.add("transUrl");
        String[] strArr = new String[fields.size()];
        List<ColumnMgrEO> list = (List<ColumnMgrEO>)getBeansBySql(sql.toString(), new Object[]{}, ColumnMgrEO.class, fields.toArray(strArr));
        return list;
    }

    @Override
    public ColumnMgrEO getById(Long indicatorId) {
        StringBuffer hql = new StringBuffer(" select r.indicatorId as indicatorId,r.name as name,r.parentId as parentId, r.urlPath as urlPath,r.sortNum as sortNum,")
                .append("r.isParent as isParent,r.createDate as createDate,r.type as type,r.contentModelCode as contentModelCode,r.columnTypeCode as columnTypeCode,")
                .append("r.isStartUrl as isStartUrl,r.transWindow as transWindow,r.transUrl as transUrl,r.genePageIds as genePageIds,r.genePageNames as genePageNames,")
                .append("r.synColumnIds as synColumnIds,r.synColumnNames as synColumnNames,r.keyWords as keyWords,r.description as description,r.content as content,")
                .append("r.referColumnIds as referColumnIds,r.referColumnNames as referColumnNames,r.referOrganCatIds as referOrganCatIds,r.referOrganCatNames as referOrganCatNames, ")
                .append("r.updateCycle as updateCycle,r.yellowCardWarning as yellowCardWarning,r.redCardWarning as redCardWarning")
                .append(" from ColumnMgrEO r where r.indicatorId=" + indicatorId);
        List<ColumnMgrEO> list = (List<ColumnMgrEO>) getBeansByHql(hql.toString(), new Object[]{}, ColumnMgrEO.class);
        if(list!=null&&list.size()>0){
            return list.get(0);
        }
        return null;
    }

    @Override
    public Long getSourceColumnCount(String referColumnId){
        String hql = "select r.indicatorId from ColumnMgrEO r where r.referColumnIds like ? ";
        return getCount(hql,new Object[]{"%".concat(referColumnId).concat("%")});
    }


    /**
     * 查询引用目录的源栏目
     * @param referOrganCatId
     * @return
     */
    @Override
    public Long getSourceOrganCatCount(String referOrganCatId){
        String hql = "select r.indicatorId from ColumnMgrEO r where r.referOrganCatIds like ? ";
        return getCount(hql,new Object[]{"%".concat(referOrganCatId).concat("%")});
    }


    public String getIntArrayStr(int[] obj) {
        String str = null;
        for (int i : obj) {
            if (null != str) {
                str += "," + i;
            } else {
                str = i + "";
            }
        }

        return str;
    }



}
