package cn.lonsun.site.words.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.site.words.internal.entity.ContentCheckEO;
import cn.lonsun.site.words.internal.entity.vo.WordsPageVO;

/**
 * @author gu.fei
 * @version 2015-12-31 13:42
 */
public interface IContentCheckDao extends IMockDao<ContentCheckEO> {

    public Pagination getPageEO(WordsPageVO vo);

    public void deleteById(Long id);

    public void deleteByCheckType(String checkType,Long siteId);

    public void deleteByCheckType(Long contentId,String checkType);
}
