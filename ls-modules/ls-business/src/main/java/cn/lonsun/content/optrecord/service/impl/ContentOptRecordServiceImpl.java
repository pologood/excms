package cn.lonsun.content.optrecord.service.impl;

import cn.lonsun.content.optrecord.dao.IContentOptRecordDao;
import cn.lonsun.content.optrecord.entity.ContentOptRecordEO;
import cn.lonsun.content.optrecord.service.IContentOptRecordService;
import cn.lonsun.content.optrecord.vo.OptRecordQueryVO;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author gu.fei
 * @version 2018-01-18 9:42
 */
@Service
public class ContentOptRecordServiceImpl extends MockService<ContentOptRecordEO> implements IContentOptRecordService {

    @Resource
    private IContentOptRecordDao contentOptRecordDao;

    @Override
    public Pagination getPage(OptRecordQueryVO vo) {
        return contentOptRecordDao.getPage(vo);
    }
}
