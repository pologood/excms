package cn.lonsun.jdbc;

import com.mongodb.DB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * mangoDb 数据源
 * @author zhongjun
 */
public abstract class MonGoAble {

    protected final Logger log = LoggerFactory.getLogger("dataImport");

    private DB mongoDb;

    public DB getMongoDb() {
        return mongoDb;
    }

    public void setMongoDb(DB mongoDb) {
        this.mongoDb = mongoDb;
    }

}
