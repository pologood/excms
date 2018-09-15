package cn.lonsun.staticcenter.generate.tag.impl.guestbook;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.entity.GuestBookEO;
import cn.lonsun.content.internal.service.IGuestBookService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.site.contentModel.vo.ContentModelParaVO;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.statistics.StatisticsQueryVO;
import cn.lonsun.util.ModelConfigUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hujun
 * @ClassName: GuestBookUnitStatisticsBeanService
 * @Description: 分页标签
 * @date 2015年12月1日 下午5:36:41
 */
@Component
public class GuestBookUnitStatisticsBeanService extends AbstractBeanService {

    @Autowired
    private IGuestBookService guestBookService;

    @Override
    public Object getObject(JSONObject paramObj) {
        Map<String, Object> result = new HashMap<String, Object>();
        Context context = ContextHolder.getContext();
        Map<String, String> map = context.getParamMap();
        Long columnId = paramObj.getLong(GenerateConstant.ID);
        Long siteIds = paramObj.getLong("siteIds");
        String columnIds = null;
        if (null == columnId || columnId == 0) {// 如果栏目id为空,说明栏目id是在页面传入的
            columnIds= map.get("columnId");
        }else{
            columnIds = String.valueOf(columnId);
        }
        Long siteId = paramObj.getLong("siteId");
        if (siteId == null) {
            siteId = context.getSiteId();
        }
        String organIdStr = map.get("organId");
        String organName = map.get("organName");
        String startDate = map.get("startDate");
        String endDate = map.get("endDate");
        Long pageIndex = context.getPageIndex() == null ? 0 : (context.getPageIndex() - 1);
        Integer pageSize = paramObj.getInteger(GenerateConstant.PAGE_SIZE);
        StatisticsQueryVO vo = new StatisticsQueryVO();
        vo.setPageIndex(pageIndex);
        vo.setPageSize(pageSize);
        vo.setSiteId(siteId);
        vo.setTypeCode(BaseContentEO.TypeCode.guestBook.toString());
        if(!StringUtils.isEmpty(organIdStr)){
            vo.setOrganName(organName);
            System.out.println("单位名称为:"+organName);
            result.put("organId",organIdStr);
        }
        if (!AppUtil.isEmpty(startDate)) {
            vo.setStartDate(startDate);
            result.put("startDate",startDate);

        }
        if (!AppUtil.isEmpty(endDate)) {
            vo.setEndDate(endDate);
            result.put("endDate",endDate);
        }
        if (!AppUtil.isEmpty(columnIds)) {
            vo.setColumnIds(columnIds);
        }
        Pagination page = guestBookService.getGuestPage(vo);

        List<ContentModelParaVO> recList = ModelConfigUtil.getParam(columnId, siteIds, null);

        if (recList != null && recList.size() > 0) {
            result.put("organList", recList);
        }
        result.put("page",page);

        return result;
    }
}
