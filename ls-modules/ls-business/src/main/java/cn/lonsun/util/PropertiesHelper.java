package cn.lonsun.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/**
 * @author gu.fei
 * @version 2016-2-26 15:40
 */
@Repository("propertiesHelper")
public class PropertiesHelper {

    @Value("${SOLR_URL}")
    private String sorlUrl;

    @Value("${syn.sensitive.words.url}")
    private String synSensitiveUrl;

    @Value("${syn.easyerr.words.url}")
    private String synEasyerrUrl;

    @Value("${jdbc.driverClassName}")
    private String driverclass;

    @Value("${db.type}")
    private String dbType;

    @Value("${sys.domain}")
    private String sysDomain;

    public String getSorlUrl() {
        return sorlUrl;
    }

    public void setSorlUrl(String sorlUrl) {
        this.sorlUrl = sorlUrl;
    }

    public String getSynSensitiveUrl() {
        return synSensitiveUrl;
    }

    public void setSynSensitiveUrl(String synSensitiveUrl) {
        this.synSensitiveUrl = synSensitiveUrl;
    }

    public String getSynEasyerrUrl() {
        return synEasyerrUrl;
    }

    public void setSynEasyerrUrl(String synEasyerrUrl) {
        this.synEasyerrUrl = synEasyerrUrl;
    }

    public String getDriverclass() {
        return driverclass;
    }

    public void setDriverclass(String driverclass) {
        this.driverclass = driverclass;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getSysDomain() {
        return sysDomain;
    }

    public void setSysDomain(String sysDomain) {
        this.sysDomain = sysDomain;
    }
}
