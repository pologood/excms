package cn.lonsun.net.service.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.net.service.dao.IWorkGuideDao;
import cn.lonsun.net.service.entity.CmsWorkGuideEO;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.site.template.util.SqlHelper;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

/**
 * @author gu.fei
 * @version 2015-11-18 13:46
 */
@Repository
public class WorkGuideDao extends BaseDao<CmsWorkGuideEO> implements IWorkGuideDao {

    @Override
    public List<CmsWorkGuideEO> getEOs() {

        StringBuffer hql = new StringBuffer(" select t.id as id,t.contentId as contentId,t.name as name, t.linkType as linkType,");
        hql.append(" t.linkUrl as linkUrl,t.organId as organId,t.joinDate as joinDate,t.turnLink as turnLink,b.isPublish as publish,t.content as content, ");
        hql.append(" t.zxLink as zxLink ,t.tsLink as tsLink,t.sbLink as sbLink,t.setAccord as setAccord,t.applyCondition as applyCondition,t.handleData as handleData,b.columnId as columnId,");
        hql.append("t.handleProcess as handleProcess,t.handleAddress as handleAddress,t.handleDate as handleDate,t.phone as phone,t.siteId as siteId,b.publishDate as publishDate,t.createDate as createDate");
        hql.append(" from CmsWorkGuideEO t,BaseContentEO b where b.isPublish=1 and t.contentId = b.id and b.recordStatus='" + AMockEntity.RecordStatus.Normal + "'");

        return (List<CmsWorkGuideEO>)  this.getBeansByHql(hql.toString(), new Object[]{},CmsWorkGuideEO.class);
    }

    @Override
    public CmsWorkGuideEO getByContentId(Long contentId) {
        return this.getEntityByHql("from CmsWorkGuideEO where contentId = ?" ,new Object[] {contentId});
    }

    @Override
    public List<CmsWorkGuideEO> getEOsByCIds(ParamDto dto, List<Long> cIds) {
        String ids = getPIds(cIds);
        StringBuffer hql = new StringBuffer(" select t.id as id,t.contentId as contentId,t.name as name, t.linkType as linkType,");
        hql.append(" t.linkUrl as linkUrl,t.organId as organId,t.joinDate as joinDate,t.turnLink as turnLink,b.isPublish as publish,t.content as content, ");
        hql.append(" t.zxLink as zxLink ,t.tsLink as tsLink,t.sbLink as sbLink,t.minLocalTime as minLocalTime,t.setAccord as setAccord,t.applyCondition as applyCondition,t.handleData as handleData,");
        hql.append("t.handleProcess as handleProcess,t.handleAddress as handleAddress,t.handleDate as handleDate,t.phone as phone,t.siteId as siteId ");
        hql.append(" from CmsWorkGuideEO t,BaseContentEO b where t.contentId = b.id and b.recordStatus='" + AMockEntity.RecordStatus.Normal + "'");

        Long siteId = LoginPersonUtil.getSiteId();
        if(null == siteId) {
            siteId = dto.getSiteId();
        }
        hql.append(" and t.siteId = " + siteId);

        if(dto.getOrganId() != null) {
            hql.append(" and t.organId = ");
            hql.append(dto.getOrganId());
        }

        if(ids != null) {
            Long[] idsl = StringUtils.getArrayWithLong(ids,",");
            int n = 500;
            int count = idsl.length/n;
            hql.append(" and (");
            for(int i = 0;i < count ;i++) {
                if(i == 0) {
                    hql.append(" t.id in(");
                } else {
                    hql.append(" or t.id in(");
                }
                Long[] newarr = Arrays.copyOfRange(idsl,i*n,(i+1)*n);
                for(Long id : newarr) {
                    hql.append(id + ",");
                }

                hql.append("-1)");
            }

            if(null != idsl && idsl.length > 0) {
                Long[] lastarr = Arrays.copyOfRange(idsl, count * n, idsl.length);
                if(count > 0) {
                    hql.append(" or t.id in(");
                } else {
                    hql.append("t.id in(");
                }
                for(Long id : lastarr) {
                    hql.append(id + ",");
                }

                hql.append("-1)");
            }

            hql.append(")");
        }

        return (List<CmsWorkGuideEO>) this.getBeansByHql(SqlHelper.getSearchAndOrderSqlByAS(hql.toString(), dto), new Object[]{}, CmsWorkGuideEO.class);
    }

    @Override
    public Pagination getPageEOsByCIds(ParamDto dto, List<Long> cIds) {
        String ids = getPIds(cIds);
        StringBuffer hql = new StringBuffer(" select t.id as id,t.contentId as contentId,t.name as name, t.linkType as linkType,");
        hql.append(" t.linkUrl as linkUrl,t.organId as organId,t.joinDate as joinDate,t.turnLink as turnLink,b.isPublish as publish,t.content as content, ");
        hql.append(" t.zxLink as zxLink ,t.tsLink as tsLink,t.sbLink as sbLink,t.setAccord as setAccord,t.applyCondition as applyCondition,t.handleData as handleData,");
        hql.append("t.handleProcess as handleProcess,t.handleAddress as handleAddress,t.handleDate as handleDate,t.phone as phone,t.siteId as siteId,b.publishDate as publishDate");
        hql.append(" from CmsWorkGuideEO t,BaseContentEO b where b.isPublish=1 and t.contentId = b.id and b.recordStatus='" + AMockEntity.RecordStatus.Normal + "'");

        Long siteId = dto.getSiteId();
        hql.append(" and t.siteId = " + siteId);

        if(dto.getOrganId() != null) {
            hql.append(" and t.organId = ");
            hql.append(dto.getOrganId());
        }

        if(ids != null) {
            Long[] idsl = StringUtils.getArrayWithLong(ids,",");
            int n = 500;
            int count = idsl.length/n;
            hql.append(" and (");
            for(int i = 0;i < count ;i++) {
                if(i == 0) {
                    hql.append(" t.id in(");
                } else {
                    hql.append(" or t.id in(");
                }
                Long[] newarr = Arrays.copyOfRange(idsl,i*n,(i+1)*n);
                for(Long id : newarr) {
                    hql.append(id + ",");
                }

                hql.append("-1)");
            }

            if(null != idsl && idsl.length > 0) {
                Long[] lastarr = Arrays.copyOfRange(idsl, count * n, idsl.length);
                if(count > 0) {
                    hql.append(" or t.id in(");
                } else {
                    hql.append("t.id in(");
                }
                for(Long id : lastarr) {
                    hql.append(id + ",");
                }

                hql.append("-1)");
            }

            hql.append(")");
        }
        Long pageIndex = dto.getPageIndex();
        Integer pageSize = dto.getPageSize();
        return this.getPagination(pageIndex, pageSize, SqlHelper.getSearchAndOrderSqlByAS(hql.toString(), dto), new Object[]{}, CmsWorkGuideEO.class);
    }

    @Override
    public Pagination getPageEOs(ParamDto dto) {
        Long pageIndex = dto.getPageIndex();
        Integer pageSize = dto.getPageSize();
        String ids = getPIds(dto.getClassifyId());
        StringBuffer hql = new StringBuffer(" select t.id as id,t.contentId as contentId,t.name as name, t.linkType as linkType,t.handleLimit as handleLimit,t.feeStandard as feeStandard,");
        hql.append(" t.linkUrl as linkUrl,t.organId as organId,t.joinDate as joinDate,t.turnLink as turnLink,b.isPublish as publish,t.content as content, ");
        hql.append(" t.zxLink as zxLink ,t.tsLink as tsLink,t.sbLink as sbLink,t.minLocalTime as minLocalTime,t.setAccord as setAccord,t.applyCondition as applyCondition,t.handleData as handleData,");
        hql.append("t.handleProcess as handleProcess,t.handleAddress as handleAddress,t.handleDate as handleDate,t.phone as phone,t.siteId as siteId ");
        hql.append(" from CmsWorkGuideEO t,BaseContentEO b where t.contentId = b.id and b.recordStatus='" + AMockEntity.RecordStatus.Normal + "'");

        Long siteId = dto.getSiteId();
        if(null == siteId) {
            siteId = LoginPersonUtil.getSiteId();
        }
        hql.append(" and t.siteId = " + siteId);

        if(dto.getOrganId() != null) {
            hql.append(" and t.organId = ");
            hql.append(dto.getOrganId());
        }

        if(ids != null) {
            Long[] idsl = StringUtils.getArrayWithLong(ids,",");
            int n = 500;
            int count = idsl.length/n;
            hql.append(" and (");
            for(int i = 0;i < count ;i++) {
                if(i == 0) {
                    hql.append(" t.id in(");
                } else {
                    hql.append(" or t.id in(");
                }
                Long[] newarr = Arrays.copyOfRange(idsl,i*n,(i+1)*n);
                for(Long id : newarr) {
                    hql.append(id + ",");
                }

                hql.append("-1)");
            }

            if(null != idsl && idsl.length > 0) {
                Long[] lastarr = Arrays.copyOfRange(idsl, count * n, idsl.length);
                if(count > 0) {
                    hql.append(" or t.id in(");
                } else {
                    hql.append("t.id in(");
                }
                for(Long id : lastarr) {
                    hql.append(id + ",");
                }

                hql.append("-1)");
            }

            hql.append(")");
        }

        if(dto.getByOrgan()) {
            hql.append(" and t.createOrganId = " + LoginPersonUtil.getOrganId());
        }

        return this.getPagination(pageIndex, pageSize, SqlHelper.getSearchAndOrderSqlByAS(hql.toString(), dto), new Object[]{}, CmsWorkGuideEO.class);
    }

    @Override
    public Pagination getPageEOs(ParamDto dto,List<Long> cIds,String condition) {
        Long pageIndex = dto.getPageIndex();
        Integer pageSize = dto.getPageSize();

        String ids = getPIds(cIds);
        StringBuffer hql = new StringBuffer(" select t.id as id,t.contentId as contentId,t.name as name, t.linkType as linkType,");
        hql.append(" t.linkUrl as linkUrl,t.organId as organId,t.joinDate as joinDate,t.turnLink as turnLink,b.isPublish as publish,t.content as content, ");
        hql.append(" t.zxLink as zxLink ,t.tsLink as tsLink,t.sbLink as sbLink,t.minLocalTime as minLocalTime,t.setAccord as setAccord,t.applyCondition as applyCondition,b.publishDate as createDate,");
        hql.append("t.handleProcess as handleProcess,t.handleAddress as handleAddress,t.handleDate as handleDate,t.phone as phone,t.siteId as siteId ");
        hql.append(" from CmsWorkGuideEO t,BaseContentEO b where t.contentId = b.id and b.isPublish=1 and b.recordStatus='" + AMockEntity.RecordStatus.Normal + "'");

        Long siteId = dto.getSiteId();
        hql.append(" and t.siteId = " + siteId);

        if(dto.getOrganId() != null) {
            hql.append(" and t.organId = ");
            hql.append(dto.getOrganId());
        }

        if(ids != null) {
            Long[] idsl = StringUtils.getArrayWithLong(ids,",");
            int n = 500;
            int count = idsl.length/n;
            hql.append(" and (");
            for(int i = 0;i < count ;i++) {
                if(i == 0) {
                    hql.append(" t.id in(");
                } else {
                    hql.append(" or t.id in(");
                }
                Long[] newarr = Arrays.copyOfRange(idsl,i*n,(i+1)*n);
                for(Long id : newarr) {
                    hql.append(id + ",");
                }

                hql.append("-1)");
            }

            if(null != idsl && idsl.length > 0) {
                Long[] lastarr = Arrays.copyOfRange(idsl, count * n, idsl.length);
                if(count > 0) {
                    hql.append(" or t.id in(");
                } else {
                    hql.append("t.id in(");
                }
                for(Long id : lastarr) {
                    hql.append(id + ",");
                }

                hql.append("-1)");
            }

            hql.append(")");
        }

        if(!AppUtil.isEmpty(condition)) {
        }

        return this.getPagination(pageIndex, pageSize, SqlHelper.getSearchAndOrderSqlByAS(hql.toString(), dto), new Object[]{}, CmsWorkGuideEO.class);
    }

    @Override
    public Pagination getPageEOs(ParamDto dto, Long organId,String name, String condition,String typeCode) {
        Long pageIndex = dto.getPageIndex();
        Integer pageSize = dto.getPageSize();

        StringBuffer hql = new StringBuffer(" select t.id as id,t.contentId as contentId,t.name as name, t.linkType as linkType,b.publishDate as publishDate,");
        hql.append(" t.linkUrl as linkUrl,t.organId as organId,t.joinDate as joinDate,t.turnLink as turnLink,b.isPublish as publish,t.content as content, ");
        hql.append(" t.zxLink as zxLink ,t.tsLink as tsLink,t.sbLink as sbLink,t.minLocalTime as minLocalTime,t.setAccord as setAccord,t.applyCondition as applyCondition,t.handleData as handleData,");
        hql.append("t.handleProcess as handleProcess,t.handleAddress as handleAddress,t.handleDate as handleDate,t.createDate as createDate,t.phone as phone,t.siteId as siteId ");
        hql.append(" from CmsWorkGuideEO t,BaseContentEO b where t.contentId = b.id and b.isPublish=1 and b.recordStatus='" + AMockEntity.RecordStatus.Normal + "'");

        Long siteId = dto.getSiteId();
        if(null != siteId) {
            hql.append(" and t.siteId = " + siteId);
        }

        if(organId != null) {
            hql.append(" and t.organId = ");
            hql.append(organId);
        }

        if(name != null) {
            hql.append(" and t.name like '%");
            hql.append(name);
            hql.append("%' escape '\\'");
        }

        if(!AppUtil.isEmpty(typeCode)) {
            hql.append(" and t.typeCode = '");
            hql.append(typeCode);
            hql.append("'");
        }

        return this.getPagination(pageIndex, pageSize, SqlHelper.getSearchAndOrderSqlByAS(hql.toString(), dto), new Object[]{}, CmsWorkGuideEO.class);
    }

    @Override
    public Pagination getPageSEOs(ParamDto dto, String organIds, String name, String condition, String typeCode) {
        Long pageIndex = dto.getPageIndex();
        Integer pageSize = dto.getPageSize();

        StringBuffer hql = new StringBuffer(" select t.id as id,t.contentId as contentId,t.name as name, t.linkType as linkType,b.publishDate as publishDate,");
        hql.append(" t.linkUrl as linkUrl,t.organId as organId,t.joinDate as joinDate,t.turnLink as turnLink,b.isPublish as publish,t.content as content, ");
        hql.append(" t.zxLink as zxLink ,t.tsLink as tsLink,t.sbLink as sbLink,t.minLocalTime as minLocalTime,t.setAccord as setAccord,t.applyCondition as applyCondition,t.handleData as handleData,");
        hql.append("t.handleProcess as handleProcess,t.handleAddress as handleAddress,t.handleDate as handleDate,t.phone as phone,t.siteId as siteId ");
        hql.append(" from CmsWorkGuideEO t,BaseContentEO b where t.contentId = b.id and b.isPublish=1 and b.recordStatus='" + AMockEntity.RecordStatus.Normal + "'");

        Long siteId = dto.getSiteId();
        if(null != siteId) {
            hql.append(" and t.siteId = " + siteId);
        }

        if(organIds != null) {
            hql.append(" and t.organId in (");
            hql.append(organIds + ") ");
        }

        if(name != null) {
            hql.append(" and t.name like '%");
            hql.append(name);
            hql.append("%' escape '\\'");
        }

        if(!AppUtil.isEmpty(typeCode)) {
            hql.append(" and t.typeCode = '");
            hql.append(typeCode);
            hql.append("'");
        }

        return this.getPagination(pageIndex, pageSize, SqlHelper.getSearchAndOrderSqlByAS(hql.toString(), dto), new Object[]{}, CmsWorkGuideEO.class);
    }

    @Override
    public Object publish(Long[] ids,Long publish) {

        String idsStr = String.valueOf(-1);
        for(Long id : ids) {
            if("-1".equals(idsStr)) {
                idsStr = String.valueOf(id);
            } else {
                idsStr += "," + String.valueOf(id);
            }
        }

        String hql = "update CmsWorkGuideEO set publish = ? where id in(" + idsStr + ")";
        return this.executeUpdateByHql(hql, new Object[]{publish.intValue()});
    }

    private String getPIds(Long cId) {

        if(AppUtil.isEmpty(cId)) {
            return null;
        }

        String sql = "SELECT DISTINCT T.P_ID FROM CMS_NET_RESOURCES_CLASSIFY T WHERE T.C_ID = " + cId;
        //这里自定义原因是因为底层生sql执行的方法报错
        List<Object> sids = this.getCurrentSession().createSQLQuery(sql).list();

        if(sids == null || sids.size() <= 0) {
            return  "-1";
        }

        String rst = null;
        for(Object obj : sids) {
            if(rst == null) {
                rst = String.valueOf(obj);
            } else {
                rst += "," + String.valueOf(obj);
            }
        }

        return rst;
    }

    private String getPIds(List<Long> cIds) {
        String ids = "";

        for(Long cId : cIds) {
            if(!"".equals(ids)) {
                ids += "," + cId;
            } else {
                ids = cId + "";
            }
        }

        String sql = "SELECT DISTINCT T.P_ID FROM CMS_NET_RESOURCES_CLASSIFY T WHERE T.C_ID IN("+ids+")";
        //这里自定义原因是因为底层生sql执行的方法报错
        List<Object> sids = this.getCurrentSession().createSQLQuery(sql).list();

        if(sids == null || sids.size() <= 0) {
            return  "-1";
        }

        String rst = null;
        for(Object obj : sids) {
            if(rst == null) {
                rst = String.valueOf(obj);
            } else {
                rst += "," + String.valueOf(obj);
            }
        }

        return rst;
    }
}
