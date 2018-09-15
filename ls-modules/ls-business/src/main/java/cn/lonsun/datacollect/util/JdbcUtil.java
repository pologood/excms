package cn.lonsun.datacollect.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author gu.fei
 * @version 2016-2-17 9:29
 */
public class JdbcUtil {

    private static DruidDataSource dataSource;

    static { //初始化数据源参数
    }

    public JdbcUtil(String url,String username,String password) throws Exception {
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
    }

    public void setDbType(String dbType) {
        dataSource.setDbType(dbType);
    }

    public String isConnected() {
        String rst = null;
        try {
            DruidPooledConnection connection = dataSource.getConnection();
            dataSource.validateConnection(connection);
        } catch (SQLException e) {
            rst = e.getMessage();
        }

        return rst;
    }

    public DruidPooledConnection getConnection() throws SQLException {
        DruidPooledConnection connection = dataSource.getConnection();
        dataSource.validateConnection(connection);
        return connection;
    }

    public JdbcTemplate getJdbcTemplate() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate;
    }

    public static void main(String[] args){
        try {
            JdbcUtil util = new JdbcUtil("jdbc:oracle:thin:@192.168.1.206:1521:hfoa","ex8","12345678");
            List<Map<String,Object>> obj = util.getJdbcTemplate().queryForList("select * from CMS_HTML_COLLECT_TASK");
            System.out.println("asda");
        } catch (Exception e) {

        }
    }

    public void close() {
        dataSource.close();
    }
}
