package cn.lonsun.staticcenter.util;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.vo.PostItemVO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 定时抓取指定政府网的数据
 * @author : liuk
 * @date : 2017/08/28
 */
public class CollectUtil {
    private static CollectUtil instance = new CollectUtil();//单例模式
    private Map<String,List<PostItemVO>> dataMap = new HashMap<String,List<PostItemVO>>();
    private Map<String,Long> timeMap = new HashMap<String,Long>();//定时查询

    private CollectUtil() {

    }

    /**
     * 获取实例
     * @author : liuk
     * @date : 2017/01/17
     * @return
     */
    public static synchronized CollectUtil getInstance() {
        return instance;
    }

    /**
     * 从缓存中获取九江市政府新闻数据
     * @author : liuk
     * @date : 2017/08/28
     * @param url
     * @return
     */
    public List<PostItemVO> getShizfxxData(String url){

        if(dataMap.get(url)==null){//第一次查询数据，放入dataMap中
            queryShizfxxData(url);
            //刷新定时时间
            timeMap.put(url, System.currentTimeMillis() / 1000);
        }else{
            if (timeMap.get(url) == null) {
                //定时刷新缓存
                queryShizfxxData(url);
                timeMap.put(url, System.currentTimeMillis() / 1000);
            } else {
                Long e = System.currentTimeMillis() / 1000;
                //2 小时更新一次
                if (e - timeMap.get(url) > 7200) {
                    //定时刷新缓存
                    queryShizfxxData(url);
                    timeMap.put(url,e);
                }
            }
        }
        return dataMap.get(url);
    }

    /**
     * 从缓存中获取江西省政府新闻数据
     * @author : liuk
     * @date : 2017/01/17
     * @param url
     * @return
     */
    public List<PostItemVO> getSzfxxData(String url){

        if(dataMap.get(url)==null){//第一次查询数据，放入dataMap中
            querySzfxxData(url);
            //刷新定时时间
            timeMap.put(url, System.currentTimeMillis() / 1000);
        }else{
            if (timeMap.get(url) == null) {
                //定时刷新缓存
                querySzfxxData(url);
                timeMap.put(url, System.currentTimeMillis() / 1000);
            } else {
                Long e = System.currentTimeMillis() / 1000;
                //2 小时更新一次
                if (e - timeMap.get(url) > 7200) {
                    //定时刷新缓存
                    querySzfxxData(url);
                    timeMap.put(url,e);
                }
            }
        }
        return dataMap.get(url);
    }


    /**
     * 从缓存中获取国务院数据
     * @author : liuk
     * @date : 2017/01/17
     * @param url
     * @return
     */
    public List<PostItemVO> getGwyxxData(String url){

        if(dataMap.get(url)==null){//第一次查询数据，放入dataMap中
            queryGwyxxData(url);
            //刷新定时时间
            timeMap.put(url, System.currentTimeMillis() / 1000);
        }else{
            if (timeMap.get(url) == null) {
                //定时刷新缓存
                queryGwyxxData(url);
                timeMap.put(url, System.currentTimeMillis() / 1000);
            } else {
                Long e = System.currentTimeMillis() / 1000;
                //2 小时更新一次
                if (e - timeMap.get(url) > 7200) {
                    //定时刷新缓存
                    queryGwyxxData(url);
                    timeMap.put(url,e);
                }
            }
        }
        return dataMap.get(url);
    }

    /**
     * 从缓存中获取安徽省政府文件
     * @author : zhusy
     * @date : 2018/08/01
     * @param url
     * @return
     */
    public List<PostItemVO> getAHSZFWJData(String url){

        if(dataMap.get(url)==null){//第一次查询数据，放入dataMap中
            queryAHSZFWJData(url);
            //刷新定时时间
            timeMap.put(url, System.currentTimeMillis() / 1000);
        }else{
            if (timeMap.get(url) == null) {
                //定时刷新缓存
                queryAHSZFWJData(url);
                timeMap.put(url, System.currentTimeMillis() / 1000);
            } else {
                Long e = System.currentTimeMillis() / 1000;
                //2 小时更新一次
                if (e - timeMap.get(url) > 7200) {
                    //定时刷新缓存
                    queryAHSZFWJData(url);
                    timeMap.put(url,e);
                }
            }
        }
        return dataMap.get(url);
    }

    /**
     * 从缓存中获取国务院文件数据
     * @author : liuk
     * @date : 2017/07/19
     * @param url
     * @return
     */
    public List<PostItemVO> getGwywjData(String url){

        if(dataMap.get(url)==null){//第一次查询数据，放入dataMap中
            queryGwywjData(url);
            //刷新定时时间
            timeMap.put(url,System.currentTimeMillis() / 1000);
        }else{
            if (timeMap.get(url) == null) {
                //定时刷新缓存
                queryGwywjData(url);
                timeMap.put(url,System.currentTimeMillis() / 1000);
            } else {
                Long e = System.currentTimeMillis() / 1000;
                //2 小时更新一次
                if (e - timeMap.get(url) > 7200) {
                    //定时刷新缓存
                    queryGwywjData(url);
                    timeMap.put(url,e);
                }
            }
        }
        return dataMap.get(url);
    }



    /**
     * 抓取九江市政府新闻数据
     * @author : liuk
     * @date : 2017/08/28
     * @param url
     */
    private void queryShizfxxData(String url){
        List<PostItemVO> list = new ArrayList<PostItemVO>();
        Document doc = null;
        String content="";
        if(!AppUtil.isEmpty(url)){
            URL urlfile = null;
            try {
                urlfile = new URL(url);
                BufferedReader in = new BufferedReader(new InputStreamReader(urlfile.openStream()));
                String inputLine =in.readLine();
                while(inputLine!=null){
                    content +=inputLine;
                    inputLine =in.readLine();
                }
//                if(content!=null){
//                    content = content.replace("&lt;","<").replace("&gt;",">")
//                            .replace("&quot;","\"").replace("&amp;","&")
//                            .replace("<![CDATA[","").replace("]]>","");
//                }
//                System.out.println(content);
                in.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            doc = Jsoup.parse(content);

            Elements datas = doc.getElementsByTag("div");

            for (Element data : datas) {
                if(data.hasClass("pageBox")){
                    Elements itemBodys = data.getElementsByTag("li");
                    if(itemBodys.size()>0){
                        for (Element itemBody : itemBodys) {
                            Elements itemBodys1 = itemBody.getElementsByTag("a");
                            Elements itemBodys2 = itemBody.getElementsByTag("p");

                            PostItemVO item = new PostItemVO();
                            String title = itemBodys1.get(0).html().replace("·","").replace(" ","");
                            String href = itemBodys1.get(0).attr("href").replace("./",url+"/");;
                            String date = itemBodys2.size()>0?itemBodys2.get(0).html():"";

                            item.setInfoTitle(title);
                            item.setInfoDate(date);
                            item.setInfoUrl(href);
                            item.setImageLink("");
                            list.add(item);
                        }
                    }
                }
            }
            dataMap.put(url,list);
        }
    }

    /**
     * 抓取江西省政府新闻数据
     * @author : liuk
     * @date : 2017/01/17
     * @param url
     */
    private void querySzfxxData(String url){
        List<PostItemVO> list = new ArrayList<PostItemVO>();
        Document doc = null;
        String content="";
        if(!AppUtil.isEmpty(url)){
            URL urlfile = null;
            try {
                urlfile = new URL(url);
                BufferedReader in = new BufferedReader(new InputStreamReader(urlfile.openStream()));
                String inputLine =in.readLine();
                while(inputLine!=null){
                    content +=inputLine;
                    inputLine =in.readLine();
                }
                in.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            doc = Jsoup.parse(content);

            Elements datas = doc.getElementsByTag("div");
            for (Element data : datas) {
                if(data.hasClass("columnZXBM2")){
                    Elements itemBodys = data.getElementsByTag("a");
                    if(itemBodys.size()>0){
                        for (Element itemBody : itemBodys) {
//                            Elements itemBodys1 = itemBody.getElementsByTag("a");
//                            Elements itemBodys2 = itemBody.getElementsByTag("span");

                            PostItemVO item = new PostItemVO();
                            String title = itemBody.attr("title");
                            String href = itemBody.attr("href").replace("./",url+"/");
//                            String date = itemBodys2.size()>0?itemBodys2.get(0).html():"";

                            item.setInfoTitle(title);
                            item.setInfoDate("");
                            item.setInfoUrl(href);
                            item.setImageLink("");
                            list.add(item);
                        }
                    }
                }
            }
            dataMap.put(url,list);
        }
    }

    /**
     * 抓取国务院数据
     * @author : liuk
     * @date : 2017/06/05
     * @param url
     */
    private void queryGwyxxData(String url){
        List<PostItemVO> list = new ArrayList<PostItemVO>();
        Document doc = null;
        String content="";
        if(!AppUtil.isEmpty(url)){
            URL urlfile = null;
            try {
                urlfile = new URL(url);
                BufferedReader in = new BufferedReader(new InputStreamReader(urlfile.openStream()));
                String inputLine =in.readLine();
                while(inputLine!=null){
                    content +=inputLine;
                    inputLine =in.readLine();
                }
                in.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            doc = Jsoup.parse(content);

            Elements datas = doc.getElementsByTag("ul");
            for (Element data : datas) {
                Elements itemBodys = data.getElementsByTag("li");
                if(itemBodys.size()>0){
                    for (Element itemBody : itemBodys) {
                        Elements itemBodys1 = itemBody.getElementsByTag("a");
                        Elements itemBodys2 = itemBody.getElementsByTag("span");

                        PostItemVO item = new PostItemVO();
                        String title = itemBodys1.get(0).html();
                        String href = "http://www.gov.cn"+itemBodys1.get(0).attr("href");
//                        String date = itemBodys2.size()>0?itemBodys2.get(0).html():"";

                        item.setInfoTitle(title);
                        item.setInfoDate("");
                        item.setInfoUrl(href);
                        item.setImageLink("");
                        list.add(item);
                    }
                }
            }
            dataMap.put(url,list);
        }
    }
    /**
     * 安徽省政府文件
     * @author : liuk
     * @date : 2017/06/05
     * @param url
     */
    private void queryAHSZFWJData(String url){
        List<PostItemVO> list = new ArrayList<PostItemVO>();
        Document doc = null;
        String content="";
        if(!AppUtil.isEmpty(url)){
            URL urlfile = null;
            try {
                urlfile = new URL(url);
                BufferedReader in = new BufferedReader(new InputStreamReader(urlfile.openStream(),"gb2312"));
                String inputLine =in.readLine();
                while(inputLine!=null){
                    content +=inputLine;
                    inputLine =in.readLine();
                }
                in.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            doc = Jsoup.parse(content);
            String urlPrefix = url.replace("http://","");
            urlPrefix = "http://"+urlPrefix.substring(0,urlPrefix.indexOf("/"));
            Elements datas = doc.getElementsByTag("div");
            for (Element data : datas) {
                if(data.hasClass("xxgk_lb")){
                    Elements tdItemBodys = data.getElementsByTag("td");
                    if(tdItemBodys.size()>0){
                        for (Element itemBody : tdItemBodys) {
                            Elements aItemBodys = itemBody.getElementsByTag("a");
                            if(null != aItemBodys && aItemBodys.size() >0){
                                Element aItemBody = aItemBodys.get(0);
                                PostItemVO item = new PostItemVO();
                                String title = aItemBody.text();
                                String href = aItemBody.attr("href").replace("./",url+"/");
                                item.setInfoTitle(title);
                                item.setInfoDate("");
                                item.setInfoUrl(urlPrefix+href);
                                item.setImageLink("");
                                list.add(item);
                            }
                        }
                    }
                }
            }
            dataMap.put(url,list);
        }
    }

    /**
     * 抓取国务院文件信息
     * @author : liuk
     * @date : 2017/07/19
     * @param url
     */
    private void queryGwywjData(String url){
        List list = new ArrayList();
        Document doc = null;
        if (!AppUtil.isEmpty(url)) {
            try {
                doc = Jsoup.connect(url).timeout(10000).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Elements datas = doc.getElementsByTag("table");
            for (Element data : datas) {
                if (data.hasClass("dataList")) {
                    Elements itemBodys1 = data.getElementsByTag("tr");
                    for (Element itemBody : itemBodys1) {
                        Elements itemBodys2 = itemBody.getElementsByTag("td");
                        if(itemBodys2.size()>0){
                            PostItemVO item = new PostItemVO();
                            String title = itemBodys2.get(1).getElementsByTag("a").get(0).html();
                            String href = itemBodys2.get(1).getElementsByTag("a").get(0).attr("href");
                            String date = itemBodys2.get(4).html().replace("年","-").replace("月","-").replace("日","").trim();

                            if(!AppUtil.isEmpty(href) && !href.contains("http")){
                                href = href.replace("./",url);
                            }

                            item.setInfoTitle(title);
                            item.setInfoDate(date);
                            item.setInfoUrl(href);
                            list.add(item);
                        }
                    }

                }
            }
            dataMap.put(url, list);
        }
    }



}
