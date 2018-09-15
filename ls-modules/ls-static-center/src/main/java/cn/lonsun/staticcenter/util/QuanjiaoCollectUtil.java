package cn.lonsun.staticcenter.util;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.vo.PostItemVO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 定时抓取全椒县信息公开网的数据
 * @author : liuk
 * @date : 2016/10/21
 */
public class QuanjiaoCollectUtil {
    private static QuanjiaoCollectUtil instance = new QuanjiaoCollectUtil();//单例模式
    private Map<String,List<PostItemVO>> dataMap = new HashMap<String,List<PostItemVO>>();
    private Map<String,Long> timeMap = new HashMap<String,Long>();//定时查询

    private QuanjiaoCollectUtil() {

    }

    /**
     * 获取实例
     * @author : liuk
     * @date : 2016/10/21
     * @return
     */
    public static synchronized QuanjiaoCollectUtil getInstance() {
        return instance;
    }

    /**
     * 从缓存中获取数据
     * @author : liuk
     * @date : 2016/10/21
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
     * @date : 2016/10/21
     * @param url
     */
    private void queryData(String url){
        List<PostItemVO> list = new ArrayList<PostItemVO>();
        Document doc = null;
        if(!AppUtil.isEmpty(url)){
            try {
                doc = Jsoup.connect(url).timeout(10000).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Elements datas = doc.getElementsByTag("tr");
            for (Element data : datas) {
                Elements itemBodys1 = data.getElementsByTag("a");
                Elements itemBodys2 = data.getElementsByTag("span");
                if(itemBodys1.size()>0){
                    PostItemVO item = new PostItemVO();
                    String title = itemBodys1.get(0).html();
                    String href = itemBodys1.get(0).attr("href");
                    String date = itemBodys2.get(0).html().replace("[","").replace("]","");

                    item.setInfoTitle(title);
                    item.setInfoDate(date);
                    item.setInfoUrl("http://zwgk.quanjiao.gov.cn"+href);
                    list.add(item);
                }
            }
            dataMap.put(url,list);
        }
    }


}
