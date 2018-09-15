package cn.lonsun.publishproblem.internal.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SqlUtil;
import cn.lonsun.publishproblem.internal.dao.IPublishProblemDao;
import cn.lonsun.publishproblem.vo.PublishProblemVO;
import cn.lonsun.publishproblem.vo.PulishProblemQueryVO;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangxx on 2017/9/1.
 */
@Repository
public class PublishProblemDaoImpl extends MockDao<BaseContentEO> implements IPublishProblemDao{
    @Override
    public Pagination getPage(PulishProblemQueryVO vo) {

        StringBuffer hql = new StringBuffer();
        List<Object> params = new ArrayList<Object>();

        hql.append("select t.id as id,t.title as title,t.columnId as columnId,t.remarks as remarks,t.typeCode as typeCode,t.isPublish as isPublish,t.publishDate as publishDate")
                .append(" from BaseContentEO t where t.recordStatus = ? and t.siteId = ?");
        params.add(AMockEntity.RecordStatus.Normal.toString());
        params.add(LoginPersonUtil.getSiteId());
        hql.append(" and t.isPublish = ?");
        if(!AppUtil.isEmpty(vo.getIsPublish())) {
            params.add(vo.getIsPublish());
        } else {
            params.add(Integer.valueOf(2));
        }
        if(!AppUtil.isEmpty(vo.getTitle())) {
            hql.append(" and t.title like ?");
            params.add("%" + SqlUtil.prepareParam4Query(vo.getTitle()) + "%");
        }
       /* if(!AppUtil.isEmpty(vo.getColumnName())) {
            hql.append(" and b.name like ?");
            params.add("%" + SqlUtil.prepareParam4Query(vo.getColumnName()) + "%");
        }*/

        hql.append(" order by t.publishDate desc");
        return getPagination(vo.getPageIndex(),vo.getPageSize(),hql.toString(),params.toArray(), PublishProblemVO.class);
    }
}
