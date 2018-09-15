package cn.lonsun.staticcenter.generate.tag.impl.common;

import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.AssertUtil;
import cn.lonsun.statistics.StatisticsQueryVO;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 单位发文量统计 <br/>
 *
 * @author fth <br/>
 * @version v1.0 <br/>
 * @date 2017/8/2 <br/>
 */
@Component
public class OrganCountBeanService extends AbstractBeanService {

    @Autowired
    private IBaseContentService baseContentService;

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        Long siteId = ObjectUtils.defaultIfNull(paramObj.getLong("siteId"), context.getSiteId());
        AssertUtil.isEmpty(siteId, "站点id不能为空！");

        Integer num = paramObj.getInteger(GenerateConstant.NUM);// 查询条数
        AssertUtil.isEmpty(num, "查询条数num不能为空！");

        StatisticsQueryVO vo = new StatisticsQueryVO();
        vo.setSiteId(siteId);
        vo.setPageIndex(0L);// 默认查询第一页
        vo.setTypeCode(BaseContentEO.TypeCode.articleNews.toString());// 查询文字类型
        vo.setPageSize(num);

        return baseContentService.getWordPage(vo);
    }
}
