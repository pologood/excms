package cn.lonsun.monitor.task.util;

import cn.lonsun.monitor.task.internal.entity.MonitorSeriousErrorResultEO;
import cn.lonsun.monitor.words.internal.cache.EasyerrCache;
import cn.lonsun.monitor.words.internal.cache.SensitiveCache;
import cn.lonsun.monitor.words.internal.entity.WordsEasyerrEO;
import cn.lonsun.monitor.words.internal.entity.WordsSensitiveEO;
import cn.lonsun.monitor.words.internal.util.Type;
import net.paoding.analysis.analyzer.PaodingAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.StringReader;
import java.util.*;

/**
 * @author gu.fei
 * @version 2017-10-10 14:34
 */
public class WordsCheckUtils {

    /**
     * 严重错误检测
     * @param taskId
     * @param text
     * @param siteId
     * @return
     */
    public static void wordsCheck(Long taskId,Long contentId,Long columnId,Long siteId,String text,
                               String fromCode,String title,String typeCode,List<MonitorSeriousErrorResultEO> errors,String domain) {
        Analyzer paodingAnalyzer = new PaodingAnalyzer();
        Map<String,Object> map = new HashMap<String, Object>();
        try {
            TokenStream ts = paodingAnalyzer.tokenStream("text", new StringReader(text));
            ts.reset();
            CharTermAttribute offAtt = ts.addAttribute(CharTermAttribute.class);
            while (ts.incrementToken()) {
                String words = offAtt.toString();
                WordsSensitiveEO sensitive = SensitiveCache.get(siteId + "_" + words);
                if (sensitive == null) sensitive = SensitiveCache.get("-1_" + words);
                if(null != sensitive && null != sensitive.getSeriousErr() && sensitive.getSeriousErr() == 0) {
                    MonitorSeriousErrorResultEO error = new MonitorSeriousErrorResultEO();
                    error.setTaskId(taskId);
                    error.setTitle(title);
                    error.setTypeCode(typeCode);
                    error.setMonitorDate(new Date());
                    error.setContentId(contentId);
                    error.setWord(words);
                    error.setCheckType(Type.SENSITIVE.toString());
                    error.setFromCode(fromCode);
                    error.setResult("词汇[" + words + "]为敏感词");
                    error.setDomain(domain);
                    error.setColumnId(columnId);
                    if(!map.containsKey(Type.SENSITIVE.toString() + words)) {
                        errors.add(error);
                        map.put(Type.SENSITIVE.toString() + words,true);
                    }
                }

                WordsEasyerrEO easyerr = EasyerrCache.get(siteId + "_" + words);
                if (easyerr == null) easyerr = EasyerrCache.get("-1_" + words);
                if(null != easyerr && null != easyerr.getSeriousErr() && easyerr.getSeriousErr() == 0) {
                    MonitorSeriousErrorResultEO error = new MonitorSeriousErrorResultEO();
                    error.setTaskId(taskId);
                    error.setTitle(title);
                    error.setTypeCode(typeCode);
                    error.setMonitorDate(new Date());
                    error.setContentId(contentId);
                    error.setWord(words);
                    error.setCheckType(Type.EASYERR.toString());
                    error.setFromCode(fromCode);
                    error.setResult("词汇[" + words + "]为易错词");
                    error.setDomain(domain);
                    error.setColumnId(columnId);
                    if(!map.containsKey(Type.EASYERR.toString() + words)) {
                        errors.add(error);
                        map.put(Type.EASYERR.toString() + words,true);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
