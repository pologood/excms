package cn.lonsun.developer;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * 开发模板<br/>
 * Jdbc操作工具类
 * @Author taodp
 * @Time 2014年8月16日 上午11:51:02
 */
public class JdbcUtil {
    private String driver = "oracle.jdbc.driver.OracleDriver";
    private String dbURL = "jdbc:oracle:thin:@192.168.1.206:1521:hfoa";
    private String user = "ex8test";
    private String password = "12345678";


    private Connection  conn;
    private Statement stmt;
    public JdbcUtil(){
        
    }
    
    public JdbcUtil(String driver,String dbURL,String user,String password){
        this.driver = driver;
        this.dbURL = dbURL;
        this.user = user;
        this.password = password;
    }
    /*
     * 打开db链接
     */
    public void openConnection(){
        try {
            Class.forName(driver);
            if ((user==null) && (password==null))
            {
              conn = DriverManager.getConnection(dbURL);
            }
            else
            {
              conn = DriverManager.getConnection(dbURL,user,password);
            }
            stmt = conn.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
    }
    /**
     * 关闭db链接
     */
    public void closeConnection()
    {
     try
     {
       if(null !=stmt)
       stmt.close();
       if(null !=conn)
       conn.close();
     }
     catch(Exception e)
     {
         e.printStackTrace();
     }
    }
    /**
     * db查询
     * @param SqlString
     * @return
     */
    public ResultSet executeQuery(String SqlString)
    {
      ResultSet result=null;
      try
      {
        result=stmt.executeQuery(SqlString);
      }
      
      catch(Exception e)
      {
       e.printStackTrace();
      }
      return (result);
    }
    /***
     * 获取oracle表及字段注释
     * @param tableName
     * @return
     */
    public List<DsColumn> getOracleDsColumnList(String tableName){
        List<DsColumn> cols = getDsColumnList(tableName);
        String sql = "select COLUMN_NAME,COMMENTS from USER_COL_COMMENTS where TABLE_NAME='" + tableName + "'";
        Map<String,Object> colMap = new HashMap<String,Object>();
        ResultSet rs = executeQuery(sql);
        try {
            while(rs.next()){
                colMap.put(rs.getString("COLUMN_NAME"),rs.getString("COMMENTS"));
            }
            rs.close();
            for(DsColumn col:cols){
                if(null !=colMap.get(col.getName())){
                    col.setRemark(colMap.get(col.getName())+"");
                }           
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cols;
    }
    /***
     * 获取表字段列表及相关属性
     * @param tableName
     * @return
     */
    public List<DsColumn> getDsColumnList(String tableName){
        List<DsColumn> columnList = new ArrayList<DsColumn>();
        try {
            openConnection();
            //获得列的信息
            ResultSet rs = conn.getMetaData().getColumns(null, null, tableName, null);
            while (rs.next()) {
                
                //获得字段名称
                String name = rs.getString("COLUMN_NAME");
                //获得字段类型名称
                String type = rs.getString("TYPE_NAME");
                //获得字段大小
                int size = rs.getInt("COLUMN_SIZE");
                //获得字段备注
                String remark = rs.getString("REMARKS");
                columnList.add(new DsColumn(name,type,size,remark));
            }
            //获得主键的信息
            rs = conn.getMetaData().getPrimaryKeys(null, null, tableName);
            while(rs.next()){
                String  primaryKey = rs.getString("COLUMN_NAME");
                //设置是否为主键
                for (DsColumn column : columnList) {
                     if(primaryKey != null && primaryKey.equals(column.getName()))
                         column.setIsprimarykey(true);
                     else
                         column.setIsprimarykey(false);
                }
            }
            //获得外键信息
            rs = conn.getMetaData().getImportedKeys(null, null, tableName);
            while(rs.next()){
                String  foreignKey = rs.getString("COLUMN_NAME");
                //设置是否为主键
                for (DsColumn column : columnList) {
                     if(foreignKey != null && foreignKey.equals(column.getName()))
                         column.setIsforeignkey(true);
                     else
                         column.setIsforeignkey(false);
                }
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return columnList;
    }
}
