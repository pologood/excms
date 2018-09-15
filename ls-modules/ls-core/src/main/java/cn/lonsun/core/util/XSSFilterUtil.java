package cn.lonsun.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author gu.fei
 * @version 2016-08-09 11:44
 */
public class XSSFilterUtil {

    public static String stripXSS(String value) {
        if (null != value && !"".equals(value)) {
            value = value.replaceAll("", "");

            // Avoid anything between script tags
            Pattern scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid anything in a src='...' type of e­xpression
            scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            // Remove any lonesome </script> tag
            scriptPattern = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");

            // Remove any lonesome <script ...> tag
            scriptPattern = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid eval(...) e­xpressions
            scriptPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid e­xpression(...) e­xpressions
            scriptPattern = Pattern.compile("e­xpression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid javascript:... e­xpressions
            scriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid vbscript:... e­xpressions
            scriptPattern = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid onload= e­xpressions
            scriptPattern = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            //
            scriptPattern = Pattern.compile("(\r\n|\r|\n|\n\r)");
            value = scriptPattern.matcher(value).replaceAll("");

            scriptPattern = Pattern.compile("<a href(.*?)>", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");

        }
        return stringFilter(value);
    }

    public static String stripXSSSolr(String value) {
        if (null != value && !"".equals(value)) {
            value = value.replaceAll("", "");

            // Avoid anything between script tags
            Pattern scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid anything in a src='...' type of e­xpression
            scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            // Remove any lonesome </script> tag
            scriptPattern = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");

            // Remove any lonesome <script ...> tag
            scriptPattern = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid eval(...) e­xpressions
            scriptPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid e­xpression(...) e­xpressions
            scriptPattern = Pattern.compile("e­xpression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid javascript:... e­xpressions
            scriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid vbscript:... e­xpressions
            scriptPattern = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid onload= e­xpressions
            scriptPattern = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");
        }
        return value;
    }

    public static String filterSpecialStr(String value) {
        if(null != value && !"".equals(value)) {
            value = value.replaceAll("!","").replaceAll("@","").replaceAll("#","").replaceAll("$","").replaceAll("%","").
                    replaceAll("^","").replaceAll("&","").replace("*","").replace("(","").replace(")","").replaceAll("'","");
        }

        return value;
    }

    // 过滤特殊字符
    public static String stringFilter(String str) {
        // 清除掉所有特殊字符
        try {
            String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(str);
            return m.replaceAll("").trim();
        }catch (Exception e) {
            return str;
        }
    }

    /**
     * sql注入过滤
     * @param str
     * @return
     */
    public static String filterSqlInject(String str) {
        if(null == str || "".equals(str)) {
            return "";
        }
        String str2 = str.toLowerCase();//统一转为小写
        String[] SqlStr1 = {"and","exec","execute","insert","select","delete","update","count","drop","chr","mid","master","truncate","char","declare","sitename","net user","xp_cmdshell","like","and","exec","execute","insert","create","drop","table","from","grant","use","group_concat","column_name","information_schema.columns","table_schema","union","where","select","delete","update","order","by","count","chr","mid","master","truncate","char","declare","or"};//词语
        String[] SqlStr2 = {"/*","'",";","or","-","--","/+","//","/","%","#"};//特殊字符

        for (int i = 0; i < SqlStr1.length; i++) {
            if (str2.indexOf(SqlStr1[i])>=0) {
                str = str.replaceAll("(?i)"+SqlStr1[i],"");//正则替换词语，无视大小写
            }
        }
        for (int i = 0; i < SqlStr2.length; i++) {
            if (str2.indexOf(SqlStr2[i]) >= 0) {
                str = str.replaceAll(SqlStr2[i],"");
            }
        }
        return str;
    }

    /**
     * 删除所有的HTML标签
     *
     * @param source 需要进行除HTML的文本
     * @return
     */
    public static String deleteAllHTMLTag(String source) {

        if(source == null) {
            return "";
        }

        String s = source;
        /** 删除普通标签  */
        s = s.replaceAll("<(S*?)[^>]*>.*?|<.*? />", "");
        /** 删除转义字符 */
        s = s.replaceAll("&.{2,6}?;", "");
        return s;
    }


    public static void main(String[] args) {
        /*Long[] arr = new Long[9];
        for(int i=0 ;i< 9;i++) {
            arr[i] = new Long(i);
        }

        int count = arr.length/10;
        for(int i = 0;i < count ;i++) {
            Long[] newarr = Arrays.copyOfRange(arr,i*10,((i+1)*10));
            for(int j=0;j<newarr.length;j++) {
                System.out.println(newarr[j]);
            }
        }

        Long[] lastarr = Arrays.copyOfRange(arr,count*10,arr.length);

        for(int k=0;k<lastarr.length;k++) {
            System.out.println(lastarr[k]);
        }*/



      /*  Long[] arr1 = Arrays.copyOfRange(arr,0,10);

        for(int i = 0;i< arr1.length;i++) {
            System.out.println(arr1[i]);
        }
*/
//        System.out.println(1120000/10000);
//        String str = "*abc'asdas'";
//        System.out.println(stringFilter(str));
    }
}
