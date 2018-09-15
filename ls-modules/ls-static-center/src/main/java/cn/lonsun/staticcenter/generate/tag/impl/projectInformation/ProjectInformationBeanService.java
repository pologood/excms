package cn.lonsun.staticcenter.generate.tag.impl.projectInformation;

import cn.lonsun.projectInformation.internal.entity.ProjectInformationEO;
import cn.lonsun.projectInformation.internal.service.IProjectInformationService;
import cn.lonsun.projectInformation.vo.ProjectInformationQueryVO;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by huangxx on 2017/3/6.
 */
@Component
public class ProjectInformationBeanService extends AbstractBeanService{

    @Autowired
    private IProjectInformationService projectInformationService;

    @Override
    public Object getObject(JSONObject paramObj) throws GenerateException {

        Context context = ContextHolder.getContext();
        ProjectInformationQueryVO queryVO = new ProjectInformationQueryVO();
        List<Long> ids = new ArrayList<Long>();

        Long pageIndex = context.getPageIndex() == null ? 0 : (context.getPageIndex() - 1);
        Long siteId = context.getSiteId();//当前站点id
        String id = paramObj.getString(GenerateConstant.ID);//获得标签id串
        //Long columnId = paramObj.getLong("columnId");
        Integer pageSize = paramObj.getInteger("pageSize");

        if (!StringUtils.isEmpty(id)) {
            ids.addAll(Arrays.asList((Long[]) (ConvertUtils.convert(id.split(","), Long.class))));
        } else {
            Long columnId = context.getColumnId();
            // 默认查询本栏目
            ids.add(columnId);
        }
        Long[] idsArray = ids.toArray(new Long[ids.size()]);
        queryVO.setSiteId(siteId);
        queryVO.setPageIndex(pageIndex);
        queryVO.setPageSize(pageSize);

        //获取的list包含标签给出的columnId的所有的eo,未分组
        List<ProjectInformationEO> list = projectInformationService.getPageEntities(queryVO,idsArray);
        Map<String,List<ProjectInformationEO>> map = new HashMap<String,List<ProjectInformationEO>>();

        //将list获取的eo分组，按照columnId做key，list做value,返回map
        for(ProjectInformationEO eo : list) {
            if(!map.containsKey(eo.getColumnId().toString())) {//如果不map包含columnId键,则新建一对值
                List<ProjectInformationEO> tempList = new ArrayList<ProjectInformationEO>();
                tempList.add(eo);
                map.put(eo.getColumnId().toString(),tempList);
            }else {//如果包含，则直接获取value增加值
                map.get(eo.getColumnId().toString()).add(eo);
            }
        }
        return map;
    }
}
