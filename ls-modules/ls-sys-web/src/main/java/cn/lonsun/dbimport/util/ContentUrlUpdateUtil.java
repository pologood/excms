package cn.lonsun.dbimport.util;

import cn.lonsun.core.exception.BaseRunTimeException;
import com.mongodb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Pattern;

/**
 * 用于更新mongodb中内容的链接
 * Created by liming on 2017-12-25.
 * @author zhongjun
 */
public class ContentUrlUpdateUtil {

    private Logger log = LoggerFactory.getLogger(getClass());
    private MongoClient mongoClient = null;// 建立连接
    private DB get_db_credit = null;

    private DB getDB() {
        if (null == get_db_credit) {
            try {
                mongoClient = new MongoClient("192.168.15.164", 27017);// 本地访问
                get_db_credit = mongoClient.getDB("shuchengex8");// 数据库名
                get_db_credit.authenticate("shuchengex8", "ShuChengEx82017".toCharArray());
            } catch (Exception e) {
                log.error("数据源获取错误！", e.getCause());
                throw new BaseRunTimeException("数据源获取错误！");
            }
        }
        return get_db_credit;
    }

    public void updateContent(String regex, String words, String replaceWords) {
        DBCollection contentMongo = getDB().getCollection("contentMongoEO");
        log.info("数据源链接成功");
        //".*href=\"/UploadFile/*"
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        DBObject ref = new BasicDBObject("content", pattern);
        DBCursor cursor = contentMongo.find(ref);
        List<DBObject> objects = cursor.toArray();

        int i = 0;
        if (objects != null && objects.size() > 0) {
            for (int k = 0; k < objects.size(); k++) {
                DBObject dbObject = objects.get(k);
                DBObject updateObj = new BasicDBObject();
                updateObj.put("_id", dbObject.get("_id"));
                updateObj.put("_class", dbObject.get("_class"));

                String content = dbObject.get("content").toString();
                content = content.replaceAll(words, replaceWords);
                ++i;
                updateObj.put("content", content);
                contentMongo.update(dbObject, updateObj);
                log.info("更新第[{}]条数据", i);
            }
        }
    }

    public static void main(String[] args){
        final ContentUrlUpdateUtil contentUrlUpdateUtil = new ContentUrlUpdateUtil();
//        new Thread(new Callable() {
//            @Override
//            public void run() {
                contentUrlUpdateUtil.updateContent(".*href=\"/UploadFiles/*", "/UploadFiles/","/oldfiles/UploadFiles/");
//            }
//        }).start();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
                contentUrlUpdateUtil.updateContent(".*src=\"/UploadFiles/*", "/UploadFiles/","/oldfiles/UploadFiles/");
//            }
//        }).start();
    }

}
