package cn.lonsun.nlp.utils;

import cn.lonsun.nlp.internal.dao.CLibrary;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: liuk
 * @version: v1.0
 * @date:2018/5/17 15:06
 */
public class NlpirUtil {

    /**
     * 初始化函数，成功则返回1，失败返回0
     * @param sDataPath 初始化路径地址，包括核心词库和配置文件的路径
     * @param encoding UTF-8编码模式，其它的GBK对应0，BIG5对应2，含繁体字的GBK对应3
     * @param sLicenceCode
     * @return
     */
    public static int init(String sDataPath, int encoding, String sLicenceCode){
        int init_flag = CLibrary.Instance.NLPIR_Init(sDataPath, encoding, sLicenceCode);
        // 初始化失败提示
        if (0 == init_flag) {
            System.err.println("初始化失败！原因："+NlpirUtil.getLastErrorMsg());
        }
        return init_flag;
    }

    /**
     * 初始化函数，成功则返回1，失败返回0
     * @return
     */
    public static int init(){
        // 该路径指向Data文件夹（系统核心词库）
        String sDataPath = getResourcePath();
        // UTF-8编码模式，其它的GBK对应0，BIG5对应2，含繁体字的GBK对应3
        int charset_type = 1;
        return init(sDataPath,charset_type,"0");
    }

    /**
     * 退出
     * @return
     */
    public static void exit(){
        CLibrary.Instance.NLPIR_Exit();
    }

    /**
     * 获取错误信息
     * @return
     */
    public static String getLastErrorMsg(){
        return CLibrary.Instance.NLPIR_GetLastErrorMsg();
    }


    /**
     * 提取关键词
     * @param sInput 需要提取关键词的内容
     * @param num 最多选取的关键词个数
     * @param bWeightOut 是否显示关键词的权重值
     * @return
     */
    public static String getKeyWords(String sInput,int num,boolean bWeightOut){
        if(num == 0){
            num = Integer.parseInt(ReadConfigUtil.getValue("word_num"));//默认提取关键词数
        }
        init();
        sInput =  delHTMLTag(sInput);
        String keywords = CLibrary.Instance.NLPIR_GetKeyWords(sInput, num, bWeightOut);
        exit();
        return keywords;
    }

    /**
     * 提取关键词
     * @param sInput 需要提取关键词的内容
     * @param num 最多选取的关键词个数
     * @return
     */
    public static List<String> getKeyWords(String sInput, int num){
        String keywords = getKeyWords(sInput, num, false);
        List<String> keyWordList = new ArrayList<String>();
        if(!StringUtils.isEmpty(keyWordList)){
            keyWordList.addAll(Arrays.asList(keywords.split("#")));
        }
        return keyWordList;
    }


    /**
     * 分词
     * @param sSrc 需要分词的内容
     * @param bPOSTagged 是否带词性 1-是 0-否
     * @return
     */
    public static String paragraphProcess(String sSrc, int bPOSTagged){
        init();
        String result = CLibrary.Instance.NLPIR_ParagraphProcess(sSrc, bPOSTagged);
        exit();
        return result;
    }


    /**
     * 词频分析
     * @param sInput 需要分析的内容
     * @return
     */
    public static String wordFreqStat(String sInput){
        init();
        String result = CLibrary.Instance.NLPIR_WordFreqStat(sInput);//运行词频分析函数
        exit();
        return result;
    }

    /**
     * 词汇数量分析
     * @param sInput 需要分析的内容
     * @return
     */
    public static int getParagraphProcessAWordCount(String sInput){
        init();
        int result = CLibrary.Instance.NLPIR_GetParagraphProcessAWordCount(sInput);
        exit();
        return result;
    }


    /**
     * 获取新词
     * @param sLine 需要提取新词的内容
     * @param nMaxKeyLimit 最多选取的新词个数
     * @param bWeightOut 是否显示关键词的权重值
     * @return
     * @return
     */
    public static String getNewWords(String sLine, int nMaxKeyLimit, boolean bWeightOut){
        init();
        String result = CLibrary.Instance.NLPIR_GetNewWords(sLine, nMaxKeyLimit, bWeightOut);
        exit();
        return result;
    }

    /**
     * 获取当前路径
     * @return
     */
    public static String getResourcePath(){
        String resourcePath = NlpirUtil.class.getClassLoader().getResource("").getPath();
        resourcePath = resourcePath.substring(1,resourcePath.length()-1);
        return resourcePath;
    }


    /**
     * 过滤掉字符串中的html标签、style标签、script标签
     * @param htmlStr
     * @return
     */
    public static String delHTMLTag(String htmlStr){
        if(StringUtils.isEmpty(htmlStr)){
            return htmlStr;
        }
        String regEx_script="<script[^>]*?>[\\s\\S]*?<\\/script>"; //定义script的正则表达式
        String regEx_style="<style[^>]*?>[\\s\\S]*?<\\/style>"; //定义style的正则表达式
        String regEx_html="<[^>]+>"; //定义HTML标签的正则表达式

        Pattern p_script=Pattern.compile(regEx_script,Pattern.CASE_INSENSITIVE);
        Matcher m_script=p_script.matcher(htmlStr);
        htmlStr=m_script.replaceAll(""); //过滤

        Pattern p_style=Pattern.compile(regEx_style,Pattern.CASE_INSENSITIVE);
        Matcher m_style=p_style.matcher(htmlStr);
        htmlStr=m_style.replaceAll(""); //过滤style标签

        Pattern p_html=Pattern.compile(regEx_html,Pattern.CASE_INSENSITIVE);
        Matcher m_html=p_html.matcher(htmlStr);
        htmlStr=m_html.replaceAll(""); //过滤html标签

        htmlStr = htmlStr.replace("&emsp;","").replace("&nbsp;","")
                .replace("\n","").replace("\t","").replace("\r","");

        return htmlStr.trim(); //返回文本字符串
    }


    public static String getEncoding(String str) {
        String encode = "GB2312";
//        try {
//            if (str.equals(new String(str.getBytes(encode), encode))) {
//                String s = encode;
//                return s;
//            }
//        } catch (Exception exception) {
//        }
        encode = "ISO-8859-1";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s1 = encode;
                return s1;
            }
        } catch (Exception exception1) {
        }
        encode = "UTF-8";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s2 = encode;
                return s2;
            }
        } catch (Exception exception2) {
        }
        encode = "GBK";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s3 = encode;
                return s3;
            }
        } catch (Exception exception3) {
        }
        return "";
    }


}
