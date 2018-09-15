package cn.lonsun.system.sitechart.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.system.sitechart.dao.IMyCollectionDao;
import cn.lonsun.system.sitechart.internal.entity.MyCollectionEO;
import cn.lonsun.system.sitechart.vo.MyCollectionVO;
import cn.lonsun.system.sitechart.vo.VisitDeatilPageVo;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hu on 2016/8/15.
 */
@Repository
public class MyCollectionDaoImpl extends BaseDao<MyCollectionEO> implements IMyCollectionDao {
    @Override
    public Pagination getPage(MyCollectionVO query) {
        List<Object> values =new ArrayList<Object>();
        StringBuffer hql =new StringBuffer("from MyCollectionEO where 1=1");
        if(query.getSiteId() != null){
            hql.append(" and siteId = ?");
            values.add(query.getSiteId());
        }
        if(query.getMemberId() != null){
            hql.append(" and memberId = ?");
            values.add(query.getMemberId());
        }
        hql.append(" order by createDate desc");
        return getPagination(query.getPageIndex(),query.getPageSize(),hql.toString(),values.toArray());
    }

}
