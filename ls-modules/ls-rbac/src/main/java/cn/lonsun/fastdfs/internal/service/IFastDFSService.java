package cn.lonsun.fastdfs.internal.service;

import java.util.List;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.fastdfs.internal.entity.FastDFSEO;

/**
 * Created by yy on 2014/8/12.
 */
public interface IFastDFSService extends IMockService<FastDFSEO> {
    /**
     * 添加
     * 
     * @param eo
     */
    public void saveFastdfs(FastDFSEO eo);

    /**
     * 删除
     * 
     * @param id
     */
    public void deleteFastdfs(Long id);

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
