/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.lonsun.core.util;

import java.io.Serializable;

/**
 *
 * @author Dzl
 */
public class AjaxObj implements Serializable{
    /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -7133144256777652839L;
	private int status;
    private String desc;
    private Object data;

    public AjaxObj() {
    }

    public AjaxObj(int status, String desc, Object data) {
        this.status = status;
        this.desc = desc;
        this.data = data;
    }
    
   
    
    /**
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * @return the desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * @param desc the desc to set
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * @return the data
     */
    public Object getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(Object data) {
        this.data = data;
    }
    
    
    public static AjaxObj Ok(){
        AjaxObj el = new AjaxObj();
        el.setStatus(1);
        el.setDesc("操作成功");
        el.setData("");
        return el;
    }
    public static AjaxObj Ok(Object data){
        AjaxObj el = new AjaxObj();
        el.setStatus(1);
        el.setDesc("操作成功");
        el.setData(data);
        return el;
    }
    public static String Ok(Object data,String[] filterNames){
        AjaxObj el = new AjaxObj();
        el.setStatus(1);
        el.setDesc("操作成功");
        el.setData(data);
        return JSONHelper.toJSON(el, filterNames,JSONFilterNamesType.EXCLUDE);
    }
    
    public static String Ok(Object data,String[] filterNames,JSONFilterNamesType ft){
        AjaxObj el = new AjaxObj();
        el.setStatus(1);
        el.setDesc("操作成功");
        el.setData(data);
        return JSONHelper.toJSON(el, filterNames,ft);
    }
     
    public static AjaxObj Err(){
        AjaxObj el = new AjaxObj();
        el.setStatus(0);
        el.setDesc("操作失败");
        el.setData("");
        return el;
    }
    public static AjaxObj Err(String desc){
        AjaxObj el = new AjaxObj();
        el.setStatus(0);
        el.setDesc(desc);
        el.setData("");
        return el;
    }
    public static AjaxObj Err(String desc,String data){
        AjaxObj el = new AjaxObj();
        el.setStatus(0);
        el.setDesc(desc);
        el.setData(data);
        return el;
    }
    
}
