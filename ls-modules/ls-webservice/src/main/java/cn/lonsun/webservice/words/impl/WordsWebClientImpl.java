package cn.lonsun.webservice.words.impl;

import cn.lonsun.webservice.core.WebServiceCaller;
import cn.lonsun.webservice.to.WebServiceTO;
import cn.lonsun.webservice.vo.words.WordsVO;
import cn.lonsun.webservice.words.IWordsWebClient;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author chen.chao
 * @version 2017-09-22 18:06
 */
@Service("wordsWebClientImpl")
public class WordsWebClientImpl implements IWordsWebClient {

    private enum Codes {
        WordsEasyerr_getFromCloud,
        WordsEasyerr_Push,
        WordsSensitive_getFromCloud,
        WordsSensitive_Push;
    }

    /**
     * 从云平台获取词库(易错词)
     *
     * @return
     */
    @Override
    public List<WordsVO> getWordsEassyErrList(Long siteId, String registerCode) {
        String code = Codes.WordsEasyerr_getFromCloud.toString();
        Object[] params = new Object[]{siteId, registerCode};
        return (List<WordsVO>) WebServiceCaller.getList(code, params, WordsVO.class);
    }


    /**
     * 向云平台推送(易错词)
     *
     * @param words
     * @param replaceWords
     * @param provenance
     * @param seriousErr
     * @param pushDate
     * @return
     */
    @Override
    public boolean pushWordsEasyerr(String words, String replaceWords, String provenance, String siteName,
                                    Integer seriousErr, Date pushDate, Long siteId, String registerCode) {
        String code = Codes.WordsEasyerr_Push.toString();
        Object[] params = new Object[]{words, replaceWords, provenance, siteName, seriousErr, pushDate, siteId, registerCode};
        // 获取返回内容
        WebServiceTO to = WebServiceCaller.getWebServiceTO(code, params);
        return to.getStatus() == 1;
    }

    /**
     * 从云平台获取词库(敏感词)
     *
     * @return
     */
    @Override
    public List<WordsVO> getWordsSensitiveList(Long siteId, String registerCode) {
        String code = Codes.WordsSensitive_getFromCloud.toString();
        Object[] params = new Object[]{siteId, registerCode};
        return (List<WordsVO>) WebServiceCaller.getList(code, params, WordsVO.class);
    }

    /**
     * 向云平台推送(敏感词)
     *
     * @param words
     * @param replaceWords
     * @param provenance
     * @param seriousErr
     * @param pushDate
     * @return
     */
    @Override
    public boolean pushWordsSensitive(String words, String replaceWords, String provenance, String siteName,
                                      Integer seriousErr, Date pushDate, Long siteId, String registerCode) {
        String code = Codes.WordsSensitive_Push.toString();
        Object[] params = new Object[]{words, replaceWords, provenance, siteName, seriousErr, pushDate, siteId, registerCode};
        // 获取返回内容
        WebServiceTO to = WebServiceCaller.getWebServiceTO(code, params);
        return to.getStatus() == 1;
    }
}
