package cn.lonsun.datacollect.util;

import cn.edu.hfut.dmic.webcollector.crawler.BasicCrawler;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.ram.RamDB;
import cn.edu.hfut.dmic.webcollector.plugin.ram.RamDBManager;
import cn.edu.hfut.dmic.webcollector.plugin.ram.RamGenerator;
import cn.lonsun.GlobalConfig;
import cn.lonsun.activemq.MessageSenderUtil;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.datacollect.entity.HtmlCollectContentEO;
import cn.lonsun.datacollect.entity.HtmlCollectDataEO;
import cn.lonsun.datacollect.entity.HtmlCollectTaskEO;
import cn.lonsun.datacollect.service.IHtmlCollectContentService;
import cn.lonsun.datacollect.service.IHtmlCollectDataService;
import cn.lonsun.datacollect.service.IHtmlCollectTaskService;
import cn.lonsun.mongodb.vo.MongoFileVO;
import cn.lonsun.system.filecenter.internal.entity.FileCenterEO;
import cn.lonsun.util.FileUploadUtil;
import cn.lonsun.util.WaterMarkUtil;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 网页数据采集
 * @author gu.fei
 * @version 2016-1-25 13:52
 */
public class CollectCrawler extends BasicCrawler {

    // 日志
    private static final Logger logger = LoggerFactory.getLogger(CollectCrawler.class);

    private static String file_server;

    private static final String[] characters = new String[] {
        "a", "b", "c", "d", "e", "f", "g", "h", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
        "A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
    };

    private HtmlCollectTaskEO task; //任务对象

    private List<HtmlCollectContentEO> contents; //内容配置对象

    private final static String pageRegex = "[*]"; //采集任意规则符号

    private Map<String,Object> map = new HashMap<String, Object>();

    private static IHtmlCollectTaskService htmlCollectTaskService; //采集任务服务类

    private static IHtmlCollectContentService htmlCollectContentService; //采集内容服务类

    private static IHtmlCollectDataService htmlCollectDataService; //采集数据保存服务类

    private static IBaseContentService baseContentService;

    private static GlobalConfig globalConfig = SpringContextHolder.getBean(GlobalConfig.class);

    static {
        //服务接口初始化
        htmlCollectTaskService = SpringContextHolder.getBean("htmlCollectTaskService");
        htmlCollectContentService = SpringContextHolder.getBean("htmlCollectContentService");
        htmlCollectDataService = SpringContextHolder.getBean("htmlCollectDataService");
        baseContentService = SpringContextHolder.getBean("baseContentService");
    }

    /**
     * 构造函数初始化数据
     * @param autoParse
     * @param taskId
     */
    public CollectCrawler(boolean autoParse,Long taskId) {
        super(autoParse);
        RamDB ramDB = new RamDB();
        this.dbManager = new RamDBManager(ramDB);
        this.generator = new RamGenerator(ramDB);
        task = htmlCollectTaskService.getEntity(HtmlCollectTaskEO.class, taskId);
        contents = htmlCollectContentService.getByTaskId(task.getId());

        /*设置执行线程数*/
        this.setThreads(task.getThreads());

        String value = globalConfig.getFileServerNamePath();

        this.file_server = value;
        String defaultUrl = task.getDefaultUrl();
        String regexUrl = task.getRegexUrl();
        String pageType = task.getPageType();

        int pageBeginNumber = null != task.getPageBeginNumber()?task.getPageBeginNumber() : 0;
        int pageEndNumber = null != task.getPageEndNumber()?task.getPageEndNumber() : 0;
        boolean zeroFill = task.getZeroFill() == "true"?true:false;

        String pageBeginChar = task.getPageBeginChar();
        String pageEndChar = task.getPageEndChar();
        String pageList = task.getPageList();

        if(null != defaultUrl) {
            this.addSeed(defaultUrl);
        }

        if(pageType.equals(HtmlCollectTaskEO.pageType.number.toString())) {
            if(pageBeginNumber > pageBeginNumber) {
                int temp = pageEndNumber;
                pageEndNumber = pageBeginNumber;
                pageBeginNumber = temp;
            }
            //数字分页方式
            for(int i = pageBeginNumber;i <= pageEndNumber;i++) {
                this.addSeed(regexUrl.replace(pageRegex,(zeroFill&&(i<10)?"0":"") + i));
            }
        } else if(pageType.equals(HtmlCollectTaskEO.pageType.character.toString())) {
            //字符分页方式
            String scanRegexUrl = "";
            for(int i=0 ; i < characters.length; i++) {
                if(characters[i] == pageBeginChar) {
                    scanRegexUrl = regexUrl.replace(pageRegex,characters[i]);
                } else if(characters[i] == pageEndChar) {
                    scanRegexUrl = regexUrl.replace(pageRegex,characters[i]);
                    this.addSeed(scanRegexUrl);
                    break;
                } else if(characters[i] != pageEndChar) {
                    if(scanRegexUrl == "") {
                        continue;
                    }
                    scanRegexUrl = regexUrl.replace(pageRegex,characters[i]);
                }
                this.addSeed(scanRegexUrl);
            }
        } else if(pageType.equals(HtmlCollectTaskEO.pageType.auto.toString())) {
            //自定义采集页面
            if(null != pageList) {
                List<String> urls = StringUtils.getListWithString(pageList,",");
                for(String url : urls) {
                    this.addSeed(url);
                }
            }
        }

        /*正则规则设置*/
        this.addRegex(task.getRegexHref());

        //过滤地址配置
        List<String> filterUrls = StringUtils.getListWithString(task.getFilterRegexUrl(), ",");
        if(null != filterUrls) {
            for(String str : filterUrls) {
                this.addRegex("-.*" + str.trim());
            }
        }

        /*不要爬取 jpg|png|gif*/
        this.addRegex("-.*\\.(jpg|png|gif).*");
        /*不要爬取包含 # 的URL*/
        this.addRegex("-.*#.*");
    }

    /**
     * 重写方法实现采集内容处理
     * @param page
     * @param next
     */
    @Override
    public void visit(Page page, CrawlDatums next) {
        /*判断是否为新闻页，通过正则可以轻松判断*/
        String function = "collectSuccess";
        String message = "";
        if (page.matchUrl(task.getRegexHref())) {
            /*过滤地址配置*/
            page.setHtml(formatRegexStr(page.getHtml()));
            if(null != contents && !contents.isEmpty()) {
                Map<String,Object> map = new HashMap<String, Object>();
                map.put("URL",page.getUrl());
                map.put("TASK_ID",task.getId());
                message = "开始采集:" + "<u><a href=" + page.getUrl() + " target=_blank>" + page.getUrl() + "</a></u><br>";
                for(HtmlCollectContentEO content : contents) {
                    try {
                        String regex = formatRegexStr(escapeExprSpecialWord(content.getRegexBegin()).trim()) + "(.*?)" + formatRegexStr(escapeExprSpecialWord(content.getRegexEnd())).trim();
                        map.put(content.getColumnName(), AppUtil.isEmpty(filterHtmlTag(page.regex(regex.trim(), 1), content.getRegexFilter()))?
                                content.getDefaultValue():filterHtmlTag(page.regex(regex.trim(), 1), content.getRegexFilter()));
                        message +=  "<font color=green>采集:【" + content.getColumnName() + "】成功!</font><br>";
                    } catch (Exception e) {
                        e.printStackTrace();
                        function = "collectFail";
                        message +=  "<font color=red>采集:【" + content.getColumnName() + "】失败!<br>" + "错误:" + e.getMessage() + "</font><br>";
                    }
                }

                String title = String.valueOf(map.get("TITLE"));
                if(!AppUtil.isEmpty(title) && !"null".equals(title)) {
                    Map<String,Object> cmap = new HashMap<String, Object>();
                    cmap.put("siteId",task.getSiteId());
                    cmap.put("columnId",task.getColumnId());
                    cmap.put("title",title);
                    cmap.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
                    List<BaseContentEO> contents = baseContentService.getEntities(BaseContentEO.class,cmap);
                    List<HtmlCollectDataEO> datas = htmlCollectDataService.getEntityByName(task.getId(),title);

                    if(null != contents && !contents.isEmpty()) {
                        message += "<font color=#FF8C69>已经存在标题为【" + title + "】的新闻!</font><br>";
                    } else if(null != datas && !datas.isEmpty()) {
                        message += "<font color=#FF8C69>已经存在标题为【" + title + "】的新闻采集数据!</font><br>";
                    } else {
                        try {
                            String content = String.valueOf(map.get("CONTENT"));
                            List<String> urls = CatchImage.getImageSrc(content);
                            for(String url : urls) {
                                if(AppUtil.isEmpty(url)) {
                                    continue;
                                }
                                String rplUrl = url;
                                if(!url.contains("http://")) {
                                    String webDomain = task.getWebDomain();
                                    if(url.startsWith("/")) {
                                        if(webDomain != null) {
                                            if(webDomain.endsWith("/")) {
                                                webDomain = webDomain.substring(0,webDomain.length()-1);
                                            }
                                        }
                                        url = webDomain + url;
                                    } else if(url.startsWith("./")) {
                                        String tempurl =  page.getCrawlDatum().getUrl();
                                        if(tempurl != null) {
                                            tempurl = tempurl.substring(0,tempurl.lastIndexOf("/"));
                                        }
                                        url = tempurl + url.substring(1);
                                    } else if(url.startsWith("../")) {
                                        String tempurl =  page.getCrawlDatum().getUrl();
                                        if(tempurl != null) {
                                            tempurl = tempurl.substring(0,tempurl.lastIndexOf("/"));
                                        }
                                        url = tempurl + url.substring(2);
                                    } else {
                                        if(webDomain != null) {
                                            if(!webDomain.endsWith("/")) {
                                                webDomain = webDomain + "/";
                                            }
                                        }
                                        url = webDomain + url;
                                    }
                                }
                                String fileName = url.substring(url.lastIndexOf("/") + 1, url.length());
                                String suffix = fileName.substring(fileName.lastIndexOf(".") + 1,
                                        fileName.length()).toLowerCase();
                                FileCenterEO fileCenterEO = new FileCenterEO();
                                fileCenterEO.setSiteId(task.getSiteId());
                                fileCenterEO.setFileName(fileName.substring(0, fileName.lastIndexOf(".")));
                                fileCenterEO.setType(FileCenterEO.Type.Image.toString());
                                fileCenterEO.setSuffix(suffix);
                                fileCenterEO.setCode(FileCenterEO.Code.CollectPic.toString());
                                fileCenterEO.setStatus(1);
                                InputStream stream = CatchImage.getImageStream(url);
                                byte[] bytes = WaterMarkUtil.createWaterMark(stream, task.getSiteId(),suffix);
                                MongoFileVO vo = FileUploadUtil.uploadUtil(bytes, fileCenterEO);
                                content = content.replaceAll(rplUrl,file_server + vo.getFileName());
                            }
                            map.put("CONTENT", content);
                        } catch (Exception e) {
                            logger.error("采集图片失败", e);
                            Map<String, Object> logMap = new HashMap<String, Object>();
                            logMap.putAll(map);
                            logMap.put("CONTENT", "");
                            logger.info("采集内容：{}", JSONObject.toJSONString(logMap));
                            function = "collectFail";
                            message +=  "<font color=red>采集文章图片失败!<br>" + "错误:" + e.getMessage() + "</font><br>";
                        }
                        try {
                            htmlCollectDataService.saveData(map);
                            if(task.getEmployType().equals(HtmlCollectTaskEO.employType.Auto.toString())) {
                                datas = htmlCollectDataService.getEntityByName(task.getId(),title);
                                if(null != datas) {
                                    Long[] dids = new Long[datas.size()];
                                    for (int i = 0; i < datas.size(); i++) {
                                        dids[i] = datas.get(i).getId();
                                    }

                                    String returnStr = htmlCollectDataService.quoteData(null, null, dids);
                                    if(!StringUtils.isEmpty(returnStr)) {
                                        MessageSenderUtil.publishCopyNews(returnStr);
                                    }
                                }
                                message += "<font color=green>引用【" + title + "】新闻成功!</font><br>";
                            }
                        } catch (Exception e) {
                            logger.error("引用失败", e);
                            Map<String, Object> logMap = new HashMap<String, Object>();
                            logMap.putAll(map);
                            logMap.put("CONTENT", "");
                            logger.info("采集内容：{}", JSONObject.toJSONString(logMap));
                            if(task.getEmployType().equals(HtmlCollectTaskEO.employType.Auto.toString())) {
                                message += "<font color=red>引用【" + title + "】失败，原因：" + e.getMessage() + "</font><br>";
                            }
                        }
                    }
                } else {
                    message += "<font color=red>标题为空数据!</font><br>";
                }
                MessagePush.send(function, message);
            }
        } else {
            try {
                String regexUrl = escapeExprSpecialWord(task.getRegexUrl(),pageRegex).replace(pageRegex, ".*");
                if(page.matchUrl(regexUrl)) {
                    String target_b = task.getTargetBeginDom();
                    String target_e = task.getTargetEndDom();
                    if(!AppUtil.isEmpty(target_b) && !AppUtil.isEmpty(target_e)) {
                        page.setHtml(targetDom(page.getHtml(),target_b,target_e));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                function = "collectFail";
                message +=  "<font color=red>分页地址(匹配失败)：</font><u><a href=" + page.getUrl() + " target=_blank>" + page.getUrl() + "</a><u><br>";
                MessagePush.send(function,message);
            }
        }
    }

    @Override
    public void notFound(Page page, CrawlDatums next){
        MessagePush.send("collectFail","<font color=red>内容地址(访问不到)</font>：<u><a href=" + page.getUrl() + " target=_blank>" + page.getUrl() + "</a><u><br>");
    }

    @Override
    public void fail(Page page, CrawlDatums next) {
        MessagePush.send("collectFail","<font color=red>内容地址(采集失败)</font>：<u><a href=" + page.getUrl() + " target=_blank>" + page.getUrl() + "</a><u><br>");
    }

    public static String escapeExprSpecialWord(String keyword) {
        if (!StringUtils.isEmpty(keyword)) {
            String[] fbsArr = { "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|" };
            for (String key : fbsArr) {
                if (keyword.contains(key)) {
                    keyword = keyword.replace(key, "\\" + key);
                }
            }
        }
        return keyword;
    }

    public static String escapeExprSpecialWord(String keyword,String rpcwds) {
        if (!StringUtils.isEmpty(keyword)) {
            if(keyword.contains(rpcwds)) {
                keyword = keyword.replace(rpcwds,"LONSUN科技");
            }
            String[] fbsArr = { "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|" };
            for (String key : fbsArr) {
                if (keyword.contains(key)) {
                    keyword = keyword.replace(key, "\\" + key);
                }
            }

            keyword = keyword.replace("LONSUN科技",rpcwds);
        }
        return keyword;
    }


    /**
     * 对一级链接做处理
     * @param str
     * @param targetb
     * @param targete
     * @return
     */
    public String targetDom(String str,String targetb,String targete) {
        String reg = formatRegexStr(targetb).trim() + "(.*)" + formatRegexStr(targete).trim();
        reg = formatRegexStr(reg);
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(formatRegexStr(str));
        matcher.find();
        return matcher.group(0);
    }

    /**
     * 格式化标签内容
     * @param target
     * @return
     */
    public static String formatRegexStr(String target) {
        String reg = ">\\s+([^\\s<]*)\\s+<";
        return target.replace("\n", "").replace("\t","").replace("\r", "").replaceAll(reg, ">$1<");
    }

    /**
     * 过滤指定规则字符串
     * @param str
     * @param filters
     * @return
     */
    public static String filterHtmlTag(String str,String filters) {

        if(filters == null) {
            return str;
        }

        List<String> _filters = StringUtils.getListWithString(filters,",");
        if(null != _filters && !_filters.isEmpty()) {
            for(String filter : _filters) {
                filter = formatRegexStr(filter);
                str = str.replaceAll(filter.trim(),"");
            }
        }
        return str;
    }

    /**
     * 基本功能：过滤指定标签
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

    public HtmlCollectTaskEO getTask() {
        return task;
    }

    public void setTask(HtmlCollectTaskEO task) {
        this.task = task;
    }

    public List<HtmlCollectContentEO> getContents() {
        return contents;
    }

    public void setContents(List<HtmlCollectContentEO> contents) {
        this.contents = contents;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }
}
