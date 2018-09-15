package cn.lonsun.datasourcemanage.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;

/**数据源表
 * Created by lonsun on 2018-2-5.
 *
 */
@Entity
@Table(name = "DATAIMPORT_DATA_SOURCE")
public class DataimportDataSourceEO extends AMockEntity {
    @Column(name = "ID")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long dataSourceId;
    /**
     * 数据源名称
     */
    @Column(name = "NAME")
    private String name;
    /**
     * 数据库类型
     */
    @Column(name = "DATABASE_TYPE")
    private String databaseType;

    /**
     * 数据库名称
     */
    @Column(name = "DATABASE_NAME")
    private String databaseName;

    /**
     * 数据源uri
     */
    @Column(name = "DATABASE_URI")
    private String databaseUri;
    /**
     * 账号
     */
    @Column(name = "USERNAME")
    private String username;
    /*
     * 密码
     */
    @Column(name = "PASSWD")
    private String passwd;
    /**
     * 厂商id
     */
    @Column(name = "MANUFACTURERID")
    private Long manufacturerid;

    /**
     * 厂商编码
     */
    @Column(name = "MANUFACTURERCODE")
    private String manufacturercode;


    public Long getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(Long dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDatabaseUri() {
        return databaseUri;
    }

    public void setDatabaseUri(String databaseUri) {
        this.databaseUri = databaseUri;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public Long getManufacturerid() {
        return manufacturerid;
    }

    public void setManufacturerid(Long manufacturerid) {
        this.manufacturerid = manufacturerid;
    }

    public String getManufacturercode() {
        return manufacturercode;
    }

    public void setManufacturercode(String manufacturercode) {
        this.manufacturercode = manufacturercode;
    }
}
