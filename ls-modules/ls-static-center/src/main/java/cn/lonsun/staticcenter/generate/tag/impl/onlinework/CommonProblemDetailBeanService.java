package cn.lonsun.staticcenter.generate.tag.impl.onlinework;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.net.service.dao.ICommonProblemDao;
import cn.lonsun.net.service.entity.CmsCommonProblemEO;
import cn.lonsun.net.service.service.ICommonProblemService;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.RegexUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author gu.fei
 * @version 2016-5-28 9:10
 */
@Component
public class CommonProblemDetailBeanService extends AbstractBeanService {

    @Autowired
    private ICommonProblemService commonProblemService;

    @Autowired
    private ICommonProblemDao commonProblemDao;

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        Map<String, String> pmap = context.getParamMap();
        Long problemId = null;
        if(pmap != null && pmap.size() > 0) {
            if(!AppUtil.isEmpty(pmap.get("problemId"))) {
                problemId = Long.valueOf(pmap.get("problemId"));
            }
        }

        if(null == problemId) {
            return new CmsCommonProblemEO();
        }

        CmsCommonProblemEO eo = commonProblemDao.getEntityByHql("from CmsCommonProblemEO where id=?", new Object[]{problemId});
        return eo;
    }

    @Override
    public String objToStr(String content, Object resultObj, JSONObject paramObj) throws GenerateException {
        CmsCommonProblemEO eo = (CmsCommonProblemEO) resultObj;
        return RegexUtil.parseProperty(content, eo);
    }
}