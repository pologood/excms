package cn.lonsun.fastdfs.internal.dao;

import java.util.List;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.fastdfs.internal.entity.FastDFSEO;

/**
 * Created by yy on 2014/8/13.
 */
public interface IFastDFSDao extends IMockDao<FastDFSEO> {
    /**
     * 查询
     * 
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public Pagination getPage(Long pageIndex, Integer pageSize);

    /**
     * 查询所有的fastdfs
     *
     * @return
     */
    public List<FastDFSEO> getAllData();

}
