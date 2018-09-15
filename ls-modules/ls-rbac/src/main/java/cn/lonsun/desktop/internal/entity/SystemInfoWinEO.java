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
 * 桌面信息窗口实体领域类
 * @author Dzl
 */
@Entity
@Table(name = "desktop_wininfo")
public class SystemInfoWinEO extends AMockEntity{
    private static final long serialVersionUID = 1L;
    public SystemInfoWinEO(){}

    public SystemInfoWinEO(String name, String url) {
        this.name = name;
        this.url = url;
    }
    
    
    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Long id;
    /**
     * 名称
     */
    @Column(length = 50,nullable = false)
    private String name;
    /**
     * 路径
     */
    @Column(nullable=false)
    private String url;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    
    
}
