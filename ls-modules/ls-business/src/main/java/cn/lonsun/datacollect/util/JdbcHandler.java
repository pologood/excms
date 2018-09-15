package cn.lonsun.datacollect.util;

import java.sql.*;

/**
 * @author : Administrator
 * @date : 2016/9/28
 */
public class JdbcHandler {


    static {
        try {
            // 之所以要使用下面这条语句，是因为要使用MySQL的驱动，所以我们要把它驱动起来，
            // 可以通过Class.forName把它加载进去，也可以通过初始化来驱动起来，下面三种形式都可以
            Class.forName("com.mysql.jdbc.Driver");// 动态加载mysql驱动
            System.out.println("成功加载mysql驱动程序");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public  static Connection getConnection(String url,String username,String password ){

        // 一个Connection代表一个数据库连接
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url,username,password);
            System.out.println("获取数据库连接成功");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }


    public static void close(ResultSet resultSet, Statement statement,Connection connection){
        try {
            if (resultSet != null && !resultSet.isClosed()) {
                    resultSet.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        try {
            if (statement != null && !statement.isClosed()) {
                statement.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        try {
            if (connection != null && !connection.isClosed()) {
                    connection.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("连接关闭成功");
    }
}
