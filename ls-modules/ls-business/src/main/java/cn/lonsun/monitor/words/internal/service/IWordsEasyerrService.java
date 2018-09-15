package cn.lonsun.monitor.words.internal.service;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.monitor.words.internal.entity.WordsEasyerrEO;
import cn.lonsun.monitor.words.internal.vo.MonitorSiteConfigVO;
import cn.lonsun.monitor.words.internal.vo.ParamDto;

import java.util.List;
import java.util.Map;

/**
 * @author chen.chao
 * @version 2017-09-21 17:22
 */
public interface IWordsEasyerrService extends IBaseService<WordsEasyerrEO> {

    Object getPageEOList(ParamDto paramDto);

    public void delEO(Long id);

    public List<WordsEasyerrEO> getEOs(ParamDto paramDto);

    public Pagination getPage(ParamDto paramDto);

    /**
     * 加入词库
     * @param ids
     */
    public void join(String ids);

    public List<WordsEasyerrEO> getEOs();

    public Object getEOById(Long id);

    public WordsEasyerrEO getEOByWords(String words);

    public void addEO(WordsEasyerrEO eo);

    public void editEO(WordsEasyerrEO eo);

    public Map<String,Object> getMaps();

    public WordsEasyerrEO getCurSiteHas(Long siteId, String words);

    /**
     *根据来源删除
     * @param provenance
     */
    public void delEOByProvenance(String provenance, Long siteId);

    /**
     * 保存
     * @param eo
     */
    public void saveEO(WordsEasyerrEO eo);

    /**
     * 更新
     * @param eo
     */
    public void updateEO(WordsEasyerrEO eo);


    public int deleteByWords(String words, Long siteId);

    public int deleteByWords(List<WordsEasyerrEO> list, Long siteId);


    MonitorSiteConfigVO getSiteRegisterInfo(Long siteId);
}
