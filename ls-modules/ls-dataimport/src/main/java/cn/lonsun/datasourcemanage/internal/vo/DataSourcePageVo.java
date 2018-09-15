package cn.lonsun.datasourcemanage.internal.vo;

/**
 * Created by lonsun on 2018-2-5.
 *
 */
public class DataSourcePageVo {

    private Long dataSourceId;

    private String name;
    private String databaseType;
    private String databaseName;
    private String databaseUri;
    private String username;
    private String passwd;
    private String manufacturerName;

    public String getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
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

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }
}
