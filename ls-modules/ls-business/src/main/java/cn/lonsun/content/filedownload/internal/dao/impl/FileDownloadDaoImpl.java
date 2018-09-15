package cn.lonsun.content.filedownload.internal.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.filedownload.internal.dao.IFileDownloadDao;
import cn.lonsun.content.filedownload.internal.entity.FileDownloadEO;
import cn.lonsun.content.filedownload.vo.FileDownloadVO;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.util.ModelConfigUtil;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-11-23<br/>
 */
@Repository("fileDownloadDao")
public class FileDownloadDaoImpl extends MockDao<FileDownloadEO> implements IFileDownloadDao {
    @Override
    public Pagination getPage(ContentPageVO pageVO) {
        StringBuffer hql = new StringBuffer(" select c.id as id, c.title as title,c.columnId as columnId,c.siteId as siteId ")
            .append(" ,c.imageLink as imageLink ,c.remarks as remarks,c.publishDate as publishDate,c.isPublish as isPublish,c.isTop as isTop")
            .append(" ,f.addDate as addDate ,f.count as count,f.filePath as filePath,f.fileName as fileName,f.id as downId")
            .append(" from BaseContentEO c, FileDownloadEO f where c.id=f.contentId and c.typeCode=? ")
            .append(" and c.recordStatus=? and f.recordStatus=? ");

        List<Object> values=new ArrayList<Object>();
        values.add(BaseContentEO.TypeCode.fileDownload.toString());
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(AMockEntity.RecordStatus.Normal.toString());
        if (!AppUtil.isEmpty(pageVO.getTitle())) {
            hql.append(" and c.title like ?");
            values.add("%".concat(pageVO.getTitle().trim()).concat("%"));
        }
        if (!AppUtil.isEmpty(pageVO.getColumnId())) {
            hql.append(" and c.columnId=? ");
            values.add(pageVO.getColumnId());
        } else {
            hql.append(" and c.columnId is null ");
        }
        if (!AppUtil.isEmpty(pageVO.getCondition()) && pageVO.getStatus() != null) {
            if (pageVO.getCondition() == "isPublish") {
                hql.append(" and c." + pageVO.getCondition() + "=? ");
                values.add(pageVO.getStatus());
            } else {
                hql.append(" and f." + pageVO.getCondition() + "=? ");
                values.add(pageVO.getStatus());
            }

        }
        hql.append(ModelConfigUtil.getOrderByHql(pageVO.getColumnId(), pageVO.getSiteId(), BaseContentEO.TypeCode.fileDownload.toString()));
        return getPagination(pageVO.getPageIndex(), pageVO.getPageSize(), hql.toString(), values.toArray(), FileDownloadVO.class);
    }

    @Override
    public FileDownloadVO getVO(Long id) {
        StringBuffer hql = new StringBuffer(" select c.id as id, c.title as title,c.columnId as columnId,c.siteId as siteId ")
            .append(" ,c.imageLink as imageLink ,c.remarks as remarks,c.publishDate as publishDate,c.isPublish as isPublish,c.isTop as isTop")
            .append(" ,f.addDate as addDate,f.count as count,f.filePath as filePath,f.fileName as fileName,f.id as downId")
            .append(" from BaseContentEO c, FileDownloadEO f where c.id=f.contentId and c.typeCode='" + BaseContentEO.TypeCode.fileDownload.toString() + "'")
            .append(" and c.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "' and f.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "' ");

        if (id != null) {
            hql.append(" and c.id=" + id);
        }
        List<FileDownloadVO> list = (List<FileDownloadVO>) getBeansByHql(hql.toString(), new Object[]{}, FileDownloadVO.class);
        FileDownloadVO vo = null;
        if (list.size() > 0) {
            vo = list.get(0);
        }
        return vo;
    }

}
