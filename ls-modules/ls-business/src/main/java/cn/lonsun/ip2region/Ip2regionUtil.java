package cn.lonsun.ip2region;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangchao on 2016/9/2.
 */
public class Ip2regionUtil {

    //查詢對象
    private static DbSearcher searcher = null;

    private static final Logger logger = LoggerFactory.getLogger(Ip2regionUtil.class);

//    static{
//        try {
//            DbConfig config = new DbConfig();
//            searcher = new DbSearcher(config, getPath());
//        }catch (Exception e){
//
//        }
//    }

    /**
     * 獲取ip庫路徑
     * @return
     */
    public static final String getPath(){
        String file = Thread.currentThread().getContextClassLoader().
                getResource("ip2region.db").toString();
        logger.debug("file:"+file);
        logger.debug("file:"+file.substring(file.indexOf("/")+1,file.length()));
        return file.substring(file.indexOf("/")+1,file.length());
    }

    /**
     * 獲取查詢對象
     * @return
     */
    public static final DbSearcher dbSearcher(){
        if(searcher == null){
            try {
                DbConfig config = new DbConfig();
                searcher = new DbSearcher(config, getPath());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return searcher;
    }


    /**
     * 使用btree算法查询  推荐
     * @return
     */
    public static DataBlock btreeSearch(String ip){
        DataBlock  db = null;
        try {
            db = searcher.btreeSearch(ip);
        }catch (Exception e){
            e.printStackTrace();
        }
        return db;
    }

    /**
     * 使用binary算法查询
     * @return
     */
    public static DataBlock binarySearch(String ip){
        DataBlock  db = null;
        try {
            db = dbSearcher().binarySearch(ip);
        }catch (Exception e){
            e.printStackTrace();
        }
        return db;
    }

    /**
     * 使用内存算法查询
     * @return
     */
    public static DataBlock memorySearch(String ip){
        DataBlock  db = null;
        try {
            db = searcher.memorySearch(ip);
        }catch (Exception e){
            e.printStackTrace();
        }
        return db;
    }
}
