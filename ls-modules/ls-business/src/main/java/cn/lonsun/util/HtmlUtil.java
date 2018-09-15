package cn.lonsun.util;

import cn.lonsun.common.util.AppUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * @author gu.fei
 * @version 2016-7-1 11:22
 */
public class HtmlUtil {

    public static String getTextFromTHML(String htmlStr) {
        if(AppUtil.isEmpty(htmlStr)) {
            return "";
        }
        Document doc = Jsoup.parse(htmlStr);
        String text = doc.text();
        // remove extra white space
        StringBuilder builder = new StringBuilder(text);
        int index = 0;
        while(builder.length()>index){
            char tmp = builder.charAt(index);
            if(Character.isSpaceChar(tmp) || Character.isWhitespace(tmp)){
                builder.setCharAt(index, ' ');
            }
            index++;
        }
        text = builder.toString().replaceAll(" +", " ").trim();
        return text;
    }


    public static void main(String[] args){
        System.out.println(HtmlUtil.getTextFromTHML(""));
    }
}
