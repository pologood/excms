package cn.lonsun.datacollect.dao.impl;

import cn.lonsun.core.base.entity.AMockEntity;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * @author gu.fei
 * @version 2016-1-21 14:27
 */
@Repository
public class HtmlCollectDataMySqlDao extends HtmlCollectDataDao {
    @Override
    public void saveData(Map<String, Object> map) {
        if(null != map) {
            String columns = "";
            String values = "";
            Object[] params = new Object[map.size()];
            int i = 0;
            for (String key : map.keySet()) {
                if("TITLE".equalsIgnoreCase(key)) {
                }
                columns += "," + key;
                values += ",?";
                params[i++] = map.get(key);
            }

            String sql = "INSERT INTO CMS_HTML_COLLECT_DATA(RECORD_STATUS" + columns + ") VALUES ('" + AMockEntity.RecordStatus.Normal.toString() + "'" + values + ")";
            this.executeUpdateBySql(sql, params);
        }
    }
}
