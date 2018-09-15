package cn.lonsun.site.words.internal.service;

import java.util.List;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.site.words.internal.entity.ContentCheckResultEO;
import cn.lonsun.site.words.internal.entity.vo.WordsPageVO;

/**
 * @author gu.fei
 * @version 2015-12-31 13:45
 */
public interface IContentCheckResultService extends IMockService<ContentCheckResultEO> {

    /**
     * 根据checkId分页查询结果
     * @param vo
     * @return
     */
    public Pagination getPageEOById(WordsPageVO vo);

    /**
     * 根据ID获取集合
     * @param id
     * @return
     */
    public List<ContentCheckResultEO> getEOById(Long id);

    /**
     * 根据类型删除
     * @param checkType
     */
    public void deleteByCheckType(String checkType);

    /**
     * 根据类型删除
     * @param checkType
     */
    public void deleteByCheckType(Long checkId,String checkType);

    /**
     * 删除指定
     * @param checkId
     * @param words
     * @param checkType
     */
    public void deleteByCheckType(Long checkId,String words,String checkType);
}
