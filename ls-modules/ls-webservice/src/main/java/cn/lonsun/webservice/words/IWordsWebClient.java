package cn.lonsun.webservice.words;

import cn.lonsun.webservice.vo.words.WordsVO;

import java.util.Date;
import java.util.List;

/**
 * @author chen.chao
 * @version 2017-09-22 18:04
 */
public interface IWordsWebClient {

    List<WordsVO> getWordsEassyErrList(Long siteId, String registerCode);

    boolean pushWordsEasyerr(String words, String replaceWords, String provenance, String siteName,
                             Integer seriousErr, Date pushDate, Long siteId, String registerCode);

    List<WordsVO> getWordsSensitiveList(Long siteId, String registerCode);

    boolean pushWordsSensitive(String words, String replaceWords, String provenance, String siteName,
                               Integer seriousErr, Date pushDate, Long siteId, String registerCode);
}
