package cn.lonsun.supervise.errhref.internal.util;

import com.alibaba.fastjson.JSONObject;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author gu.fei
 * @version 2016-4-12 14:02
 */
public class URLHelper {

    private static URL url;
    private static HttpURLConnection con;
    private static int state = -1;

    /**
     * @param urlStr 指定URL网络地址
     * @return URL
     */
    public static synchronized int isConnect(String urlStr) {
        if (urlStr == null || urlStr.length() <= 0) {
            return -1;
        }
        try {
            urlStr = urlStr.replaceAll(" ", "%20");
            if(urlStr.contains("https://")){
                trustEveryone();
            }
            url = new URL(urlStr);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            con.setConnectTimeout(30000);
            con.setReadTimeout(30000);
            state = con.getResponseCode();
        } catch (Exception e) {
            String exceptionName = e.getClass().getSimpleName().toString();
            if("UnknownHostException".equals(exceptionName)){
                state = 404;
            }else if("SSLException".equals(exceptionName)){
                state = 403;
            }else{
                String msg = e.getMessage();
                if(msg.contentEquals("connect timed out")) {
                    state = 408;
                } else {
                    state = 404;
                }
            }

        }
        return state;
    }

    /**
     * 处理https请求被拒的情况
     */
    public static void trustEveryone() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[] { new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            } }, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取网页内容
     * @param urlString
     * @return
     */
    public static String getHtml(String urlString,String charset) {
        try {
            StringBuffer html = new StringBuffer();
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(50000);
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            InputStreamReader isr = new InputStreamReader(conn.getInputStream(),charset);
            BufferedReader br = new BufferedReader(isr);
            String temp;
            while ((temp = br.readLine()) != null) {
                html.append(temp).append("\n");
            }
            br.close();
            isr.close();
            return html.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据链接获取连接名称
     * @param html
     * @param href
     * @param webDomain
     * @return
     */
    public static String getHrefName(String html,String href,String webDomain) {
        String name = null;
        html = formatRegexStr(html);
        if(!html.contains(href)) {
            int x = href.indexOf(webDomain);
            href = href.substring(x + webDomain.length(), href.length());
        }
        String temp = html.substring(html.indexOf(href), html.length());
        name = temp.substring(temp.indexOf(">",1) + 1,temp.indexOf("<",1));
        return name;
    }


    /**
     * 获取A标签值
     * @param url
     * @param href
     * @param webDomain
     * @return
     */
    public static String getATagName(String url,String href,String webDomain,String charset) {
        String html = getHtml(url,charset);
        if(html == null) {
            return null;
        }
        String name = getHrefName(html, href, webDomain);
        return name;
    }

    public static String getBasePathUrl(String url) {
        if(url.startsWith("http://")) {
            String temp  = url.replace("http://","");
            if(temp.contains("/")) {
                temp = temp.substring(0,temp.indexOf("/",1));
            } else if(temp.contains("?")) {
                temp = temp.substring(0,temp.indexOf("?",1));
            }
            return "http://" + temp;
        } else if(url.startsWith("https://")) {
            String temp  = url.replace("https://","");
            if(temp.contains("/")) {
                temp = temp.substring(0,temp.indexOf("/",1));
            } else if(temp.contains("?")) {
                temp = temp.substring(0,temp.indexOf("?",1));
            }
            return "https://" + temp;
        } else {
            if(url.contains("/")) {
                url = url.substring(0,url.indexOf("/",1));
            } else if(url.contains("?")) {
                url = url.substring(0,url.indexOf("?",1));
            }
            return "http://" + url;
        }
    }

    /**
     * 设置提示标签
     * @param html
     * @param href
     * @param webDomain
     * @param hrefId
     * @return
     */
    public static String setTips(String html,String href,String webDomain,String hrefId) {
        String id = " id=\"" + hrefId + "\" ";
        if(!html.contains(href)) {
            int x = href.indexOf(webDomain);
            href = href.substring(x + webDomain.length(), href.length());
        }
        String begin = html.substring(0,html.indexOf(href) + href.length() + 1);
        String end = html.substring(begin.length(),html.length());
        html =  begin + id + end;
        return html;
    }

    /**
     * 给链接添加样式标签
     * @param html
     * @param href
     * @param webDomain
     * @param tag
     * @param css
     * @param style
     * @return
     */
    public static String setDiv(String html,String href,String webDomain,String curUrl,String tag,String css,String style) {
        if(!html.contains(href)) {
            int x = href.indexOf(webDomain);
            href = href.substring(x + webDomain.length(), href.length());
        }
        String begin = html.substring(0,html.indexOf(href));
        String end = html.substring(begin.length(),html.length());
        String endb = end.substring(0,end.indexOf(">",1) + 1);
        String tagName = end.substring(end.indexOf(">",1) + 1,end.indexOf("<", 1));
        String ende = end.substring(end.indexOf("<",1),end.length());
        String errimg = "<img src=\"" + curUrl + "/assets/images/err.png\" border=\"0\" title=\"错误链接\" >";
        String div = "<" + tag + " class=\"" + css + "\" style=\"" + style + "\">" + errimg  + tagName + "</" + tag + ">";
        return begin + endb + div + ende;
    }

    /**
     * 判断是否是邮件地址
     * @param url
     * @return
     */
    public static boolean isEmail(String url) {
        if(url == null) {
            return false;
        } else {
            if(url.contains("mailto:")) {
                return true;
            }
        }
        boolean flag = false;
        Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}");
        Matcher m = p.matcher(url);
        boolean b = m.matches();
        if(b) {
            flag = true;
        }
        return flag;
    }

    /**
     * 格式化标签内容
     * @param html
     * @return
     */
    public static String formatRegexStr(String html) {
        String reg = ">\\s+([^\\s<]*)\\s+<";
        return html.replace('\n', ' ').replace('\t', ' ').replace('\r', ' ').replaceAll(reg, ">$1<");
    }

    public static String getRepcodeDesc(int code) {
        String desc = "错误链接";
        if(code == 400) {
            desc = "错误请求";
        } else if(code == 403) {
            desc = "服务器拒绝请求";
        } else if(code == 404) {
            desc = "服务器找不到请求的网页";
        } else if(code == 405) {
            desc = "禁用请求中指定的方法";
        } else if(code == 406) {
            desc = "无法使用请求的内容特性响应请求的网页";
        } else if(code == 407) {
            desc = "指定请求者应当授权使用代理";
        } else if(code == 408) {
            desc = "服务器等候请求时发生超时";
        } else if(code == 409) {
            desc = "服务器在完成请求时发生冲突";
        } else if(code == 410) {
            desc = "资源已删除";
        } else if(code == 500) {
            desc = "服务器遇到错误，无法完成请求";
        } else if(code == 501) {
            desc = "服务器不具备完成请求的功能";
        } else if(code == 502) {
            desc = "错误网关";
        } else if(code == 503) {
            desc = "服务不可用";
        } else if(code == 504) {
            desc = "网关超时";
        } else if(code == 505) {
            desc = "HTTP 版本不受支持";
        }

        return desc;
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = JSONObject.parseObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
//        String name = getATagName("http://www.ah.gov.cn/UserData/SortHtml/1/85657021202.html",
//                "http://www.ahlz.cn/news/show_view.aspx?typeid=2&second_typeid=0","www.ahhs.gov.cn","gb2312");
//        boolean flag = isEmail("mgszf@126.com");
//        System.out.println(flag);
//        int code = isConnect("http://www.ahshitai.gov.cn/tmp/nav_ldzc.shtml?SS_ID=33&keywords=%E6%9D%8E%E5%86%9B");
//        String name = getBasePathUrl("http://www.ahshitai.gov.cn?temp=1");
//        System.out.println(name);

//        try {
//            JSONObject json = URLHelper.readJsonFromUrl("http://api.map.baidu.com/location/ip?ak=F454f8a5efe5e577997931cc01de3974&ip=202.198.16.3");
//            System.out.println(json.toString());
//            System.out.println(((JSONObject) json.get("content")).get("address"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
