package cn.lonsun.site.words.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.monitor.words.internal.entity.WordsEasyerrEO;
import cn.lonsun.monitor.words.internal.entity.WordsSensitiveEO;
import cn.lonsun.monitor.words.internal.service.IWordsEasyerrService;
import cn.lonsun.monitor.words.internal.service.IWordsSensitiveService;
import cn.lonsun.site.words.internal.entity.WordsHotConfEO;
import cn.lonsun.site.words.internal.service.IWordsHotConfService;

/**
 * @author gu.fei
 * @version 2015-9-7 10:46
 */
public class KeywordsFilter {

    public static final int minMatchTYpe = 1;      //最小匹配规则
//    public static final int maxMatchType = 2;      //最大匹配规则

    private static Map hotMap;
    private static Map sensitiveMap;
    private static Map easyErrMap;

    private static Map rplcHotMap;
    private static Map rplcSensitiveMap;
    private static Map pplcEasyErrMap;

    private static IWordsHotConfService wordsHotConfService;
    private static IWordsSensitiveService wordsSensitiveService;
    private static IWordsEasyerrService wordsEasyerrService;

    static {
        wordsHotConfService = SpringContextHolder.getBean("wordsHotConfService");
        wordsSensitiveService = SpringContextHolder.getBean("wordsSensitiveService");
        wordsEasyerrService = SpringContextHolder.getBean("wordsEasyerrService");
        init();
    }

    /*
    * 刷新
    * */
    public static void refresh() {
        init();
    }

    /*
    * 初始化Map集合
    * */
    private static void init() {
        addHotWordsToHashMap();
        addSensitiveWordToHashMap();
        addEasyerrWordToHashMap();

        rplcHotObjInit();
        rplcSensitiveObjInit();
        rplcEasyErrObjInit();
    }

    /*
    * 获取关键词
    * */
    private static List<String> getKeywords(String txt , int matchType , int type){
        List<String> keywordsList = new ArrayList<String>();

        for(int i = 0 ; i < txt.length() ; i++){
            int length = checkKeywords(txt, i, matchType, type);    //判断是否包含敏感字符
            if(length > 0){    //存在,加入list中
                keywordsList.add(txt.substring(i, i+length));
                i = i + length - 1;    //减1的原因，是因为for会自增
            }
        }

        Collections.sort(keywordsList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o2.compareTo(o1);
            }
        }); //倒序

        return keywordsList;
    }

    /*
    * 替换关键词
    * */
    private static String replaceKeywords(String txt , int matchType , int type){
        String resultTxt = txt;
        List<String> set = getKeywords(txt, matchType, type);     //获取所有的敏感词
        Iterator<String> iterator = set.iterator();
        String word = null;
        Map rplcmap = null;

        if(type == 0) { // 热词词库
            rplcmap = rplcHotMap;
        } else if(type == 1) { //敏感词库
            rplcmap = rplcSensitiveMap;
        } else { //易错词词库
            rplcmap = pplcEasyErrMap;
        }

        while (iterator.hasNext()) {

            word = iterator.next();

            resultTxt = resultTxt.replaceAll(word,
                    AppUtil.isEmpty(rplcmap.get(word))?"":String.valueOf(rplcmap.get(word))
            );
        }

        return resultTxt;
    }

    /*
    * 检查文本文档中关键词
    * */
    private static int checkKeywords(String txt , int beginIndex , int matchType , int type){
        boolean  flag = false;    //关键词结束标识位：用于敏感词只有1位的情况
        int matchFlag = 0;     //匹配标识数默认为0
        char word = 0;
        Map nowMap = null;

        if(type == 0) { // 热词词库
            nowMap = hotMap;
        } else if(type == 1) { //敏感词库
            nowMap = sensitiveMap;
        } else { //易错词词库
            nowMap = easyErrMap;
        }

        for(int i = beginIndex; i < txt.length() ; i++){
            word = txt.charAt(i);
            nowMap = (Map) nowMap.get(word);     //获取指定key
            if(nowMap != null){     //存在，则判断是否为最后一个
                matchFlag++;     //找到相应key，匹配标识+1
                if("1".equals(nowMap.get("isEnd"))){       //如果为最后一个匹配规则,结束循环，返回匹配标识数
                    flag = true;       //结束标志位为true
                    if(KeywordsFilter.minMatchTYpe == matchType){    //最小规则，直接返回,最大规则还需继续查找
                        break;
                    }
                }
            }
            else{     //不存在，直接返回
                break;
            }
        }
        if(matchFlag < 2 || !flag){        //长度必须大于等于1，为词
            matchFlag = 0;
        }
        return matchFlag;
    }

    /*
    * 添加热词存储
    * 基于DFA算法实现
    * */
    private static void addHotWordsToHashMap() {
        List<WordsHotConfEO> wordList = readHotKeywords();

        // 初始化敏感词容器，减少扩容操作
        hotMap = new HashMap(wordList.size());

        for (WordsHotConfEO word : wordList) {
            Map nowMap = hotMap;
            for (int i = 0; i < word.getHotName().length(); i++) {
                // 转换成char型
                char keyChar = word.getHotName().charAt(i);
                // 获取
                Object tempMap = nowMap.get(keyChar);
                // 如果存在该key，直接赋值
                if (tempMap != null) {
                    nowMap = (Map) tempMap;
                }
                // 不存在则，则构建一个map，同时将isEnd设置为0，因为他不是最后一个
                else {
                    // 设置标志位
                    Map<String, String> newMap = new HashMap<String, String>();
                    newMap.put("isEnd", "0");
                    // 添加到集合
                    nowMap.put(keyChar, newMap);
                    nowMap = newMap;
                }
                // 最后一个
                if (i == word.getHotName().length() - 1) {
                    nowMap.put("isEnd", "1");
                }
            }
        }
    }

    /*
    * 添加敏感词存储
    * 基于DFA算法实现
    * */
    private static void addSensitiveWordToHashMap() {

        List<WordsSensitiveEO> wordList = readSensitiveKeywords();

        // 初始化敏感词容器，减少扩容操作
        sensitiveMap = new HashMap(wordList.size());

        for (WordsSensitiveEO word : wordList) {
            Map nowMap = sensitiveMap;
            for (int i = 0; i < word.getWords().length(); i++) {
                // 转换成char型
                char keyChar = word.getWords().charAt(i);
                // 获取
                Object tempMap = nowMap.get(keyChar);
                // 如果存在该key，直接赋值
                if (tempMap != null) {
                    nowMap = (Map) tempMap;
                }
                // 不存在则，则构建一个map，同时将isEnd设置为0，因为他不是最后一个
                else {
                    // 设置标志位
                    Map<String, String> newMap = new HashMap<String, String>();
                    newMap.put("isEnd", "0");
                    // 添加到集合
                    nowMap.put(keyChar, newMap);
                    nowMap = newMap;
                }
                // 最后一个
                if (i == word.getWords().length() - 1) {
                    nowMap.put("isEnd", "1");
                }
            }
        }
    }

    /*
    * 添加易错词存储
    * 基于DFA算法实现
    * */
    private static void addEasyerrWordToHashMap() {

        List<WordsEasyerrEO> wordList = readEasyerrKeywords();

        // 初始化敏感词容器，减少扩容操作
        easyErrMap = new HashMap(wordList.size());

        for (WordsEasyerrEO word : wordList) {
            Map nowMap = easyErrMap;
            for (int i = 0; i < word.getWords().length(); i++) {
                // 转换成char型
                char keyChar = word.getWords().charAt(i);
                // 获取
                Object tempMap = nowMap.get(keyChar);
                // 如果存在该key，直接赋值
                if (tempMap != null) {
                    nowMap = (Map) tempMap;
                }
                // 不存在则，则构建一个map，同时将isEnd设置为0，因为他不是最后一个
                else {
                    // 设置标志位
                    Map<String, String> newMap = new HashMap<String, String>();
                    newMap.put("isEnd", "0");
                    // 添加到集合
                    nowMap.put(keyChar, newMap);
                    nowMap = newMap;
                }
                // 最后一个
                if (i == word.getWords().length() - 1) {
                    nowMap.put("isEnd", "1");
                }
            }
        }
    }


    private static List<WordsHotConfEO> readHotKeywords() {
        List<WordsHotConfEO> list = wordsHotConfService.getEntities(WordsHotConfEO.class, new HashMap<String, Object>());
        return list;
    }

    private static List<WordsSensitiveEO> readSensitiveKeywords() {
        List<WordsSensitiveEO> list = wordsSensitiveService.getEntities(WordsSensitiveEO.class, new HashMap<String, Object>());
        return list;
    }

    private static List<WordsEasyerrEO> readEasyerrKeywords() {
        List<WordsEasyerrEO> list = wordsEasyerrService.getEntities(WordsEasyerrEO.class, new HashMap<String, Object>());
        return list;
    }

    /*
    * 热词替换词初始化
    * */
    private static void rplcHotObjInit() {
        List<WordsHotConfEO> list = readHotKeywords();
        rplcHotMap = new HashMap(list.size());

        for(WordsHotConfEO eo : list) {
            StringBuilder sb = new StringBuilder("<a ");

            //打开方式
            sb.append(" target='");
            sb.append(eo.getOpenType() == 0 ? "_blank":"_slef");
            sb.append("'");
            //链接地址
            sb.append(" href='");
            sb.append(eo.getHotUrl());
            sb.append("'");
            //链接描述
            sb.append(" title='");
            sb.append(eo.getUrlDesc());
            sb.append("'");

            sb.append(">");
            sb.append(eo.getHotName());
            sb.append("</a>");

            rplcHotMap.put(eo.getHotName(), sb.toString());
        }
    }

    /*
    * 敏感词替换词初始化
    * */
    private static void rplcSensitiveObjInit() {
        List<WordsSensitiveEO> list = readSensitiveKeywords();
        rplcSensitiveMap = new HashMap(list.size());

        for(WordsSensitiveEO eo : list) {
            rplcSensitiveMap.put(eo.getWords(),eo.getReplaceWords());
        }
    }

    /*
    * 易错词替换词初始化
    * */
    private static void rplcEasyErrObjInit() {
        List<WordsEasyerrEO> list = readEasyerrKeywords();
        pplcEasyErrMap = new HashMap(list.size());

        for(WordsEasyerrEO eo : list) {
            pplcEasyErrMap.put(eo.getWords(),eo.getReplaceWords());
        }
    }

    /*
    * 根据条件规律
    * content ： 过滤的文章内容
    * str：过滤类型  例如
    *      str = {0,1,2}：热词、敏感词、易错词
    *      str = {1,2} ：敏感词，易错词
    *      str = {0,1}:热词，敏感词 .....
    * maxMatchType：匹配规则 默认：2
    * */
    public static String doFilterKeywords(String content , int maxMatchType , Integer[] str) {

        String result = content;

        for(int i = 0 ; i < str.length ; i++) {
            result = KeywordsFilter.replaceKeywords(result,AppUtil.isEmpty(maxMatchType)?2:maxMatchType,str[i]);
        }

        return result;
    }

    /*
    * 根据条件规律
    * content ： 过滤的文章内容
    * str：过滤类型  例如
    *      str = {0,1,2}：热词、敏感词、易错词
    *      str = {1,2} ：敏感词，易错词
    *      str = {0,1}:热词，敏感词 .....
    * maxMatchType：匹配规则 默认：2
    * */
    public static String doFilterKeywords(String content , Integer[] str) {
        return doFilterKeywords(content,2,str);
    }

    public static void test() {
        String string = "百度1阿斯太多的阿斯蒂伤感情怀也许只局限于饲养基地 荧幕中的情节，主人公尝试着去用某种方式渐渐的很潇洒地释自杀指南怀那些自己经历的伤感。"
                + "然后法轮功 你好我们的扮演的角色就是跟随着主人公的喜红客联阿斯蒂芬盟 怒哀阿斯蒂芬乐而过于牵强的把自己的情感也附加于阿斯银幕情节中，然后感动就流泪，"
                + "难过就躺在某一个人的怀里尽情的阐述心扉或者手机卡复制器一个人一杯红酒一部电影在夜三级片 深人静的晚上，关上电话静静的发呆着。";
        String str = KeywordsFilter.doFilterKeywords(string, new Integer[] {0});
        System.out.println(str);
    }

}
