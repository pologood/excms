package cn.lonsun.fastdfs.internal.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.fastdfs.internal.dao.IFastDFSDao;
import cn.lonsun.fastdfs.internal.entity.FastDFSEO;
import cn.lonsun.fastdfs.internal.service.IFastDFSService;

/**
 * Created by yy on 2014/8/12.
 */
@Service("fastdfsService")
public class FastDFSServiceImpl extends MockService<FastDFSEO> implements
        IFastDFSService {
    @Autowired
    private IFastDFSDao fastDFSDao;

    @Override
    public Pagination getPage(Long pageIndex,Integer pageSize) {
        return fastDFSDao.getPage(pageIndex, pageSize);
    }

    @Override
    public void saveFastdfs(FastDFSEO eo) {
        fastDFSDao.save(eo);
    }

    @Override
    public void deleteFastdfs(Long id) {
        FastDFSEO eo = getEntity(FastDFSEO.class, id);
        fastDFSDao.delete(eo);
    }

    @Override
    public List<FastDFSEO> getAllData() {
        return fastDFSDao.getAllData();
    }
}
