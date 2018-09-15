package cn.lonsun.developer;

import java.io.Serializable;

/**
 * 
 * 开发模板<br/>
 * 数据库表字段类
 * @Author taodp
 * @Time 2014年8月16日 下午12:03:24
 */
public class DsColumn implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    //字段名称
    private String name;
    //字段类型
    private String type;    
    //字段大小
    private int size;
    //字段备注
    private String remark;
    
    
    //是否为主键
    private boolean isprimarykey;
    //是否外键
    private boolean isforeignkey;
    
    
    /***
     * 初始字段信息
     * @param name
     * @param type
     * @param size
     * @param remark
     */
    public DsColumn(String name,String type,int size,String remark){
        this.name = name;
        this.type = type;
        this.size = size;
        this.remark = remark;
        
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    
    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }
    public String getRemark() {
        return remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    
    public boolean isIsprimarykey() {
        return isprimarykey;
    }
    public void setIsprimarykey(boolean isprimarykey) {
        this.isprimarykey = isprimarykey;
    }
    public boolean isIsforeignkey() {
        return isforeignkey;
    }
    public void setIsforeignkey(boolean isforeignkey) {
        this.isforeignkey = isforeignkey;
    }
}
