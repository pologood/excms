package cn.lonsun.staticcenter.generate.tag.impl.filedownload;

import cn.lonsun.content.filedownload.internal.dao.IFileDownloadDao;
import cn.lonsun.content.filedownload.vo.FileDownloadVO;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.staticcenter.service.HtmlEnum;
import cn.lonsun.util.ModelConfigUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-12-15<br/>
 */
@Component
public class FileDownloadPageListBeanService extends AbstractBeanService {
    @Resource
    private IFileDownloadDao downDao;

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        Long columnId = paramObj.getLong(GenerateConstant.ID);
        // 此写法是为了使得在页面这样调用也能解析
        if (null == columnId) {// 如果栏目id为空说明，栏目id是在页面传入的
            columnId = context.getColumnId();
        }
        //需要显示条数.
        Integer pageSize = paramObj.getInteger(GenerateConstant.PAGE_SIZE);
        Long pageIndex = context.getPageIndex() == null ? 0 : (context.getPageIndex() - 1);

        StringBuffer hql = new StringBuffer(" select c.id as id, c.title as title,c.columnId as columnId,c.siteId as siteId ")
                .append(" ,c.imageLink as imageLink ,c.remarks as remarks,c.publishDate as publishDate,c.isPublish as isPublish,c.isTop as isTop")
                .append(" ,f.addDate as addDate ,f.count as count,f.filePath as filePath,f.fileName as fileName,f.id as downId")
                .append(" from BaseContentEO c, FileDownloadEO f where c.id=f.contentId and c.typeCode=?")
                .append(" and c.recordStatus=? and f.recordStatus=? and c.columnId=? and c.siteId=? and c.isPublish=1")
                .append(ModelConfigUtil.getOrderByHql(columnId,context.getSiteId(),BaseContentEO.TypeCode.fileDownload.toString()));
        List<Object> values = new ArrayList<Object>();
        values.add(BaseContentEO.TypeCode.fileDownload.toString());
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(columnId);
        values.add(context.getSiteId());
        return downDao.getPagination(pageIndex, pageSize, hql.toString(), values.toArray(), FileDownloadVO.class);

    }

    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) throws GenerateException {

        // 获取分页对象
        Pagination pagination = (Pagination) resultObj;
        if (null != pagination) {
            Context context = ContextHolder.getContext();// 上下文
            Boolean isDetail=Boolean.parseBoolean(context.getParamMap().get("isDetail"));
            if(isDetail==null){
                isDetail=false;
            }
            List<?> resultList = pagination.getData();
            // 处理查询结果
            if (null != resultList && !resultList.isEmpty()) {
                for (Object o : resultList) {
                    FileDownloadVO vo = (FileDownloadVO) o;
                    if(isDetail){
                        // String path = PathUtil.getLinkPath(vo.getColumnId(), vo.getId());//拿到栏目页和文章页id
                        String path = PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(), HtmlEnum.ACRTILE.getValue(), vo.getColumnId(), vo.getId());
                        vo.setLink(path);
                    }else{
                        if(StringUtils.isEmpty(vo.getFilePath())){
                            vo.setLink(vo.getFileName());
                        }else{
                            if(vo.getFilePath().indexOf(".")==-1){
                                vo.setLink(context.getUri()+"/download/"+vo.getFilePath());
                            }else{
                                vo.setLink(context.getUri()+vo.getFilePath());
                            }
                        }
                    }
                }
            }

            // 设置连接地址
            /*String path = PathUtil.getLinkPath(context.getColumnId(), null);
            path = path.substring(0, path.lastIndexOf("_"));
            pagination.setLinkPrefix(path);*/
            String path = PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(), HtmlEnum.COLUMN.getValue(), context.getColumnId(), null);
            pagination.setLinkPrefix(path);

        }

        return super.doProcess(resultObj, paramObj);
    }
}
