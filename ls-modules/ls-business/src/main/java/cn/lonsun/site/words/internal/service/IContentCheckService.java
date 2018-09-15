package cn.lonsun.site.words.internal.service;

import java.util.List;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.site.words.internal.entity.ContentCheckEO;
import cn.lonsun.site.words.internal.entity.vo.WordsPageVO;

/**
 * @author gu.fei
 * @version 2015-12-31 13:45
 */
public interface IContentCheckService extends IMockService<ContentCheckEO> {

    /**
     * 分页查询
     * @param vo
     * @return
     */
    public Pagination getPageEO(WordsPageVO vo);

    /**
     * 根据检测类型删除
     * @param checkType
     */
    public void deleteByCheckType(String checkType,Long siteId);

    /**
     * 删除指定类型
     * @param contentId
     * @param checkType
     */
    public void deleteByCheckType(Long contentId,String checkType);

    /**
     * 检测词组
     */
    public void checkContent(String checkType,Long siteId);

    /**
     * 替换指定数据指定词汇
     * @param eos
     * @return
     */
    public void replace(List<ContentCheckEO> eos);

}
