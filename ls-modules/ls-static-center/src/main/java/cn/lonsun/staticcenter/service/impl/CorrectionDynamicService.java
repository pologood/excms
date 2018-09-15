package cn.lonsun.staticcenter.service.impl;

import org.springframework.stereotype.Service;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.site.site.internal.entity.SiteConfigEO;
import cn.lonsun.site.template.internal.entity.TemplateConfEO;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.service.DynamicService;
import cn.lonsun.staticcenter.service.HtmlEnum;
import cn.lonsun.staticcenter.util.GenerateUtil;

@Service
public class CorrectionDynamicService implements DynamicService {

    @Override
    public String queryHtml(String action, Long id, Context context) throws GenerateException {
        // 获取站点配置信息
        SiteConfigEO siteConfigEO = CacheHandler.getEntity(SiteConfigEO.class, CacheGroup.CMS_INDICATORID, id);
        if (null == siteConfigEO) {
            return "站点id:" + id + ",不存在.";
        }
        if (HtmlEnum.INDEX.getValue().equals(action)) {// 内容纠错首页
            TemplateConfEO templateConfEO = CacheHandler.getEntity(TemplateConfEO.class, siteConfigEO.getPublicTempId());
            // 设置站点id
            context.setSiteId(id).setScope(MessageEnum.INDEX.value());
            context.setTemplateConfEO(templateConfEO);
            return GenerateUtil.generate(context);
        }
        return null;
    }

}
