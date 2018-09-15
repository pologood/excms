package cn.lonsun.msg.submit.hn.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.msg.submit.hn.dao.IMsgSubmitHnDao;
import cn.lonsun.msg.submit.hn.entity.CmsMsgSubmitHnEO;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.site.template.util.SqlHelper;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gu.fei
 * @version 2015-11-18 13:46
 */
@Repository
public class MsgSubmitHnDao extends MockDao<CmsMsgSubmitHnEO> implements IMsgSubmitHnDao {

    @Override
    public Pagination getPageList(ParamDto dto) {
        Long pageIndex = dto.getPageIndex();
        Integer pageSize = dto.getPageSize();
        StringBuffer hql = new StringBuffer(" from CmsMsgSubmitHnEO where 1=1");
        Map<String, Object> param = new HashMap<String, Object>();
        hql.append(" and createOrganId = :createOrganId");
        param.put("createOrganId", LoginPersonUtil.getOrganId());
        hql.append(" and createUserId = :createUserId");
        param.put("createUserId", LoginPersonUtil.getUserId());
        hql.append(" and recordStatus = :recordStatus");
        param.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        if (null != dto.getStatus()) {
            hql.append(" and status = :status");
            param.put("status", dto.getStatus());
        }
        if (!StringUtils.isEmpty(dto.getStartDate())) {
            hql.append(" and publishDate >= :startDate");
            param.put("startDate", dto.getStartDate());
        }
        if (!StringUtils.isEmpty(dto.getEndDate())) {
            hql.append(" and publishDate <= :endDate");
            param.put("endDate", dto.getEndDate());
        }
        return this.getPagination(pageIndex, pageSize, SqlHelper.getSearchAndOrderSql(hql.toString(), dto), param);
    }

    @Override
    public Pagination getToMePageList(ParamDto dto) {
        Long pageIndex = dto.getPageIndex();
        Integer pageSize = dto.getPageSize();
        List<String> fields = new ArrayList<String>();
        StringBuffer sql = new StringBuffer("select");
        sql.append(" a.id as id");
        fields.add("id");
        sql.append(",a.title as title");
        fields.add("title");
        sql.append(",a.provider as provider");
        fields.add("provider");
        sql.append(",a.submit_unit_name as submitUnitName");
        fields.add("submitUnitName");
        sql.append(",a.author as author");
        fields.add("author");
        sql.append(",a.sources as sources");
        fields.add("sources");
        sql.append(",a.publish_date as publishDate");
        fields.add("publishDate");
        sql.append(",a.image_link as imageLink");
        fields.add("imageLink");
        sql.append(",a.content as content");
        fields.add("content");
        sql.append(",a.use_count as useCount");
        fields.add("useCount");
        sql.append(",a.status as status");
        fields.add("status");
        sql.append(",a.back_reason as backReason");
        fields.add("backReason");
        sql.append(",a.site_id as siteId");
        fields.add("siteId");
        sql.append(",b.msg_read_status as msgReadStatus");
        fields.add("msgReadStatus");
        sql.append(" from cms_msg_submit_hn a ,cms_msg_to_user_hn b where a.id = b.msg_id");
        sql.append(" and b.organ_id = :organId and b.user_id = :userId");
        Map<String,Object> param = new HashMap<String, Object>();
        param.put("organId", LoginPersonUtil.getOrganId());
        param.put("userId", LoginPersonUtil.getUserId());

        //审阅状态过滤条件
       if(!AppUtil.isEmpty(dto.getStatus())) {
            sql.append(" and b.msg_read_status = :msgReadStatus");
            param.put("msgReadStatus",dto.getStatus());
        }

        //开始时间
        if(!AppUtil.isEmpty(dto.getStartDate())) {
            sql.append(" and a.publish_date >=:startDate");
            param.put("startDate",dto.getStartDate());
        }

        //结束时间
        if(!AppUtil.isEmpty(dto.getEndDate())) {
            sql.append(" and a.publish_date <=:endDate");
            param.put("endDate",dto.getEndDate());
        }

        if(!AppUtil.isEmpty(dto.getTitle())) {
            sql.append(" and title like :title");
            param.put("title","%" + dto.getTitle() + "%");
        }

        sql.append(" order by a.create_date desc");
        String[] arr = new String[fields.size()];
        Pagination page = this.getPaginationBySql(pageIndex,pageSize, sql.toString(),param,CmsMsgSubmitHnEO.class,fields.toArray(arr));
        return page;
    }

    @Override
    public Pagination getTobePageList(ParamDto dto) {
        Long pageIndex = dto.getPageIndex();
        Integer pageSize = dto.getPageSize();
        List<String> fields = new ArrayList<String>();
        StringBuffer sql = new StringBuffer("select");
        sql.append(" a.id as id");
        fields.add("id");
        sql.append(",a.title as title");
        fields.add("title");
        sql.append(",a.provider as provider");
        fields.add("provider");
        sql.append(",a.submit_unit_name as submitUnitName");
        fields.add("submitUnitName");
        sql.append(",a.author as author");
        fields.add("author");
        sql.append(",a.sources as sources");
        fields.add("sources");
        sql.append(",a.publish_date as publishDate");
        fields.add("publishDate");
        sql.append(",a.image_link as imageLink");
        fields.add("imageLink");
        sql.append(",a.content as content");
        fields.add("content");
        sql.append(",a.use_count as useCount");
        fields.add("useCount");
        sql.append(",a.status as status");
        fields.add("status");
        sql.append(",a.back_reason as backReason");
        fields.add("backReason");
        sql.append(",a.site_id as siteId");
        fields.add("siteId");
        sql.append(" from cms_msg_submit_hn a");
        sql.append(" where 1=1");
        Map<String,Object> param = new HashMap<String, Object>();

        if(!AppUtil.isEmpty(dto.getStatus())) {
            sql.append(" and a.status = :status");
            param.put("status",dto.getStatus());
        }

        //开始时间
        if(!AppUtil.isEmpty(dto.getStartDate())) {
            sql.append(" and a.publish_date >=:startDate");
            param.put("startDate",dto.getStartDate());
        }

        //结束时间
        if(!AppUtil.isEmpty(dto.getEndDate())) {
            sql.append(" and a.publish_date <=:endDate");
            param.put("endDate",dto.getEndDate());
        }

        if(!AppUtil.isEmpty(dto.getTitle())) {
            sql.append(" and a.title like :title ");
            param.put("title","%" + dto.getTitle() + "%");
        }

        sql.append(" and a.id in (select msg_id from cms_msg_to_column_hn b where b.status = 0");

        //针对普通用户和栏目管理员增加栏目操作权限过滤功能
        if(!LoginPersonUtil.isRoot() && !LoginPersonUtil.isSuperAdmin()) {
            if(LoginPersonUtil.isSiteAdmin()) {
                if(null != dto.getSiteIds() && !dto.getSiteIds().isEmpty()) {
                    sql.append(" and b.site_id in (:siteIds)");
                    param.put("siteIds",dto.getSiteIds());
                } else {
                    sql.append(" and 1=2");
                }
            } else {
                sql.append(" and (");
                if(null != dto.getColumns() && !dto.getColumns().isEmpty()) {
                    sql.append("b.column_id in(");
                    sql.append(":columnIds");
                    param.put("columnIds",dto.getColumns());
                    sql.append(")");
                } else {
                    sql.append(" 1=2");
                }

                if(null != dto.getCodes() && !dto.getCodes().isEmpty()) {
                    sql.append(" or b.code in(");
                    sql.append(":codes");
                    param.put("codes",dto.getCodes());
                    sql.append(")");
                } else {
                    sql.append(" or 1=2");
                }
                sql.append(")");
            }
        }
        sql.append(")");
        sql.append(" order by a.create_date desc");
        String[] arr = new String[fields.size()];
        Pagination page = this.getPaginationBySql(pageIndex,pageSize, sql.toString(),param,CmsMsgSubmitHnEO.class,fields.toArray(arr));
        return page;
    }

    @Override
    public Pagination getBePageList(ParamDto dto) {
        Long pageIndex = dto.getPageIndex();
        Integer pageSize = dto.getPageSize();
        List<String> fields = new ArrayList<String>();
        StringBuffer sql = new StringBuffer("select");
        sql.append(" a.id as id");
        fields.add("id");
        sql.append(",a.title as title");
        fields.add("title");
        sql.append(",a.provider as provider");
        fields.add("provider");
        sql.append(",a.submit_unit_name as submitUnitName");
        fields.add("submitUnitName");
        sql.append(",a.author as author");
        fields.add("author");
        sql.append(",a.sources as sources");
        fields.add("sources");
        sql.append(",a.publish_date as publishDate");
        fields.add("publishDate");
        sql.append(",a.image_link as imageLink");
        fields.add("imageLink");
        sql.append(",a.content as content");
        fields.add("content");
        sql.append(",a.use_count as useCount");
        fields.add("useCount");
        sql.append(",a.status as status");
        fields.add("status");
        sql.append(",a.back_reason as backReason");
        fields.add("backReason");
        sql.append(",a.site_id as siteId");
        fields.add("siteId");
        sql.append(" from cms_msg_submit_hn a");

        sql.append(" where 1=1");
        Map<String,Object> param = new HashMap<String, Object>();
//        sql.append(" and siteId = :siteId");
//        param.put("siteId",LoginPersonUtil.getSiteId());

        //开始时间
        if(!AppUtil.isEmpty(dto.getStartDate())) {
            sql.append(" and a.publish_date >=:startDate");
            param.put("startDate",dto.getStartDate());
        }

        //结束时间
        if(!AppUtil.isEmpty(dto.getEndDate())) {
            sql.append(" and a.publish_date <=:endDate");
            param.put("endDate",dto.getEndDate());
        }

        if(!AppUtil.isEmpty(dto.getTitle())) {
            sql.append(" and a.title like :title ");
            param.put("title","%" + dto.getTitle() + "%");
        }

        sql.append(" and a.id in (select msg_id from cms_msg_to_column_hn b where b.status = 1");

        //针对普通用户和栏目管理员增加栏目操作权限过滤功能
        if(!LoginPersonUtil.isRoot() && !LoginPersonUtil.isSuperAdmin()) {
            if(LoginPersonUtil.isSiteAdmin()) {
                if(null != dto.getSiteIds() && !dto.getSiteIds().isEmpty()) {
                    sql.append(" and b.site_id in (:siteIds)");
                    param.put("siteIds",dto.getSiteIds());
                } else {
                    sql.append(" and 1=2");
                }
            } else {
                sql.append(" and (");
                if(null != dto.getColumns() && !dto.getColumns().isEmpty()) {
                    sql.append("b.column_id in(");
                    sql.append(":columnIds");
                    param.put("columnIds",dto.getColumns());
                    sql.append(")");
                } else {
                    sql.append(" 1=2");
                }

                if(null != dto.getCodes() && !dto.getCodes().isEmpty()) {
                    sql.append(" or b.code in(");
                    sql.append(":codes");
                    param.put("codes",dto.getCodes());
                    sql.append(")");
                } else {
                    sql.append(" or 1=2");
                }
                sql.append(")");
            }
        }

        //针对普通用户增加栏目操作权限过滤功能
        if(!LoginPersonUtil.isRoot() && !LoginPersonUtil.isSuperAdmin()) {

        }
        sql.append(")");
        sql.append(" order by a.create_date desc");
        String[] arr = new String[fields.size()];
        Pagination page = this.getPaginationBySql(pageIndex,pageSize, sql.toString(),param,CmsMsgSubmitHnEO.class,fields.toArray(arr));
        return page;
    }

    @Override
    public Long getToMeCount() {
        StringBuffer sql = new StringBuffer("select");
        sql.append(" a.id");
        sql.append(" from cms_msg_submit_hn a ,cms_msg_to_user_hn b where a.id = b.msg_id");
        sql.append(" and b.organ_id = :organId and b.user_id = :userId");
        Map<String,Object> param = new HashMap<String, Object>();
        param.put("organId", LoginPersonUtil.getOrganId());
        param.put("userId", LoginPersonUtil.getUserId());
        sql.append(" and b.msg_read_status = :msgReadStatus");
        param.put("msgReadStatus",0);
        sql.append(" order by a.create_date desc");
        return this.getCountBySql(sql.toString(),param);
    }

    @Override
    public Long getToBeCount(ParamDto dto) {
        StringBuffer sql = new StringBuffer("select");
        sql.append(" a.id as id");
        sql.append(" from cms_msg_submit_hn a");
        sql.append(" where 1=1");
        Map<String,Object> param = new HashMap<String, Object>();
        sql.append(" and a.id in (select msg_id from cms_msg_to_column_hn b where b.status = 0");

        //针对普通用户和栏目管理员增加栏目操作权限过滤功能
        if(!LoginPersonUtil.isRoot() && !LoginPersonUtil.isSuperAdmin()) {
            if(LoginPersonUtil.isSiteAdmin()) {
                if(null != dto.getSiteIds() && !dto.getSiteIds().isEmpty()) {
                    sql.append(" and b.site_id in (:siteIds)");
                    param.put("siteIds",dto.getSiteIds());
                } else {
                    sql.append(" and 1=2");
                }
            } else {
                sql.append(" and (");
                if(null != dto.getColumns() && !dto.getColumns().isEmpty()) {
                    sql.append("b.column_id in(");
                    sql.append(":columnIds");
                    param.put("columnIds",dto.getColumns());
                    sql.append(")");
                } else {
                    sql.append(" 1=2");
                }

                if(null != dto.getCodes() && !dto.getCodes().isEmpty()) {
                    sql.append(" or b.code in(");
                    sql.append(":codes");
                    param.put("codes",dto.getCodes());
                    sql.append(")");
                } else {
                    sql.append(" or 1=2");
                }
                sql.append(")");
            }
        }
        sql.append(")");
        sql.append(" order by a.create_date desc");
        return this.getCountBySql(sql.toString(),param);
    }

    @Override
    public Long getBeCount(ParamDto dto) {
        StringBuffer sql = new StringBuffer("select");
        sql.append(" a.id as id");
        sql.append(" from cms_msg_submit_hn a");
        sql.append(" where 1=1");
        Map<String,Object> param = new HashMap<String, Object>();
        sql.append(" and a.id in (select msg_id from cms_msg_to_column_hn b where b.status = 1");

        //针对普通用户和栏目管理员增加栏目操作权限过滤功能
        if(!LoginPersonUtil.isRoot() && !LoginPersonUtil.isSuperAdmin()) {
            if(LoginPersonUtil.isSiteAdmin()) {
                if(null != dto.getSiteIds() && !dto.getSiteIds().isEmpty()) {
                    sql.append(" and b.site_id in (:siteIds)");
                    param.put("siteIds",dto.getSiteIds());
                } else {
                    sql.append(" and 1=2");
                }
            } else {
                sql.append(" and (");
                if(null != dto.getColumns() && !dto.getColumns().isEmpty()) {
                    sql.append("b.column_id in(");
                    sql.append(":columnIds");
                    param.put("columnIds",dto.getColumns());
                    sql.append(")");
                } else {
                    sql.append(" 1=2");
                }

                if(null != dto.getCodes() && !dto.getCodes().isEmpty()) {
                    sql.append(" or b.code in(");
                    sql.append(":codes");
                    param.put("codes",dto.getCodes());
                    sql.append(")");
                } else {
                    sql.append(" or 1=2");
                }
                sql.append(")");
            }
        }
        sql.append(")");
        sql.append(" order by a.create_date desc");
        return this.getCountBySql(sql.toString(),param);
    }
}
