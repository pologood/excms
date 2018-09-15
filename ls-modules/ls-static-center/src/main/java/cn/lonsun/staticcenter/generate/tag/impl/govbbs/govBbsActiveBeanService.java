package cn.lonsun.staticcenter.generate.tag.impl.govbbs;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.govbbs.internal.dao.IBbsPlateDao;
import cn.lonsun.govbbs.internal.entity.BbsPlateEO;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.service.HtmlEnum;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangchao on 2017/1/11.
 */
@Component
public class govBbsActiveBeanService extends AbstractBeanService {


    @Autowired
    private IBbsPlateDao bbsPlateDao;


    @Override
    public Object getObject(JSONObject paramObj) throws GenerateException {
        Context context = ContextHolder.getContext();
        String className = paramObj.getString("className");
        String action = context.getAction();
        if(!StringUtils.isEmpty(action)){
            String id = paramObj.getString("id");
            if(action.equals(HtmlEnum.INDEX.getValue())){
                if(action.equals(id)){
                    return className;
                }
            }
            else if(action.equals(HtmlEnum.GOVMB.getValue())){
                Long columnId = context.getColumnId();
                String code = HtmlEnum.GOVMB.getValue()+"_"+columnId;
                if(code.equals(id)){
                    return className;
                }
            }
            else if(action.equals(HtmlEnum.COLUMN.getValue()) || action.equals(HtmlEnum.CONTENT.getValue())){
                Long columnId = context.getColumnId();
                if(columnId == null){
                    return "";
                }
                String cId = getParentColumnId(columnId);
                String code = HtmlEnum.COLUMN.getValue()+"_"+cId;
                if(code.equals(id)){
                    return className;
                }
            }
        }
        return "";
    }

    private String getParentColumnId(Long columnId) {
        Map<String,String> map = new HashMap<String,String>();
        getParentPlate(map,columnId);
        return map.get("plateId");
    }

    private void getParentPlate(Map<String,String> map,Long columnId) {
        if(columnId != null){
            BbsPlateEO eo_ =  CacheHandler.getEntity(BbsPlateEO.class,columnId);
            if(eo_ != null){
                map.put("plateId",eo_.getPlateId()+"");
                if(eo_.getParentId() != null){
                    getParentPlate(map,eo_.getParentId());
                }
            }
        }
    }

}
