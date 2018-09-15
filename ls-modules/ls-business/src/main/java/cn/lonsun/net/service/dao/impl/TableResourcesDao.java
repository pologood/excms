package cn.lonsun.net.service.dao.impl;

import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.net.service.dao.ITableResourcesDao;
import cn.lonsun.net.service.entity.CmsTableResourcesEO;
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
public class TableResourcesDao extends BaseDao<CmsTableResourcesEO> implements ITableResourcesDao {

    @Override
    public List<CmsTableResourcesEO> getEOs() {
        return this.getEntitiesByHql("from CmsTableResourcesEO", new Object[]{});
    }

    @Override
    public Pagination getPageEOs(ParamDto dto) {
        Long pageIndex = dto.getPageIndex();
        Integer pageSize = dto.getPageSize();
        String ids = getPIds(dto);

        StringBuffer hql = new StringBuffer(" from CmsTableResourcesEO where 1=1");
        Long siteId = dto.getSiteId();
        if(null == siteId) {
            siteId = LoginPersonUtil.getSiteId();
        }
        hql.append(" and siteId = " + siteId);

        if(dto.getOrganId() != null) {
            hql.append(" and organId = ");
            hql.append(dto.getOrganId());
        }

        if(ids != null) {

            Long[] idsl = StringUtils.getArrayWithLong(ids,",");
            int n = 500;
            int count = idsl.length/n;
            hql.append(" and (");
            for(int i = 0;i < count ;i++) {
                if(i == 0) {
                    hql.append(" id in(");
                } else {
                    hql.append(" or id in(");
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
                    hql.append(" or id in(");
                } else {
                    hql.append("id in(");
                }
                for(Long id : lastarr) {
                    hql.append(id + ",");
                }

                hql.append("-1)");
            }

            hql.append(")");
//            hql.append(" and id in(" + ids + ")");
        }

        if(dto.getResIds() != null) {
            hql.append(" and id in(" + dto.getResIds() + ")");
        }

        return this.getPagination(pageIndex, pageSize, SqlHelper.getSearchAndOrderSql(hql.toString(), dto), new Object[]{});
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

        String sql = "SELECT DISTINCT T.P_ID FROM CMS_NET_RESOURCES_CLASSIFY T WHERE T.C_ID IN("+ids+") AND TYPE = 'TABLE'";
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
