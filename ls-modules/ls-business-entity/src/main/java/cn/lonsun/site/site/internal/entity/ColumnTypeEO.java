package cn.lonsun.site.site.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * 栏目类型实体类<br/>
 *
 * @author xzj <br/>
 * @version v1.0 <br/>
 * @date 2018-1-23 <br/>
 */
@Entity
@Table(name = "cms_column_type")
public class ColumnTypeEO extends AMockEntity {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -2964053661110225063L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    //同步到栏目的名称
    @Column(name = "type_name")
    private String typeName;

    //keyWords
    @Column(name = "keywords")
    private String keyWords;

    //description
    @Column(name = "description")
    private String description;
    @Column(name = "site_id")
    private Long siteId;

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getKeyWords() {
        return keyWords;
    }

    public void setKeyWords(String keyWords) {
        this.keyWords = keyWords;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
