package cn.lonsun.jdbc;

import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.datasourcemanage.internal.entity.DataimportDataSourceEO;
import com.alibaba.druid.pool.DruidDataSource;
import com.mongodb.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.util.LinkedHashtable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

/**
 * 数据源绑定类
 * @author zhongjun
 */
public class DataSourceUtil {

    protected static Logger log = LoggerFactory.getLogger("dataImport");

    private static final Hashtable<Long, DataSource> dataSourceMap = new LinkedHashtable<Long, DataSource>();

    private static final Hashtable<Long, DB> mongoDbMap = new LinkedHashtable<Long, DB>();

    /**
     * 初始化数据源
     * @param url
     * @param username
     * @param password
     * @return
     */
    private static DruidDataSource initDataSource(String url, String username, String password){
        log.info("初始化导出数据源：url({}),name({}),password({})", url, username, password);
        DruidDataSource dataSource;
        try {
            dataSource = new DruidDataSource();
            dataSource.setUrl(url);
            dataSource.setUsername(username);
            dataSource.setPassword(password);
            dataSource.setMaxActive(256);
            dataSource.setInitialSize(16);
            dataSource.setMaxWait(60000);
            dataSource.setMinIdle(16);
            dataSource.setTimeBetweenEvictionRunsMillis(3000);
            dataSource.setMinEvictableIdleTimeMillis(300000);
            dataSource.setTestWhileIdle(true);
            dataSource.setTestOnBorrow(false);
            dataSource.setTestOnReturn(false);
            log.info("数据源初始化完成：url({}),name({}),password({})", url, username, password);
        } catch (RuntimeException e) {
            log.error("数据源初始化失败：url({}),name({}),password({})", url, username, password);
            throw e;
        }
        return dataSource;
    }

    /**
     * mongodb数据库连接初始化
     * @param address
     * @param username
     * @param password
     * @return
     */
    private static DB initMongoDbClient(String address, String database, String username, String password){
        log.info("初始化导出数据源：address({}),database({}),username({}),password({})", address, database, username, password);
        if(StringUtils.isEmpty(address)){
            throw new BaseRunTimeException("地址未配置，请完善数据源配置！");
        }
        String[] addressList = address.split(";");
        List<ServerAddress> addresses = new ArrayList<ServerAddress>();
        for(String add : addressList){
            try {
                String[] ipPort = address.split(":");
                addresses.add(new ServerAddress(ipPort[0], Integer.valueOf(ipPort[1])));
            } catch (Exception e) {
                e.printStackTrace();
                log.error("mongodb初始化异常：{} -> {}", address, e.getCause());
            }
        }
        if(addresses.isEmpty()){
            throw new BaseRunTimeException("数据库连接失败！");
        }
        MongoCredential credential = MongoCredential.createCredential(username, database, password.toCharArray());

        MongoClient mongoClient = new MongoClient(addresses, Arrays.asList(credential));

        // 大部分用户使用mongodb都在安全内网下，但如果将mongodb设为安全验证模式，就需要在客户端提供用户名和密码：
        // boolean auth = db.authenticate(myUserName, myPassword);
        MongoClientOptions.Builder options = new MongoClientOptions.Builder();
//         options.autoConnectRetry(true);// 自动重连true
//         options.maxAutoConnectRetryTime(10); // the maximum auto connect retry time
        options.connectionsPerHost(500);// 连接池设置为500个连接,默认为100
        options.connectTimeout(60000);// 连接超时，1分钟
        options.maxWaitTime(60000); //
        options.socketTimeout(0);// 套接字超时时间，0无限制
        options.threadsAllowedToBlockForConnectionMultiplier(5000);// 线程队列数，如果连接线程排满了队列就会抛出“Out of semaphores to get db”错误。
        options.writeConcern(WriteConcern.SAFE);//
        options.build();
        DB db = mongoClient.getDB(database);
        return db;
    }

    /**
     * 根据厂商初始化数据源
     * @return
     */
    public static synchronized DataSource getDataSource(DataimportDataSourceEO dataSourceConfig){
        Long dataSourceId = dataSourceConfig.getDataSourceId();
        //从缓存中获取数据源
        if(dataSourceMap.contains(dataSourceId)){
            return dataSourceMap.get(dataSourceId);
        }
        //如果没有初始化，则初始化后加入到缓存
        DataSource datasource = initDataSource(dataSourceConfig.getDatabaseUri(),dataSourceConfig.getUsername(),dataSourceConfig.getPasswd());
        dataSourceMap.put(dataSourceId, datasource);
        return datasource;
    }

    /**
     * 根据厂商初始化数据源
     * @return
     */
    public static synchronized DB getMongoDb(DataimportDataSourceEO dataSourceConfig){
        Long dataSourceId = dataSourceConfig.getDataSourceId();
        //从缓存中获取数据源
        if(mongoDbMap.contains(dataSourceId)){
            return mongoDbMap.get(dataSourceId);
        }
        //如果没有初始化，则初始化后加入到缓存
        DB db = initMongoDbClient(dataSourceConfig.getDatabaseUri(), "",dataSourceConfig.getUsername(),dataSourceConfig.getPasswd());
        mongoDbMap.put(dataSourceId, db);
        return db;
    }

}
