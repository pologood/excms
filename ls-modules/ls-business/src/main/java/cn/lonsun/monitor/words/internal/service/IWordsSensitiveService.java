package cn.lonsun.monitor.words.internal.service;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.monitor.words.internal.entity.WordsSensitiveEO;
import cn.lonsun.monitor.words.internal.vo.MonitorSiteConfigVO;
import cn.lonsun.monitor.words.internal.vo.ParamDto;
import cn.lonsun.webservice.vo.words.WordsVO;

import java.util.List;
import java.util.Map;

/**
 * @author chen.chao
 * @version 2017-09-21 17:22
 */
public interface IWordsSensitiveService extends IBaseService<WordsSensitiveEO> {

    public Object getPageEOList(ParamDto paramDto);

    public List<WordsSensitiveEO> getEOs(ParamDto paramDto);

    public List<WordsSensitiveEO> getEOs();

    public Object getEOById(Long id);

    public WordsSensitiveEO getEOByWords(String words);

    public void addEO(WordsSensitiveEO eo);

    public void delEO(Long id);

    public void editEO(WordsSensitiveEO eo);

    public Map<String,Object> getMaps();

    public WordsSensitiveEO getCurSiteHas(Long siteId, String words);

    /**
     *根据来源删除
     * @param provenance
     */
    public void delEOByProvenance(String provenance, Long siteId);

    /**
     * 保存
     * @param eo
     */
    public void saveEO(WordsSensitiveEO eo);

    /**
     * 更新
     * @param eo
     */
    public void updateEO(WordsSensitiveEO eo);

    void saveWords(List<WordsVO> list, Long siteId);

    public int deleteByWords(String words);

    public int deleteByWords(List<WordsSensitiveEO> list, Long siteId);

    MonitorSiteConfigVO getSiteRegisterInfo(Long siteId);
}
