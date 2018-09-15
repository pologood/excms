package cn.lonsun.common.upload.internal.service.impl;

import cn.lonsun.common.upload.fileManager.FileManager;
import cn.lonsun.common.upload.internal.dao.IUploadDao;
import cn.lonsun.common.upload.internal.entity.AttachmentEO;
import cn.lonsun.common.upload.internal.entity.AttachmentEO.FileStatus;
import cn.lonsun.common.upload.internal.service.IFileServer;
import cn.lonsun.common.upload.internal.service.IUploadService;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.enums.SystemCodes;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.ThreadUtil;
import cn.lonsun.core.util.TipsMode;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("uploadService")
public class UploadServiceImpl extends BaseService<AttachmentEO> implements IUploadService{

    @Autowired
    private IUploadDao uploadDao;

    @Override
    public List<AttachmentEO> uploadFile(MultipartFile[] files, SystemCodes moduleEnum, String rootPath, Integer storageLocation) {
        if(files==null||files.length<1){
            throw new BaseRunTimeException(TipsMode.Message.toString(),"上传文件不能为空");
        }
        List<AttachmentEO> attachs = new ArrayList<AttachmentEO>(files.length);
        if(null != storageLocation && storageLocation.equals(1)){//文件存放至应用服务器
            SimpleDateFormat d = new SimpleDateFormat("yyyyMM");
            Date date = new Date();
            // 用户上传文件目录-upload/userData/moduleName/organId/userId/yyyy/MM/
            StringBuilder fileSavePath = new StringBuilder()
                    .append("upload").append(File.separator)
                    .append(moduleEnum.toString()).append(File.separator)
                    .append(d.format(date)).append(File.separator)
                    .append(ThreadUtil.getLong(ThreadUtil.LocalParamsKey.OrganId)).append(File.separator)
                    .append(ThreadUtil.getString(ThreadUtil.LocalParamsKey.UserId)).append(File.separator);
            File saveFolder = new File(rootPath+fileSavePath.toString());
            if (!saveFolder.exists()) {
                saveFolder.mkdirs();
            }
            for(MultipartFile file:files){
                // 判断文件是否为空
                if (!file.isEmpty()) {
                    //以当前精确到秒的日期为上传的文件的文件名
                    String oldFileName = file.getOriginalFilename();
                    String fileType = getType(oldFileName);
                    // 保存文件的新的名称
                    String uuid = AppUtil.getUuid();
                    String newFileName = uuid +"."+fileType;
                    // 包含路径信息的全路径文件地址
                    String newFullFileName = rootPath+fileSavePath.append(newFileName).toString();
                    // 创建File对象
                    File savedFile = new File(newFullFileName);
                    /*保存文件*/
                    try {
                        FileUtils.copyInputStreamToFile(file.getInputStream(), savedFile);
                        //  保存到数据库
                        AttachmentEO attach = new AttachmentEO();
                        attach.setFileTyle(fileType);
                        attach.setAttachmentUuid(uuid);
                        attach.setFileSize(file.getSize());
                        attach.setFileName(file.getOriginalFilename());
                        attach.setFilePath(File.separator + fileSavePath.toString());
                        attach.setStorageLocation(1);
                        saveEntity(attach);
                        attachs.add(attach);
                    } catch (Exception e) {
                        throw new BaseRunTimeException(TipsMode.Message.toString(), "文件上传保存出错");
                    }
                }
            }
        }else{//文件存放至文件服务器
            IFileServer fs = FileManager.getFileServer();
            for(MultipartFile file:files){
                // 判断文件是否为空
                if (!file.isEmpty()) {
                    //以当前精确到秒的日期为上传的文件的文件名
                    String oldFileName = file.getOriginalFilename();
                    String fileType = getType(oldFileName);
                    // 保存文件的新的名称
                    String uuid = AppUtil.getUuid();
                    try {
                        String filePath = fs.uploadMultipartFile(file, fileType);
                        //  保存到数据库
                        AttachmentEO attach = new AttachmentEO();
                        attach.setFileTyle(fileType);
                        attach.setAttachmentUuid(uuid);
                        attach.setFileSize(file.getSize());
                        attach.setFileName(file.getOriginalFilename());
                        attach.setFilePath(filePath);
                        attach.setStorageLocation(0);
                        saveEntity(attach);
                        attachs.add(attach);
                    } catch (Exception e) {
                        throw new BaseRunTimeException(TipsMode.Message.toString(), "文件上传保存出错");
                    }
                }
            }
        }
        return attachs;
    }

    private String getType(String fileName) {
        int index = fileName.lastIndexOf(".");
           if (index != -1) {
               String suffix = fileName.substring(index + 1);//后缀
               return suffix;
           } else {
               return null;
           }
    }

    @Override
    public boolean deleteFile(Long attachmentId,String rootPath) {
        boolean flag = false;
        AttachmentEO att = getEntity(AttachmentEO.class, attachmentId);
        if(att!=null){
            try {
                System.out.println("删除物理文件,文件路径"+att.getFilePath());
                flag = FileManager.getFileServer().deleteFile(att.getFilePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
            delete(att);
        }
        return flag;
    }

    @Override
    public boolean deleteFile(String attachmentUuid,String rootPath) {
        boolean flag = false;
        AttachmentEO att = uploadDao.getAttachmentEO(attachmentUuid);
        if(att!=null){
//            if(rootPath.endsWith(File.separator)){
//                rootPath = rootPath.substring(0, rootPath.length()-File.separator.length());
//            }
//            String filePath = rootPath.concat(att.getFilePath());
//            File file = new File(filePath);
//            if(file.exists()){
//                file.delete();
//            }
            try {
                //flag = FdfsUtil.getFileServer().deleteFile(att.getFilePath());
                System.out.println("删除物理文件,文件路径"+att.getFilePath());
                flag = FileManager.getFileServer().deleteFile(att.getFilePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
            delete(att);
        }
        return flag;
    }

   /**
    * 批量删除
    * TODO
    *
    * @author zhusy
    * @param ids
    * @param rootPath
    * @return
    */
    public boolean deleteFiles(Long[] ids,String rootPath){
    	if(null != ids && ids.length>0){
    		List<AttachmentEO> list = getEntities(AttachmentEO.class, ids);
    		for(AttachmentEO eo : list){
    			if(eo != null){
                    try {
                        System.out.println("删除物理文件,文件路径"+eo.getFilePath());
                        FileManager.getFileServer().deleteFile(eo.getFilePath());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                    delete(eo);
    			}
    		}
    		return true;
    	}
    	return false;
    }

   /**
    * 批量删除
    * TODO
    *
    * @author zhangx
    * @param uuids
    * @param rootPath
    * @return
    */
    @Override
    public boolean deleteFiles(String[] uuids,String rootPath){
        if(null != uuids && uuids.length>0){
            List<AttachmentEO> list = uploadDao.getAttachs(uuids);
            if(rootPath.endsWith(File.separator)){
                rootPath = rootPath.substring(0, rootPath.length()-File.separator.length());
            }
            for(AttachmentEO eo : list){
                if(eo != null){
                    String filePath = rootPath.concat(eo.getFilePath());
                    File file = new File(filePath);
                    if(file.exists()){
                        file.delete();
                    }
                    delete(eo);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public List<AttachmentEO> getAttachs(Long[] ids) {
        return getEntities(AttachmentEO.class, ids);
    }

    @Override
    public List<AttachmentEO> getAttachs(String[] uuids) {
        return uploadDao.getAttachs(uuids);
    }

    @Override
    public void lockAttachment(Long[] ids) {
        List<AttachmentEO> atts = getEntities(AttachmentEO.class, ids);
        for(AttachmentEO att:atts){
            if(att!=null){
                att.setFileStatus(FileStatus.Locked.toString());
                updateEntity(att);
            }
        }
    }

    /**
     * 获取附件大小
     * TODO
     *
     * @author zhusy
     * @param id
     * @return
     */
    @Override
    public long getAttachSize(Long id){
    	if(null == id)
    		return 0L;
    	return this.getEntity(AttachmentEO.class, id).getFileSize();
    }

    /**
     * 获取附件大小
     * TODO
     *
     * @author zhusy
     * @param ids
     * @return
     */
    @Override
    public long getAttachSize(Long ids[]){
    	if(null == ids)
    		return 0L;
    	if(0 == ids.length)
    		return 0L;
    	 List<AttachmentEO> atts = getEntities(AttachmentEO.class, ids);
    	 long fileSize = 0L;
    	 for(AttachmentEO att:atts){
             if(att!=null){
            	 fileSize+=att.getFileSize();
             }
         }
    	 return fileSize;
    }

    @Override
    public AttachmentEO getAttachmentEO(String uuid){

        return uploadDao.getAttachmentEO(uuid);
    }

    @Override
    public void downLoadFile(String filePath, String fileName, HttpServletResponse response) {
        byte[] b = null;
        java.io.BufferedOutputStream bos = null;
        try {
            //b = FdfsUtil.getFileServer().getFileByID(filePath);
            b = FileManager.getFileServer().getFileByID(filePath);

            bos = new BufferedOutputStream(response.getOutputStream());
//            int b = FileManager.getFileServer().downloadByID(filePath, bos);

            response.setContentType("application/x-msdownload;");
            //response.setHeader("Content-disposition", "attachment; filename="+new String(name.getBytes(),"iso-8859-1"));
            response.setHeader("Content-disposition", "attachment; filename="+ URLEncoder.encode(fileName, "UTF-8"));
            response.setHeader("Content-Length", String.valueOf(b.length));

            bos.write(b);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        System.exit(0);
    }
}
