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
import java.util.List;
import java.util.Map;

/**
 * @author gu.fei
 * @version 2015-12-14 13:56
 */
@Component
public class OnlineNavUnitBeanService extends AbstractBeanService {

    @Autowired
    private IContentModelService contentModelService;

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        Map<String, String> pmap = context.getParamMap();
        Long organId = null;
        if(pmap != null && pmap.size() > 0) {
            if(!AppUtil.isEmpty(pmap.get("organId"))) {
                organId = Long.valueOf(pmap.get("organId"));
            }
        }

        String ids = paramObj.getString("columnIds");
        Long siteId = null;
        try {
            siteId = paramObj.getLong("siteId");
        } catch (Exception e) {
            e.printStackTrace();
            siteId = context.getSiteId();
        }

        if(null == siteId) {
            siteId = context.getSiteId();
        }
        List<OrganEO> result = new ArrayList<OrganEO>();
        if(!AppUtil.isEmpty(ids)) {
            List<Long> columnIds = StringUtils.getListWithLong(ids,",");
            List<ContentModelParaVO> assem = new ArrayList<ContentModelParaVO>();
            for(Long columnId : columnIds) {
                List<ContentModelParaVO> single = ModelConfigUtil.getParam(columnId,siteId,null);
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
            List<OrganEO> list = contentModelService.getAllBindUnit(siteId);
            result.addAll(list);
        }

        List<OrganEO> temp = new ArrayList<OrganEO>();
        for(OrganEO organEO : result) {
            if(AppUtil.isEmpty(organEO)) {
               continue;
            }

            temp.add(organEO);
        }

        //选中
        if(null != organId) {
            for(OrganEO eo : temp) {
                if(null != eo && null != eo.getOrganId() && (eo.getOrganId().intValue() == organId.intValue())) {
                    eo.setActive("active");
                }
            }
        }
        return temp;
    }
}
