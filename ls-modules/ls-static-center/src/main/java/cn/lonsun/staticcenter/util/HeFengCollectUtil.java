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
 * 定时抓取鹤峰新闻网的数据
 * @author : liuk
 * @date : 2017/01/17
 */
public class HeFengCollectUtil {
    private static HeFengCollectUtil instance = new HeFengCollectUtil();//单例模式
    private Map<String,List<PostItemVO>> dataMap = new HashMap<String,List<PostItemVO>>();
    private Map<String,Long> timeMap = new HashMap<String,Long>();//定时查询

    private HeFengCollectUtil() {

    }

    /**
     * 获取实例
     * @author : liuk
     * @date : 2017/01/17
     * @return
     */
    public static synchronized HeFengCollectUtil getInstance() {
        return instance;
    }

    /**
     * 从缓存中获取数据
     * @author : liuk
     * @date : 2017/01/17
     * @param url
     * @return
     */
    public List<PostItemVO> getData(String url){

        if(dataMap.get(url)==null){//第一次查询数据，放入dataMap中
            queryData(url);
            //刷新定时时间
            timeMap.put(url,System.currentTimeMillis() / 1000);
        }else{
            if (timeMap.get(url) == null) {
                //定时刷新缓存
                queryData(url);
                timeMap.put(url,System.currentTimeMillis() / 1000);
            } else {
                Long e = System.currentTimeMillis() / 1000;
                //2 小时更新一次
                if (e - timeMap.get(url) > 7200) {
                    //定时刷新缓存
                    queryData(url);
                    timeMap.put(url,e);
                }
            }
        }
        return dataMap.get(url);
    }


    /**
     * 抓取数据
     * @author : liuk
     * @date : 2017/01/17
     * @param url
     */
    private void queryData(String url){
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
                System.out.println(content);
                in.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            doc = Jsoup.parse(content);

            Elements datas = doc.getElementsByTag("li");
            for (Element data : datas) {
                Elements itemBodys1 = data.getElementsByTag("a");
                Elements itemBodys2 = data.getElementsByTag("span");
                Elements itemBodys3 = data.getElementsByTag("img");
                if(itemBodys1.size()>0){
                    PostItemVO item = new PostItemVO();
                    String title = itemBodys1.get(itemBodys1.size()-1).html();
                    String href = itemBodys1.get(0).attr("href");
                    String date = itemBodys2.size()>0?itemBodys2.get(0).html():"";
                    String imageLink = itemBodys3.size()>0?itemBodys3.get(0).attr("src"):"";

                    item.setInfoTitle(title);
                    item.setInfoDate(date);
                    item.setInfoUrl(href);
                    item.setImageLink(imageLink);
                    list.add(item);
                }
            }
            dataMap.put(url,list);
        }
    }


}
