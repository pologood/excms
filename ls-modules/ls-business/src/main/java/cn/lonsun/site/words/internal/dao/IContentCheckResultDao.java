package cn.lonsun.site.words.internal.dao;

import java.util.List;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.site.words.internal.entity.ContentCheckResultEO;
import cn.lonsun.site.words.internal.entity.vo.WordsPageVO;

/**
 * @author gu.fei
 * @version 2015-12-31 13:42
 */
public interface IContentCheckResultDao extends IMockDao<ContentCheckResultEO> {

    public Pagination getPageEOById(WordsPageVO vo);

    public List<ContentCheckResultEO> getEOById(Long id);

    public void deleteByCheckType(String checkType);

    public void deleteByCheckType(Long checkId, String checkType);

    public void deleteByCheckType(Long checkId,String words,String checkType);
}
