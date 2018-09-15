package cn.lonsun.monitor.words.internal.util;


import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.monitor.words.internal.cache.EasyerrCache;
import cn.lonsun.monitor.words.internal.cache.SensitiveCache;
import cn.lonsun.monitor.words.internal.entity.WordsEasyerrEO;
import cn.lonsun.monitor.words.internal.entity.WordsSensitiveEO;
import cn.lonsun.monitor.words.internal.service.IWordsEasyerrService;
import cn.lonsun.monitor.words.internal.service.IWordsSensitiveService;
import cn.lonsun.site.words.internal.cache.HotCache;
import cn.lonsun.site.words.internal.entity.WordsHotConfEO;
import cn.lonsun.site.words.internal.service.IWordsHotConfService;
import cn.lonsun.util.LoginPersonUtil;
import net.paoding.analysis.analyzer.PaodingAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WordsSplitHolder {

    private static String DIC_PATH = null;
    private static IWordsHotConfService wordsHotConfService;
    private static IWordsSensitiveService wordsSensitiveService;
    private static IWordsEasyerrService wordsEasyerrService;

    static {
        wordsHotConfService = SpringContextHolder.getBean("wordsHotConfService");
        wordsSensitiveService = SpringContextHolder.getBean("wordsSensitiveService");
        wordsEasyerrService = SpringContextHolder.getBean("wordsEasyerrService");
    }

    public static void init() {
        synHotDic();
        synSensitiveDic();
        synEasyerrDic();
    }

    /**
     * 词检测
     * @param text
     * @param type
     * @param <T>
     * @return
     */
    public static <T> List<T> wordsCheck(String text,String type) {

        List<Object> list = new ArrayList<Object>();
        Analyzer paodingAnalyzer = new PaodingAnalyzer();

        try {
            TokenStream ts = paodingAnalyzer.tokenStream("text", new StringReader(text));
            ts.reset();
            CharTermAttribute offAtt = ts.addAttribute(CharTermAttribute.class);

            if(type.equals(Type.HOT.toString())) {
                while (ts.incrementToken()) {//热词处理
                    Long siteId = getSiteId();
                    String words = offAtt.toString();

                    WordsHotConfEO eo = HotCache.get(siteId + "_" + words);
                    if(AppUtil.isEmpty(eo)) {
                        eo = HotCache.get("-1_" + words);
                    }
                    if (!AppUtil.isEmpty(eo)) {
                        list.add(eo);
                    }

                    Collections.sort(list, new Comparator<Object>() { // 菜单排序
                        public int compare(Object s1, Object s2) {
                            WordsHotConfEO eo1 = (WordsHotConfEO) s1;
                            WordsHotConfEO eo2 = (WordsHotConfEO) s2;
                            return eo2.getHotName().length() - eo1.getHotName().length();
                        }
                    });
                }
            } else if(type.equals(Type.SENSITIVE.toString())) {
                while (ts.incrementToken()) {//敏感词处理
                    Long siteId = getSiteId();
                    String words = offAtt.toString();

                    WordsSensitiveEO eo = SensitiveCache.get(siteId + "_" + words);
                    if(AppUtil.isEmpty(eo)) {
                        eo = SensitiveCache.get("-1_" + words);
                    }
                    if (!AppUtil.isEmpty(eo)) {
                        list.add(eo);
                    }

                    Collections.sort(list, new Comparator<Object>() { // 菜单排序
                        public int compare(Object s1, Object s2) {
                            WordsSensitiveEO eo1 = (WordsSensitiveEO) s1;
                            WordsSensitiveEO eo2 = (WordsSensitiveEO) s2;
                            return eo2.getWords().length() - eo1.getWords().length();
                        }
                    });
                }
            } else if(type.equals(Type.EASYERR.toString())) {
                while (ts.incrementToken()) {//易错词处理
                    Long siteId = getSiteId();
                    String words = offAtt.toString();

                    WordsEasyerrEO eo = EasyerrCache.get(siteId + "_" + words);
                    if(AppUtil.isEmpty(eo)) {
                        eo = EasyerrCache.get("-1_" + words);
                    }
                    if (!AppUtil.isEmpty(eo)) {
                        list.add(eo);
                    }

                    Collections.sort(list, new Comparator<Object>() { // 菜单排序
                        public int compare(Object s1, Object s2) {
                            WordsEasyerrEO eo1 = (WordsEasyerrEO) s1;
                            WordsEasyerrEO eo2 = (WordsEasyerrEO) s2;
                            return eo2.getWords().length() - eo1.getWords().length();
                        }
                    });
                }
            }
            ts.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Object> tempList= new ArrayList<Object>();
        for(Object i:list){ //去重
            if(!tempList.contains(i)){
                tempList.add(i);
            }
        }

        return (List<T>) tempList;
    }

    /**
     * 词检测
     * @param text
     * @param type
     * @param <T>
     * @return
     */
    public static <T> List<T> wordsCheck(String text,String type,Long siteId) {

        List<Object> list = new ArrayList<Object>();
        Analyzer paodingAnalyzer = new PaodingAnalyzer();

        try {
            TokenStream ts = paodingAnalyzer.tokenStream("text", new StringReader(text));
            ts.reset();
            CharTermAttribute offAtt = ts.addAttribute(CharTermAttribute.class);

            if(type.equals(Type.HOT.toString())) {
                while (ts.incrementToken()) {//热词处理
                    String words = offAtt.toString();

                    WordsHotConfEO eo = HotCache.get(siteId + "_" + words);
                    if(AppUtil.isEmpty(eo)) {
                        eo = HotCache.get("-1_" + words);
                    }
                    if (!AppUtil.isEmpty(eo)) {
                        list.add(eo);
                    }

                    Collections.sort(list, new Comparator<Object>() { // 菜单排序
                        public int compare(Object s1, Object s2) {
                            WordsHotConfEO eo1 = (WordsHotConfEO) s1;
                            WordsHotConfEO eo2 = (WordsHotConfEO) s2;
                            return eo2.getHotName().length() - eo1.getHotName().length();
                        }
                    });
                }
            } else if(type.equals(Type.SENSITIVE.toString())) {
                while (ts.incrementToken()) {//敏感词处理
                    String words = offAtt.toString();

                    WordsSensitiveEO eo = SensitiveCache.get(siteId + "_" + words);
                    if(AppUtil.isEmpty(eo)) {
                        eo = SensitiveCache.get("-1_" + words);
                    }
                    if (!AppUtil.isEmpty(eo)) {
                        list.add(eo);
                    }

                    Collections.sort(list, new Comparator<Object>() { // 菜单排序
                        public int compare(Object s1, Object s2) {
                            WordsSensitiveEO eo1 = (WordsSensitiveEO) s1;
                            WordsSensitiveEO eo2 = (WordsSensitiveEO) s2;
                            return eo2.getWords().length() - eo1.getWords().length();
                        }
                    });
                }
            } else if(type.equals(Type.EASYERR.toString())) {
                while (ts.incrementToken()) {//易错词处理
                    String words = offAtt.toString();

                    WordsEasyerrEO eo = EasyerrCache.get(siteId + "_" + words);
                    if(AppUtil.isEmpty(eo)) {
                        eo = EasyerrCache.get("-1_" + words);
                    }
                    if (!AppUtil.isEmpty(eo)) {
                        list.add(eo);
                    }

                    Collections.sort(list, new Comparator<Object>() { // 菜单排序
                        public int compare(Object s1, Object s2) {
                            WordsEasyerrEO eo1 = (WordsEasyerrEO) s1;
                            WordsEasyerrEO eo2 = (WordsEasyerrEO) s2;
                            return eo2.getWords().length() - eo1.getWords().length();
                        }
                    });
                }
            }
            ts.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Object> tempList= new ArrayList<Object>();
        for(Object i:list){ //去重
            if(!tempList.contains(i)){
                tempList.add(i);
            }
        }

        return (List<T>) tempList;
    }

    /**
     * 替换所有
     * @param text
     * @param content
     * @param type
     * @return
     */
    public static String wordsRplc(String text, String content, String type) {

        Analyzer paodingAnalyzer = new PaodingAnalyzer();
        try {
            TokenStream ts = paodingAnalyzer.tokenStream("text", new StringReader(text));
            ts.reset();
            CharTermAttribute offAtt = ts.addAttribute(CharTermAttribute.class);

            if(type.equals(Type.HOT.toString())) {
                while (ts.incrementToken()) {//热词处理
                    Long siteId = getSiteId();
                    String words = offAtt.toString();

                    WordsHotConfEO eo = HotCache.get(siteId + "_" + words);
                    if(AppUtil.isEmpty(eo)) {
                        eo = HotCache.get("-1_" + words);
                    }
                    if (!AppUtil.isEmpty(eo)) {
                        StringBuffer sb = new StringBuffer("<a ");
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
                        content = content.replaceAll(words,sb.toString());
                    }
                }
            } else if(type.equals(Type.SENSITIVE.toString())) {
                while (ts.incrementToken()) {//敏感词处理
                    Long siteId = getSiteId();
                    String words = offAtt.toString();

                    WordsSensitiveEO eo = SensitiveCache.get(siteId + "_" + words);
                    if(AppUtil.isEmpty(eo)) {
                        eo = SensitiveCache.get("-1_" + words);
                    }
                    if (!AppUtil.isEmpty(eo)) {
                        content = content.replaceAll(words,eo.getReplaceWords());
                    }
                }
            } else if(type.equals(Type.EASYERR.toString())) {
                while (ts.incrementToken()) {//易错词处理
                    Long siteId = getSiteId();
                    String words = offAtt.toString();

                    WordsEasyerrEO eo = EasyerrCache.get(siteId + "_" + words);
                    if(AppUtil.isEmpty(eo)) {
                        eo = EasyerrCache.get("-1_" + words);
                    }
                    if (!AppUtil.isEmpty(eo)) {
                        content = content.replaceAll(words,eo.getReplaceWords());
                    }
                }
            }
            ts.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return content;
    }

    /**
     * 替换所有
     * @param text
     * @param type
     * @return
     */
    public static String wordsRplc(String text, String type) {

        String content = new String(text);
        Analyzer paodingAnalyzer = new PaodingAnalyzer();
        try {
            TokenStream ts = paodingAnalyzer.tokenStream("text", new StringReader(text));
            ts.reset();
            CharTermAttribute offAtt = ts.addAttribute(CharTermAttribute.class);

            if(type.equals(Type.HOT.toString())) {
                while (ts.incrementToken()) {//热词处理
                    Long siteId = getSiteId();
                    String words = offAtt.toString();

                    WordsHotConfEO eo = HotCache.get(siteId + "_" + words);
                    if(AppUtil.isEmpty(eo)) {
                        eo = HotCache.get("-1_" + words);
                    }
                    if (!AppUtil.isEmpty(eo)) {
                        StringBuffer sb = new StringBuffer("<a ");
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
                        content = content.replaceAll(words,sb.toString());
                    }
                }
            } else if(type.equals(Type.SENSITIVE.toString())) {
                while (ts.incrementToken()) {//敏感词处理
                    Long siteId = getSiteId();
                    String words = offAtt.toString();

                    WordsSensitiveEO eo = SensitiveCache.get(siteId + "_" + words);
                    if(AppUtil.isEmpty(eo)) {
                        eo = SensitiveCache.get("-1_" + words);
                    }
                    if (!AppUtil.isEmpty(eo)) {
                        content = content.replaceAll(words,eo.getReplaceWords());
                    }
                }
            } else if(type.equals(Type.EASYERR.toString())) {
                while (ts.incrementToken()) {//易错词处理
                    Long siteId = getSiteId();
                    String words = offAtt.toString();

                    WordsEasyerrEO eo = EasyerrCache.get(siteId + "_" + words);
                    if(AppUtil.isEmpty(eo)) {
                        eo = EasyerrCache.get("-1_" + words);
                    }
                    if (!AppUtil.isEmpty(eo)) {
                        content = content.replaceAll(words,eo.getReplaceWords());
                    }
                }
            }
            ts.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return content;
    }

    /**
     * 替换指定词汇
     * @param list
     * @param text
     * @param type
     * @return
     */
    public static String wordsRplc(List<String> list, String text, String type) {

        String content = new String(text);
        if(type.equals(Type.HOT.toString())) {
            for(String key:list) {
                Long siteId = getSiteId();
                WordsHotConfEO eo = HotCache.get(siteId + "_" + key);
                if(AppUtil.isEmpty(eo)) {
                    eo = HotCache.get("-1_" + key);
                }
                if(!AppUtil.isEmpty(eo)) {
                    StringBuffer sb = new StringBuffer("<a ");
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
                    content = content.replaceAll(key,sb.toString());
                }
            }

        } else if(type.equals(Type.SENSITIVE.toString())) {
            for(String key:list) {
                Long siteId = getSiteId();
                WordsSensitiveEO eo = SensitiveCache.get(siteId + "_" + key);
                if(AppUtil.isEmpty(eo)) {
                    eo = SensitiveCache.get("-1_" + key);
                }
                if(!AppUtil.isEmpty(eo)) {
                    content = content.replaceAll(key,eo.getReplaceWords());
                }
            }
        } else if(type.equals(Type.EASYERR.toString())) {
            for(String key:list) {
                Long siteId = getSiteId();
                WordsEasyerrEO eo = EasyerrCache.get(siteId + "_" + key);
                if(AppUtil.isEmpty(eo)) {
                    eo = EasyerrCache.get("-1_" + key);
                }
                if(eo != null) {
                    content = content.replaceAll(key,eo.getReplaceWords());
                }
            }
        }

        return content;
    }

    /**
     * 同步热词到字典库
     */
    public static void synHotDic() {
        List<WordsHotConfEO> list = (List<WordsHotConfEO>) wordsHotConfService.getEOList();
        try {
            FileOutputStream fos = new FileOutputStream(new File(getDicPath() + "hot.dic"));
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            BufferedWriter bw=new BufferedWriter(osw);

            for(WordsHotConfEO eo:list){
                bw.write(eo.getHotName()+"\t\n");
            }

            bw.close();
            osw.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        delCacheDic();
    }

    /*
    * 同步敏感词字典库
    * */
    public static void synSensitiveDic() {
        List<WordsSensitiveEO> list = wordsSensitiveService.getEOs();
        try {
            FileOutputStream fos = new FileOutputStream(new File(getDicPath() + "sensitive.dic"));
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            BufferedWriter bw = new BufferedWriter(osw);

            for(WordsSensitiveEO eo:list){
                bw.write(eo.getWords()+"\t\n");
            }

            bw.close();
            osw.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        delCacheDic();
    }

    /*
    * 同步易错词字典库
    * */
    public static void synEasyerrDic() {
        List<WordsEasyerrEO> list = wordsEasyerrService.getEOs();
        try {
            FileOutputStream fos = new FileOutputStream(new File(getDicPath() + "easyerr.dic"));
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            BufferedWriter bw = new BufferedWriter(osw);

            for(WordsEasyerrEO eo:list){
                bw.write(eo.getWords()+"\t\n");
            }

            bw.close();
            osw.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        delCacheDic();
    }

    /*
    * 删除缓存的dic文件
    * */
    public static void delCacheDic() {
        deleteDirectory(getDicPath() + "/.compiled");
    }

    public static boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    public static boolean deleteDirectory(String sPath) {
        //如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        //如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        //删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            //删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

    public static String getDicPath() {
        String dicPath = DIC_PATH;
        if(AppUtil.isEmpty(dicPath)) {
            Resource fileRource = new ClassPathResource("dic");
            try {
                File file = fileRource.getFile();
                dicPath = file.getPath() + File.separator;
            }catch (Exception e) {

            }
        }
        dicPath = dicPath.replaceAll("\\\\","/");
        return dicPath;
    }

    public static Long getSiteId() {
        return LoginPersonUtil.getSiteId();
    }

    public static void main(String[] args) throws IOException {
//        WordsSplitHolder wsh = new WordsSplitHolder();
        String text = "傻帽少女 添加二货青年工具类 流氓少女时代 注意：以下这些与之前lucene2.x版本不同的地方";
        WordsSplitHolder.wordsCheck(text, Type.SENSITIVE.toString());
    }
}