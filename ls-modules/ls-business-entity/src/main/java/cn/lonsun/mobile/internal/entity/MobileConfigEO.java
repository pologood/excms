package cn.lonsun.mobile.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Doocal
 * @ClassName: LimitIPEO
 * @Description: 限制IP类
 */
@Entity
@Table(name = "cms_mobile_config")
public class MobileConfigEO extends AMockEntity implements Serializable {

    private static final long serialVersionUID = 4857349989564882716L;

    //主键
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NUM")
    private Long num;

    @Column(name = "SITEID")
    private Long siteId;

    //栏目ID
    @Column(name = "INDICATOR_ID")
    private Long indicatorId;

    //栏目名称
    @Column(name = "NAME")
    private String name;

    //区分导航内容和轮播 nav focus articleNews special
    @Column(name = "TYPE")
    private String type;

    //对应该的模型编码
    @Column(name = "CODE")
    private String code;

    //URI 地址
    @Column(name = "URI")
    private String uri;

    //是否隐藏
    @Column(name = "IS_VISIBLE")
    private Integer isVisible = 0;

    //是否选种
    @Column(name = "IS_CHECKED")
    private Integer isChecked = 0;

    //是否调用下级分类
    @Column(name = "IS_CHILD")
    private Integer isChild = 0;

    //是否回复
    @Column(name = "IS_REPLY")
    private Integer isReply = 0;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getIsReply() {
        return isReply;
    }

    public void setIsReply(Integer isReply) {
        this.isReply = isReply;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNum() {
        return num;
    }

    public void setNum(Long num) {
        this.num = num;
    }

    public Long getIndicatorId() {
        return indicatorId;
    }

    public void setIndicatorId(Long indicatorId) {
        this.indicatorId = indicatorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Integer getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(Integer isVisible) {
        this.isVisible = isVisible;
    }

    public Integer getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(Integer isChecked) {
        this.isChecked = isChecked;
    }

    public Integer getIsChild() {
        return isChild;
    }

    public void setIsChild(Integer isChild) {
        this.isChild = isChild;
    }
}
