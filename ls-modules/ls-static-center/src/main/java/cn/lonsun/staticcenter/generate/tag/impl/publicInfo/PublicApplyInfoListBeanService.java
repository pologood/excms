package cn.lonsun.staticcenter.generate.tag.impl.publicInfo;

import cn.lonsun.core.util.Pagination;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.publicInfo.internal.service.IPublicApplyService;
import cn.lonsun.publicInfo.vo.PublicApplyQueryVO;
import cn.lonsun.publicInfo.vo.PublicApplyVO;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.MapUtil;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 查询依申请公开信息 ADD REASON. <br/>
 *
 * @date: 2016年8月13日 上午11:49:30 <br/>
 * @author liukun
 */
@Component
public class PublicApplyInfoListBeanService extends AbstractBeanService {

    @Resource
    private IPublicApplyService publicApplyService;

    /**
     * 查询依申请公开信息
     *
     * @throws GenerateException
     *
     * @see AbstractBeanService#getObject(JSONObject)
     */


    @Override
    public Object getObject(JSONObject paramObj) throws GenerateException{
        // 访问路径
        Context context = ContextHolder.getContext();
        // 合并map
        MapUtil.unionContextToJson(paramObj);

        Long siteId = context.getSiteId();
        Long organId = paramObj.getLong("organId");

        PublicApplyQueryVO queryVO = new PublicApplyQueryVO();
        queryVO.setSiteId(siteId);
        queryVO.setIsPublish(1);// 查询已发布的文章
        queryVO.setOrganId(organId);

        // 查询分页
        paramObj.put(GenerateConstant.ACTION, PublicConstant.ACTION_LIST);
        queryVO.setPageSize(paramObj.getInteger(GenerateConstant.PAGE_SIZE));// 标签中定义
        queryVO.setPageIndex(context.getPageIndex() - 1);

        Pagination page = publicApplyService.getPublicApplyInfo(queryVO);
        return page;
    }

    /**
     * 预处理跳转链接
     *
     * @see cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService#doProcess(java.lang.Object,
     *      com.alibaba.fastjson.JSONObject)
     */
    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) throws GenerateException {
        Context context = ContextHolder.getContext();
        String action = paramObj.getString(GenerateConstant.ACTION);// 动作
        Map<String, Object> resultMap = super.doProcess(resultObj, paramObj);
        resultMap.put(GenerateConstant.ACTION, action);// 放入动作在页面进行判断
        Long source = context.getSource();
        context.setSource(MessageEnum.PUBLICINFO.value());
        if (PublicConstant.ACTION_LIST.equals(action)) {// 分页时
            Pagination pagination = (Pagination) resultObj;
            pagination.setLinkPrefix(context.getPath());// 放入分页访问地址
            List<?> list = pagination.getData();
            for (Object obj : list) {
                PublicApplyVO vo = (PublicApplyVO) obj;
                String path = PathUtil.getLinkPath(vo.getOrganId(), vo.getContentId());
                vo.setLink(path);
            }
        }
        context.setSource(source);
        return resultMap;
    }

}