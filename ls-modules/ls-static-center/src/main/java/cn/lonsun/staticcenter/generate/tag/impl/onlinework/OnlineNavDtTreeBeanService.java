package cn.lonsun.staticcenter.generate.tag.impl.onlinework;

import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.indicator.internal.dao.IIndicatorDao;
import cn.lonsun.staticcenter.generate.IndicatorTreeNode;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gu.fei
 * @version 2015-12-14 13:56
 */
@Component
public class OnlineNavDtTreeBeanService extends AbstractBeanService {

    @Autowired
    private IIndicatorDao indicatorDao;

    @Override
    public Object getObject(JSONObject paramObj) {
        Long  columnId = paramObj.getLong("columnId");
        List<String> fields = new ArrayList<String>();
        StringBuilder sql = new StringBuilder("select");
        sql.append(" a.lev as lev,");
        fields.add("lev");
        sql.append(" a.indicator_Id as indicatorId,");
        fields.add("indicatorId");
        sql.append(" a.name as name,");
        fields.add("name");
        sql.append(" a.parent_id as parentId,");
        fields.add("parentId");
        sql.append(" a.is_parent as isParent,");
        fields.add("isParent");
        sql.append(" b.is_start_url as isStartUrl,");
        fields.add("isStartUrl");
        sql.append(" b.trans_url as transUrl,");
        fields.add("transUrl");
        sql.append(" b.trans_window as transWindow,");
        fields.add("transWindow");
        sql.append(" b.is_show as isShow");
        fields.add("isShow");
        sql.append(" from (select level as lev,indicator_id,name,parent_id,is_parent from rbac_indicator ");
        sql.append(" start with indicator_id = :columnId connect by prior indicator_id = parent_id order by level,parent_id) a,");
        sql.append(" cms_column_config b,RBAC_INDICATOR  r where a.indicator_id = b.indicator_id and a.indicator_id = r.indicator_id and r.RECORD_STATUS = '" + AMockEntity.RecordStatus.Normal.toString() + "'");
        sql.append(" order by r.sort_num");
        Map<String,Object> param = new HashMap<String, Object>();
        param.put("columnId",columnId);
        String[] arr = new String[fields.size()];
        Object nodes = indicatorDao.getBeansBySql(sql.toString(),param,IndicatorTreeNode.class,fields.toArray(arr));
        return JSON.toJSON(nodes);
    }

    /**
     * 预处理结果
     * @param resultObj
     * @param paramObj
     * @return
     */
    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) {

        // 数据预处理
        Map<String, Object> map = new HashMap<String, Object>();

        //打开方式
        String target = paramObj.getString("target");
        map.put("target",target);

        return map;
    }
}
