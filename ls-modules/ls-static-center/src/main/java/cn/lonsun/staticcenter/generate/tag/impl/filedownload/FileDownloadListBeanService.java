package cn.lonsun.staticcenter.generate.tag.impl.filedownload;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import cn.lonsun.util.ModelConfigUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import cn.lonsun.content.filedownload.internal.dao.IFileDownloadDao;
import cn.lonsun.content.filedownload.internal.service.IFileDownloadService;
import cn.lonsun.content.filedownload.vo.FileDownloadVO;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.staticcenter.service.HtmlEnum;

import com.alibaba.fastjson.JSONObject;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-12-15<br/>
 */
@Component
public class FileDownloadListBeanService extends AbstractBeanService {
    @Resource
    private IFileDownloadDao downloadDao;

    @Resource
    private IFileDownloadService downloadService;

    @Override
    public Object getObject(JSONObject paramObj) {

        Long[] ids = super.getQueryColumnIdByChild(paramObj, BaseContentEO.TypeCode.fileDownload.toString());
        if (null == ids) {
            return null;
        }
        Context context = ContextHolder.getContext();
        Long columnId = context.getColumnId();
        if (ids != null && ids.length > 0) {// 当传入多栏目时，依第一个栏目为准
            columnId = ids[0];
        }
        Integer size = ids.length;
        Map<String, Object> map = new HashMap<String, Object>();
        Integer num = paramObj.getInteger(GenerateConstant.NUM);
        String where = paramObj.getString(GenerateConstant.WHERE);
        StringBuffer hql = new StringBuffer(" select c.id as id, c.title as title,c.columnId as columnId,c.siteId as siteId ");
        hql.append(" ,c.imageLink as imageLink ,c.remarks as remarks,c.publishDate as publishDate,c.isPublish as isPublish,c.isTop as isTop");
        hql.append(" ,f.addDate as addDate ,f.count as count,f.filePath as filePath,f.fileName as fileName,f.id as downId");
        hql.append(" from BaseContentEO c, FileDownloadEO f where c.id=f.contentId and c.typeCode=:typeCode");
        hql.append(" and c.recordStatus=:recordStatus and f.recordStatus=:recordStatus and c.isPublish=1 and c.siteId=:siteId ");
        hql.append(" and c.columnId").append(size == 1 ? " =:ids " : " in (:ids) ");
        hql.append(StringUtils.isEmpty(where) ? "" : " AND " + where);
        hql.append(ModelConfigUtil.getOrderByHql(columnId,context.getSiteId(),BaseContentEO.TypeCode.fileDownload.toString()));
        map.put("typeCode", BaseContentEO.TypeCode.fileDownload.toString());
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        map.put("siteId", ContextHolder.getContext().getSiteId());
        if (size == 1) {
            map.put("ids", ids[0]);
        } else {
            map.put("ids", ids);
        }
        return downloadDao.getBeansByHql(hql.toString(), map, FileDownloadVO.class, num);
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
        List<FileDownloadVO> list = (List<FileDownloadVO>) resultObj;
        if (null != list && !list.isEmpty()) {
            Context context = ContextHolder.getContext();// 上下文
            Boolean isDetail = Boolean.parseBoolean(context.getParamMap().get("isDetail"));
            if (isDetail == null) {
                isDetail = false;
            }
            // 处理文章链接
            for (FileDownloadVO vo : list) {
                // FileDownloadVO vo = downloadService.getVO(eo.getId());
                if (isDetail) {
                    // String path = PathUtil.getLinkPath(vo.getColumnId(),
                    // vo.getId());//拿到栏目页和文章页id
                    String path = PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(), HtmlEnum.ACRTILE.getValue(), vo.getColumnId(), vo.getId());
                    vo.setLink(path);
                } else {
                    if (StringUtils.isEmpty(vo.getFilePath())) {
                        vo.setLink(vo.getFileName());
                    } else {
                        vo.setLink(context.getUri() + "/download/" + vo.getFilePath());
                    }
                }
            }
        }
        return super.doProcess(resultObj, paramObj);
    }
}