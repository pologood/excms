package cn.lonsun.staticcenter.util;

import cn.edu.hfut.dmic.webcollector.crawler.BasicCrawler;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.ram.RamDB;
import cn.edu.hfut.dmic.webcollector.plugin.ram.RamDBManager;
import cn.edu.hfut.dmic.webcollector.plugin.ram.RamGenerator;
import cn.lonsun.GlobalConfig;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.datacollect.entity.HtmlCollectContentEO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 网页数据采集
 *
 * @author gu.fei
 * @version 2016-1-25 13:52
 */
public class ChangfengCollectUtil extends BasicCrawler {

    private static String file_server;

    private List<HtmlCollectContentEO> contents; //内容配置对象

    private final static String pageRegex = "[*]"; //采集任意规则符号

    private Map<String, Object> map = new ConcurrentHashMap<String, Object>();

    private static GlobalConfig globalConfig = SpringContextHolder.getBean(GlobalConfig.class);

    /**
     * 构造函数初始化数据
     *
     * @param autoParse
     */
    public ChangfengCollectUtil(boolean autoParse) {
        super(autoParse);

        RamDB ramDB = new RamDB();
        this.dbManager = new RamDBManager(ramDB);
        this.generator = new RamGenerator(ramDB);

        /*this.addSeed("http://www.cfzwzx.gov.cn/index.asp");
        this.addSeed("http://www.hefei.gov.cn/zxzx/xwzx/hfyw/index.html");
        this.addSeed("http://zwgk.hefei.gov.cn/zwgk/public/index.xp?doAction=xxlist&unitid=000400010011");*/

        /*不要爬取 jpg|png|gif*/
        this.addRegex("-.*\\.(jpg|png|gif).*");
        /*不要爬取包含 # 的URL*/
        this.addRegex("-.*#.*");
    }

    /**
     * 重写方法实现采集内容处理
     *
     * @param page
     * @param next
     */
    @Override
    public void visit(Page page, CrawlDatums next) {
        page.setHtml(formatRegexStr(page.getHtml()));
        try {
            String hfyw = "", bjgs = "", bjtb = "", xxgk = "";
            String url = page.getCrawlDatum().getUrl();
            //System.out.println(url);
            /*System.out.println(page.getHtml());
            getXXGK(page.getHtml());*/
            if (url.equals("http://www.hefei.gov.cn/zxzx/xwzx/hfyw/index.html")) {
                //合肥要闻
                hfyw = page.regex("<div class=\"content clearfix\"><ul>(.+?)</ul>", 1);
                getHFYW("hfyw", hfyw);
            } else if (url.equals("http://zwgk.hefei.gov.cn/zwgk/public/index.xp?doAction=xxlist&unitid=000400010011")) {
                getXXGK("zxgk", page.getHtml());
            } else if (url.equals("http://zwgk.hefei.gov.cn/zwgk/public/spage.xp?doAction=sxxlist&cid=&type=1&mlid=001200040001&unitid=000400010011")) {
                getXXGK("zfwj", page.getHtml());
            } else if (url.equals("http://zwgk.hefei.gov.cn/zwgk/public/spage.xp?doAction=sxxlist&cid=&type=1&mlid=00120006&unitid=000400010011")) {
                getXXGK("gzbg", page.getHtml());
            } else if (url.equals("http://zwfw.changfeng.gov.cn/index.asp")) {
                //办件公示
                bjgs = page.regex("<marquee[^>]+id=qing[^>]+>(.+?)</marquee>", 1);
                //办件通报
                bjtb = page.regex(formatRegexStr("<img src=\"images/r-bjtb.gif\" width=\"165\" height=\"33\"></td></tr></table><table width=\"100%\"  border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr><td height=\"63\" background=\"images/r-bg.gif\">(.+?)</td></tr></table><table width=\"100%\" height=6 border=\"0\" cellpadding=\"0\" cellspacing=\"0\">"), 1);
                map.put("bjgs", bjgs);
                map.put("bjtb", bjtb);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //返回合肥要闻
    private void getHFYW(String mapType, String html) {

        // 按指定模式在字符串查找
        String pattern = "<a href=\"(.+?)\"[^>]+>(.+?)</a>(.+?)</li>";

        // 创建 Pattern 对象
        Pattern r = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

        // 现在创建 matcher 对象
        Matcher m = r.matcher(html);
        Integer i = 0;
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        while (m.find()) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("title", m.group(2).trim());
            map.put("href", m.group(1).trim());
            map.put("date", m.group(3).trim());
            list.add(map);
        }
        map.put(mapType, list);
    }


    //返回最新信息公开
    private void getXXGK(String mapType, String html) {

        // 按指定模式在字符串查找
        String pattern = "<a[^>]+onmouseover[^>]+href=\\\"(.+?)\\\"[^>]+>(.+?)</a></td><td[^>]+>(.+?)</td>[\\s\\S]+?<td colspan=\"3\" valign=\"top\">(.+?)</td>[\\s\\S]+?<strong>";

        // 创建 Pattern 对象
        Pattern r = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

        // 现在创建 matcher 对象
        Matcher m = r.matcher(html);
        Integer i = 0;
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        while (m.find()) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("shortTitle", m.group(2).trim());
            map.put("title", m.group(4).trim());
            map.put("href", m.group(1).trim());
            map.put("date", m.group(3).trim());
            list.add(map);
        }
        map.put(mapType, list);
    }

    /**
     * 格式化标签内容
     *
     * @param target
     * @return
     */
    public static String formatRegexStr(String target) {
        String reg = ">\\s+([^\\s<]*)\\s+<";
        return target.replace('\n', ' ').replace('\t', ' ').replace('\r', ' ').replaceAll(reg, ">$1<");
    }

    /**
     * 基本功能：过滤指定标签
     *
     * @param str
     * @param tag
     * @return String
     */
    public static String fiterHtmlTag(String str, String tag) {
        String regxp = "<\\s*" + tag + "\\s+([^>]*)\\s*>";
        Pattern pattern = Pattern.compile(regxp);
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        boolean result1 = matcher.find();
        while (result1) {
            matcher.appendReplacement(sb, "");
            result1 = matcher.find();
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }
}
