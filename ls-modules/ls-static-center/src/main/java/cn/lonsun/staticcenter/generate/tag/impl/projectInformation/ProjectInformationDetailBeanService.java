package cn.lonsun.staticcenter.generate.tag.impl.projectInformation;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.projectInformation.internal.entity.ProjectInformationEO;
import cn.lonsun.projectInformation.internal.service.IProjectInformationService;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.RegexUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author huangxx
 * @version 2016-5-28 8:37
 */
@Component
public class ProjectInformationDetailBeanService extends AbstractBeanService {

    @Autowired
    private IProjectInformationService projectInformationService;

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        Map<String, String> pmap = context.getParamMap();
        Long projectInfoId = null;
        if(pmap != null && pmap.size() > 0) {
            if(!AppUtil.isEmpty(pmap.get("projectInfoId"))) {
                projectInfoId = AppUtil.getLong(pmap.get("projectInfoId"));
            }
        }
        ProjectInformationEO eo = null;
        if(null != projectInfoId) {
            eo = projectInformationService.getEntity(ProjectInformationEO.class,projectInfoId);
        }
        return eo;
    }

    @Override
    public String objToStr(String content, Object resultObj, JSONObject paramObj) throws GenerateException {
        ProjectInformationEO eo = null;
        try {
            Object neo = BeanUtils.cloneBean(resultObj);
            eo = (ProjectInformationEO) neo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return RegexUtil.parseProperty(content, eo);
    }
}
