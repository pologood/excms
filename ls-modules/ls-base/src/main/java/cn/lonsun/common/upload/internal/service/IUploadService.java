package cn.lonsun.common.upload.internal.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import cn.lonsun.common.upload.internal.entity.AttachmentEO;
import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.enums.SystemCodes;

import javax.servlet.http.HttpServletResponse;

public interface IUploadService extends IBaseService<AttachmentEO>{
    /**
     * 上传附件的方法
     * @Time 2014年9月25日 上午9:51:46
     * @param file 上传的文件
     * @param moduleEnum 模块的枚举
     * @param rootPath 系统根路径
     * @param storageLocation
     * @return
     */
    public List<AttachmentEO> uploadFile(MultipartFile[] file, SystemCodes moduleEnum, String rootPath, Integer storageLocation);
    
    /**
     * 删除附件的方法
     * @Time 2014年9月25日 上午9:52:30
     * @param attachmentId
     * @return
     */
    public boolean deleteFile(Long attachmentId,String rootPath);
  
    /**
     * 删除附件的方法(跨平台)
     * @Time 2014-11-6 14:17:14
     * @param attachmentUuid
     * @param rootPath
     * @return
     */
    public boolean deleteFile(String attachmentUuid,String rootPath);  
    
    /**
     * 批量删除 
     * TODO
     *
     * @author zhusy
     * @param ids
     * @param rootPath
     * @return
     */
     public boolean deleteFiles(Long[] ids,String rootPath);
    /**
     * 批量删除 
     * TODO
     *
     * @author zhusy
     * @param ids
     * @param rootPath
     * @return
     */    
     public boolean deleteFiles(String[] uuids,String rootPath);
    /**
     * 获取附件列表的方法
     * @Time 2014年9月26日 上午10:06:32
     * @param ids 附件的ID数组
     * @return
     */
    public List<AttachmentEO> getAttachs(Long[] ids);
 
    /**
     * 获取附件列表的方法
     * @Time 2014年9月26日 上午10:06:32
     * @param uuids 附件的ID数组
     * @return
     */
    public List<AttachmentEO> getAttachs(String[] uuids);
   
    /**
     * 锁定附件的方法,如果上传的附件锁定，那么系统会在固定的时间把没有使用的附件清除掉
     * @Time 2014年9月26日 上午11:03:58
     * @param ids
     * @param rootPath
     */
    public void lockAttachment(Long[] ids); 
    
    /**
     * 获取附件大小
     * TODO
     *
     * @author zhusy
     * @param id
     * @return
     */
    public long getAttachSize(Long id);
    
    /**
     * 获取附件大小
     * TODO
     *
     * @author zhusy
     * @param id
     * @return
     */
    public long getAttachSize(Long ids[]);
    
    /**
     * 通过uuid获取附件 
     * 
     * @param uuid 附件的唯一标识符
     * @return
     */
    public AttachmentEO getAttachmentEO(String uuid);

    /**
     * 下载文件
     *
     * @param filePath
     * @param fileName
     * @param response
     */
    public void downLoadFile(String filePath, String fileName, HttpServletResponse response);
}
