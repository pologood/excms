package cn.lonsun.special.internal.vo;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;

/**
 * 专题分类
 */
public class SpecialTypeVO{

    private Long id;

    private String name;

    private Long sortNumber = 0l;

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

    public Long getSortNumber() {
        return sortNumber;
    }

    public void setSortNumber(Long sortNumber) {
        this.sortNumber = sortNumber;
    }
}
