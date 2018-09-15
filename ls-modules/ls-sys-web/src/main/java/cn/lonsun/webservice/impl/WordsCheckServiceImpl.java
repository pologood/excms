package cn.lonsun.webservice.impl;

import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.exception.util.Jacksons;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.site.words.internal.entity.WordsEasyerrConfEO;
import cn.lonsun.site.words.internal.entity.WordsHotConfEO;
import cn.lonsun.site.words.internal.entity.WordsSensitiveConfEO;
import cn.lonsun.monitor.words.internal.util.Type;
import cn.lonsun.monitor.words.internal.util.WordsSplitHolder;
import cn.lonsun.webservice.WordsCheckService;
import cn.lonsun.webservice.to.WebServiceTO;
import cn.lonsun.webservice.utils.WebServiceTOUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author gu.fei
 * @version 2016-09-18 8:54
 */
@Service("wordsCheckService")
public class WordsCheckServiceImpl implements WordsCheckService {

    @Override
    public WebServiceTO getWords(String content,String type,Long siteId) {
        WebServiceTO to = new WebServiceTO();
        try {
            if (StringUtils.isEmpty(content)) {
                throw new BaseRunTimeException(TipsMode.Message.toString(),"content不能为空");
            }

            if (StringUtils.isEmpty(type)) {
                throw new BaseRunTimeException(TipsMode.Message.toString(),"type不能为空");
            }

            if (null == siteId) {
                throw new BaseRunTimeException(TipsMode.Message.toString(),"siteId不能为空");
            }

            String json = null;
            if(type.equals(Type.HOT.toString())) {
                List<WordsHotConfEO> list = WordsSplitHolder.wordsCheck(content,type,siteId);
                json = Jacksons.json().fromObjectToJson(list);
            } else if(type.equals(Type.SENSITIVE.toString())) {
                List<WordsSensitiveConfEO> list = WordsSplitHolder.wordsCheck(content,type,siteId);
                json = Jacksons.json().fromObjectToJson(list);
            } else if(type.equals(Type.EASYERR.toString())) {
                List<WordsEasyerrConfEO> list = WordsSplitHolder.wordsCheck(content,type,siteId);
                json = Jacksons.json().fromObjectToJson(list);
            }
            //将返回内容转换为json串
            to.setJsonData(json);
        } catch (Exception e) {
            WebServiceTOUtil.setErrorInfo(to, e);
        }
        return to;
    }
}
