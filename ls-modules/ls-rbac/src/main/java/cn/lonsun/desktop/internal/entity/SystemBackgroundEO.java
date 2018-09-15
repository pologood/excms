/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.lonsun.desktop.internal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.lonsun.core.base.entity.AMockEntity;

/**
 * 桌面背景实体对象
 *
 * @version 1.0
 * @author Dzl 2014.09.30
 */
@Entity
@Table(name = "desktop_background") // 请填写表名
public class SystemBackgroundEO extends AMockEntity {

    private static final long serialVersionUID = 1L;

    public SystemBackgroundEO() {

    }

    public SystemBackgroundEO(String name, String thumb, String imgUrl) {
        this.name = name;
        this.thumb = thumb;
        this.imgUrl = imgUrl;
    }

    public SystemBackgroundEO(String name, String thumb, String imgUrl, Boolean isSysBg) {
        this.name = name;
        this.thumb = thumb;
        this.imgUrl = imgUrl;
        this.isSysBg = isSysBg;
    }
    
    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Long id;
    
    
    @Column(length = 50, nullable = false)
    private String name;
    /**
     * 缩略图
     */
    @Column 
    private String thumb;
    /**
     * 背景图片地址
     */
    @Column
    private String imgUrl;
    /**
     * 是否为系统默认(系统默认背景不允许删除)
     */
    @Column
    @org.hibernate.annotations.Type(type="yes_no")
    private Boolean isDeafult = false;
    /**
     * 是否为系统背景(如果是系统背景,仅供管理员修改)
     */
    @Column 
    @org.hibernate.annotations.Type(type="yes_no")
    private Boolean isSysBg = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Boolean getIsDeafult() {
        return isDeafult;
    }

    public void setIsDeafult(boolean isDeafult) {
        this.isDeafult = isDeafult;
    }

    public Boolean getIsSysBg() {
        return isSysBg;
    }

    public void setIsSysBg(boolean isSysBg) {
        this.isSysBg = isSysBg;
    }
    
    
    
}
