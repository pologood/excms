package cn.lonsun.datacollect.service.impl;

import cn.lonsun.base.anno.DbInject;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.datacollect.dao.IHtmlCollectContentDao;
import cn.lonsun.datacollect.entity.HtmlCollectContentEO;
import cn.lonsun.datacollect.service.IHtmlCollectContentService;
import cn.lonsun.datacollect.vo.CollectPageVO;
import cn.lonsun.datacollect.vo.ColumnVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author gu.fei
 * @version 2016-1-21 14:36
 */
@Service
public class HtmlCollectContentService extends MockService<HtmlCollectContentEO> implements IHtmlCollectContentService {

    @DbInject("htmlCollectContent")
    private IHtmlCollectContentDao htmlCollectContentDao;

    @Override
    public Pagination getPageEOs(CollectPageVO vo) {
        return htmlCollectContentDao.getPageEOs(vo);
    }

    @Override
    public List<HtmlCollectContentEO> getByTaskId(Long taskId) {
        return htmlCollectContentDao.getByTaskId(taskId);
    }

    @Override
    public void saveEO(HtmlCollectContentEO eo) {
        this.saveEntity(eo);
    }

    @Override
    public void updateEO(HtmlCollectContentEO eo) {
        this.updateEntity(eo);
    }

    @Override
    public void deleteEOs(Long[] ids) {
        htmlCollectContentDao.deleteEOs(ids);
    }

    @Override
    public List<ColumnVO> getColumns(String tableName) {
        return htmlCollectContentDao.getColumns(tableName);
    }
}
