package cn.lonsun.net.service.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.net.service.dao.IRelatedRuleDao;
import cn.lonsun.net.service.entity.CmsRelatedRuleEO;
import cn.lonsun.net.service.entity.dto.MapVO;
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
public class RelatedRuleDao extends BaseDao<CmsRelatedRuleEO> implements IRelatedRuleDao {

    @Override
    public List<CmsRelatedRuleEO> getEOs() {
        return this.getEntitiesByHql("from CmsRelatedRuleEO", new Object[]{});
    }

    @Override
    public Pagination  getPageEOs(ParamDto dto) {
        Long pageIndex = dto.getPageIndex();
        Integer pageSize = dto.getPageSize();

        String ids = getPIds(dto);

//        StringBuffer hql = new StringBuffer(" from CmsRelatedRuleEO where 1=1");

        StringBuffer hql = new StringBuffer(" select t.id as id,t.contentId as contentId,t.name as name,t.linkUrl as linkUrl, t.uploadUrl as uploadUrl,t.organId as organId, t.content as content,");
        hql.append(" t.joinDate as joinDate,");
        hql.append(" t.type as type,");
        hql.append(" b.isPublish as publish,");
        hql.append(" b.columnId as columnId,");
        hql.append(" t.siteId as siteId,t.createDate as createDate");
        hql.append(" from CmsRelatedRuleEO t,BaseContentEO b where t.contentId = b.id and b.recordStatus='" + AMockEntity.RecordStatus.Normal + "'");

        if(dto.getByOrgan()) {
            hql.append(" and t.createOrganId = " + LoginPersonUtil.getOrganId());
        }

        Long siteId = dto.getSiteId();

        if(null == siteId) {
            siteId = LoginPersonUtil.getSiteId();
        }
        hql.append(" and t.siteId = " + siteId);

        if(!AppUtil.isEmpty(dto.getOrganId())) {
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
//            hql .append(" and t.id in(" + ids + ")");
        }

        if(!AppUtil.isEmpty(dto.getPublish())) {
            hql .append(" and b.isPublish=" + dto.getPublish());
        }

        if(dto.getResIds() != null) {
            hql .append(" and t.id in(" + dto.getResIds() + ")");
        }

        if(!AppUtil.isEmpty(dto.getType())) {
            hql .append(" and t.type = '" + dto.getType() + "' ");
        }

        return this.getPagination(pageIndex, pageSize, SqlHelper.getSearchAndOrderSqlByAS(hql.toString(), dto), new Object[]{},CmsRelatedRuleEO.class);
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

        String hql = "update CmsRelatedRuleEO set publish = ? where id in(" + idsStr + ")";
        return this.executeUpdateByHql(hql,new Object[]{publish.intValue()});
    }

    private String getPIds(ParamDto dto) {
        if(dto == null) {
            return null;
        }
        List<MapVO> list = dto.getMapVOs();
        String ids = null;
        if(list != null && list.size() >0) {
            for(MapVO vo : list) {
                if(ids == null) {
                    ids = vo.getKey();
                } else {
                    ids += "," + vo.getKey();
                }
            }
        } else if(dto.getColumnId() != null) {
            ids = dto.getColumnId() + "";
        } else {
            return null;
        }
        if(ids == null) {
            return null;
        }

        String sql = "SELECT DISTINCT(T.P_ID) FROM CMS_NET_RESOURCES_CLASSIFY T WHERE T.C_ID IN("+ids+") AND TYPE = 'RULE'";
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
