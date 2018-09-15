package cn.lonsun.jdbc;


import cn.lonsun.core.exception.BaseRunTimeException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * sql配置文件支持
 */
public abstract class SqlConfigAble {

    protected static Logger log = LoggerFactory.getLogger("dataImport");

    private Element sqlConfig;

    private String filePath;

    public SqlConfigAble(String filePath) {
        this.filePath = filePath + ".xml";
    }

    /**
     * 获取一级节点
     * @return
     * @throws Exception
     */
    private Element getSqlConfig() throws RuntimeException {
        if(sqlConfig == null){
            long start = System.currentTimeMillis();
            try {
                //创建SAXReader对象
                SAXReader reader = new SAXReader();
                InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
                //读取文件 转换成Document
                sqlConfig = reader.read(is).getRootElement();
                is.close();
            } catch (Exception e) {
                log.error("sql配置读取失败：{}", e.getCause());
                throw new BaseRunTimeException("获取sql失败：" + e.getMessage());
            }
            log.info("解析sql配置文件【{}】耗时： {}", filePath, System.currentTimeMillis() - start);
        }
        return sqlConfig;
    }

    /**
     * 获取root->group 下的sql
     * @param manufacturer 厂商代码
     * @param key
     * @return
     */
    protected String getSql(String manufacturer, String key){
        Element sqlElement = null;
        if(manufacturer != null){
            sqlElement = getSqlConfig().element(manufacturer);
        }else{
            sqlElement = getSqlConfig();
        }
        sqlElement = sqlElement.element(key);
        return sqlElement.getTextTrim();
    }

}
