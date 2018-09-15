package cn.lonsun.monitor.words.internal.service.impl;

import cn.lonsun.base.anno.DbInject;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.monitor.words.internal.dao.IWordsEasyerrDao;
import cn.lonsun.monitor.words.internal.entity.WordsEasyerrEO;
import cn.lonsun.monitor.words.internal.service.IWordsEasyerrService;
import cn.lonsun.monitor.words.internal.vo.MonitorSiteConfigVO;
import cn.lonsun.monitor.words.internal.vo.ParamDto;
import cn.lonsun.site.site.internal.service.ISiteConfigService;
import cn.lonsun.webservice.words.IWordsWebClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author chen.chao
 * @version 2017-09-21 17:22
 */
@Service("wordsEasyerrService")
public class WordsEasyerrServiceImpl extends BaseService<WordsEasyerrEO> implements IWordsEasyerrService {

    @DbInject("wordsEasyErr")
    private IWordsEasyerrDao wordsEasyErrConfDao;
    @Autowired
    private IWordsWebClient wordsWebClient;
    @Resource
    private ISiteConfigService siteConfigService;

    @Override
    public Object getPageEOList(ParamDto paramDto) {
        return wordsEasyErrConfDao.getPageEOList(paramDto);
    }

    @Override
    public void delEO(Long id) {
        wordsEasyErrConfDao.delEO(id);
    }

    @Override
    public List<WordsEasyerrEO> getEOs(ParamDto paramDto) {
        return wordsEasyErrConfDao.getEOs(paramDto);
    }

    @Override
    public Pagination getPage(ParamDto paramDto) {
        return wordsEasyErrConfDao.getPage(paramDto);
    }

    @Override
    public void join(String ids){
        String[] idstr = ids.split(",");
        for(int i=0;i<idstr.length;i++) {
            Long id = Long.parseLong(idstr[i]);
            WordsEasyerrEO eo = this.getEntity(WordsEasyerrEO.class,id);
            eo.setIsInto(0);
            this.updateEntity(eo);
        }
    }

    @Override
    public List<WordsEasyerrEO> getEOs() {
        return wordsEasyErrConfDao.getEOs();
    }

    @Override
    public Object getEOById(Long id) {
        return wordsEasyErrConfDao.getEOById(id);
    }

    @Override
    public WordsEasyerrEO getEOByWords(String words) {
        return wordsEasyErrConfDao.getEOByWords(words);
    }

    @Override
    public void addEO(WordsEasyerrEO eo) {
        wordsEasyErrConfDao.addEO(eo);
    }



    @Override
    public void editEO(WordsEasyerrEO eo) {
        wordsEasyErrConfDao.editEO(eo);
    }

    @Override
    public Map<String, Object> getMaps() {
        return wordsEasyErrConfDao.getMaps();
    }

    @Override
    public WordsEasyerrEO getCurSiteHas(Long siteId, String words) {
        return wordsEasyErrConfDao.getCurSiteHas(siteId,words);
    }

    /**
     * 根据来源删除
     * @param provenance
     */
    @Override
    public void delEOByProvenance(String provenance, Long siteId) {
        wordsEasyErrConfDao.delEOByProvenance(provenance, siteId);
    }

    /**
     * 保存
     * @param eo
     */
    @Override
    public void saveEO(WordsEasyerrEO eo) {
        if(eo.getWhetherPush()!=null && eo.getWhetherPush()==0){//推送到云平台
            pushToCloud(eo);
        }
        this.saveEntity(eo);
    }

    /**
     * 将错词推送到云平台，然后修改推送状态
     * @param eo
     */
    private void pushToCloud(WordsEasyerrEO eo) {
        //获取站点注册码
        MonitorSiteConfigVO siteConfigVO = getSiteRegisterInfo(eo.getSiteId());
//        SiteConfigEO siteConfigEO = CacheHandler.getEntity(SiteConfigEO.class, CacheGroup.CMS_INDICATORID, eo.getSiteId());
        if(siteConfigVO == null){
            throw new BaseRunTimeException("未找到站点");
        }
        if(siteConfigVO.getIsRegistered() == null || siteConfigVO.getIsRegistered() == 0 || StringUtils.isEmpty(siteConfigVO.getRegisteredCode())){
            throw new BaseRunTimeException("站点尚未注册云监控服务，注册后即可同步到云平台！");
        }
        wordsWebClient.pushWordsEasyerr(eo.getWords(),eo.getReplaceWords(),eo.getProvenance(),eo.getSiteName(),
                eo.getSeriousErr(),new Date(), eo.getSiteId(), siteConfigVO.getRegisteredCode());
        eo.setPushStatus(WordsEasyerrEO.PushStatus.Pushed.toString());
    }

    /**
     * 更新
     * @param eo
     */
    public void updateEO(WordsEasyerrEO eo) {
//        if(eo.getWhetherPush()!=null && eo.getWhetherPush()==0 && eo.getPushStatus().equals(WordsEasyerrEO.PushStatus.UnPush)){//推送到云平台
        if(eo.getWhetherPush()!=null && eo.getWhetherPush()==0){//推送到云平台
            //获取站点注册码
            pushToCloud(eo);
        }
        this.updateEntity(eo);
    }


    @Override
    public int deleteByWords(String words, Long siteId) {
        return wordsEasyErrConfDao.deleteByWords(words, siteId);
    }

    @Override
    public int deleteByWords(List<WordsEasyerrEO> list, Long siteId) {
        return wordsEasyErrConfDao.deleteByWords(list, siteId);
    }

    @Override
    public MonitorSiteConfigVO getSiteRegisterInfo(Long siteId){
        List<MonitorSiteConfigVO> vos = wordsEasyErrConfDao.getSiteRegisterInfos(siteId);
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
