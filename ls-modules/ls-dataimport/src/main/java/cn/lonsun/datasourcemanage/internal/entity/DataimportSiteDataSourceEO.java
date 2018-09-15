package cn.lonsun.datasourcemanage.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;

/**站点数据源
 * Created by lonsun on 2018-2-5.
 *
 */
@Entity
@Table(name = "DATAIMPORT_SITE_DATA_SOURCE")
public class DataimportSiteDataSourceEO extends AMockEntity {

    @Column(name = "ID")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**
     * 数据源id
     */
    @Column(name = "DATA_SOURCE_ID")
    private Long  dataSourceId;

    /**
     * 栏目类型编码
     */
    @Column(name = "TYPE_CODE")
    private String  typeCode;
    /**
     * 栏目类型编码名称
     */
    @Column(name = "TYPE_CODE_NAME")
    private String  typeCodeName;
    /**
     * 站点ID
     */
    @Column(name = "SITE_ID")
    private Long  siteId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(Long dataSourceId) {
        this.dataSourceId = dataSourceId;
    }



    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getTypeCodeName() {
        return typeCodeName;
    }

    public void setTypeCodeName(String typeCodeName) {
        this.typeCodeName = typeCodeName;
    }
}
