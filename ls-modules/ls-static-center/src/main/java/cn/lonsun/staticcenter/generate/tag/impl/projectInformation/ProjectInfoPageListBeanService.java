package cn.lonsun.staticcenter.generate.tag.impl.projectInformation;

import cn.lonsun.core.util.Pagination;
import cn.lonsun.projectInformation.internal.service.IProjectInformationService;
import cn.lonsun.projectInformation.vo.ProjectInformationQueryVO;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by huangxx on 2017/3/6.
 */
@Component
public class ProjectInfoPageListBeanService extends AbstractBeanService{

    @Autowired
    private IProjectInformationService projectInformationService;

    @Override
    public Object getObject(JSONObject paramObj) throws GenerateException {

        Context context = ContextHolder.getContext();
        ProjectInformationQueryVO queryVO = new ProjectInformationQueryVO();

        Long pageIndex = context.getPageIndex() == null ? 0 : (context.getPageIndex() - 1);
        Long siteId = context.getSiteId();//当前站点id
        Long columnId = context.getColumnId();//栏目id
        Integer pageSize = paramObj.getInteger("pageSize");

        queryVO.setColumnId(columnId);
        queryVO.setSiteId(siteId);
        queryVO.setPageIndex(pageIndex);
        queryVO.setPageSize(pageSize);

        //获得项目规划列表页
        Pagination page = projectInformationService.getPageEntities(queryVO);
        return page;
    }

    /**
     * 预处理结果
     *
     * @param resultObj
     * @param paramObj
     * @return
     */
    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) {
        Map<String, Object> map = new HashMap<String, Object>();
        /*map.put("linkPrefix", "");*/
        return map;
    }
}
