package cn.lonsun.monitor.words.internal.service.impl;

import cn.lonsun.base.anno.DbInject;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.monitor.words.internal.dao.IWordsSensitiveDao;
import cn.lonsun.monitor.words.internal.entity.WordsSensitiveEO;
import cn.lonsun.monitor.words.internal.service.IWordsSensitiveService;
import cn.lonsun.monitor.words.internal.vo.MonitorSiteConfigVO;
import cn.lonsun.monitor.words.internal.vo.ParamDto;
import cn.lonsun.site.site.internal.service.ISiteConfigService;
import cn.lonsun.webservice.vo.words.WordsVO;
import cn.lonsun.webservice.words.IWordsWebClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author chen.chao
 * @version 2017-09-21 17:22
 */
@Service("wordsSensitiveService")
public class WordsSensitiveServiceImpl extends BaseService<WordsSensitiveEO> implements IWordsSensitiveService {

    @DbInject("wordsSensitive")
    private IWordsSensitiveDao wordsSensitiveDao;
    @Autowired
    private IWordsWebClient wordsWebClient;
    @Resource
    private ISiteConfigService siteConfigService;

    @Override
    public Object getPageEOList(ParamDto paramDto) {
        return wordsSensitiveDao.getPageEOList(paramDto);
    }

    @Override
    public List<WordsSensitiveEO> getEOs(ParamDto paramDto) {
        return wordsSensitiveDao.getEOs(paramDto);
    }

    @Override
    public List<WordsSensitiveEO> getEOs() {
        return wordsSensitiveDao.getEOs();
    }

    @Override
    public Object getEOById(Long id) {
        return wordsSensitiveDao.getEOById(id);
    }

    @Override
    public WordsSensitiveEO getEOByWords(String words) {
        return wordsSensitiveDao.getEOByWords(words);
    }

    @Override
    public void addEO(WordsSensitiveEO eo) {
        wordsSensitiveDao.addEO(eo);
    }

    @Override
    public void delEO(Long id) {
        wordsSensitiveDao.delEO(id);
    }

    @Override
    public void editEO(WordsSensitiveEO eo) {
        wordsSensitiveDao.editEO(eo);
    }

    @Override
    public Map<String, Object> getMaps() {
        return wordsSensitiveDao.getMaps();
    }

    @Override
    public WordsSensitiveEO getCurSiteHas(Long siteId,String words) {
        return wordsSensitiveDao.getCurSiteHas(siteId,words);
    }

    @Override
    public void delEOByProvenance(String provenance, Long siteId) {
        wordsSensitiveDao.delEOByProvenance(provenance, siteId);
    }

    @Override
    public void saveEO(WordsSensitiveEO eo) {
        if(eo.getWhetherPush()!=null && eo.getWhetherPush()==0){//推送到云平台
            //获取站点注册码
            pushToCloud(eo);
        }
        this.saveEntity(eo);
    }

    @Override
    public void updateEO(WordsSensitiveEO eo) {
        if(eo.getWhetherPush()!=null && eo.getWhetherPush()==0){//推送到云平台
            pushToCloud(eo);
        }
        this.updateEntity(eo);
    }

    /**
     * 将敏感词推送到云平台，同时修改状态
     * @param eo
     */
    private void pushToCloud(WordsSensitiveEO eo) {
        //获取站点注册码
        MonitorSiteConfigVO siteConfigVO = getSiteRegisterInfo(eo.getSiteId());
//        SiteConfigEO siteConfigEO = CacheHandler.getEntity(SiteConfigEO.class, CacheGroup.CMS_INDICATORID, eo.getSiteId());
        if(siteConfigVO == null){
            throw new BaseRunTimeException("未找到站点");
        }
        if(siteConfigVO.getIsRegistered() == null || siteConfigVO.getIsRegistered() == 0 || StringUtils.isEmpty(siteConfigVO.getRegisteredCode())){
            throw new BaseRunTimeException("站点尚未注册云监控服务，注册后即可同步到云平台！");
        }
        wordsWebClient.pushWordsSensitive(eo.getWords(),eo.getReplaceWords(),eo.getProvenance(), eo.getSiteName(),
                eo.getSeriousErr(),new Date(), eo.getSiteId(), siteConfigVO.getRegisteredCode());
        eo.setPushStatus(WordsSensitiveEO.PushStatus.Pushed.toString());
    }

    @Transactional
    @Override
    public void saveWords(List<WordsVO> list, Long siteId) {
        if (list == null || list.isEmpty()) {
            return;
        }
        delEOByProvenance(WordsSensitiveEO.Provenance.Cloud.toString(), -1l);
        Map<String, Object> exit = getMaps();
        for (WordsVO vo : list) {
            if(exit.containsKey("-1_" + vo.getWords())){
                continue;
            }
            WordsSensitiveEO eo = new WordsSensitiveEO();
            eo.setWords(vo.getWords());
            eo.setReplaceWords(vo.getReplaceWords());
            eo.setSeriousErr(vo.getSeriousErr());
            eo.setSiteId(-1l);
            eo.setSiteName("云平台");
            eo.setCreateDate(vo.getCreateDate());
            eo.setProvenance(WordsSensitiveEO.Provenance.Cloud.toString());
            wordsSensitiveDao.save(eo);
        }
    }

    @Override
    public int deleteByWords(String words) {
        return wordsSensitiveDao.deleteByWords(words);
    }

    @Override
    public int deleteByWords(List<WordsSensitiveEO> list, Long siteId) {
        return wordsSensitiveDao.deleteByWords(list, siteId);
    }


    @Override
    public MonitorSiteConfigVO getSiteRegisterInfo(Long siteId){
        List<MonitorSiteConfigVO> vos = wordsSensitiveDao.getSiteRegisterInfos(siteId);
        if(vos!=null&&vos.size()>0){
            if(siteId == null){
                for(MonitorSiteConfigVO v : vos){
                    if(StringUtils.isNotEmpty(v.getRegisteredCode()) && v.getIsRegistered() == 1){
                        return v;
                    }
                }
            }
            return vos.get(0);
        }
        return null;
    }
}
