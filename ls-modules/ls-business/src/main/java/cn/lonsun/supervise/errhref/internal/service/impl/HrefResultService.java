package cn.lonsun.supervise.errhref.internal.service.impl;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.supervise.errhref.internal.dao.IHrefResultDao;
import cn.lonsun.supervise.errhref.internal.entity.HrefResultEO;
import cn.lonsun.supervise.errhref.internal.service.IHrefResultService;
import cn.lonsun.supervise.vo.SupervisePageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author gu.fei
 * @version 2016-4-5 10:48
 */
@Service
public class HrefResultService extends MockService<HrefResultEO> implements IHrefResultService {

    @Autowired
    private IHrefResultDao hrefResultDao;

    @Override
    public Pagination getPageEOs(SupervisePageVO vo) {
        return hrefResultDao.getPageEOs(vo);
    }

    @Override
    public List<HrefResultEO> getByTaskId(Long taskId) {
        return hrefResultDao.getByTaskId(taskId);
    }

    @Override
    public void physDelEOs(Long taskId) {
        hrefResultDao.physDelEOs(taskId);
    }

    @Override
    public void physDelEO(Long resultId) {
        hrefResultDao.physDelEO(resultId);
    }

    @Override
    public Long getCountByTaskId(Long taskId) {
        return hrefResultDao.getCountByTaskId(taskId);
    }
}
