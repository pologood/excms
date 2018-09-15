package cn.lonsun.staticcenter.generate.tag.impl.onlinework;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.site.contentModel.internal.service.IContentModelService;
import cn.lonsun.site.contentModel.vo.ContentModelParaVO;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.util.ModelConfigUtil;
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
public class OnlineNavUnitOneBeanService extends AbstractBeanService {

    @Autowired
    private IContentModelService contentModelService;

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        Map<String, String> pmap = context.getParamMap();
        String letter = paramObj.getString("letter");
        if(pmap != null && pmap.size() > 0) {
            if(!AppUtil.isEmpty(pmap.get("letter"))) {
                letter = String.valueOf(pmap.get("letter"));
            }
        }

        String ids = paramObj.getString("columnIds");
        List<OrganEO> result = new ArrayList<OrganEO>();
        if(!AppUtil.isEmpty(ids)) {
            List<Long> columnIds = StringUtils.getListWithLong(ids,",");
            List<ContentModelParaVO> assem = new ArrayList<ContentModelParaVO>();
            for(Long columnId : columnIds) {
                List<ContentModelParaVO> single = ModelConfigUtil.getParam(columnId,context.getSiteId(),null);
                if(null != single) {
                    assem.addAll(single);
                }
            }
            for(ContentModelParaVO vo : assem) {
                OrganEO eo = new OrganEO();
                eo.setOrganId(vo.getRecUnitId());
                eo.setName(vo.getRecUnitName());
                if(!result.contains(eo)) {
                    result.add(eo);
                }
            }
        } else {
            //获取所有网上办事绑定的单位
            List<OrganEO> list = contentModelService.getAllBindUnit(ContextHolder.getContext().getSiteId());
            if(null != list && !list.isEmpty()) {
                result.addAll(list);
            }
        }

        List<OrganEO> filter = new ArrayList<OrganEO>();

        //选中
        if(!AppUtil.isEmpty(letter)) {
            for(OrganEO eo : result) {
                if(null != eo && null != eo.getFullPy() && eo.getFullPy().substring(0,1).equalsIgnoreCase(letter)) {
                    filter.add(eo);
                }
            }
        } else {
            filter.addAll(result);
        }
        return filter;
    }

    /**
     * 预处理结果
     * @param resultObj
     * @param paramObj
     * @return
     */
    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        // 数据预处理
        Map<String, Object> map = new HashMap<String, Object>();
        //打开方式
        Long gotoId = paramObj.getLong("gotoId");
        String link = org.apache.commons.lang3.StringUtils.defaultString(context.getUri()) + "/content/column/" + gotoId;
        map.put("link",link);
        return map;
    }
}
