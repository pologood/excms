/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.lonsun.common.util;

/**
 * 件上传状态
 * @author Dzl
 */
public enum UploadState {
    UPLOAD_SUCCSSS(0, "上传文件成功！"), 
    UPLOAD_FAILURE(1, "上传文件失败！"), 
    UPLOAD_TYPE_ERROR(2, "上传文件类型错误！"), 
    UPLOAD_OVERSIZE(3, "上传文件过大！"),
    UPLOAD_ZEROSIZE(4, "上传文件为空！"),
    UPLOAD_NOTFOUND(5, "上传文件路径错误！");
    
    /**
     * 状态
     */
    private String state;
    /**
     * 标记
     */
    private int flag;
/*    *//**
     * 上传成功后的原文件名
     *//*
    private String oldFileName;
    *//**
     * 上传成功后的新文件名
     *//*
    private String newFileName;*/
    
    
    public String getState() {
        return this.state;
    }
    
    public int getFlag() {
        return this.flag;
    }
    UploadState(int flag, String state) {
        this.state = state;
        this.flag = flag;
    }
}