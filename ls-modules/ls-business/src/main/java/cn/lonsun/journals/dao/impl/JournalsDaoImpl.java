package cn.lonsun.journals.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SqlUtil;
import cn.lonsun.journals.dao.IJournalsDao;
import cn.lonsun.journals.entity.JournalsEO;
import cn.lonsun.journals.vo.JournalsQueryVO;
import cn.lonsun.journals.vo.JournalsSearchVO;
import cn.lonsun.journals.vo.JournalsVO;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lonsun on 2017-1-3.
 */
@Repository
public class JournalsDaoImpl extends MockDao<JournalsEO> implements IJournalsDao {
    @Override
    public Pagination getJournalsList(JournalsQueryVO journalsQueryVO) {
        StringBuilder hql =new StringBuilder();
        List<Object> param =new ArrayList<Object>();
        hql.append("select s.materiaId as id,c.id as baseContentId,s.materiaName as materiaName,s.year as year,s.periodical as periodical,c.isPublish as isPublish,s.review as review from BaseContentEO c,JournalsEO s where c.id=s.baseContentId and c.recordStatus=? and s.siteId=? and c.columnId=? ");
        param.add(AMockEntity.RecordStatus.Normal.toString());
        param.add(journalsQueryVO.getSiteId());
        param.add(journalsQueryVO.getColumnId());
        if(!AppUtil.isEmpty(journalsQueryVO.getKeyWord())){
            hql.append(" and s.materiaName like ?");
            param.add( "%"+ SqlUtil.prepareParam4Query(journalsQueryVO.getKeyWord())+"%");

        }
        if(!AppUtil.isEmpty(journalsQueryVO.getCreate())){
            hql.append(" and s.year = ?");
            param.add(journalsQueryVO.getCreate());

        }
        hql.append("order by s.createDate desc");

        return getPagination(journalsQueryVO.getPageIndex(),journalsQueryVO.getPageSize(),hql.toString(),param.toArray(), JournalsVO.class);
    }

    @Override
    public List<JournalsEO> checkMateria(JournalsEO journalsEO) {
        StringBuilder hql =new StringBuilder();
        List<Object> param =new ArrayList<Object>();
        hql.append(" from JournalsEO s where   s.recordStatus=? and s.siteId=? ");
        hql.append("  and s.year =? and s.periodical=?");
        param.add(AMockEntity.RecordStatus.Normal.toString());
        param.add(journalsEO.getSiteId());
        param.add(journalsEO.getYear());
        param.add(journalsEO.getPeriodical());
        return  getEntitiesByHql(hql.toString(),param.toArray());
    }

    @Override
    public List<JournalsSearchVO> getAllSearchVO() {
        StringBuilder hql =new StringBuilder();
        hql.append("select b.id as baseContentId,b.columnId as columnId,b.siteId as siteId,b.typeCode as typeCode,b.title as title,b.publishDate as publishDate  from JournalsEO j,BaseContentEO b where j.baseContentId =b.id and b.recordStatus = '"+AMockEntity.RecordStatus.Normal.toString()+"' and b.isPublish =1");

        return  (List<JournalsSearchVO>)getBeansByHql(hql.toString(),new Object[]{},JournalsSearchVO.class);
    }
}
