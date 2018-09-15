package cn.lonsun.manufacturer.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author caohaitao
 * @Title: ManufacturerEO
 * @Package cn.lonsun.manufacturer.internal.entity
 * @Description: 技术厂商实体类
 * @date 2018/2/1 16:10
 */
@Entity
@Table(name = "DATAIMPORT_MANUFACTURER")
public class ManufacturerEO extends AMockEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    //厂商名称
    @Column(name = "NAME")
    private String name;

    //产品名称
    @Column(name = "PRODUCT_NAME")
    private String productName;

    //唯一编码
    @Column(name = "UNIQUE_CODE")
    private String uniqueCode;

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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }
}
