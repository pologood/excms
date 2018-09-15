package cn.lonsun.msg.submit.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.msg.submit.dao.IMsgSubmitClassifyDao;
import cn.lonsun.msg.submit.entity.CmsMsgSubmitClassifyEO;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.site.template.util.SqlHelper;

/**
 * @author gu.fei
 * @version 2015-11-18 13:46
 */
@Repository
public class MsgSubmitClassifyDao extends BaseDao<CmsMsgSubmitClassifyEO> implements IMsgSubmitClassifyDao {

    @Override
    public List<CmsMsgSubmitClassifyEO> getEOs() {
        return this.getEntitiesByHql("from CmsMsgSubmitClassifyEO", new Object[]{});
    }

    @Override
    public CmsMsgSubmitClassifyEO getEOById(Long id) {
        return this.getEntity(CmsMsgSubmitClassifyEO.class,id);
    }

    @Override
    public Pagination getPageEOs(ParamDto dto) {
        Long pageIndex = dto.getPageIndex();
        Integer pageSize = dto.getPageSize();
        StringBuffer hql = new StringBuffer(" from CmsMsgSubmitClassifyEO where 1=1");

        return this.getPagination(pageIndex, pageSize, SqlHelper.getSearchAndOrderSql(hql.toString(), dto), new Object[]{});
    }
}
