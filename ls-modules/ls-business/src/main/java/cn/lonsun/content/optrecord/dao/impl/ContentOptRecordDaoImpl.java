package cn.lonsun.content.optrecord.dao.impl;

import cn.lonsun.content.optrecord.dao.IContentOptRecordDao;
import cn.lonsun.content.optrecord.entity.ContentOptRecordEO;
import cn.lonsun.content.optrecord.vo.OptRecordQueryVO;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.util.Pagination;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gu.fei
 * @version 2018-01-18 9:38
 */
@Repository
public class ContentOptRecordDaoImpl extends MockDao<ContentOptRecordEO> implements IContentOptRecordDao {

    @Override
    public Pagination getPage(OptRecordQueryVO vo) {
        List<Object> values = new ArrayList<Object>();
        StringBuilder hql = new StringBuilder("from ContentOptRecordEO where contentId=?");
        values.add(vo.getContentId());
        if(null != vo.getType()) {
            hql.append(" and type = ?");
            values.add(vo.getType());
        }
        hql.append(" order by createDate desc");
        return getPagination(vo.getPageIndex(),vo.getPageSize(),hql.toString(),values.toArray());
    }
}
