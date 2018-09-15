package cn.lonsun.staticcenter.generate.tag.impl.publicInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import cn.lonsun.util.PublicCatalogUtil;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogEO;
import cn.lonsun.publicInfo.internal.service.IPublicCatalogService;
import cn.lonsun.publicInfo.internal.service.IPublicContentService;
import cn.lonsun.publicInfo.util.PublicUtil;
import cn.lonsun.publicInfo.vo.PublicContentQueryVO;
import cn.lonsun.publicInfo.vo.PublicContentVO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.AssertUtil;
import cn.lonsun.staticcenter.generate.util.MapUtil;
import cn.lonsun.staticcenter.generate.util.PathUtil;

import com.alibaba.fastjson.JSONObject;

/**
 * 获取信息公开内容列表 ADD REASON. <br/>
 *
 * @author fangtinghua
 * @date: 2016年7月6日 上午11:49:30 <br/>
 */
@Component
public class PublicCatFlowInfoListBeanService extends AbstractBeanService {

    @Resource
    private IOrganService organService;
    @Resource
    private IPublicCatalogService publicCatalogService;
    @Resource
    private IPublicContentService publicContentService;

    /**
     * 根据部门id,目录id查询行政职权目录和流程图信息，通过传type区分查询的类别（0-总表，1-分表和流程图，3-结果）
     *
     * @throws GenerateException
     * @see AbstractBeanService#getObject(JSONObject)
     */
    @Override
    public Object getObject(JSONObject paramObj) throws GenerateException {
        // 访问路径
        // /public/column/organId?type=1&catId=1&cId=1&action=list&pageIndex=1
        Context context = ContextHolder.getContext();
        // 合并map
        MapUtil.unionContextToJson(paramObj);
        String strId = context.getParamMap().get("siteId");
        Long siteId = null;
        if (NumberUtils.isNumber(strId)) {
            siteId = NumberUtils.toLong(strId);
        } else {
            siteId = context.getSiteId();
        }
        context.setSiteId(siteId);
        AssertUtil.isEmpty(siteId, "站点id不能为空！");

        String organId = paramObj.getString(GenerateConstant.ORGAN_ID);
        String catId = paramObj.getString(PublicConstant.CATID);// 目录id
        String type = paramObj.getString(GenerateConstant.TYPE);// 类型
        // String action = paramObj.getString(GenerateConstant.ACTION);// 动作
        // 如果为空，默认查询条数
        // action = ObjectUtils.defaultIfNull(action,
        // PublicConstant.ACTION_NUM);
        // 构建查询参数

        PublicContentQueryVO queryVO = new PublicContentQueryVO();
        queryVO.setSiteId(siteId);
        queryVO.setIsPublish(1);// 查询已发布的文章
        if (!AppUtil.isEmpty(organId)) {
            queryVO.setOrganId(Long.parseLong(organId));
        }
        queryVO.setType(PublicConstant.PublicTypeEnum.getType("4"));// 主动公开

        // Map<String, Object> map = new HashMap<String, Object>();
        List<PublicCatalogEO> tempList = publicCatalogService.getAllChildListByCatId(Long.parseLong(catId));// 行政执法目录Id
        //过滤隐藏的目录
        PublicCatalogUtil.filterCatalogList(tempList, Long.parseLong(organId), true);
        // 查询分页
        paramObj.put(GenerateConstant.ACTION, PublicConstant.ACTION_LIST);
        queryVO.setPageSize(paramObj.getInteger(GenerateConstant.PAGE_SIZE));// 标签中定义
        queryVO.setPageIndex(context.getPageIndex() - 1);

        if (type.equals("0")) {// 查询总表
            queryVO.setCatId(Long.parseLong(catId));// 目录总表id

        } else if (type.equals("1")) {// 查询分表和流程图

            List<Long> catList = new ArrayList<Long>();
            for (PublicCatalogEO eo : tempList) {
                if (eo.getName().contains("分表及流程图")) {
                    catList.add(eo.getId());
                }
            }
            if (catList.size() > 0) {
                Long[] cat = new Long[catList.size()];
                catList.toArray(cat);
                queryVO.setCatIds(cat);
                queryVO.setCatId(1L);// 此处传的无效参数，是为了尽dao里的查询条件
            }

        } else if (type.equals("2")) {

            List<Long> catList = new ArrayList<Long>();
            for (PublicCatalogEO eo : tempList) {
                if (eo.getName().contains("结果")) {
                    catList.add(eo.getId());
                }
            }
            if (catList.size() > 0) {
                Long[] cat = new Long[catList.size()];
                catList.toArray(cat);
                queryVO.setCatId(1L);// 此处传的无效参数，是为了尽dao里的查询条件
                queryVO.setCatIds(cat);
            }
        }

        Pagination page = publicContentService.getPagination(queryVO);
        return page;
    }

    /**
     * 预处理跳转链接
     *
     * @see cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService#doProcess(java.lang.Object,
     * com.alibaba.fastjson.JSONObject)
     */
    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) throws GenerateException {
        Context context = ContextHolder.getContext();
        String type = paramObj.getString(GenerateConstant.TYPE);
        String action = paramObj.getString(GenerateConstant.ACTION);// 动作
        Map<String, Object> resultMap = super.doProcess(resultObj, paramObj);
        resultMap.put(GenerateConstant.TYPE, type);// 放入类型在页面进行判断
        resultMap.put(GenerateConstant.ACTION, action);// 放入动作在页面进行判断
        // boolean isDriving =
        // PublicConstant.PublicTypeEnum.DRIVING_PUBLIC.getKey().equals(type);
        Long source = context.getSource();
        context.setSource(MessageEnum.PUBLICINFO.value());
        if (PublicConstant.ACTION_LIST.equals(action)) {// 分页时
            Pagination pagination = (Pagination) resultObj;
            pagination.setLinkPrefix(context.getPath());// 放入分页访问地址
            List<?> list = pagination.getData();
            for (Object obj : list) {
                PublicContentVO vo = (PublicContentVO) obj;
                vo.setCatName(PublicUtil.getCatName(vo.getOrganId(), vo.getCatId()));
                vo.setClassNames(PublicUtil.getClassName(vo.getClassIds()));
                String path = PathUtil.getLinkPath(vo.getOrganId(), vo.getContentId());
                vo.setLink(path);
            }
        }
        context.setSource(source);
        return resultMap;
    }

}