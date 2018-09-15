package cn.lonsun.core.util;


public class CharacterTool {
	public CharacterTool() {
    }

    /**
     * 将字符串中的回车换行等特殊符号转换成html中的转义字符
     * 用于流程批示意见和javascript中使用
     * @param str String
     * @return String
     */
    public static String escapeHTMLTags(String str) {
        StringBuffer stringBuffer = new StringBuffer(str.length() + 6);
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            switch (c) {
            case 60: // '<'
                stringBuffer.append("&lt;");
                break;

            case 62: // '>'
                stringBuffer.append("&gt;");
                break;

            case 13: // '\r'
                stringBuffer.append("<br />");
                break;
            case 10: // '\n'
                stringBuffer.append("<br />");
                break;

            case 39: // '\''
                stringBuffer.append("&acute");
                break;

            case 34: // '"'
                stringBuffer.append("&quot");
                break;
            case 32: // ' '
                stringBuffer.append("&nbsp;");
                break;

            default:
                stringBuffer.append(c);
                break;
            }
        }
        return stringBuffer.toString();
    }
    /***
     * 还原html转义
     * @param str
     * @return
     */
    public static String unescapeHTMLTags(String str) {
    	return str.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&acute", "\'").replaceAll("&quot", "\"").replaceAll("&nbsp;", " ");       
    }
   
    /**
     * html特列殊字符转换成转义字符(不含回车)
     * @param str
     * @return
     */
    public static String escapeHTMLTagsGW(String str) {
           StringBuffer stringBuffer = new StringBuffer(str.length() + 6);
           for (int i = 0; i < str.length(); i++) {
               char c = str.charAt(i);
               switch (c) {
               case 60: // '<'
                   stringBuffer.append("&lt;");
                   break;

               case 62: // '>'
                   stringBuffer.append("&gt;");
                   break;


               case 39: // '\''
                   stringBuffer.append("&acute");
                   break;

               case 34: // '"'
                   stringBuffer.append("&quot");
                   break;
               case 32: // ' '
                   stringBuffer.append("&nbsp;");
                   break;

               default:
                   stringBuffer.append(c);
                   break;
               }
           }
           return stringBuffer.toString();
       }
    /**
     * 转换html回车空格符为转义符
     * @param str
     * @return
     */
    public static String escapeHTMLTagsSimple(String str) {
        StringBuffer stringBuffer = new StringBuffer(str.length() + 6);
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if(c<10)continue;
            switch (c) {
//            case 0: // 空字符
//                break;
//            case 1: // 标题开始
//                break;
//            case 2: //正文开始
//                break;
//            case 3: // 正文结束
//                break;
//            case 4: // 传输结束
//                break;
//            case 5: // 请求
//                break;
//            case 6: // 收到通知
//                break;
//            case 7: // 响铃
//                break;
//            case 8: // 退格
//                break;
//            case 9: // 水平制表符   
//                break;
            case 10: // 换行键
                stringBuffer.append("<br />");
                break;
            case 32: // ' '
                stringBuffer.append("&nbsp;");
                break;
            default:
                stringBuffer.append(c);
                break;
            }
        }
        return stringBuffer.toString();
    }
    public static String escapeTextSimple(String str) {
        StringBuffer stringBuffer = new StringBuffer(str.length() + 6);
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if(c<32)continue;
            switch (c) {
//            case 0: // 空字符
//                break;
//            case 1: // 标题开始
//                break;
//            case 2: //正文开始
//                break;
//            case 3: // 正文结束
//                break;
//            case 4: // 传输结束
//                break;
//            case 5: // 请求
//                break;
//            case 6: // 收到通知
//                break;
//            case 7: // 响铃
//                break;
//            case 8: // 退格
//                break;
//            case 9: // 水平制表符   
//                break;
            case 10: // 换行键
                stringBuffer.append("");
                break;
            case 32: // ' '
                stringBuffer.append("");
                break;
            default:
                stringBuffer.append(c);
                break;
            }
        }
        return stringBuffer.toString();
    }
    
    /**
     * 将字符串中的'"' 前面加上转义符'\'
     * @param str String
     * @return String
     */
    public static String escapeHTMLQuot(String str){
       StringBuffer stringBuffer = new StringBuffer(str.length() + 6);
       for (int i = 0; i < str.length(); i++) {
           char c = str.charAt(i);
           if(c==34)
               stringBuffer.append("\\");
           stringBuffer.append(c);
       }
       return stringBuffer.toString();
    }
    
    public static void main(String[] args) {
		String str = "<br /><br /><br />开始<br />中国<br />";
		
//		StringReader reader = new StringReader(str);
//		BufferedReader reader1 = new BufferedReader(reader);
//		while(reader1.ready()){
//			String line = reader1.readLine();
//			if(null !=line && line.equals("<br>")){
//				
//			}
//		}
		if(str.startsWith("<br />")){
			str = str.replaceFirst("[<br />]+", "");
		}
		System.out.println(str);
		
	}
}
