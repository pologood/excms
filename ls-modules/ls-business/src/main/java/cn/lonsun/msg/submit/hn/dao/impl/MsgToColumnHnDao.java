package cn.lonsun.msg.submit.hn.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.msg.submit.hn.dao.IMsgToColumnHnDao;
import cn.lonsun.msg.submit.hn.entity.CmsMsgToColumnHnEO;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gu.fei
 * @version 2015-11-18 13:46
 */
@Repository
public class MsgToColumnHnDao extends MockDao<CmsMsgToColumnHnEO> implements IMsgToColumnHnDao {

    @Override
    public void deleteByMsgId(Long msgId) {
        Map<String,Object> param = new HashMap<String, Object>();
        param.put("msgId",msgId);
        this.executeUpdateByJpql("delete from CmsMsgToColumnHnEO where msgId = :msgId",param);
    }

    @Override
    public Pagination getColumnPageList(Long msgId, ParamDto dto) {
        Long pageIndex = dto.getPageIndex();
        Integer pageSize = dto.getPageSize();
        StringBuffer hql = new StringBuffer(" from CmsMsgToColumnHnEO where 1=1");
        Map<String, Object> param = new HashMap<String, Object>();
        hql.append(" and msgId = :msgId");
        param.put("msgId",msgId);
        if(null != dto.getStatus()) {
            hql.append(" and status = :status");
            param.put("status",dto.getStatus());
        }

        if(!LoginPersonUtil.isRoot() && !LoginPersonUtil.isSuperAdmin()) {
            if(LoginPersonUtil.isSiteAdmin()) {
                if(null != dto.getSiteIds() && !dto.getSiteIds().isEmpty()) {
                    hql.append(" and siteId in (:siteIds)");
                    param.put("siteIds",dto.getSiteIds());
                } else {
                    hql.append(" and 1=2");
                }
            } else {
                hql.append(" and (");
                if(null != dto.getColumns() && !dto.getColumns().isEmpty()) {
                    hql.append(" columnId in (:columnIds)");
                    param.put("columnIds",dto.getColumns());
                } else {
                    hql.append(" 1=2");
                }
                if(null != dto.getCodes() && !dto.getCodes().isEmpty()) {
                    hql.append(" or code in (:codes)");
                    param.put("codes",dto.getCodes());
                } else {
                    hql.append(" or 1=2");
                }
                hql.append(" )");
            }
        }

        return this.getPagination(pageIndex, pageSize, hql.toString(), param);
    }
}
