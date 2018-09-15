package cn.lonsun.staticcenter.generate.tag.impl.onlinedeclaration;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.onlineDeclaration.internal.service.IOnlineDeclarationService;
import cn.lonsun.content.onlineDeclaration.vo.OnlineDeclarationVO;
import cn.lonsun.net.service.entity.CmsTableResourcesEO;
import cn.lonsun.net.service.service.ITableResourcesService;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-7-8<br/>
 */

public class OnlineDeclarationInfoBeanService extends AbstractBeanService {


    @Autowired
    private IOnlineDeclarationService declarationService;

    @Autowired
    private ITableResourcesService resourcesService;


    @Override
    public Object getObject(JSONObject paramObj) {
        Long contentId = paramObj.getLong("id");
        if (contentId == null) {
            contentId= ContextHolder.getContext().getContentId();
        }
        BaseContentEO contentEO = CacheHandler.getEntity(BaseContentEO.class, contentId);
        if (null == contentEO) {
            return null;
        }
        OnlineDeclarationVO vo = declarationService.getVO(contentId);
        return vo;
    }
    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) throws GenerateException {
        Context context = ContextHolder.getContext();// 上下文
        Map<String, Object> map = super.doProcess(resultObj, paramObj);

        String ids=context.getParamMap().get("ids");
        if(!StringUtils.isEmpty(ids)){
            Long[] idArr= AppUtil.getLongs(ids,",");
            List<CmsTableResourcesEO> tableList=resourcesService.getEntities(CmsTableResourcesEO.class,idArr);
            map.put("tableList",tableList);
        }
        return map;
        }
    }
