/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.lonsun.desktop.vo;

import cn.lonsun.common.vo.PageQueryVO;

/**
 * 桌面背景查询对象
 * @author Dzl
 */
public class SystemBackgroundQueryVO extends PageQueryVO{
    
    public Boolean isSysBg=false;

    public Boolean getIsSysBg() {
        return isSysBg;
    }

    public void setIsSysBg(Boolean isSysBg) {
        this.isSysBg = isSysBg;
    }
    
    
}
