package cn.lonsun.site.words.internal.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.site.words.internal.dao.IContentCheckResultDao;
import cn.lonsun.site.words.internal.entity.ContentCheckResultEO;
import cn.lonsun.site.words.internal.entity.vo.WordsPageVO;
import cn.lonsun.site.words.internal.service.IContentCheckResultService;

/**
 * @author gu.fei
 * @version 2015-12-31 13:46
 */
@Service
public class ContentCheckResultService extends MockService<ContentCheckResultEO> implements IContentCheckResultService {

    @Autowired
    private IContentCheckResultDao contentCheckResultDao;

    @Override
    public Pagination getPageEOById(WordsPageVO vo) {
        return contentCheckResultDao.getPageEOById(vo);
    }

    @Override
    public List<ContentCheckResultEO> getEOById(Long id) {
        return contentCheckResultDao.getEOById(id);
    }

    @Override
    public void deleteByCheckType(String checkType) {
        contentCheckResultDao.deleteByCheckType(checkType);
    }

    @Override
    public void deleteByCheckType(Long checkId, String checkType) {
        contentCheckResultDao.deleteByCheckType(checkId,checkType);
    }

    @Override
    public void deleteByCheckType(Long checkId, String words, String checkType) {
        contentCheckResultDao.deleteByCheckType(checkId,words,checkType);
    }
}
