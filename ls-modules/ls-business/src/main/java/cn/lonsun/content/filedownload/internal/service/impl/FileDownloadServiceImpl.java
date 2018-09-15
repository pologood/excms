package cn.lonsun.content.filedownload.internal.service.impl;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.filedownload.internal.dao.IFileDownloadDao;
import cn.lonsun.content.filedownload.internal.entity.FileDownloadEO;
import cn.lonsun.content.filedownload.internal.service.IFileDownloadService;
import cn.lonsun.content.filedownload.vo.FileDownloadVO;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.mongodb.entity.ContentMongoEO;
import cn.lonsun.mongodb.service.ContentMongoServiceImpl;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.FileUploadUtil;
import cn.lonsun.util.SysLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-11-23<br/>
 */
@Service("fileDownloadService")
public class FileDownloadServiceImpl extends MockService<FileDownloadEO> implements IFileDownloadService {
    @Autowired
    private IFileDownloadDao downloadDao;
    @Autowired
    private IBaseContentService contentService;

    @Autowired
    private ContentMongoServiceImpl contentMongoService;

    @Override
    public Pagination getPage(ContentPageVO pageVO) {
        return downloadDao.getPage(pageVO);
    }

    @Override
    public FileDownloadVO getVO(Long id) {
        return downloadDao.getVO(id);
    }

    @Override
    public void saveVO(FileDownloadVO vo) {
        BaseContentEO eo = new BaseContentEO();
        if (vo.getId() == null) {
            eo.setSiteId(vo.getSiteId());
            eo.setColumnId(vo.getColumnId());
            eo.setTitle(vo.getTitle());
            eo.setImageLink(vo.getImageLink());
            eo.setTypeCode(BaseContentEO.TypeCode.fileDownload.toString());
            eo.setIsTop(vo.getIsTop());
            eo.setIsPublish(vo.getIsPublish());
            eo.setPublishDate(vo.getPublishDate());
            contentService.saveEntity(eo);
            CacheHandler.saveOrUpdate(BaseContentEO.class, eo);
            SysLog.log("添加文件下载 >> ID：" + eo.getId() + "，标题：" + eo.getTitle(),
                "BaseContentEO", CmsLogEO.Operation.Add.toString());

            FileDownloadEO downloadEO = new FileDownloadEO();
            downloadEO.setContentId(eo.getId());
            downloadEO.setFilePath(vo.getFilePath());
            downloadEO.setFileName(vo.getFileName());
            //downloadEO.setAddDate(vo.getAddDate());
            downloadEO.setCount(vo.getCount());
            saveEntity(downloadEO);
        } else {
            eo = contentService.getEntity(BaseContentEO.class, vo.getId());
            if (eo != null) {
                FileUploadUtil.deleteFileCenterEO(eo.getImageLink());
                eo.setSiteId(vo.getSiteId());
                eo.setColumnId(vo.getColumnId());
                eo.setTitle(vo.getTitle());
                eo.setImageLink(vo.getImageLink());
                eo.setIsTop(vo.getIsTop());
                eo.setIsPublish(vo.getIsPublish());
                eo.setPublishDate(vo.getPublishDate());
                contentService.updateEntity(eo);
                CacheHandler.saveOrUpdate(BaseContentEO.class, eo);
                SysLog.log("修改文件下载 >> ID：" + eo.getId() + "，标题：" + eo.getTitle(),
                    "BaseContentEO", CmsLogEO.Operation.Update.toString());

                Map<String, Object> map = new HashMap<String, Object>();
                map.put("contentId", vo.getId());
                map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
                List<FileDownloadEO> list = getEntities(FileDownloadEO.class, map);
                if (list != null && list.size() > 0) {
                    FileDownloadEO downloadEO = list.get(0);
                    FileUploadUtil.deleteFileCenterEO(eo.getImageLink());
                    downloadEO.setFilePath(vo.getFilePath());
                    downloadEO.setFileName(vo.getFileName());
                    //downloadEO.setAddDate(vo.getAddDate());
                    downloadEO.setCount(vo.getCount());
                    updateEntity(downloadEO);
                }
            }
        }
        //编辑器保存
        if (BaseContentEO.TypeCode.fileDownload.toString().equals(vo.getTypeCode()) && !AppUtil.isEmpty(vo.getRemarks())) {
            ContentMongoEO _eo = new ContentMongoEO();
            _eo.setId(eo.getId());
            _eo.setContent(vo.getRemarks());
            contentMongoService.save(_eo);
        }

        if (!AppUtil.isEmpty(vo.getFilePath())) {
            FileUploadUtil.saveFileCenterEO(vo.getFilePath());
        }
        if (!AppUtil.isEmpty(vo.getImageLink())) {
            FileUploadUtil.saveFileCenterEO(vo.getImageLink());
        }
    }

    @Override
    public void deleteVOs(String ids) {
        Long[] idArr = AppUtil.getLongs(ids, ",");
        List<BaseContentEO> list = contentService.getEntities(BaseContentEO.class, idArr);
        if (list != null && list.size() > 0) {
            String[] arr = new String[list.size() * 2];
            int i = 0;
            for (BaseContentEO baseEO : list) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("contentId", baseEO.getId());
                List<FileDownloadEO> list1 = getEntities(FileDownloadEO.class, map);
                //删除文件下载辅表
                if (list1 != null && list1.size() > 0) {
                    FileDownloadEO eo = list1.get(0);
                    if (!AppUtil.isEmpty(eo.getFilePath())) {
                        arr[i++] = eo.getFilePath();
                    }
                }
                //删除主表
                contentService.delete(baseEO);
                CacheHandler.delete(BaseContentEO.class, baseEO);
                if (!AppUtil.isEmpty(baseEO.getImageLink())) {
                    arr[i++] = baseEO.getImageLink();
                }
            }
            SysLog.log("删除文件下载 >> ID：" + ids,
                "BaseContentEO", CmsLogEO.Operation.Delete.toString());
            FileUploadUtil.deleteFileCenterEO(arr);
        }
    }

    @Override
    public void addCount(Long downId) {
        FileDownloadEO downEO = getEntity(FileDownloadEO.class, downId);
        downEO.setCount(downEO.getCount() + 1);
        saveEntity(downEO);
    }
}
