package cn.lonsun.fastdfs.internal.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.fastdfs.internal.dao.IFastDFSDao;
import cn.lonsun.fastdfs.internal.entity.FastDFSEO;

/**
 * Created by yy on 2014/8/12.
 */
@Repository("fastdfsDao")
public class FastDFSDaoImpl extends MockDao<FastDFSEO> implements IFastDFSDao {

    @Override
    public Pagination getPage(Long pageIndex,Integer pageSize) {
        String hql = " from FastDFSEO t where  t.recordStatus='Normal' ";
        List<Object> values = new ArrayList<Object>();
        
        hql += " order by t.createDate desc ";
        return getPagination(pageIndex, pageSize, hql, values.toArray());
    }

    @Override
    public List<FastDFSEO> getAllData() {
        String hql = " from FastDFSEO t where  t.recordStatus='Normal' ";
        return getEntitiesByHql(hql);
    }
}
