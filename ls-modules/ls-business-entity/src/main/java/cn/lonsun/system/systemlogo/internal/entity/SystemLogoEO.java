package cn.lonsun.system.systemlogo.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;
import java.io.Serializable;
import javax.persistence.*;

/**
 * Created by hu on 2016/7/11.
 */
@Entity
@Table(name="CMS_SYSTEM_LOGO")
public class SystemLogoEO extends AMockEntity implements Serializable {

    @Id
    @Column(name="ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long logoId;
    //首页图片
    @Column(name="INDEXIMG")
    private String indexImg;
    //登录图片
    @Column(name="LOGINIMG")
    private String loginImg;
    //系统图片
    @Column(name="SYSTEMIMG")
    private String  systemImg;
    //状态
    @Column(name="STATUS")
    private String status;


    public Long getLogoId() {
        return logoId;
    }

    public void setLogoId(Long logoId) {
        this.logoId = logoId;
    }

    public String getIndexImg() {
        return indexImg;
    }

    public void setIndexImg(String indexImg) {
        this.indexImg = indexImg;
    }

    public String getLoginImg() {
        return loginImg;
    }

    public void setLoginImg(String loginImg) {
        this.loginImg = loginImg;
    }

    public String getSystemImg() {
        return systemImg;
    }

    public void setSystemImg(String systemImg) {
        this.systemImg = systemImg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
