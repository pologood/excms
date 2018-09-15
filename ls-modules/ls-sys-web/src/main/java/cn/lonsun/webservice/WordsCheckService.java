package cn.lonsun.webservice;

import cn.lonsun.webservice.to.WebServiceTO;

/**
 * @author gu.fei
 * @version 2016-09-18 8:52
 */
public interface WordsCheckService {

    /**
     * 获取热词检测结果
     * @param content
     * @return
     */
    WebServiceTO getWords(String content,String type,Long siteId);
}
