package cn.lonsun.util;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.util.IpUtil;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.mongodb.base.IMongoDbFileServer;
import cn.lonsun.mongodb.vo.MongoFileVO;
import cn.lonsun.site.site.internal.service.IWaterMarkConfigService;
import cn.lonsun.system.filecenter.internal.entity.FileCenterEO;
import cn.lonsun.system.filecenter.internal.service.IFileCenterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;

/**
 * @ClassName: FileUploadUtil
 * @Description: file upload util
 * @author Hewbing
 * @date 2015年11月18日 上午10:24:27
 * 
 */
public class FileUploadUtil {
    static Logger logger = LoggerFactory.getLogger(FileUploadUtil.class);
    private static IMongoDbFileServer mongoDbFileServer = SpringContextHolder.getBean("mongoDbFileServer");
    private static IFileCenterService fileCenterService = SpringContextHolder.getBean("fileCenterService");
    private static IWaterMarkConfigService waterMarkConfigService = SpringContextHolder.getBean("waterMarkConfigService");


    /**
     * 
     * @Title: uploadUtil
     * @Description: flash upload with no session
     * @param Filedata
     * @param
     * @param type
     * @param siteId
     * @param columnId
     * @param contentId
     * @param sessionId
     * @param
     * @return MongoFileVO
     * @throws
     */
    public static MongoFileVO uploadUtil(MultipartFile Filedata, String type, String code, Long siteId, Long columnId, Long contentId, String desc,
            String sessionId) {
        MongoFileVO mongoVO = mongoDbFileServer.uploadMultipartFile(Filedata, null);
        String fileName = Filedata.getOriginalFilename();
        String singleName = fileName.substring(0, fileName.lastIndexOf("."));
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        String md5 = null;
        try {
            md5 = MD5Util.getMd5ByByte(Filedata.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileCenterEO fileCenter = transFileEO(singleName, suffix, Filedata.getSize(), md5, mongoVO.getMongoId(),mongoVO.getFileName(), type, code, siteId, columnId, contentId, desc);
        fileCenterService.saveEntity(fileCenter);
        return mongoVO;
    }

    /**
     * 
     * @Title: uploadUtil
     * @Description: TODO
     * @param b
     * @param fileName
     * @param type
     * @param siteId
     * @param columnId
     * @param contentId
     * @param sessionId
     * @param
     * @return MongoFileVO 返回类型
     * @throws
     */
    public static MongoFileVO uploadUtil(byte[] b, String fileName, String type, String code, Long siteId, Long columnId, Long contentId, String desc,
            String sessionId) {

        MongoFileVO mongoVO = mongoDbFileServer.uploadByteFile(b, fileName, null, null);
        String singleName = fileName.substring(0, fileName.lastIndexOf("."));
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        String md5 = MD5Util.getMd5ByByte(b);
        FileCenterEO fileCenter = transFileEO(singleName, suffix, (long) b.length, md5, mongoVO.getMongoId(),mongoVO.getFileName(), type, code, siteId, columnId, contentId, desc);
        fileCenterService.saveEntity(fileCenter);
        return mongoVO;
    }

    /**
     * 
     * @Title: uploadUtil
     * @Description: No session upload file to mongoDB
     * @param b
     * @param fileEO
     * @return Parameter
     * @return MongoFileVO return type
     * @throws
     */
    public static MongoFileVO uploadUtil(byte[] b, FileCenterEO fileEO) {
        String fileName = fileEO.getFileName();
        MongoFileVO mongoVO = mongoDbFileServer.uploadByteFile(b, fileName + "." + fileEO.getSuffix(), null, null);
        // String singleName = fileName.substring(0, fileName.lastIndexOf("."));
        // String suffix = fileName.substring(fileName.lastIndexOf(".") +
        // 1,fileName.length());
        String md5 = MD5Util.getMd5ByByte(b);
        fileEO.setFileSize((long) b.length);
        fileEO.setMd5(md5);
        fileEO.setMongoId(mongoVO.getMongoId());
        fileEO.setMongoName(mongoVO.getFileName());
        fileCenterService.saveEntity(fileEO);
        // logger.error("============>  success <======="+fileEO.getId());
        return mongoVO;
    }

    /**
     * 
     * @Title: uploadUtil
     * @Description: No session upload file to mongoDB
     * @param Filedata
     * @param fileEO
     * @return Parameter
     * @return MongoFileVO return type
     * @throws
     */
    public static MongoFileVO uploadUtil(MultipartFile Filedata, FileCenterEO fileEO) {
        MongoFileVO mongoVO = mongoDbFileServer.uploadMultipartFile(Filedata, null);
        String fileName = Filedata.getOriginalFilename();
        String singleName = fileName.substring(0, fileName.lastIndexOf("."));
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        String md5 = null;
        fileEO.setFileName(singleName);
        fileEO.setSuffix(suffix);
        fileEO.setMd5(md5);
        fileEO.setFileSize(Filedata.getSize());
        try {
            md5 = MD5Util.getMd5ByByte(Filedata.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileCenterService.doSave(fileEO);
        return mongoVO;
    }

    /**
     * 
     * @Title: uploadUtil
     * @Description: 上传工具，带request
     * @param Filedata
     * @param type
     * @param siteId
     * @param columnId
     * @param contentId
     * @param request
     * @param
     * @return MongoFileVO 返回类型
     * @throws
     */
    public static MongoFileVO uploadUtil(MultipartFile Filedata, String type, String code, Long siteId, Long columnId, Long contentId, String desc,
            HttpServletRequest request) {
        MongoFileVO mongoVO = mongoDbFileServer.uploadMultipartFile(Filedata, null);
        String fileName = Filedata.getOriginalFilename();
        String singleName = fileName.substring(0, fileName.lastIndexOf("."));
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        String md5 = null;
        try {
            md5 = MD5Util.getMd5ByByte(Filedata.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileCenterEO fileCenter = transFileEO(singleName, suffix, Filedata.getSize(), md5, mongoVO.getMongoId(),mongoVO.getFileName(), type, code, siteId, columnId, contentId, desc);
        fileCenterService.saveEntity(fileCenter);
        return mongoVO;
    }


    /**
     * 
     * @Title: uploadUtil
     * @Description:
     * @param b
     * @param fileName
     * @param type
     * @param siteId
     * @param columnId
     * @param contentId
     * @param request
     * @param
     * @return MongoFileVO 返回类型
     * @throws
     */
    public static MongoFileVO uploadUtil(byte[] b, String fileName, String type, String code, Long siteId, Long columnId, Long contentId, String desc,
            HttpServletRequest request) {
        MongoFileVO mongoVO = mongoDbFileServer.uploadByteFile(b, fileName, null, null);
        String singleName = fileName.substring(0, fileName.lastIndexOf("."));
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        String md5 = MD5Util.getMd5ByByte(b);
        FileCenterEO fileCenter = transFileEO(singleName, suffix, (long) b.length, md5, mongoVO.getMongoId(),mongoVO.getFileName(), type, code, siteId, columnId, contentId, desc);
        fileCenterService.saveEntity(fileCenter);
        return mongoVO;
    }

    /**
     * 
     * @Title: setStatus
     * @Description: set quote status NO.1:has quoted NO.2:No reference 设置文件引用状态
     *               ，1：标记引用 0：标记未引用
     * @param mongoIds
     * @param status
     * @return void
     * @throws
     */
    public static void setStatus(String[] mongoIds, Integer status) {
        if (!AppUtil.isEmpty(mongoIds)) {
            fileCenterService.setStatus(mongoIds, status);
        }
    }

    /**
     * 
     * @Title: setStatus
     * @Description: TODO
     * @param mongoId
     * @param status
     * @return void
     * @throws
     */
    public static void setStatus(String mongoId, Integer status) {
        if (null != mongoId) {
            fileCenterService.setStatus(new String[] { mongoId }, status);
        }
    }

    /**
     * 
     * @Title: setStatus
     * @Description: TODO
     * @param mongoId
     * @param status
     * @param ids
     *            , first param is contentId,second is columnId,third is siteId
     * @return void
     * @throws
     */
    public static void setStatus(String mongoId, Integer status, Long... ids) {
        if (null != mongoId) {
            fileCenterService.setFileEO(new String[] { mongoId }, status, ids);
        }
    }

    /**
     * 
     * @Title: setStatus
     * @Description: TODO
     * @param mongoIds
     * @param status
     * @param ids
     *            , first param is contentId,second is columnId,third is siteId
     * @return void
     * @throws
     */
    public static void setStatus(String[] mongoIds, Integer status, Long... ids) {
        if (null != mongoIds) {
            fileCenterService.setFileEO(mongoIds, status, ids);
        }
    }

    public static void setStatus(Long contentId) {

    }

    // save method
    public static void saveFileCenterEO(String[] mongoIds) {
        if (!AppUtil.isEmpty(mongoIds)) {
            fileCenterService.setStatus(mongoIds, 1);
        }
    }

    public static void saveFileCenterEO(String mongoId) {
        if (null != mongoId) {
            fileCenterService.setStatus(new String[] { mongoId }, 1);
        }
    }

    // delete method
    public static void deleteFileCenterEO(String mongoId) {
        if (null != mongoId) {
            fileCenterService.setStatus(new String[] { mongoId }, 0);
        }
    }

    public static void deleteFileCenterEO(String[] mongoIds) {
        if (!AppUtil.isEmpty(mongoIds)) {
            fileCenterService.setStatus(mongoIds, 0);
        }
    }

    public static FileCenterEO transFileEO(String singleName, String suffix, Long size, String md5, String mongoId, String mongoName,String type, String code, Long siteId,
            Long columnId, Long contentId, String desc) {
        if (AppUtil.isEmpty(siteId))
            siteId = (Long) LoginPersonUtil.getSiteId();
        FileCenterEO fileCenter = new FileCenterEO();
        fileCenter.setFileName(singleName);
        fileCenter.setSuffix(suffix);
        fileCenter.setFileSize(size);
        fileCenter.setMd5(md5);
        fileCenter.setMongoId(mongoId);
        fileCenter.setMongoName(mongoName);
        fileCenter.setType(type);
        fileCenter.setCode(code);
        fileCenter.setStatus(0);
        fileCenter.setSiteId(siteId);
        fileCenter.setColumnId(columnId);
        fileCenter.setContentId(contentId);
        fileCenter.setCreateDate(new Date());
        fileCenter.setCreateUserId(LoginPersonUtil.getUserId());
        fileCenter.setCreateOrganId(LoginPersonUtil.getOrganId());
        fileCenter.setUpdateDate(new Date());
        fileCenter.setUpdateUserId(LoginPersonUtil.getUserId());
        fileCenter.setDesc(desc);
        fileCenter.setRecordStatus("Normal");
        try {
            HttpServletRequest request2 = LoginPersonUtil.getRequest();
            fileCenter.setIp(IpUtil.getIpAddr(request2));
        } catch (Exception e) {
            fileCenter.setIp("127.0.0.1");
            e.printStackTrace();
        }
        return fileCenter;
    }

    /**
     * 
     * @Title: editorUpload
     * @Description: the editor upload util
     * @param b
     * @param fileName
     * @param type
     * @param code
     * @param siteId
     * @param columnId
     * @param contentId
     * @param desc
     * @param request
     * @return 设定文件
     * @return MongoFileVO 返回类型
     * @throws
     */
    public static MongoFileVO editorUpload(byte[] b, String fileName, String type, String code, Long siteId, Long columnId, Long contentId, String desc,
            HttpServletRequest request) {
        MongoFileVO mongoVO = mongoDbFileServer.uploadByteFile(b, fileName, null, null);
        String singleName = fileName.substring(0, fileName.lastIndexOf("."));
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        String md5 = MD5Util.getMd5ByByte(b);
        FileCenterEO fileCenter = transFileEO(singleName, suffix, (long) b.length, md5, mongoVO.getMongoId(),mongoVO.getFileName(), type, code, siteId, columnId, contentId, desc);
        fileCenter.setStatus(1);
        fileCenter.setCreateUserId((Long) LoginPersonUtil.getSession().getAttribute("userId"));
        fileCenter.setCreateOrganId((Long) LoginPersonUtil.getSession().getAttribute("organId"));
        fileCenter.setCreateDate(new Date());
        fileCenter.setUpdateUserId((Long) LoginPersonUtil.getSession().getAttribute("userId"));
        fileCenter.setUpdateDate(new Date());
        HttpServletRequest request2 = LoginPersonUtil.getRequest();
        fileCenter.setIp(IpUtil.getIpAddr(request2));
        fileCenterService.saveEntity(fileCenter);
        return mongoVO;
    }

    /**
     * 
     * @Title: editorUpload
     * @Description: the editor upload util
     * @param Filedata
     * @param type
     * @param code
     * @param siteId
     * @param columnId
     * @param contentId
     * @param desc
     * @param request
     * @return 设定文件
     * @return MongoFileVO 返回类型
     * @throws
     */
    public static MongoFileVO editorUpload(MultipartFile Filedata, String type, String code, Long siteId, Long columnId, Long contentId, String desc,
            HttpServletRequest request) {
        MongoFileVO mongoVO = mongoDbFileServer.uploadMultipartFile(Filedata, null);
        String fileName = Filedata.getOriginalFilename();
        String singleName = fileName.substring(0, fileName.lastIndexOf("."));
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        String md5 = null;
        try {
            md5 = MD5Util.getMd5ByByte(Filedata.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileCenterEO fileCenter = transFileEO(singleName, suffix, Filedata.getSize(), md5, mongoVO.getMongoId(), mongoVO.getFileName(),type, code, siteId, columnId, contentId, desc);
        fileCenter.setStatus(1);
        fileCenter.setCreateUserId((Long) LoginPersonUtil.getSession().getAttribute("userId"));
        fileCenter.setCreateOrganId((Long) LoginPersonUtil.getSession().getAttribute("organId"));
        fileCenter.setCreateDate(new Date());
        fileCenter.setUpdateUserId((Long) LoginPersonUtil.getSession().getAttribute("userId"));
        fileCenter.setUpdateDate(new Date());
        fileCenterService.saveEntity(fileCenter);
        return mongoVO;
    }

    /**
     * 
     * @Title: deleteByContentIds
     * @Description: 根据contentIds 更改引用状态 set the quote status by contentIds
     * @param contentIds
     * @param status
     *            1：标记已被引用 0： 标记取消引用
     * @return void 返回类型
     * @throws
     */
    public static void markByContentIds(Long[] contentIds, Integer status) {
        fileCenterService.markStatusByContentId(contentIds, status);
    }
}
