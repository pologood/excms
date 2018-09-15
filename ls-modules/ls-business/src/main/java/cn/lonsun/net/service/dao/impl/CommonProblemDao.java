package cn.lonsun.net.service.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.net.service.dao.ICommonProblemDao;
import cn.lonsun.net.service.entity.CmsCommonProblemEO;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.site.template.util.SqlHelper;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.stereotype.Repository;

/**
 * @author gu.fei
 * @version 2015-11-18 13:46
 */
@Repository
public class CommonProblemDao extends MockDao<CmsCommonProblemEO> implements ICommonProblemDao {

    @Override
    public Pagination getPageEntities(ParamDto dto) {
        Long pageIndex = dto.getPageIndex();
        Integer pageSize = dto.getPageSize();
        StringBuffer hql = new StringBuffer(" select t.id as id,t.contentId as contentId,t.title as title, t.content as content,");
        hql.append(" t.publish as publish,");
        hql.append(" b.columnId as columnId,");
        hql.append(" t.siteId as siteId,t.createDate as createDate");
        hql.append(" from CmsCommonProblemEO t,BaseContentEO b where t.contentId = b.id and b.recordStatus='" + AMockEntity.RecordStatus.Normal + "'");

        if(dto.getByOrgan()) {
            hql.append(" and createOrganId = " + LoginPersonUtil.getOrganId());
        }

        return this.getPagination(pageIndex, pageSize, SqlHelper.getSearchAndOrderSqlByAS(hql.toString(), dto), new Object[]{},CmsCommonProblemEO.class);
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

        String hql = "update CmsCommonProblemEO set publish = ? where id in(" + idsStr + ")";
        return this.executeUpdateByHql(hql, new Object[]{publish});
    }
}
