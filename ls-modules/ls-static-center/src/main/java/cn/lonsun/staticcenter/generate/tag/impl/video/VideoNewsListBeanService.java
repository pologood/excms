package cn.lonsun.staticcenter.generate.tag.impl.video;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import cn.lonsun.content.internal.dao.IVideoNewsDao;
import cn.lonsun.content.vo.VideoNewsVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import cn.lonsun.content.internal.dao.IBaseContentDao;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.util.ModelConfigUtil;

import com.alibaba.fastjson.JSONObject;

import static cn.lonsun.staticcenter.generate.util.PathUtil.getPathConfig;
import static org.jsoup.nodes.Entities.EscapeMode.base;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-12-2<br/>
 */
@Component
public class VideoNewsListBeanService extends AbstractBeanService {

    @Resource
    private IVideoNewsDao videoNewsDao;

    @Override
    public Object getObject(JSONObject paramObj) {
        Long[] ids = super.getQueryColumnIdByChild(paramObj, BaseContentEO.TypeCode.videoNews.toString());
        if (null == ids) {
            return null;
        }
        Context context = ContextHolder.getContext();
        Long columnId = context.getColumnId();
        String inIds = paramObj.getString(GenerateConstant.ID);
        if (StringUtils.isNotEmpty(inIds)) {// 当传入多栏目时，依第一个栏目为准
            columnId = Long.valueOf(StringUtils.split(inIds, ",")[0]);
        }
        Integer size = ids.length;
        Map<String, Object> map = new HashMap<String, Object>();
        Integer num = paramObj.getInteger(GenerateConstant.NUM);
        String where = paramObj.getString(GenerateConstant.WHERE);
        StringBuffer hql =
                new StringBuffer(" select c.id as id, c.title as title,c.titleColor as titleColor,c.columnId as columnId,c.siteId as siteId,c.hit as hit ")
                        .append(" ,c.imageLink as imageLink ,c.author as author,c.publishDate as publishDate")
                        .append(",v.videoId as videoId,v.videoPath as videoPath,v.videoName as videoName,v.status as status")
                        .append(" from BaseContentEO c, VideoNewsEO v where c.id=v.contentId  and c.typeCode=:typeCode")
                        .append(" and c.columnId").append(size == 1 ? " =:ids " : " in (:ids) ")
                        .append(" and c.siteId=:siteId and c.recordStatus=:recordStatus and c.isPublish=1");

        hql.append(StringUtils.isEmpty(where) ? "" : " AND " + where);
        hql.append(" ").append(ModelConfigUtil.getOrderByHql(columnId, context.getSiteId(),BaseContentEO.TypeCode.videoNews.toString()));
        map.put("typeCode",BaseContentEO.TypeCode.videoNews.toString());
        if (size == 1) {
            map.put("ids", ids[0]);
        } else {
            map.put("ids", ids);
        }
        map.put("siteId", context.getSiteId());
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        return videoNewsDao.getBeansByHql(hql.toString(), map, VideoNewsVO.class, num);
    }

    /**
     *
     * 预处理数据
     *
     * @throws GenerateException
     *
     * @see cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService#doProcess(java.lang.Object,
     *      java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) throws GenerateException {
        // 预处理数据，处理文章链接问题
        List<VideoNewsVO> list = (List<VideoNewsVO>) resultObj;
        if (null != list && !list.isEmpty()) {
//            String fileServerPath=PathUtil.getPathConfig().getFileServerPath();
            String videoName=null;
            String videoPath=null;
            // 处理文章链接
            for (VideoNewsVO eo : list) {
                eo.setLink(PathUtil.getLinkPath(eo.getColumnId(), eo.getId()));
                videoName=eo.getVideoName();
                videoPath=eo.getVideoPath();
                if (!StringUtils.isEmpty(videoName)&&!videoName.equals(videoPath)) {
//                    videoPath = fileServerPath + videoPath;
                    eo.setVideoPath(PathUtil.getUrl(eo.getVideoPath()));
                }else{
                    eo.setVideoPath(videoName);
                }
            }
        }
        return super.doProcess(resultObj, paramObj);
    }
}