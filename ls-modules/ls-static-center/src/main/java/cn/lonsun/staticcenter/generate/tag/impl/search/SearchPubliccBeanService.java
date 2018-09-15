package cn.lonsun.staticcenter.generate.tag.impl.search;

import cn.lonsun.core.util.Pagination;
import cn.lonsun.publicInfo.internal.entity.PublicContentEO;
import cn.lonsun.publicInfo.internal.service.IPublicContentService;
import cn.lonsun.publicInfo.vo.PublicContentQueryVO;
import cn.lonsun.publicInfo.vo.PublicContentVO;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 搜索界面 - 最新信息公开
 * @author zhongjun
 */
@Component
public class SearchPubliccBeanService extends AbstractBeanService {

    @Autowired
    private IPublicContentService publicContentService;

    @Override
    public Object getObject(JSONObject paramObj) throws GenerateException {
        Context context = ContextHolder.getContext();

        Long siteId = paramObj.getLong("siteId");
        siteId = siteId != null ? siteId : Long.valueOf(context.getParamMap().get("siteId"));

        Integer num = paramObj.getInteger("num");
        num = num != null ? num : Integer.valueOf(context.getParamMap().get("num"));

        PublicContentQueryVO publicpagevo = new PublicContentQueryVO();
        publicpagevo.setSiteId(siteId);
        publicpagevo.setType(PublicContentEO.Type.DRIVING_PUBLIC.toString());
        publicpagevo.setPageSize(num);
        Pagination publiccatalogs = publicContentService.getPagination(publicpagevo);
        if (null == publiccatalogs.getData()|| publiccatalogs.getData().isEmpty()) {
            return Collections.emptyList();
        }
        List<PublicContentVO> pvos = (List<PublicContentVO>) publiccatalogs.getData();
        for (PublicContentVO pvo : pvos) {
//                    String link = PathUtil.getLinkPath(pvo.getOrganId(), pvo.getId());
            String link = "/public/content/" + pvo.getContentId();
            pvo.setLink(link);
        }
        return pvos;
    }
}
