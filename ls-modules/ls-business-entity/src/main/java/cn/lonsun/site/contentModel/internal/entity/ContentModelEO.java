package cn.lonsun.site.contentModel.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;

/**
 * 内容模型实体类<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-10-8<br/>
 */
@Entity
@Table(name = "cms_content_model")
public class ContentModelEO extends AMockEntity {
    private static final long serialVersionUID = 881540343350073755L;
    // 主键
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    // 名称
    @Column(name = "name")
    private String name;

    //code值
    @Column(name = "code")
    private String code;

    //站点Id
    @Column(name = "site_id")
    private Long siteId;

    //描述
    @Column(name = "description")
    private String description;

    //配置项
    @Column(name = "content")
    private String content;

    @Transient
    private Object config;

    //是否为公共的
    @Column(name = "is_public")
    private Integer isPublic = 0;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Object getConfig() {
        return config;
    }

    public void setConfig(Object config) {
        this.config = config;
    }

    public Integer getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Integer isPublic) {
        this.isPublic = isPublic;
    }
}