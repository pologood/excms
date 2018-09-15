package cn.lonsun.wechatmgr.internal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.lonsun.core.base.entity.AMockEntity;

/**
 * @author Hewbing
 * @ClassName: WeChatMenuEO
 * @Description: 微信菜单表
 * @date 2015年12月22日 下午8:19:07
 */
@Entity
@Table(name = "CMS_WECHAT_MENU")
public class WeChatMenuEO extends AMockEntity {

    /**
     *
     */
    private static final long serialVersionUID = 5177899207970084198L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @Column(name = "SITE_ID")
    private Long siteId;
    //层级
    @Column(name = "LEVELS")
    private Integer leves;
    //上级菜单ID
    @Column(name = "PARENT_ID")
    private Long pId;
    //菜单名
    @Column(name = "NAME")
    private String name;
    //类型
    @Column(name = "TYPE")
    private String type;
    //跳转URL
    @Column(name = "URL")
    private String url;
    //关键词
    @Column(name = "KEY_MENU")
    private String key;
    //序号
    @Column(name = "SORT")
    private Integer sort;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Integer getLeves() {
        return leves;
    }

    public void setLeves(Integer leves) {
        this.leves = leves;
    }

    public Long getpId() {
        return pId;
    }

    public void setpId(Long pId) {
        this.pId = pId;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
